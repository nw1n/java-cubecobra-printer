package org.example;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Card {
    private String idOfSet;
    private String collectorNumber;
    private String nameOfCard;
    private String frontImageUrl;
    private String backImageUrl;
    private Boolean isFlipCard = false;

    public Card(String idOfSet, String collectorNumber) {
        this.idOfSet = idOfSet;
        this.collectorNumber = collectorNumber;
    }

    public boolean initScryfallDataFromObj(JSONObject jsonObj) {
        if (jsonObj == null) {
            return false;
        }
        if (!jsonObj.has("name")) {
            return false;
        }

        this.nameOfCard = jsonObj.getString("name");

        if (jsonObj.getString("layout").equals("adventure")) {
            this.isFlipCard = false;
        } else if(jsonObj.getString("layout").equals("split")) {
            this.isFlipCard = false;
        } else if(jsonObj.has("card_faces")) {
            this.isFlipCard = true;
        }

        if(this.isFlipCard) {
            this.frontImageUrl = jsonObj.getJSONArray("card_faces").getJSONObject(0).getJSONObject("image_uris").getString("png");
            this.backImageUrl = jsonObj.getJSONArray("card_faces").getJSONObject(1).getJSONObject("image_uris").getString("png");
        } else {
            this.frontImageUrl = jsonObj.getJSONObject("image_uris").getString("png");
        }

        if(this.frontImageUrl == null || this.frontImageUrl.equals("")) {
            return false;
        }

        return true;
    }

    public boolean initScryfallDataFromLocalFile() throws IOException {
        JSONObject jsonObject = null;
        String jsonFilePath = this.getLocalJsonFilePath();

        if (!fileExists(jsonFilePath)) {
            return false;
        }

        FileReader fileReader = new FileReader(jsonFilePath);
        jsonObject = new JSONObject(new JSONTokener(fileReader));
        this.initScryfallDataFromObj(jsonObject);
        return true;
    }

    private static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    public String getScryfallJsonUrl() {
        return Scryfall.getCardUrl(this.idOfSet, this.collectorNumber);
    }

    public String getLocalJsonFilePath() {
        return Scryfall.getLocalJsonFilePathFromIds(this.idOfSet, this.collectorNumber);
    }

    public String getLocalFrontImageFilePath() {
        return Scryfall.getLocalFrontImageFilePathFromIds(this.idOfSet, this.collectorNumber);
    }

    public String getLocalBackImageFilePath() {
        return Scryfall.getLocalBackImageFilePathFromIds(this.idOfSet, this.collectorNumber);
    }

    public Boolean getIsFlipCard() {
        return this.isFlipCard;
    }

    public String getFrontImageUrl() {
        return this.frontImageUrl;
    }

    public String getBackImageUrl() {
        return this.backImageUrl;
    }

    public String getIdOfSet() {
        return idOfSet;
    }

    public String getCollectorNumber() {
        return collectorNumber;
    }

    public String getNameOfCard() {
        return nameOfCard;
    }
}
