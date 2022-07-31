package com.rock.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * AWS S3 预签名响应
 */
@ToString
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PreUploadResDto {

    private String key;

    private String url;

}
