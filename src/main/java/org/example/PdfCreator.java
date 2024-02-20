package org.example;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.opencsv.exceptions.CsvException;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.Loader;

public class PdfCreator {
    public static void createPdfs() throws IOException, CsvException {
        CardsManager cardsManager = CardsManager.getInstance();

        if(cardsManager.getCards() == null || cardsManager.getCards().size() == 0){
            System.out.println("No cards to add to PDFs. Skipping creation of PDFs.");
            return;
        }

        deleteAndRecreatePdfFolder();

        System.out.println("Init Creating PDFs...");
        if(Config.getInstance().getIsChunkMode()) {
            System.out.println("Chunk mode is enabled. Init PDF chunk creation...");
            createPdfChunks("one-sided");
            createPdfChunks("two-sided");
            System.out.println("Finished Creating PDF chunks.");
        } else {
            System.out.println("Chunk mode is disabled. Init PDF creation...");
            // create pdfs
            createFullPdf("one-sided");
            //createAllOneSidedCardsPdfChunks();
            createFullPdf("two-sided");
            System.out.println("Finished Creating PDFs.");
            System.out.println("PDF one-sided-cards page count: " + getNumberOfPages(Config.getInstance().getPdfOneSidedLocalPath()));
            System.out.println("PDF two-sided-cards page count: " + getNumberOfPages(Config.getInstance().getPdfTwoSidedLocalPath()));
            checkPdfsPageSum();
        }
    }

    public static void createPdfChunks(String mode) throws IOException, CsvException {
        boolean isTwoSidedMode = mode.equals("two-sided");
        boolean isOneSidedMode = isTwoSidedMode ? false : true;
        CardsManager cardsManager = CardsManager.getInstance();
        ArrayList<Card> cards = isTwoSidedMode ? cardsManager.getTwoSidedCards() : cardsManager.getOneSidedCards();

        if(isOneSidedMode) {
            Util.createFolder(Config.getInstance().getChunkedOneSidedPdfFolder());
        } else {
            Util.createFolder(Config.getInstance().getChunkedTwoSidedPdfFolder());
        }
        
        if(cards.size() == 0) {
            System.out.println("No cards to add to PDF. Skipping creation of one sided PDF.");
            return;
        }

        int chunkSize = Config.getInstance().getPdfChunkSize();

        int chunkCount = (int) Math.ceil((double) cards.size() / chunkSize);

        System.out.println("Creating " + chunkCount + " one sided PDF chunks...");

        ArrayList <String> pdfPaths = new ArrayList<>();

        for (int i = 1; i <= chunkCount; i++) {
            int start = (i - 1) * chunkSize;
            int end = Math.min(cards.size(), start + chunkSize);
            ArrayList<Card> cardsChunk = new ArrayList<>(cards.subList(start, end));
            createCardsPdfChunk(mode, i, cardsChunk, pdfPaths);
        }

        System.out.println("Finished creating one sided PDF chunks.");
    }

    private static void createCardsPdfChunk(String mode, int Index, ArrayList<Card> cardsChunk, ArrayList <String> pdfPaths) throws IOException {
        if(cardsChunk.size() == 0) {
            System.out.println("No one cards to add to PDF Chunk. Skipping creation of new pdf chunk.");
            return;
        }

        boolean isTwoSidedMode = mode.equals("two-sided");
        //boolean isOneSidedMode = isTwoSidedMode ? false : true;

        System.out.println("Creating PDF chunk for " + mode + " mode...");
        CardsManager cardsManager = CardsManager.getInstance();
        PDDocument document = new PDDocument();

        Card firstCard = cardsManager.getCards().get(0);
        System.out.println("Using first image " + firstCard.getLocalFrontImageFilePath() + " for PDF Dimensions");
        PDImageXObject pdImageFirst = PDImageXObject.createFromFile(firstCard.getLocalFrontImageFilePath(), document);
        float imageWidth = pdImageFirst.getWidth();
        float imageHeight = pdImageFirst.getHeight();

        for (int i = 0; i < cardsChunk.size(); i++) {
            Card currentCard = cardsChunk.get(i);
            addImagePageToPdf(document, currentCard.getLocalFrontImageFilePath(), imageWidth, imageHeight);
            if(isTwoSidedMode) {
                addImagePageToPdf(document, currentCard.getLocalBackImageFilePath(), imageWidth, imageHeight);
            }
        }

        String baseFolder = isTwoSidedMode ? Config.getInstance().getChunkedTwoSidedPdfFolder() : Config.getInstance().getChunkedOneSidedPdfFolder();
        String pdfPath = baseFolder + "chunk-" + Index + ".pdf";
        pdfPaths.add(pdfPath);
        document.save(pdfPath);
        document.close();
        System.out.println("Succesfully Created PDF Chunk " + Index + " for " + mode + " mode.");
    }

