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


public class FileDownloader {

    public static void downloadFileIfNotExists(String fileURL, String localFilePath) throws InterruptedException {
        Path pathVar = Path.of(localFilePath);
        if (Files.exists(pathVar)) {
            System.out.println("File already exists: " + localFilePath);
            return;
        }
        downloadFile(fileURL, localFilePath);
    }

    public static void downloadFileIfNotExistsAndDrawBorder(String fileURL, String localFilePath) throws InterruptedException, IOException {
        Path pathVar = Path.of(localFilePath);
        if (Files.exists(pathVar)) {
            System.out.println("File already exists: " + localFilePath);
            //addBordersToImage(localFilePath); // tmp
            return;
        }
        downloadFile(fileURL, localFilePath);
        addBordersToImage(localFilePath);
    }

    public static void addBordersToImage(String localFilePath) throws IOException {
        if (!(localFilePath.endsWith(".jpg") || localFilePath.endsWith(".png"))) {
            return;
            //throw new IllegalArgumentException("Only jpg and png files are supported.");
        }

        // Load the image
        File imageFile = new File(localFilePath);
        BufferedImage originalImage = ImageIO.read(imageFile);

        // Desired border width in pixels
        int borderWidth = 24;
        int triangleSize = 72;
        Color borderColor = new Color(24, 21, 16, 255);

        // Create a new BufferedImage with increased dimensions to accommodate the border
        int newWidth = originalImage.getWidth() + 2 * borderWidth;
        int newHeight = originalImage.getHeight() + 2 * borderWidth;
        BufferedImage borderedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // Create graphics object to draw on the bordered image
        Graphics2D g2d = borderedImage.createGraphics();

        // Set the background color (in this case, white)
        g2d.setColor(borderColor);
        g2d.fillRect(0, 0, newWidth, newHeight);

        // Draw the original image onto the bordered image with an offset to account for the border
        int x = borderWidth;
        int y = borderWidth;
        g2d.drawImage(originalImage, x, y, null);

        // Set the color of the borders (in this case, black)
        g2d.setColor(borderColor);

        // Draw the top border
        g2d.fillRect(0, 0, newWidth, borderWidth);
        // Draw the bottom border
        g2d.fillRect(0, newHeight - borderWidth, newWidth, borderWidth);
        // Draw the left border
        g2d.fillRect(0, borderWidth, borderWidth, originalImage.getHeight());
        // Draw the right border
        g2d.fillRect(newWidth - borderWidth, borderWidth, borderWidth, originalImage.getHeight());

        // Set the color of the triangles (in this case, black)
        g2d.setColor(borderColor);

        // Draw triangles at each corner
        g2d.fillPolygon(new int[]{0, triangleSize, 0}, new int[]{0, 0, triangleSize}, 3); // Top-left corner
        g2d.fillPolygon(new int[]{newWidth - triangleSize, newWidth, newWidth}, new int[]{0, 0, triangleSize}, 3); // Top-right corner
        g2d.fillPolygon(new int[]{0, triangleSize, 0}, new int[]{newHeight, newHeight, newHeight - triangleSize}, 3); // Bottom-left corner
        g2d.fillPolygon(new int[]{newWidth - triangleSize, newWidth, newWidth}, new int[]{newHeight, newHeight, newHeight - triangleSize}, 3); // Bottom-right corner

        // Dispose of the graphics object to free up resources
        g2d.dispose();

        // Save the image with borders and triangles
        File outputFile = new File(localFilePath);
        ImageIO.write(borderedImage, "png", outputFile);

        System.out.println("Borders and triangles added successfully!");
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