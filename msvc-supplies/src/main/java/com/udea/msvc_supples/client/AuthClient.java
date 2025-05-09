package com.udea.msvc_supples.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "msvc-auth", url = "${app.auth-service.url}")
public interface AuthClient {

    @GetMapping("/api/auth/validate")
    boolean validateToken(@RequestHeader("Authorization") String token);
}