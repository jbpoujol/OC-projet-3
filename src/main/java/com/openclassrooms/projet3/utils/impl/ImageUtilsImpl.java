package com.openclassrooms.projet3.utils.impl;

import com.openclassrooms.projet3.utils.ImageUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageUtilsImpl implements ImageUtils {

    @Override
    public String storePicture(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }
        String uploadsDirPath = "uploads";
        Path uploadsDir = Paths.get(uploadsDirPath);
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationFile = uploadsDir.resolve(Paths.get(filename)).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(uploadsDir.toAbsolutePath())) {
            throw new IOException("Cannot store file outside of the predefined directory.");
        }

        file.transferTo(destinationFile);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + "/uploads/" + filename;
    }
}
