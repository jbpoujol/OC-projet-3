package com.openclassrooms.projet3.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUtils {
    public String storePicture(MultipartFile file) throws IOException;
}
