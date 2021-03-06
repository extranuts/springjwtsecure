package com.springsec.springjwtsecure.rest;




/*
 * @author
 * @version
 * @return
 */

import com.springsec.springjwtsecure.dto.AuthenticationRequestDTO;
import com.springsec.springjwtsecure.model.User;
import com.springsec.springjwtsecure.security.jwt.JWTAuthenticationException;
import com.springsec.springjwtsecure.security.jwt.JWTTokenProvider;
import com.springsec.springjwtsecure.service.UserService;
import liquibase.pro.packaged.O;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    private final UserService userService;

    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    public ResponseEntity login(@RequestBody AuthenticationRequestDTO requestDTO) {
        try {
            String username = requestDTO.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,requestDTO.getPassword()));
            User user = userService.findByUsername(username);

            if(user==null) {
                throw  new UsernameNotFoundException("User with username: " + username + " not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getRoles());
            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);
            return ResponseEntity.ok(response);

        }catch (ArithmeticException e){
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
