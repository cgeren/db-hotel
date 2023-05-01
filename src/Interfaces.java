import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.*;
import java.util.Date;

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
                        System.out.println("Welcome to The Hotel California! Please select an option (1-3):");
                        try {
                            customerInterface(scanner, connection);
                        } catch (SQLException sqlException) {
                            System.out.println("An unexpected error has occurred. Returning to main menu...");
                        }
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

    /**
     * Function to test if a users input is a valid integer within a range.
     * @param min Minimum input range
     * @param max Maximum input range
     * @param scanner Scanner instance used to retrieve input
     * @return Returns user input when input is valid, returns 0 when input is invalid
     */
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

    public static LocalDate getValidDate(Scanner scanner) throws DateTimeException {
        ZoneId zone = ZoneId.of("America/Montreal");
        ZonedDateTime currentTime = ZonedDateTime.now(zone);

        LocalDate inputTime;

        System.out.println("Please enter the year (YYYY):");
        int inputYear = testValidInteger(currentTime.getYear(), 2099, scanner);

        System.out.println("Please enter the month: (M/MM)");
        int inputMonth = testValidInteger(1, 12, scanner);

        inputTime = LocalDate.of(inputYear, inputMonth, 1); //initialize localdate object to retrieve length of month for day input

        System.out.println("Please enter the day: (D/DD)");
        int inputDay = testValidInteger(1, inputTime.lengthOfMonth(), scanner);

        return inputTime.withDayOfMonth(inputDay);
    }

    /**
     * Customer Interface function, enables the creation of reservations, viewing of all cities with hotels,
     * login and creation of new user as well as the option to enroll in the Hotel California frequent guest
     * program.
     * @param scanner Scanner instance to retrieve user input.
     * @param connection Connection instance to the database for queries and updates.
     */
    public static void customerInterface(Scanner scanner, Connection connection) throws SQLException {
        String cityQuery = "SELECT city, state FROM hotel";
        Statement cityStatement = connection.createStatement();

        String preparedCityStatement = "SELECT * FROM hotel WHERE city = ?";
        PreparedStatement userCityQuery = connection.prepareStatement(preparedCityStatement);

        int selection = -1;
        boolean isValid = false, isExit = false;
        scanner.nextLine();
        while (!isExit) {
            isValid = false;
            while (!isValid) {
                ResultSet cityResult = cityStatement.executeQuery(cityQuery);
                System.out.println("\t1. Make a Reservation\n" +
                        "\t2. View List of Locations\n" +
                        "\t3. Back");

                selection = testValidInteger(1, 3, scanner);

                if (selection > 0) {
                    isValid = true;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }

                switch (selection) {
                    case 1: //make a reservation case
                        scanner.nextLine();
                        System.out.println("Please enter a city you would like to stay in:");
                        String cityInput = scanner.nextLine();

                        userCityQuery.setString(1, cityInput);
                        ResultSet hotelInCity = userCityQuery.executeQuery();

                        String selectedHotelId = "";

                        if (!hotelInCity.next()) {
                            System.out.println("We currently do not have any Hotel California locations in " + cityInput +
                                    ". You may view a list of available locations by pressing '2'.");
                        } else {
                            int counter = 1;
                            ArrayList<String> hotelIds = new ArrayList<String>();
                            System.out.println("We currently have the following locations in " + cityInput + ":");
                            System.out.println("Please note they may not all be in the same state.\n");
                            do {
                                System.out.println(counter + ".\t" + hotelInCity.getString("unit_number") +
                                        " " + hotelInCity.getString("street_name") + " " + hotelInCity.getString("city") +
                                        ", " + hotelInCity.getString("state") + " " + hotelInCity.getString("zip"));
                                hotelIds.add(hotelInCity.getString("h_id"));
                                counter++;
                            } while (hotelInCity.next());
                            System.out.println("\nPlease press the number associated with the hotel you would like to choose.");

                            boolean isValidHotel = false;
                            while (!isValidHotel) {
                                int hotelSelection = testValidInteger(1, counter - 1, scanner);

                                if (hotelSelection > 0) {
                                    isValidHotel = true;
                                    selectedHotelId = hotelIds.get(hotelSelection - 1);
                                } else {
                                    System.out.println("Not a valid input. Please try again.");
                                }
                            }
                            System.out.println("Please enter your preferred check-in date.");
                            LocalDate inputDate = LocalDate.now();

                            boolean isValidDate = false;

                            while (!isValidDate) {
                                try {
                                    inputDate = getValidDate(scanner);
                                    isValidDate = true;
                                } catch (DateTimeException d) {
                                    System.out.println("Date input format incorrect. Remember to enter your dates as the prompts direct.");
                                }
                            }

                            System.out.println("Your check-in date is: " + inputDate.toString());

                            System.out.println("Please enter the number of nights you would like to stay:");
                        }

                        break;
                    case 2: //show list of locations, queried through database
                        if (!cityResult.next()) {
                            System.out.println("Hotel California is currently only servicing " +
                                    "extraterrestrial colonies due to the ongoing Chaos Space Marine invasion.");
                        } else {
                            System.out.println("List of cities with Hotel California locations:\n");
                            while (cityResult.next()) {
                                String city = cityResult.getString("city");
                                String state = cityResult.getString("state");
                                System.out.println(city + ", " + state);
                            }
                        }
                        break;
                    case 3: // return to main menu
                        System.out.println("Exiting to main menu...");
                        isExit = true;
                }
            }
        }
        return;
    }
}