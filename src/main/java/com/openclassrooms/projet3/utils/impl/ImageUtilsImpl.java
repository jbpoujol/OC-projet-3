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


    /**
     * Stores the uploaded file in the server's filesystem and returns the URL to access the file.
     * This method performs several checks and operations to securely store an uploaded file:
     * - Validates that the file is not empty to prevent storing unnecessary data.
     * - Determines the 'uploads' directory path where the file will be stored. If the directory
     *   doesn't exist, it creates it.
     * - Generates a unique filename for the uploaded file to avoid name collisions and maintain
     *   the original file extension. This is achieved by prefixing the original filename with
     *   the current timestamp.
     * - Validates that the file will be stored within the predefined 'uploads' directory to
     *   prevent directory traversal attacks.
     * - Transfers the file to its final destination in the filesystem.
     * - Constructs a URL that can be used to access the uploaded file. The URL is based on
     *   the current context path of the application, ensuring compatibility across different
     *   deployment environments.
     *
     * @param file the multipart file uploaded by the user.
     * @return A String representing the URL to access the uploaded file.
     * @throws IOException if the file is empty, if there's an error creating the 'uploads'
     *         directory, if the file cannot be stored securely, or if there's an error during
     *         the file transfer.
     */

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
