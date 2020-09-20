package com.is4103.matchub.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

//    String upload(MultipartFile file, String directory);
    String upload(MultipartFile file);

    Boolean deleteFile(String directory) throws IOException;
}
