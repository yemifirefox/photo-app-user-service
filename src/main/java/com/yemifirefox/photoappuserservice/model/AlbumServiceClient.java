package com.yemifirefox.photoappuserservice.model;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "albums-ws", fallbackFactory = AlbumServiceClientFallBackFactory.class)
public interface AlbumServiceClient {

    @GetMapping("/users/{userId}/albums")
    public List<AlbumResponseModel> getAlbums(@PathVariable String userId);
}

@Component
class AlbumServiceClientFallBackFactory implements FallbackFactory<AlbumServiceClient> {


    @Override
    public AlbumServiceClient create(Throwable cause) {
        return new AlbumServiceClientFallBack(cause);
    }

    class AlbumServiceClientFallBack implements AlbumServiceClient{

        Logger logger = LoggerFactory.getLogger(this.getClass());
        private final Throwable cause;
        public AlbumServiceClientFallBack(Throwable cause) {
        this.cause = cause;
        }

        @Override
        public List<AlbumResponseModel> getAlbums(String userId) {

            if(cause instanceof FeignException && ((FeignException) cause).status() == 404){
                logger.info("404 error took place when calling getAlbums endpoint");
            }
            return new ArrayList<>();
        }
    }
}
