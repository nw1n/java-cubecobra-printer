package org.example;

import java.io.File;
import java.io.FileReader;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Scryfall {
    public static String replaceScryfallSpecialChars(String strArg) {
        // replace any character that is not a letter or a number with an x
        return strArg.replaceAll("[^a-zA-Z0-9]", "x");
    }
    public static String getCardUrl(String idOfSet, String idOfCard) {
        return "https://api.scryfall.com/cards/" + idOfSet + "/" + idOfCard;
    }
    public static String getLocalJsonFilePathFromIds(String idOfSet, String idOfCard) {
        String fileName = idOfSet + "__" + idOfCard + ".json";
        fileName = replaceScryfallSpecialChars(fileName);
        return Config.getInstance().getCardJsonFolder() + fileName;
    }
    public static String getLocalJsonFilePathFromUrl(String url) {
        String fileName = url.replace("https://api.scryfall.com/cards/", "");
        fileName = fileName.replace("/", "__") + ".json";
        fileName = replaceScryfallSpecialChars(fileName);
        return Config.getInstance().getCardJsonFolder() + fileName;
    }
    public static String getLocalFrontImageFilePathFromIds(String idOfSet, String idOfCard) {
        idOfCard = replaceScryfallSpecialChars(idOfCard);
        String fileName = idOfSet + "__" + idOfCard + ".png";
        return Config.getInstance().getCardImgFolder() + fileName;
    }
    public static String getLocalBackImageFilePathFromIds(String idOfSet, String idOfCard) {
        idOfCard = replaceScryfallSpecialChars(idOfCard);
        String fileName = idOfSet + "__" + idOfCard + "--back.png";
        return Config.getInstance().getCardImgFolder() + fileName;
    }

    public static void downloadJsonFile(String url) throws InterruptedException {
        String localFilePath = getLocalJsonFilePathFromUrl(url);
        FileDownloader.downloadFileIfNotExists(url, localFilePath);
    }

    public static void downloadImagesOfAllJsonFiles() {
        File folder = new File(Config.getInstance().getCardJsonFolder());
        File[] listOfFiles = folder.listFiles();
    
        if (listOfFiles == null) {
            System.out.println("No files found in folder: " + folder);
            return;
        }

        for (File file : listOfFiles) {
            if (!file.isFile()) {
                continue;
            }
            processJsonFile(file);
        }
    }
    
    private static void processJsonFile(File jsonFile) {
        String imageFileLocalPath = Config.getInstance().getCardImgFolder() + jsonFile.getName().replace(".json", ".png");

        if(fileExists(imageFileLocalPath)) {
            System.out.println("File already exists: " + imageFileLocalPath);
            return;
        }

        try (FileReader fileReader = new FileReader(jsonFile.getPath())) {
            System.out.println("Downloading file: " + imageFileLocalPath);
            JSONObject jsonObject = new JSONObject(new JSONTokener(fileReader));
            String pngUrl = extractPngUrl(jsonObject);
            FileDownloader.downloadFileIfNotExists(pngUrl, imageFileLocalPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractPngUrl(JSONObject jsonObject) {
        return jsonObject.getJSONObject("image_uris").getString("png");
    }
    
    private static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
}
