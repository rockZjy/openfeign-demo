package com.rock.demo.Controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/feign")
public class ProviderController {

    @GetMapping("test/{data}")
    public String testGet(@PathVariable("data") String data) {
        return "test-get";
    }

    @PostMapping("test/{data}")
    public String testPost(@PathVariable("data") String data) {
        return "test-post";
    }
    @GetMapping("test/download/file")
    public void testDowanload(@RequestParam String filename, HttpServletResponse response) {
        File file = new File("E:/ZTest/test.txt");
        String fileName = "test";
        response.setHeader("content-type","application/octet-stream");
        response.setContentType("application/octet-stream");
        try {
            response.setHeader("Content-Disposition","attachment;filename="+java.net.URLEncoder.encode(fileName+ ".txt","UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //throw new Exception("错误信息");
        }
        byte[] buff = new byte[1024];
        FileInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bis = new FileInputStream(file);
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e2){
            e2.printStackTrace();
        } finally {
            if(bis != null){
                try{
                    bis.close();
                    //os.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
