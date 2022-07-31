package com.rock.demo.feign.api;

import feign.Logger;
import feign.Response;
import feign.slf4j.Slf4jLogger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "feignTest", url = "localhost:8081/feign",
        configuration= DemoFeignApi.FeignAPIConfiguration.class)

public interface DemoFeignApi {
    @GetMapping("test/{data}")
    String testGetMethod(@PathVariable("data") String data);

    @PostMapping("test/{data}")
    String testPostMethod(@PathVariable("data") String data);

    @GetMapping(value = "/test/download/file")
    Response download(@RequestParam("filename") String fileName);
    class FeignAPIConfiguration {
        @Bean
        public Logger feignLogger() {
            return new Slf4jLogger();
        }

        @Bean
        public Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }
    }
}