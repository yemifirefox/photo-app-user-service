package com.yemifirefox.photoappuserservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yemifirefox.photoappuserservice.model.AlbumResponseModel;
import com.yemifirefox.photoappuserservice.model.AlbumServiceClient;
import com.yemifirefox.photoappuserservice.model.AlbumServiceClient;
import com.yemifirefox.photoappuserservice.model.User;
import com.yemifirefox.photoappuserservice.model.UserDto;
import com.yemifirefox.photoappuserservice.repository.UserRepository;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AlbumServiceClient albumServiceClient;

    public UserDto createUser(UserDto userDto){

        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User userEntity = modelMapper.map(userDto, User.class);

        userRepository.save(userEntity);
        UserDto response = modelMapper.map(userEntity, UserDto.class);
        return response;
    }


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) throw new UsernameNotFoundException(email);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getEncryptedPassword(), true, true,
                true, true, new ArrayList<>()
        );
    }

    public UserDto getUserDetailsByEmail(String email) {

        User user = userRepository.findByEmail(email);
        if(user == null) throw new UsernameNotFoundException(email);

        return new ModelMapper().map(user, UserDto.class);
    }

    public UserDto getUserById(String userId) {
        User user = userRepository.findByUserId(userId);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
       UserDto userDto = new ModelMapper().map(user, UserDto.class);

        //List<AlbumResponseModel> listOfAlbum = null;


        logger.info("before calling album micro-service");
        List<AlbumResponseModel> listOfAlbum = albumServiceClient.getAlbums(userDto.getUserId());
        logger.info("after calling album micro-service");

        userDto.setAlbums(listOfAlbum);
        return userDto;
    }
}
