package com.is4103.matchub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;

@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    @Value("${upload.image.extensions}")
    private String validImageExtensions;

    @Value("${upload.file.directory}/resources/main/files")
    private String imageDirectory;

//    @Override
//    public String upload(MultipartFile file, String directory) {
//        checkValidExtension(file.getOriginalFilename());
//        String extension = getFileExtension(file.getOriginalFilename());
//        File fileName;
//        try {
//            System.out.println("Uploaded File To: " + directory);
//            File temp;
//            if (directory != null) {
//                temp = new File(imageDirectory + "/" + directory);
//            } else {
//                temp = new File(imageDirectory);
//            }
//            temp.mkdirs();
//            fileName = File.createTempFile("att-", "." + extension, temp);
//            Files.copy(file.getInputStream(), fileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
////        return fileName.getName();
//        return fileName.getAbsolutePath();
//    }
//    
    @Override
    public String upload(MultipartFile file) {
        checkValidExtension(file.getOriginalFilename());
        String extension = getFileExtension(file.getOriginalFilename());
        File fileName;
        try {
//            System.out.println("Uploaded File To: " + directory);
            File temp;

            temp = new File(imageDirectory);
            temp.mkdirs();
            fileName = File.createTempFile("att-", "." + extension, temp);
            Files.copy(file.getInputStream(), fileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return fileName.getName();
        return imageDirectory + "/" + fileName.getName();
    }

    String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return null;
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    void checkValidExtension(String fileName) {
        String fileExtension = getFileExtension(fileName);

        if (fileExtension == null) {
            throw new InvalidRequestException("No File Extension");
        }

        fileExtension = fileExtension.toLowerCase();

        for (String validExtension : validImageExtensions.split(",")) {
            if (fileExtension.equals(validExtension)) {
                return;
            }
        }
        throw new InvalidRequestException("Invalid file extension: " + fileExtension);
    }
}
