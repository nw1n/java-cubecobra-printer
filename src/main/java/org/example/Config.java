package org.example;

public class Config {
    // Volatile keyword ensures visibility across threads
    private static volatile Config instance;
    private String tmpFolder;
    private String csvUrl;

    // Private constructor to prevent instantiation
    private Config() {
        this.useTestTmpFolder();
        //this.csvUrl = "https://cubecobra.com/cube/download/csv/tol_up360";
        this.csvUrl = "https://cubecobra.com/cube/download/csv/398e6e50-d585-43e0-ad97-6e4609da5e76";
    }

    public void setArgs(String[] args) {
        if(args.length > 0) {
            this.setCsvUrlRaw(args[0]);
        }
    }

    public String setCsvUrlRaw(String csvUrl) {
        return this.csvUrl = csvUrl;
    }

    public String getCsvUrlRaw() {
        return this.csvUrl;
    }

    public String getCsvUrl() {
        return Util.getCubecobraCsvUrl(this.csvUrl);
    }

    public String getCubeId() {
        return Util.getCubeIdFromUrl(this.csvUrl);
    }

    public String getCsvLocalFilePath() {
        // split the url by "/" and get the last element
        String fileName = this.csvUrl.split("/")[this.csvUrl.split("/").length - 1] + ".csv";
        return this.getCsvFolder() + fileName;
    }

    public Config useDefaultTmpFolder() {
        this.tmpFolder = "./tmp/";
        return this;
    }

    public Config useTestTmpFolder() {
        this.tmpFolder = "./tmp-test/";
        return this;
    }

    public String getTmpFolder() {
        return this.tmpFolder;
    }

    public String getCardJsonFolder() {
        return this.tmpFolder + "card-json/";
    }

    public String getCardImgFolder() {
        return this.tmpFolder + "img/";
    }

    public String getPdfFolder() {
        return this.tmpFolder + "pdf/";
    }

    public String getCsvFolder() {
        return this.tmpFolder + "csv/";
    }

    public String getPdfOneSidedLocalPath() {
        return this.getPdfFolder() + this.getCubeId() + "-one-sided.pdf";
    }

    public String getPdfTwoSidedLocalPath() {
        return this.getPdfFolder() + this.getCubeId() + "-two-sided.pdf";
    }

    // Lazy initialization with double-checked locking
    public static Config getInstance() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }
}
