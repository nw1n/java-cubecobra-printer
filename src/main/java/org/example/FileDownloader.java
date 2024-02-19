package org.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class FileDownloader {

    public static void downloadFileIfNotExists(String fileURL, String localFilePath) throws InterruptedException {
        Path pathVar = Path.of(localFilePath);
        if (Files.exists(pathVar)) {
            System.out.println("File already exists: " + localFilePath);
            addBordersToImage(localFilePath);
            return;
        }
        downloadFile(fileURL, localFilePath);
        addBordersToImage(localFilePath);
    }

    public static void addBordersToImage(String localFilePath) {
        if (!(localFilePath.endsWith(".jpg") || localFilePath.endsWith(".png"))) {
            return;
            //throw new IllegalArgumentException("Only jpg and png files are supported.");
        }
        // add triangles to image
        try {
            // Load the image
            File imageFile = new File(localFilePath);
            BufferedImage originalImage = ImageIO.read(imageFile);

            // Create a graphics object to draw on the original image
            Graphics2D g2d = originalImage.createGraphics();

            // Set the color of the triangles (in this case, black)
            g2d.setColor(new Color(24, 21, 16, 255));

            // Draw triangles at each corner
            int triangleSize = 80; // Size of the triangles
            g2d.fillPolygon(new int[]{0, triangleSize, 0}, new int[]{0, 0, triangleSize}, 3); // Top-left corner
            g2d.fillPolygon(new int[]{originalImage.getWidth() - triangleSize, originalImage.getWidth(), originalImage.getWidth()}, new int[]{0, 0, triangleSize}, 3); // Top-right corner
            g2d.fillPolygon(new int[]{0, triangleSize, 0}, new int[]{originalImage.getHeight(), originalImage.getHeight(), originalImage.getHeight() - triangleSize}, 3); // Bottom-left corner
            g2d.fillPolygon(new int[]{originalImage.getWidth() - triangleSize, originalImage.getWidth(), originalImage.getWidth()}, new int[]{originalImage.getHeight(), originalImage.getHeight(), originalImage.getHeight() - triangleSize}, 3); // Bottom-right corner

            // Dispose of the graphics object to free up resources
            g2d.dispose();

            // Save the image with triangles
            File outputFile = new File(localFilePath.replace(".png", "_triangles.png"));
            ImageIO.write(originalImage, "png", outputFile);

            System.out.println("Triangles added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
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