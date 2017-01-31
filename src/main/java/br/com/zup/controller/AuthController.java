package br.com.zup.controller;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.request.AuthRequest;
import br.com.zup.response.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AuthResponse login(@RequestBody @Valid AuthRequest request) {
    	if (StringUtils.isEmpty(request.getCpf())) return null;
        AuthResponse authResponse = new AuthResponse();
        authResponse.setClientId(5L);
        return authResponse;
    }
}
