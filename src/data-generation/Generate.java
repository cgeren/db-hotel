import java.io.*;

/**
 * Program to generate data for fields within the hotel DB
 * and save the data to csv files which will be loaded into
 * the DB with SQL Loader
 *
 * https://www.baeldung.com/java-csv
 */

public class Generate {
    public static void main(String[] args) {
        
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void writeToCSV(List<String[]> lines, Path path) {

    }
}

