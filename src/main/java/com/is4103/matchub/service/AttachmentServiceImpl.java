package com.is4103.matchub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
//        return imageDirectory + "/" + fileName.getName();
        return "https://localhost:8443/api/v1/files/" + fileName.getName();
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

    @Override
    @Transactional
    public Boolean deleteFile(String directory) throws IOException {
        String substring = directory.substring(35);
//        System.out.println(substring);

        String pathToDelete = imageDirectory + substring;
//        System.out.println(pathToDelete);

        Path fileToDeletePath = Paths.get(pathToDelete);
        try {
            Files.delete(fileToDeletePath);
            return true;
        } catch (IOException ex) {
            throw new IOException("Unable to delete file: " + directory);
        }

    }
}
