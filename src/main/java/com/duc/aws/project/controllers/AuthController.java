package com.duc.aws.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duc.aws.project.dto.AuthDTO;
import com.duc.aws.project.model.JwtUser;
import com.duc.aws.project.services.JwtService;
import com.duc.aws.project.services.UserService;

@RestController
public class AuthController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtService jwtService;
	
	@GetMapping(value = "/api/secure/hello/{name}")
    public ResponseEntity<?> helloSecure(@PathVariable String name)
    {
        String result = String.format("Hello JWT, %s! (Secure)", name);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/api/public/hello/{name}")
    public ResponseEntity<?> helloPublic(@PathVariable String name)
    {
        String result = String.format("Hello JWT, %s! (Public)", name);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/api/public/auth")
    public ResponseEntity<?> auth(@RequestBody AuthDTO auth) {
        String userName = auth.getUsername();
        String passWord = auth.getPassword();
        Boolean correctCredentials = userService.authenticate(userName, passWord);
        if (correctCredentials) {
            JwtUser jwtUser = new JwtUser(userName);
            return ResponseEntity.ok(jwtService.getToken(jwtUser));
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }
	
}
