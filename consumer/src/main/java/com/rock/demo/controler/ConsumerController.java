package com.rock.demo.controler;

import com.rock.demo.feign.api.DemoFeignApi;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;


@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Autowired
    private DemoFeignApi demoFeignApi;

    @GetMapping("test/{data}")
    public String testGet(@PathVariable("data") String data) {
        return demoFeignApi.testGetMethod(data);
    }

    @PostMapping("test/{data}")
    public String testPost(@PathVariable("data") String data) {
        return demoFeignApi.testPostMethod(data);
    }

    @GetMapping("/test/file/download")
    public void testDownload(@RequestParam String filename, HttpServletResponse httpResponse){
            /*设置参数*/
            Response feignResponse= demoFeignApi.download(filename);
            try {
                httpResponse.reset();
                httpResponse.setHeader("content-type","application/octet-stream");
                httpResponse.setContentType("application/octet-stream");
                httpResponse.setHeader("Content-Disposition","attachment;filename="+java.net.URLEncoder.encode(filename+ ".txt" ,"UTF-8"));
                ServletOutputStream outputStream = httpResponse.getOutputStream();
                Response.Body body = feignResponse.body();
                InputStream inputStream = body.asInputStream();
                IOUtils.copy(inputStream,outputStream);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

}
