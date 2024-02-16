package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CsvUtils {

    public static List<String[]> readCsvFile(String fileName) throws IOException, CsvException {
        String csvFilePath = fileName;
        List<String[]> records = null;
        
        CSVReader reader = new CSVReader(new FileReader(csvFilePath));
        reader.skip(1);
        
        records = reader.readAll();
        reader.close();
        
        return records;
    }
}