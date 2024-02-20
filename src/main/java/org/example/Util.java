package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Util {
    public static void createProjectFolders() {
        Config config = Config.getInstance();
        createFolder(config.getTmpFolder());
        createFolder(config.getCardJsonFolder());
        createFolder(config.getCardImgFolder());
        createFolder(config.getPdfFolder());
        createFolder(config.getCsvFolder());
    }

    public static void createFolder(String folderPath) {
        File folder = new File(folderPath);

        // Check if the folder does not exist
        if (!folder.exists()) {
            // Create the folder and its parent directories if they do not exist
            boolean success = folder.mkdirs();

            if (success) {
                System.out.println("Folder created successfully.");
            } else {
                System.err.println("Failed to create folder.");
            }
        } else {
            System.out.println("Folder already exists.");
        }
    }

    public static void deleteFolderRecursively(String folderStr) throws IOException {
        Path folder = Path.of(folderStr);
        if (Files.exists(folder)) {
            Files.walk(folder)
                 .sorted((a, b) -> -a.compareTo(b))
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         System.err.println("Failed to delete: " + path);
                         e.printStackTrace();
                     }
                 });
        }
    }

    public static String getCubeIdFromUrl(String url) {
        url = url.trim();
        String[] splitUrl = url.split("/");
        String cubeId = splitUrl[splitUrl.length - 1];
        return cubeId;
    }

    public static String getCubecobraCsvUrl(String inputStr) {
        String cubeId = getCubeIdFromUrl(inputStr);
        return String.format("https://cubecobra.com/cube/download/csv/%s", cubeId);
    }

    public static String getAsciiArtMainTitle() {
        return "\n" +
                "██████╗██╗   ██╗██████╗ ███████╗ ██████╗ ██████╗ ██████╗ ██████╗  █████╗ \n" +
                "██╔════╝██║   ██║██╔══██╗██╔════╝██╔════╝██╔═══██╗██╔══██╗██╔══██╗██╔══██╗\n" +
                "██║     ██║   ██║██████╔╝█████╗  ██║     ██║   ██║██████╔╝██████╔╝███████║\n" +
                "██║     ██║   ██║██╔══██╗██╔══╝  ██║     ██║   ██║██╔══██╗██╔══██╗██╔══██║\n" +
                "╚██████╗╚██████╔╝██████╔╝███████╗╚██████╗╚██████╔╝██████╔╝██║  ██║██║  ██║\n" +
                " ╚═════╝ ╚═════╝ ╚═════╝ ╚══════╝ ╚═════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝\n" +
                "\n" +
                "██████╗ ██████╗ ██╗███╗   ██╗████████╗███████╗██████╗                     \n" +
                "██╔══██╗██╔══██╗██║████╗  ██║╚══██╔══╝██╔════╝██╔══██╗                    \n" +
                "██████╔╝██████╔╝██║██╔██╗ ██║   ██║   █████╗  ██████╔╝                    \n" +
                "██╔═══╝ ██╔══██╗██║██║╚██╗██║   ██║   ██╔══╝  ██╔══██╗                    \n" +
                "██║     ██║  ██║██║██║ ╚████║   ██║   ███████╗██║  ██║                    \n" +
                "╚═╝     ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝   ╚══════╝╚═╝  ╚═╝                    \n" +
                "\n";
    }
}
