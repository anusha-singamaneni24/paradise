package com.anusha.paradise.controller;

import com.anusha.paradise.model.ResponseModel;
import com.anusha.paradise.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/image")
public class ImageController {
    @Autowired
    ImageService imageService;

    @PostMapping()
    public ResponseEntity<ResponseModel> uploadFile(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("propertyId") String propertyId) {
        return new ResponseEntity<>(imageService.storeImage(file,propertyId), HttpStatus.CREATED);
    }
}
