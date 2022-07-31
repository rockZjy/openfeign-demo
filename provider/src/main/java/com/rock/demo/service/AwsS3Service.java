package com.rock.demo.service;
import com.rock.demo.dto.AwsS3UploadFileResDto;
import com.rock.demo.dto.PreUploadResDto;
import org.springframework.web.multipart.MultipartFile;

public interface AwsS3Service {

    /**
     * 预签名上传文件
     *
     * @param bucket bucket
     * @param fileName 文件名
     * @return {@link PreUploadResDto} 上传连接和key在这里插入代码片
     */
    PreUploadResDto preUploadFile(String fileName);

    /**
     * 将文件上传到s3
     *
     * @param bucket 桶
     * @param file   文件
     * @return {@link AwsS3UploadFileResDto}
     */
    AwsS3UploadFileResDto uploadFileToS3(MultipartFile file);

    void downloadFromS3();


}
