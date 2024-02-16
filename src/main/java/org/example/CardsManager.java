package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvException;

public class CardsManager {
    private static CardsManager instance;
    private final ArrayList<Card> cards;

    private CardsManager() {
        this.cards = new ArrayList<>();
    }

    public static CardsManager getInstance() {
        if (instance == null) {
            instance = new CardsManager();
        }
        return instance;
    }

    public void downloadCardJsonFiles() throws InterruptedException {
        for(int i = 0; i < 10; i++) {
            for (Card card : cards) {
                Scryfall.downloadJsonFile(card.getScryfallJsonUrl());
            }
            if(isAllCardsJsonExist()) {
                System.out.println("Sucess. All json downloaded.");
                return;
            }
            Thread.sleep(2000);
        }

        throw new InterruptedException("Download json failed. Not all json files downloaded.");
    }

    public void downloadCardImages() throws InterruptedException {
        for(int i = 0; i < 10; i++) {
            for (Card card : cards) {
                FileDownloader.downloadFileIfNotExists(card.getFrontImageUrl(), card.getLocalFrontImageFilePath());
                if(card.getIsFlipCard()) {
                    FileDownloader.downloadFileIfNotExists(card.getBackImageUrl(), card.getLocalBackImageFilePath());
                }
            }
            if(isAllCardsImagesExist()) {
                System.out.println("Sucess. All images downloaded.");
                return;
            }
            Thread.sleep(2000);
        }
        
        throw new InterruptedException("Download images failed. Not all image files downloaded.");
    }

    public boolean isAllCardsImagesExist() {
        for (Card card : cards) {
            if (!Files.exists(Path.of(card.getLocalFrontImageFilePath()))) {
                return false;
            }
            if (card.getIsFlipCard() && !Files.exists(Path.of(card.getLocalBackImageFilePath()))) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllCardsJsonExist() {
        for (Card card : cards) {
            if (!Files.exists(Path.of(card.getLocalJsonFilePath()))) {
                return false;
            }
        }
        return true;
    }

    public void processCards() throws IOException {
        for (Card card : cards) {
            card.initScryfallDataFromLocalFile();
        }
    }

    // Updated createCardsFromCsvData method
    public void createCardsFromCsvData() throws IOException, CsvException {
        List<String[]> csvData = CsvUtils.readCsvFile(Config.getInstance().getCsvLocalFilePath());

        for (String[] row : csvData) {
            addCard(new Card(row[4], row[5]));
        }
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }
    
    public ArrayList<Card> getOneSidedCards() {
        ArrayList<Card> oneSidedCards = new ArrayList<>();
        for (Card card : cards) {
            if(!card.getIsFlipCard()) {
                oneSidedCards.add(card);
            }
        }
        return oneSidedCards;
    }

    public ArrayList<Card> getTwoSidedCards() {
        ArrayList<Card> twoSidedCards = new ArrayList<>();
        for (Card card : cards) {
            if(card.getIsFlipCard()) {
                twoSidedCards.add(card);
            }
        }
        return twoSidedCards;
    }
}