package org.example;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.opencsv.exceptions.CsvException;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.Loader;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PdfCreator {
    public static void createPdfs() throws IOException, CsvException {
        CardsManager cardsManager = CardsManager.getInstance();

        if(cardsManager.getCards() == null || cardsManager.getCards().size() == 0){
            System.out.println("No cards to add to PDFs. Skipping creation of PDFs.");
            return;
        }

        Boolean isDeletePdfsMode  = true;

        if(isDeletePdfsMode) {
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

        System.out.println("Init Creating PDFs...");
        // create pdfs
        createOneSidedCardsPdf();
        //createAllOneSidedCardsPdfChunks();
        createTwoSidedCardsPdf();
        System.out.println("Finished Creating PDFs.");
        System.out.println("PDF one-sided-cards page count: " + getNumberOfPages(Config.getInstance().getPdfOneSidedLocalPath()));
        System.out.println("PDF two-sided-cards page count: " + getNumberOfPages(Config.getInstance().getPdfTwoSidedLocalPath()));
        checkPdfsPageSum();
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

    public static void createAllOneSidedCardsPdfChunks() throws IOException, CsvException {
        CardsManager cardsManager = CardsManager.getInstance();
        ArrayList<Card> cards = cardsManager.getOneSidedCards();
        if(cards.size() == 0) {
            System.out.println("No cards to add to PDF. Skipping creation of one sided PDF.");
            return;
        }
        int chunkSize = 50;
        int chunkCount = (int) Math.ceil((double) cards.size() / chunkSize);
        System.out.println("Creating " + chunkCount + " one sided PDF chunks...");
        ArrayList <String> pdfPaths = new ArrayList<>();
        for (int i = 1; i <= chunkCount; i++) {
            int start = (i - 1) * chunkSize;
            int end = Math.min(cards.size(), start + chunkSize);
            ArrayList<Card> cardsChunk = new ArrayList<>(cards.subList(start, end));
            createOneSidedCardsPdfChunk(i, cardsChunk, pdfPaths);
        }
        System.out.println("Finished creating one sided PDF chunks.");
        System.out.println("Merging one sided PDF chunks...");
        mergeOneSidedPdfChunks(pdfPaths);
    }

    private static void mergeOneSidedPdfChunks(ArrayList<String> pdfPaths) throws IOException {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDestinationFileName(Config.getInstance().getPdfOneSidedLocalPath());

        PDDocument document = new PDDocument();
        document.save(Config.getInstance().getPdfOneSidedLocalPath());

        for (String pdfPath : pdfPaths) {
            PDDocument documentToAdd = Loader.loadPDF(new File(pdfPath));
            pdfMerger.appendDocument(document, documentToAdd);
            documentToAdd.close();
            document.save(Config.getInstance().getPdfOneSidedLocalPath());
        }
        document.close();

        System.out.println("Succesfully Merged one sided PDF Chunks.");

        System.out.println("Deleting one sided PDF Chunks...");
        for (String pdfPath : pdfPaths) {
            System.out.println("Deleting " + pdfPath);
            Files.deleteIfExists(Path.of(pdfPath));
        }
        System.out.println("Succesfully Deleted one sided PDF Chunks.");
    }


    private static void createOneSidedCardsPdfChunk(int Index, ArrayList<Card> cardsChunk, ArrayList <String> pdfPaths) throws IOException {
        System.out.println("Creating PDF chunk...");
        CardsManager cardsManager = CardsManager.getInstance();
        PDDocument document = new PDDocument();
        Card firstCard = cardsManager.getCards().get(0);
        System.out.println("Using first image " + firstCard.getLocalFrontImageFilePath() + " for PDF Dimensions");
        PDImageXObject pdImageFirst = PDImageXObject.createFromFile(firstCard.getLocalFrontImageFilePath(), document);
        float imageWidth = pdImageFirst.getWidth();
        float imageHeight = pdImageFirst.getHeight();

        ArrayList<Card> oneSidedCards = cardsChunk;

        if(oneSidedCards.size() == 0) {
            System.out.println("No one sided cards to add to PDF Chunk. Skipping creation of one sided PDF.");
            document.close();
            return;
        }

        for (int i = 0; i < oneSidedCards.size(); i++) {
            Card currentCard = oneSidedCards.get(i);
            addImagePageToPdf(document, currentCard.getLocalFrontImageFilePath(), imageWidth, imageHeight);
        }

        String pdfPath = Config.getInstance().getPdfOneSidedLocalPath().replace(".pdf", "_" + Index + ".pdf");
        pdfPaths.add(pdfPath);
        document.save(pdfPath);
        document.close();
        System.out.println("Succesfully Created one sided PDF Chunk.");
    }

    private static void createOneSidedCardsPdf() throws IOException {
        System.out.println("Creating one sided PDF...");
        CardsManager cardsManager = CardsManager.getInstance();
        PDDocument document = new PDDocument();
        Card firstCard = cardsManager.getCards().get(0);
        System.out.println("Using first image " + firstCard.getLocalFrontImageFilePath() + " for PDF Dimensions");
        PDImageXObject pdImageFirst = PDImageXObject.createFromFile(firstCard.getLocalFrontImageFilePath(), document);
        float imageWidth = pdImageFirst.getWidth();
        float imageHeight = pdImageFirst.getHeight();

        ArrayList<Card> oneSidedCards = cardsManager.getOneSidedCards();

        if(oneSidedCards.size() == 0) {
            System.out.println("No one sided cards to add to PDF. Skipping creation of one sided PDF.");
            document.close();
            return;
        }

        for (int i = 0; i < oneSidedCards.size(); i++) {
            Card currentCard = oneSidedCards.get(i);
            addImagePageToPdf(document, currentCard.getLocalFrontImageFilePath(), imageWidth, imageHeight);
        }

        document.save(Config.getInstance().getPdfOneSidedLocalPath());
        document.close();
        System.out.println("Succesfully Created one sided PDF.");
    }

    private static void createTwoSidedCardsPdf() throws IOException {
        System.out.println("Creating two sided PDF...");
        CardsManager cardsManager = CardsManager.getInstance();

        ArrayList<Card> twoSidedCards = cardsManager.getTwoSidedCards();

        if(twoSidedCards.size() == 0) {
            System.out.println("No two sided cards to add to PDF. Skipping creation of two sided PDF.");
            return;
        }

        PDDocument document = new PDDocument();
        Card firstCard = cardsManager.getCards().get(0);
        System.out.println("Using first image " + firstCard.getLocalFrontImageFilePath() + " for PDF Dimensions");
        PDImageXObject pdImageFirst = PDImageXObject.createFromFile(firstCard.getLocalFrontImageFilePath(), document);
        float imageWidth = pdImageFirst.getWidth();
        float imageHeight = pdImageFirst.getHeight();

        for (int i = 0; i < twoSidedCards.size(); i++) {
            Card currentCard = twoSidedCards.get(i);
            addImagePageToPdf(document, currentCard.getLocalFrontImageFilePath(), imageWidth, imageHeight);
            addImagePageToPdf(document, currentCard.getLocalBackImageFilePath(), imageWidth, imageHeight);
        }

        document.save(Config.getInstance().getPdfTwoSidedLocalPath());
        document.close();
        System.out.println("Succesfully Created two sided PDF.");
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
}
