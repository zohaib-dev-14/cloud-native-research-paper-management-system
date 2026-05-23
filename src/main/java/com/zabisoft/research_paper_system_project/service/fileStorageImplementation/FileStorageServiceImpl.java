package com.zabisoft.research_paper_system_project.service.fileStorageImplementation;

import com.zabisoft.research_paper_system_project.exception.FileStorageException;
import com.zabisoft.research_paper_system_project.exception.InvalidFileException;
import com.zabisoft.research_paper_system_project.interfaces.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static com.zabisoft.research_paper_system_project.util.FileConstants.MAX_FILE_SIZE;
import static com.zabisoft.research_paper_system_project.util.FileConstants.UPLOAD_DIR;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public String storeFile(MultipartFile multipartFile) {
     // null check
        if (multipartFile == null) {
            throw new InvalidFileException(
                    "File is required"
            );
        }
        // empty file check

        if (multipartFile.isEmpty()) {
            throw new InvalidFileException(
                    "File is empty"
            );
        }

        // size check must not exceed 10MB
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    "File size exceeds 10 MB limit"
            );
        }

        // get content type
        String contentType = multipartFile.getContentType();

        // allow only PDF files
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new InvalidFileException(
                    "Only PDF files are allowed"
            );
        }


        // file name validation

        String originalFileName = multipartFile.getOriginalFilename();

        if (originalFileName == null) {
            throw new InvalidFileException(
                    "Invalid PDF file name"
            );
        }
        // sanitize file name

        String cleanFileName = Paths.get(originalFileName).getFileName().toString();


        // extension validation

        if (!cleanFileName.toLowerCase().endsWith(".pdf")) {
            throw new InvalidFileException(
                    "Invalid PDF extension"
            );
        }



        try {

            // create upload directory
            Path uploadPath = Paths.get(UPLOAD_DIR);


            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // unique file name

            String uniqueFileName = UUID.randomUUID() + "-" + cleanFileName;


            // final path

            Path filePath =
                    uploadPath.resolve(uniqueFileName);

            // save file

            Files.copy(
                    multipartFile.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );
            return UPLOAD_DIR + "/" + uniqueFileName;

        } catch (IOException e) {
            throw new FileStorageException(
                    "Failed to store PDF file"
            );
        }

    }



    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete a file");
        }
    }
}
