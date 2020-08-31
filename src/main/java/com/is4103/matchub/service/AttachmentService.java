package com.is4103.matchub.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface AttachmentService {
    String upload(MultipartFile file, String directory);
}
