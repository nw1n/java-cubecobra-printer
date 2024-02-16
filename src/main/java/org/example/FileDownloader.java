package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileDownloader {

    public static void downloadFileIfNotExists(String fileURL, String localFilePath) throws InterruptedException {
        Path pathVar = Path.of(localFilePath);
        if (Files.exists(pathVar)) {
            System.out.println("File already exists: " + localFilePath);
            return;
        }
        downloadFile(fileURL, localFilePath);
    }

    public static void downloadFile(String fileURL, String localFilePath) throws InterruptedException {
        Path pathVar = Path.of(localFilePath);
        Thread.sleep(50);
    
        try {
            URL url = new URL(fileURL);
            try (InputStream in = url.openStream()) {
                Files.copy(in, pathVar, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File downloaded successfully:" + localFilePath);
            }
        } catch (IOException e) {
            System.out.println("Error downloading file: " + e.getMessage());
        }
    }
}