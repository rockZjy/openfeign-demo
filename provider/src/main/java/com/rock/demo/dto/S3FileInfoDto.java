package com.rock.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *  S3 文件 DTO
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3FileInfoDto {
    /**
     * 文件名称
     */
    private String fileName;

    /**
     * bucket名称
     */
    private String bucketName;

    /**
     * 文件url
     */
    private String fileUrl;
}


