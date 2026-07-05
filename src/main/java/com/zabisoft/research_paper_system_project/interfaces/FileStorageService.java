package com.zabisoft.research_paper_system_project.interfaces;

import org.springframework.web.multipart.MultipartFile;


public interface FileStorageService {
    String storeFile(MultipartFile multipartFile);
    void deleteFile(String filePath);
}