    private static void addImagePageToPdf(PDDocument document, String imagePath, float imageWidth, float imageHeight) throws IOException {
        System.out.println("Adding image " + imagePath + " to PDF");
        PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, document);
        PDPage page = new PDPage(new PDRectangle(imageWidth, imageHeight));
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Add image to cover the entire page
            contentStream.drawImage(pdImage, 0, 0, imageWidth, imageHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFullPdf(String mode) throws IOException {
        boolean isTwoSidedMode = mode.equals("two-sided");
        String pdfPath = isTwoSidedMode ? Config.getInstance().getPdfTwoSidedLocalPath() : Config.getInstance().getPdfOneSidedLocalPath();
        System.out.println("Creating one sided PDF...");
        CardsManager cardsManager = CardsManager.getInstance();
        PDDocument document = new PDDocument();
        Card firstCard = cardsManager.getCards().get(0);
        System.out.println("Using first image " + firstCard.getLocalFrontImageFilePath() + " for PDF Dimensions");
        PDImageXObject pdImageFirst = PDImageXObject.createFromFile(firstCard.getLocalFrontImageFilePath(), document);
        float imageWidth = pdImageFirst.getWidth();
        float imageHeight = pdImageFirst.getHeight();

        ArrayList<Card> cardsList = isTwoSidedMode ? cardsManager.getTwoSidedCards() : cardsManager.getOneSidedCards();

        if(cardsList.size() == 0) {
            System.out.println("No one sided cards to add to PDF. Skipping creation of one sided PDF.");
            document.close();
            return;
        }

        for (int i = 0; i < cardsList.size(); i++) {
            Card currentCard = cardsList.get(i);
            addImagePageToPdf(document, currentCard.getLocalFrontImageFilePath(), imageWidth, imageHeight);
            if(isTwoSidedMode) {
                addImagePageToPdf(document, currentCard.getLocalBackImageFilePath(), imageWidth, imageHeight);
            }
        }

        document.save(pdfPath);
        document.close();
        System.out.println("Succesfully Created one sided PDF.");
    }

    public static boolean checkPdfsPageSum() throws IOException, CsvException {
        // read csv to get number of cards
        List<String[]> csvData = CsvUtils.readCsvFile(Config.getInstance().getCsvLocalFilePath());
        int numberOfCardsInCsv = csvData.size();
        // get number of pages from pdfs
        int pagesInPdfOneSided = getNumberOfPages(Config.getInstance().getPdfOneSidedLocalPath());
        int pagesInPdfTwoSided = getNumberOfPages(Config.getInstance().getPdfTwoSidedLocalPath());
        // check if number of pages match number of cards
        int cardsInPdfOneSided = pagesInPdfOneSided;
        int cardsInPdfTwoSided = pagesInPdfTwoSided / 2;
        int numberOfCardsInPdfs = cardsInPdfOneSided + cardsInPdfTwoSided;

        if(numberOfCardsInPdfs != numberOfCardsInCsv) {
            System.out.println("Check failed. Number of cards in one sided PDF does not match number of cards in two sided PDF!");
            System.out.println("Number of cards in one sided PDF: " + cardsInPdfOneSided);
            System.out.println("Number of cards in two sided PDF: " + cardsInPdfTwoSided);
            System.out.println("Number of cards in CSV: " + numberOfCardsInCsv);
            return false;
        }

        System.out.println("Check passed. Number of cards in PDFs matches number of rows in CSV: " + numberOfCardsInCsv);

        System.out.println("");
        System.out.println("----------------------------------------");
        System.out.println("");
        System.out.println("SUCCESS!");
        System.out.println("");
        System.out.println("Finished creating PDFs.");
        System.out.println("The PDFs are located in the following folder:");
        System.out.println(Config.getInstance().getPdfFolder());
        return true;
    }

    public static int getNumberOfPages(String pdfFilePath) {
        // check if file exists first
        File pdfFile = new File(pdfFilePath);
        if(!pdfFile.exists()) {
            // System.out.println("File " + pdfFilePath + " does not exist.");
            return 0;
        }

        try (PDDocument document = Loader.loadPDF(new File(pdfFilePath))) {
            int pageCount = document.getNumberOfPages();
            return pageCount;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static void deleteAndRecreatePdfFolder() throws IOException {
        // delete pdf folder and all its content
        File pdfFolder = new File(Config.getInstance().getPdfFolder());
        if(pdfFolder.exists()) {
            System.out.println("Deleting PDF folder and all its content...");
            Util.deleteFolderRecursively(Config.getInstance().getPdfFolder());
            System.out.println("Succesfully Deleted PDF folder and all its content.");
            // recreate pdf folder
            Util.createFolder(Config.getInstance().getPdfFolder());
            System.out.println("Recreated PDF folder.");
        }
    }
}
