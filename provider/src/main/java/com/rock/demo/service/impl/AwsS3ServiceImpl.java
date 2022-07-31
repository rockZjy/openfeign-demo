package com.rock.demo.service.impl;

import cn.hutool.json.JSONUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.rock.demo.constant.ErrorMsg;
import com.rock.demo.dto.AwsS3UploadFileResDto;
import com.rock.demo.dto.PreUploadResDto;
import com.rock.demo.exception.ServiceException;
import com.rock.demo.service.AwsS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    private static final String prefix = "demo";

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${s3.region}")
    private String region;

    @Value("${s3.accessKeyId}")
    private String accessKeyId;

    @Value("${s3.accessKeySecret}")
    private String accessKeySecret;

    @Value("${s3.endpoint}")
    private String endpoint;


    @Override
    public PreUploadResDto preUploadFile(String fileName) {
        PreUploadResDto res = new PreUploadResDto();

        try {
            AmazonS3 amazonS3 = s3client();
            String fastFileName = getFileKey(fileName);
            // token设置1小时后过期
            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);
            URL url = amazonS3.generatePresignedUrl(new GeneratePresignedUrlRequest(bucketName, fastFileName)
                    .withExpiration(expiration).withMethod(HttpMethod.PUT));
                res.setKey(fastFileName);
                res.setUrl(url.toString());
            log.info("Generate preUrl Request is success :{}", JSONUtil.toJsonStr(res));
        } catch (Exception e) {
            log.error("Generate preUrl Request is failure :{}", e.getMessage(), e);
            throw new ServiceException(ErrorMsg.CONNECT_S3_FAILED);
        }
        return res;
    }

    @Override
    public AwsS3UploadFileResDto uploadFileToS3(MultipartFile file) {
        String fastFileName = getFileKey(file.getOriginalFilename());
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, fastFileName, file.getInputStream(), null);
            ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                request.setMetadata(metadata);
            PutObjectResult putObjectResult = s3client().putObject(request);
            String url = endpoint + "/" + fastFileName;
            log.info("upload to s3 bucket:{} file:{} url:{} is success ! ===> putRes:{}",
                    bucketName, fastFileName, url, JSONUtil.toJsonStr(putObjectResult));
            AwsS3UploadFileResDto awsS3UploadFileResDto = new AwsS3UploadFileResDto().setS3PutRes(putObjectResult);
                awsS3UploadFileResDto.setFileName(fastFileName);
                awsS3UploadFileResDto.setBucketName(bucketName);
                awsS3UploadFileResDto.setFileUrl(url);
            return awsS3UploadFileResDto;
        } catch (Exception e) {
            log.error("upload original file:{}, fastFileName:{} to S3 is Error:{}", file.getName(), fastFileName, e.getMessage());
            throw new ServiceException(ErrorMsg.CONNECT_S3_FAILED);
        }
    }

    @Override
    public void downloadFromS3() {
        String bucketName = "";
        String keyName = "";
        S3Object s3 = s3client().getObject(bucketName, keyName);
        S3ObjectInputStream s3is = s3.getObjectContent();
        try {
            FileOutputStream fos = new FileOutputStream(new File(keyName));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * s3client
     *
     * @return {@link AmazonS3}
     */
    public AmazonS3 s3client() {
        log.info("accessKeyId: "+accessKeyId+"   accessKeySecret: "+accessKeySecret);
        try {
            AtomicReference<AmazonS3> res = new AtomicReference<>();
            Regions regions = Regions.fromName(region);
            AwsClientBuilder.EndpointConfiguration endpointConfig =
                    new AwsClientBuilder.EndpointConfiguration(endpoint, region);
            res.set(AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(accessKeyId, accessKeySecret)))
                    .withRegion(regions)
                    //.withEndpointConfiguration(endpointConfig)
                    //.withPathStyleAccessEnabled(true)
                    .build());
            if (Objects.isNull(res.get())) {
                log.warn("connect S3 Server is failure , please check your config param!");
                throw new ServiceException(ErrorMsg.CONNECT_S3_FAILED);
            }
            return res.get();
        } catch (ServiceException | IllegalArgumentException e) {
            log.warn("Failed to connect to S3 server e:{}", e.getMessage(), e);
            throw new ServiceException(ErrorMsg.CONNECT_S3_FAILED);
        }
    }

    private String getFileKey(String fileName) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //return String.format("%s/%s/%s_%s", prefix, dateDir, uuid, fileName);
        return String.format("%s/%s/%s_%s", prefix, dateDir, uuid, fileName);
    }

    private URL getPreSignedUrl(String fileName,Long expTimeMillis){
        try {
            AmazonS3 amazonS3 = s3client();
            String fastFileName = getFileKey(fileName);
            // token设置1小时后过期
            Date expiration = new Date();
            expiration.setTime(expiration.getTime() + expTimeMillis);
            URL url = amazonS3.generatePresignedUrl(new GeneratePresignedUrlRequest(bucketName, fastFileName)
                    .withExpiration(expiration).withMethod(HttpMethod.PUT));
            log.info("Generate preUrl Request is success :{}", JSONUtil.toJsonStr(url.toString()));
            return url;
        } catch (Exception e) {
            log.error("Generate preUrl Request is failure :{}", e.getMessage(), e);
            throw new ServiceException(ErrorMsg.CONNECT_S3_FAILED);
        }
    }



}
