package org.example;

import java.io.IOException;
import java.util.List;

import com.opencsv.exceptions.CsvException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, CsvException {
        initSetup(args);
        downLoadCsvFile();
        createBasicCardsFromCsv();
        downloadJsonFiles();
        initFullCardsFromJsonFiles();
        downloadImages();
        createPdfs();
        keepConsoleOpenIfNeeded(args);
    }

    private static void initSetup(String[] args) throws IOException, InterruptedException {
        Config.getInstance().useDefaultTmpFolder();
        Util.createProjectFolders();
        if(args.length > 0){
            Config.getInstance().setArgs(args);
        } else {
            Wizard.run(args);
        }
    }

    private static void downLoadCsvFile() throws InterruptedException, IOException, CsvException {
        FileDownloader.downloadFile(Config.getInstance().getCsvUrl(), Config.getInstance().getCsvLocalFilePath());
        // check if downloaded file is csv
        List<String[]> csvData = CsvUtils.readCsvFile(Config.getInstance().getCsvLocalFilePath());
        if (csvData == null || csvData.size() == 0){
            throw new IOException("Downloaded file is not csv or is empty.");
        }
    }

    private static void createBasicCardsFromCsv() throws IOException, CsvException {
        System.out.println("Start creating cards from csv...");
        CardsManager.getInstance().createCardsFromCsvData();
        System.out.println("Cards created: " + CardsManager.getInstance().getCards().size());
    }

    private static void downloadJsonFiles() throws InterruptedException {
        System.out.println("Start downloading json files...");
        CardsManager.getInstance().downloadCardJsonFiles();
        System.out.println("Downloading json files finished.");
    }

    private static void initFullCardsFromJsonFiles() throws IOException {
        System.out.println("Initialize full cards from json files.");
        CardsManager.getInstance().processCards();
    }

    private static void downloadImages() throws IOException, InterruptedException {
        System.out.println("Start downloading images...");
        CardsManager.getInstance().downloadCardImages();
        System.out.println("Downloading images finished.");
    }

    private static void createPdfs() throws IOException, CsvException {
        PdfCreator.createPdfs();
    }

    private static void keepConsoleOpenIfNeeded(String[] args) throws IOException {
        if (args.length > 0){
            return;
        }
        System.out.println("\n" + "Press Enter to exit.");
        System.in.read();
    }
}