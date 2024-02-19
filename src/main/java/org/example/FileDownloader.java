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
        // add borders to image
        try {
            // Load the image
            File imageFile = new File(localFilePath);
            BufferedImage originalImage = ImageIO.read(imageFile);

            // Desired border width in pixels
            int borderWidth = 10;

            // Create a new BufferedImage with increased dimensions to accommodate the border
            int newWidth = originalImage.getWidth() + 2 * borderWidth;
            int newHeight = originalImage.getHeight() + 2 * borderWidth;
            BufferedImage borderedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

            // Create graphics object to draw on the bordered image
            Graphics2D g2d = borderedImage.createGraphics();

            // Set the background color (in this case, white)
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, newWidth, newHeight);

            // Draw the original image onto the bordered image with an offset to account for the border
            int x = borderWidth;
            int y = borderWidth;
            g2d.drawImage(originalImage, x, y, null);

            // Set the color of the border (in this case, black)
            g2d.setColor(Color.BLACK);

            // Draw the top border
            g2d.fillRect(0, 0, newWidth, borderWidth);
            // Draw the bottom border
            g2d.fillRect(0, newHeight - borderWidth, newWidth, borderWidth);
            // Draw the left border
            g2d.fillRect(0, borderWidth, borderWidth, originalImage.getHeight());
            // Draw the right border
            g2d.fillRect(newWidth - borderWidth, borderWidth, borderWidth, originalImage.getHeight());

            // Dispose of the graphics object to free up resources
            g2d.dispose();

            // Save the bordered image
            File outputFile = new File(localFilePath.replace(".png", "_bordered.png"));
            ImageIO.write(borderedImage, "png", outputFile);

            System.out.println("Border added successfully!");
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