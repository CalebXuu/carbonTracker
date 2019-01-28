package richardshen.carbon_tracker;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for CSV parsing methods
 */
public class CSVParser {
    /**
     * Gets all the values from a given row in a CSV file.
     * @param inputCSV The file to be parsed.
     * @param row The row (0 indexed) to be extracted from the given CSV.
     * @param removeDuplicates Option to have only unique values read from the file.
     */
    public static ArrayList<String> getRowFromCSV(File inputCSV, int row, boolean removeDuplicates) {
        ArrayList<String> readValues = new ArrayList<>(10);

        try {
            Scanner inputFile = new Scanner(inputCSV);

            String line = inputFile.nextLine();
            while(inputFile.hasNextLine()) {
                line = inputFile.nextLine();
                String[] vals = line.split(",");

                if(vals[row].isEmpty()) {
                    continue;
                }

                if(removeDuplicates) {
                    if(!(readValues.contains(vals[row]))) {
                        readValues.add(vals[row]);
                    }
                } else {
                    readValues.add(vals[row]);
                }
            }
            return readValues;

        } catch(IOException e) {
            Log.i("File not found!", "");
        } catch(ArrayIndexOutOfBoundsException e) {
            Log.i("Row number invalid!","");
        }

        return readValues;
    }


    //This method was not used anywhere in the android code.
    //However, it was used to create the files of the individual brands in raw directory.
    /**
     * Method used to break the input CSV into CSVs for specific brands
     * @param inputCSV The input CSV file.
     */
    public static void breakFileIntoBrands(File inputCSV, String outPutDir) {
        ArrayList<String> brandsFound = new ArrayList<>(40);

        try {
            Scanner inputFile = new Scanner(inputCSV);

            while(inputFile.hasNextLine()) {
                String line = inputFile.nextLine();

                String[] fields = line.split(",");

                if(!(brandsFound.contains(fields[1]))) {
                    String fileName = fields[1].replaceAll("[^a-zA-Z0-9]", "");
                    fileName = fileName.toLowerCase();
                    File newFile = new File(outPutDir + fileName + ".csv");

                    FileWriter writer = new FileWriter(newFile, true);
                    PrintWriter printer = new PrintWriter(writer);
                    printer.println(line);

                    printer.close();
                } else {
                    String fileName = fields[1].replaceAll("[^a-zA-Z0-9]", "");
                    fileName = fileName.toLowerCase();

                    File newFile = new File(outPutDir + fileName + ".csv");

                    FileWriter writer = new FileWriter(newFile, true);
                    PrintWriter printer = new PrintWriter(writer);
                    printer.println(line);

                    printer.close();
                }
                brandsFound.add(fields[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
