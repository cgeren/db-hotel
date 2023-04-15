import java.util.Scanner;
import java.io.*;
import java.sql.*;

public class Interfaces {
    public static void main(String[] args)
            throws SQLException, IOException, java.lang.ClassNotFoundException {
        System.out.println("Login to the Hotel California Database:");
        Scanner scanner = new Scanner(System.in);
        System.out.println("userid: ");
        String username = scanner.nextLine();
        System.out.println("password: ");
        String password = scanner.nextLine();

        try (
                Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", username, password);
        ) {
            System.out.println("Connection successfully established.\n");

            boolean isValid = false, isMainExit = false;
            int isValidInteger = -1;
            while (!isMainExit) {
                isValid = false;
                while (!isValid) {
                    System.out.println("Please Select an Option (1-5):");
                    System.out.println("\t1. Customer\n" +
                            "\t2. Front-desk\n" +
                            "\t3. Housekeeping\n" +
                            "\t4. Management\n" +
                            "\t5. Exit\n");
                    isValidInteger = testValidInteger(1, 5, scanner);
                    if (isValidInteger > 0) {
                        isValid = true;
                        if (isValidInteger == 5) {
                            isMainExit = true;
                        }
                    } else {
                        System.out.println("Not a valid input option. Try again.\n");
                    }
                }
                switch (isValidInteger) {
                    case (1):
                        System.out.println("Welcome to The Hotel California!");
                        break;
                    case (2):
                        System.out.println("Front-Desk Interface");
                        break;
                    case (3):
                        System.out.println("Housekeeping Interface");
                        break;
                    case (4):
                        System.out.println("King of Da Highway Interface");
                        break;
                    case (5):
                        System.out.println("Exiting...");
                        break;
                }
            }
            connection.close();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.toString());
            System.out.println("Invalid login to the Hotel California Database. Shutting down ...");
        }
        scanner.close();
    }

    public static int testValidInteger(int min, int max, Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("That is not an integer. Please try again.");
            scanner.nextLine();
        }
        int input = scanner.nextInt();
        if (input <= max && input >= min) {
            return input;
        } else {
            return 0;
        }
    }
}