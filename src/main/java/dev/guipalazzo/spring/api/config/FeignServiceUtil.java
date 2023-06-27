package dev.guipalazzo.spring.api.config;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value="feignDemo", url = "https://picsum.photos/200")
public interface FeignServiceUtil {

    @GetMapping()
    Response getPicture();
}
