package com.yemifirefox.photoappuserservice.controller;

import com.yemifirefox.photoappuserservice.model.*;
import com.yemifirefox.photoappuserservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {



    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/status/check")
    public String status(){
        return "Running on Port:" +environment.getProperty("local.server.port") +", with token secret: " +environment.getProperty("token.secret");
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userRequest, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        CreateUserResponse response = modelMapper.map(createdUser, CreateUserResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping(path = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponseModel> getUser(@PathVariable String userId){

        UserDto userDto = userService.getUserById(userId);

        UserResponseModel userResponseModel = new ModelMapper().map(userDto, UserResponseModel.class);

       /* String albumUrl = String.format(environment.getProperty("album-ws-url"), userId);
        ResponseEntity<List<AlbumResponseModel>> albumListResponse  =
                restTemplate.exchange(albumUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
                });*/
/*
        List<AlbumResponseModel> albumList = albumListResponse.getBody();
        userResponseModel.setAlbums(albumList);*/
        return new ResponseEntity<>(userResponseModel, HttpStatus.OK);
    }
}
