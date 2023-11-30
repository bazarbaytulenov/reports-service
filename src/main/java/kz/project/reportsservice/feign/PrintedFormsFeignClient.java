package kz.project.reportsservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "printedFormsService", url = "localhost:8081/api/template")
public interface PrintedFormsFeignClient {

    @GetMapping("/{code}")
    Map<String, byte[]> getTemplate(@PathVariable("code") String templateCode);

}
