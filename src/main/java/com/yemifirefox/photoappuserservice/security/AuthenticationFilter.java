package com.yemifirefox.photoappuserservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yemifirefox.photoappuserservice.model.LoginRequest;
import com.yemifirefox.photoappuserservice.model.UserDto;
import com.yemifirefox.photoappuserservice.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private UserService userService;
    private Environment environment;

    @Value("${token.secret}")
    private String tokenSecret;

    @Value("${token.expiration-time}")
    private long tokenExpirationTime;

    public AuthenticationFilter(UserService userService, Environment environment){
       this.userService = userService;
       this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try{
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword(),
                            new ArrayList<>()
                    )
            );

        }catch (IOException ex){
            throw new RuntimeException(ex);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String email = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(email);

        String secret = environment.getProperty("token.secret");
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(environment.getProperty("token.expiration-time"))))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());

    }

    }
