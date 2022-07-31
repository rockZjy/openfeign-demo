package com.rock.demo.dto;

import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Aws S3 上传后文件响应dto
 *
 * @author yunnuo
 * @date 2022-04-15
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AwsS3UploadFileResDto extends S3FileInfoDto {

    /**
     * s3 put 响应数据
     */
    private PutObjectResult s3PutRes;

}
