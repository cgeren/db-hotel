import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * Program to generate data for fields within the hotel DB
 * and save the data to csv files which will be loaded into
 * the DB with SQL Loader
 *
 * https://www.baeldung.com/java-csv
 */

public class Generate {
    public final int NUM_HOTELS = 19;

    public static void main(String[] args) {
        String amenitiesCSV = "amenities.csv";
        Path amenitiesPath = Paths.get(amenitiesCSV);
        ArrayList<ArrayList<String>> amenitiesList = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempList = new ArrayList<String>();
        generateAmenities(0, amenitiesList, tempList);

        try {
            writeToCSV(amenitiesList, amenitiesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * https://www.java2novice.com/java-collections-and-util/arraylist/list-to-csv/
     * @param list
     * @return
     */
    public static String getListAsCsvString(ArrayList<String> list){
        StringBuilder sb = new StringBuilder();
        for(String str:list){
            if(sb.length() != 0){
                sb.append(",");
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static void writeToCSV(ArrayList<ArrayList<String>> lines, Path path) throws IOException {
        File csvOutputFile = new File(path.toString());

        System.out.println(path.toString());

        FileWriter myWriter = new FileWriter(csvOutputFile);

        for (int i = 0; i < lines.size(); i++) {
            String convertedToCSV = getListAsCsvString(lines.get(i));
            myWriter.write(convertedToCSV);

            if (i != (lines.size() - 1)) {
                myWriter.write(System.getProperty("line.separator"));
            }
        }

        myWriter.close();
    }

    public static void generateAmenities(int i, ArrayList<ArrayList<String>> amenities, ArrayList<String> list) {
        int maxAmenities = 4;

        if (i == maxAmenities) {
            amenities.add(list);
            list.removeAll();
            return;
        }

        list.add("N");
        generateAmenities(i + 1, amenities, list);

        list.remove(list.size() - 1);

        list.add("Y");
        generateAmenities(i + 1, amenities, list);
    }

    public static void prependID(ArrayList<ArrayList<String>> toPrepend) {
        for (int i = 1; i < toPrepend.size() + 1; i++) {
            toPrepend.get(i).add(0, Integer.toString(i));
        }
    }
}

