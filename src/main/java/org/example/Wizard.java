package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Wizard {

    public static void run(String[] args) throws InterruptedException, IOException {
        String TMP_FOLDER = Config.getInstance().getTmpFolder();

        System.out.println(Util.getAsciiArtMainTitle());
        Thread.sleep(200);

        System.out.println("Welcome to the Cubecobra Printer!\n");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        Thread.sleep(500);
        System.out.println("This program will create a PDF of your cubecobra.com cube.\n");
        Thread.sleep(100);
        System.out.println("It will download all images in highest quality from scryfall.com, process them, and create a PDF from it.");
        System.out.println("This process will use a lot of Disk Space and can take a while.");
        System.out.println("A 540 card cube will use up to 10 GB of disk space, so make sure you have enough disk space available.\n");
        System.out.println("PLEASE NOTE: Use this program at your own risk.");
        System.out.println("If you encounter issues, try deleting the created 'cubecobra-printer-data' folder and restart the process.");
        Thread.sleep(100);

        System.out.printf("This Process will create a new folder in your current directory called '%s'.\n", TMP_FOLDER);
        System.out.println("\nIf you want to continue type 'y' and press Enter. To exit enter 'n' or press Ctrl+C.\n");

        String startInputStr = reader.readLine().trim();

        if (!startInputStr.equals("y")) {
            System.out.println("You did not type y. Aborting Process.");
            Thread.sleep(1000);
            return;
        }

        Thread.sleep(100);
        System.out.println("\nInitializing the cube creation process.\n");
        Thread.sleep(200);

        String cubeUrl = "";

        Thread.sleep(100);
        System.out.println("Please enter the URL of your cube's url:");
        Thread.sleep(100);
        System.out.println("(for example: https://cubecobra.com/cube/overview/09f91955-989e-4abf-a470-b3763ba3b255)");

        while (true) {
            cubeUrl = reader.readLine().trim();

            if (cubeUrl.isEmpty()) {
                Thread.sleep(100);
                System.out.println("No valid input. Please enter a valid input for your cube's url.");
                continue;
            }

            break;
        }

        System.out.println("");
        Thread.sleep(100);
        System.out.println("Your cube will now be downloaded and processed.");
        System.out.println("This process can take a while depending on the size of your cube.");
        System.out.println("Please be patient and do not close the program.");
        System.out.println("");
        Thread.sleep(100);
        System.out.println("If you want to continue type 'y' and press Enter. To exit enter 'n' or press Ctrl+C.\n");

        String continueInputStr = reader.readLine().trim();

        System.out.println("");
        if (!continueInputStr.equals("y")) {
            System.out.println("You did not type y. Aborting Process.");
            Thread.sleep(1000);
            return;
        }

        Thread.sleep(100);
        System.out.println("Start Downloading cube data...\n");
        Thread.sleep(100);

        // set values to config
        Config.getInstance().setCsvUrlRaw(cubeUrl);
    }
}
