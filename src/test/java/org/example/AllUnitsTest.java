package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class AllUnitsTest {

    @BeforeAll
    static void setup() {
        //Config.getInstance().useDefaultTmpFolder();
    }

    @Test
    public void testScryFall() {
        // ----------------
        String result = Scryfall.getCardUrl("m21", "1");
        assertEquals("https://api.scryfall.com/cards/m21/1", result);
    }

    @Test
    public void testScryFall2() {
        // ----------------
        String result = Scryfall.getLocalJsonFilePathFromIds("m21", "1");
        assertEquals(result, "./tmp-test/card-json/m21__1.json");
    }

    @Test
    public void testFolders() {
        // ----------------
        String result = Config.getInstance().getTmpFolder();
        assertEquals(result, "./tmp-test/");
    }

    @Test
    public void testJsonData() throws IOException {
        ClassLoader classLoader = AllUnitsTest.class.getClassLoader();
        URL resourceUrl = classLoader.getResource("dummyCard.json");

        if (resourceUrl != null) {
            // Use the URL as needed
            System.out.println("Resource URL: " + resourceUrl);
        } else {
            System.out.println("Resource not found: " + resourceUrl);
        }

        // read json file data to String
        JSONObject jsonObject = null;
        try (FileReader fileReader = new FileReader(resourceUrl.getPath())) {
            jsonObject = new JSONObject(new JSONTokener(fileReader));
            System.out.println(jsonObject.get("name"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // test card object
        Card card = new Card("m21", "1");
        card.initScryfallDataFromObj(jsonObject);
        assertEquals(card.getFrontImageUrl(), "https://cards.scryfall.io/png/front/9/c/9c017fa9-7021-417a-9c2e-3df409644fcf.png?1639052473");
        assertEquals(card.getNameOfCard(), "Ugin, the Spirit Dragon");
        assertEquals(card.getScryfallJsonUrl(), "https://api.scryfall.com/cards/m21/1");
        assertEquals(card.getLocalJsonFilePath(), "./tmp-test/card-json/m21__1.json");
        assertEquals(card.getLocalFrontImageFilePath(), "./tmp-test/img/m21__1.png");
    }

    @Test
    public void testCardsManager() {
        CardsManager manager = CardsManager.getInstance();
        manager.addCard(new Card("m21", "1"));
        assertEquals(manager.getCards().size(), 1);
        assertEquals(manager.getCards().get(0).getScryfallJsonUrl(), "https://api.scryfall.com/cards/m21/1");
    }
}