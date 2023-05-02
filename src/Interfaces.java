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

    public static long testValidLong(long min, long max, Scanner scanner) {
        while (!scanner.hasNextLong()) {
            System.out.println("That is not an integer. Please try again.");
            scanner.nextLine();
        }
        long input = scanner.nextLong();
        if (input <= max && input >= min) {
            return input;
        } else {
            return 0;
        }
    }

    /**
     * What I get for not making all of my primary keys auto increments and being too lazy to change all of my tables.
     * I am certain this oversight will create concurrency issues.
     * @param table_name
     * @param id_name
     * @param connection
     * @return
     */
    public static String fetchLatestID(int table, Connection connection) throws SQLException {
        String latestID = "";
        String preparedLatestID = "";

        /*
        Ya I know it's janky
         */
        switch (table) {
            case 1:
                preparedLatestID = "SELECT MAX(r_id) as MAXID FROM reservations";
                break;
            case 2:
                preparedLatestID = "SELECT MAX(c_id) as MAXID FROM customer";
                break;
            default:
                preparedLatestID = "SELECT MAX(t_id) as MAXID FROM transactions";
                break;
        }

        PreparedStatement latestIDQuery = connection.prepareStatement(preparedLatestID);

        ResultSet latestIdResult = latestIDQuery.executeQuery();

        if (!latestIdResult.next()) {
            System.out.println("Something went wrong.");
            return latestID;
        } else {
            do {
                latestID = latestIdResult.getString("MAXID");
            } while (latestIdResult.next());
        }

        latestIDQuery.close();
        return latestID;
    }

    public static LocalDate getValidDate(Scanner scanner) throws DateTimeException {
        ZoneId zone = ZoneId.of("America/Montreal");
        ZonedDateTime currentTime = ZonedDateTime.now(zone);

        LocalDate inputTime;

        System.out.println("Today's date: " + currentTime.toString());

        int inputYear = 0;
        boolean isValid = false;
        while (!isValid) {
            System.out.println("Please enter the year (YYYY):");
            inputYear = testValidInteger(currentTime.getYear(), 2099, scanner);
            if (inputYear == 0) {
                System.out.println("Invalid year. Enter only integers, and ensure year is not in the past.");
            } else {
                isValid = true;
            }
        }

        int inputMonth = 0;
        isValid = false;
        while (!isValid) {
            System.out.println("Please enter the month: (M/MM)");
            if (inputYear == currentTime.getYear()) {
                inputMonth = testValidInteger(currentTime.getMonthValue(), 12, scanner);
            } else {
                inputMonth = testValidInteger(1, 12, scanner);
            }
            if (inputMonth == 0) {
                System.out.println("Invalid month. Enter only integers, and ensure your month is not in the past.");
            } else {
                isValid = true;
            }
        }

        inputTime = LocalDate.of(inputYear, inputMonth, 1); //initialize localdate object to retrieve length of month for day input

        int inputDay = 0;
        isValid = false;
        while(!isValid) {
            System.out.println("Please enter the day: (D/DD)");
            if (inputYear == currentTime.getYear() && inputMonth == currentTime.getMonthValue()) {
                inputDay = testValidInteger(currentTime.getDayOfMonth(), inputTime.lengthOfMonth(), scanner);
            } else {
                inputDay = testValidInteger(1, inputTime.lengthOfMonth(), scanner);
            }
            if (inputDay == 0) {
                System.out.println("Invalid day. Enter only integers, and ensure your day is appropriate for the month you have chosen.");
            } else {
                isValid = true;
            }
        }

        return inputTime.withDayOfMonth(inputDay);
    }

    public static void createCustomer(Connection connection, String customerID, String first_name, String last_name,
                                      long phone, int points, long cc_num) throws SQLException {
        CallableStatement createCustomer = connection.prepareCall("{call createCustomer(?, ?, ?, ?, ?, ?)}");
        createCustomer.setString(1, customerID);
        createCustomer.setString(2, first_name);
        createCustomer.setString(3, last_name);
        createCustomer.setLong(4, phone);
        createCustomer.setInt(5, points);
        createCustomer.setLong(6, cc_num);

        createCustomer.execute();
    }

    public static float createReservation(Connection connection, String customerID, String hotelID,
                                         String reservationID, String room_type,
                                         LocalDate start_date, LocalDate end_date) throws SQLException {
        CallableStatement createReservation = connection.prepareCall("{call createReservation(?, ?, ?, ?, ?, ?)}");
        createReservation.setString(1, reservationID);
        createReservation.setString(2, customerID);
        createReservation.setString(3, hotelID);
        createReservation.setDate(4, java.sql.Date.valueOf(start_date.toString()));
        createReservation.setDate(5, java.sql.Date.valueOf(end_date.toString()));
        createReservation.setString(6, room_type);

        createReservation.execute();

        String fetchCostString = "SELECT cost FROM reservations WHERE r_id = ?";
        PreparedStatement fetchCostQuery = connection.prepareStatement(fetchCostString);
        fetchCostQuery.setString(1, reservationID);

        ResultSet costResult = fetchCostQuery.executeQuery();

        float cost = 0;

        if (!costResult.next()) {
            System.out.println("An error occurred. Your reservation is corrupted.");
        } else {
            do {
                cost = costResult.getFloat("cost");
            } while (costResult.next());
        }
        return cost;
    }

    public static void validateUserCreateRes(Scanner scanner, Connection connection, String hotel_id, String room_type, LocalDate start_date, LocalDate end_date) throws SQLException {
        String customerId = "";
        boolean isValidUser = false;
        long maxPhone = 9999999999L;

        while (!isValidUser) {
            System.out.println("Are you a new or returning customer?\n1. New\n2. Returning");
            int customerType = testValidInteger(1, 2, scanner);
            if (customerType == 1) {
                scanner.nextLine();
                System.out.println("Welcome to the Hotel California chain of hotels! To begin, please enter your first name: ");
                String firstNameNewInput = scanner.nextLine();
                System.out.println("Last name: ");
                String lastNameNewInput = scanner.nextLine();
                System.out.println("Phone number: ");
                long phoneInputNew = testValidLong(1000000000, maxPhone, scanner);

                String checkPhoneExists = "SELECT COUNT(phone) as NUMPHONES\n" +
                        "FROM customer\n" +
                        "WHERE phone = ?";
                PreparedStatement phoneExistsQuery = connection.prepareStatement(checkPhoneExists);
                phoneExistsQuery.setLong(1, phoneInputNew);
                ResultSet phoneExistsResult = phoneExistsQuery.executeQuery();
                int numPhonesSame = 0;

                if (!phoneExistsResult.next()) {
                    System.out.println("An error occurred fetching data from the server.");
                } else {
                    do {
                        numPhonesSame = phoneExistsResult.getInt("NUMPHONES");
                    } while (phoneExistsResult.next());
                }

                if (numPhonesSame > 0) {
                    System.out.println("A user with that phone number already exists.");
                } else {
                    isValidUser = true;
                    System.out.println("Would you like to sign up for our frequent stayer program? Y/N");
                    String frequentInput = "";
                    int points = 0;
                    boolean isValidResult = false;
                    while (!isValidResult) {
                        scanner.nextLine();
                        frequentInput = scanner.nextLine();
                        if (frequentInput.equals("Y")) {
                            System.out.println("Wonderful! You have a balance of 0 points. You gain points when you pay for" +
                                    "\nreservations, our current point rate is 1 point for every 10 dollars you spend!");
                            isValidResult = true;
                        } else if (frequentInput.equals("N")) {
                            System.out.println("We hope you will change your mind in the future :) It is free money.");
                            isValidResult = true;
                            points = -1;
                        } else {
                            System.out.println("Please only enter, 'Y' or 'N'. Try again.");
                        }
                    }

                    System.out.println("\nPlease enter your 16-digit credit card number.");

                    long cc_number = testValidLong(100000000000000L, 9999999999999999L, scanner);

                    String customerNewIDString = null;
                    try {
                        String latestCustomerID = fetchLatestID(2, connection);
                        Long newCustomerID = Long.parseLong(latestCustomerID);
                        newCustomerID += 1;
                        customerNewIDString = newCustomerID.toString();
                    } catch (Exception e) {
                        System.out.println("Something went wrong while fetching from the Hotel California servers.");
                        return;
                    }

                    try {
                        createCustomer(connection, customerNewIDString, firstNameNewInput, lastNameNewInput, phoneInputNew, points, cc_number);
                    } catch (Exception e) {
                        System.out.println("Something went wrong while initializing your new customer profile. You may have entered duplicate input.\n" +
                                "We apologize for the inconvenience.");
                        return;
                    }
                    System.out.println("New customer profile created successfully! Welcome to a lovely place, " + firstNameNewInput + ".");
                    System.out.println("Creating your new reservation...");

                    String latestResID = null;
                    String newResIDString = null;
                    try {
                        latestResID = fetchLatestID(1, connection);

                        Long newResID = Long.parseLong(latestResID);
                        newResID += 1;
                        newResIDString = newResID.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("An error occurred while accessing the Hotel California servers. We are sorry " +
                                "for any inconvenience.");
                        return;
                    }
                    float costRes = 0;
                    try {
                        costRes = createReservation(connection, customerNewIDString, hotel_id, newResIDString, room_type, start_date, end_date);
                    } catch (Exception e) {
                        System.out.println("There was an error while attempting to create your reservation. We are sorry" +
                                "for any inconvenience.");
                        return;
                    }
                    System.out.println("Success! Your reservation was created. You will be charged " +
                            costRes + " at the time of check in. Or, you may opt to pay with points. Have a nice day.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Please enter your last name: ");
                String lastNameInput = scanner.nextLine();

                System.out.println("Please enter your phone number: ");
                long phoneNumberInput = testValidLong(1000000000, maxPhone, scanner);

                PreparedStatement customerQuery = null;
                String preparedCustomer = null;
                ResultSet customerResult = null;

                try {
                    preparedCustomer = "SELECT c_id, first_name FROM customer WHERE last_name = ? and phone = ?";
                    customerQuery = connection.prepareStatement(preparedCustomer);

                    customerQuery.setString(1, lastNameInput);
                    customerQuery.setLong(2, phoneNumberInput);

                    customerResult = customerQuery.executeQuery();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!customerResult.next()) {
                    System.out.println("A customer with that last name and phone number does not exist.");
                } else {
                    System.out.println("Success!");
                    do {
                        System.out.println("Welcome, " + customerResult.getString("first_name"));
                        customerId = customerResult.getString("c_id");
                    } while (customerResult.next());
                    isValidUser = true;
                    System.out.println("Creating a reservation at your selected hotel...");

                    String latestID = null;
                    String newIDString = null;
                    try {
                        latestID = fetchLatestID(1, connection);

                        Long newID = Long.parseLong(latestID);
                        newID += 1;
                        newIDString = newID.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("An error occurred while accessing the Hotel California servers. We are sorry " +
                                "for any inconvenience.");
                        return;
                    }
                    float costRes = 0;
                    try {
                        costRes = createReservation(connection, customerId, hotel_id, newIDString, room_type, start_date, end_date);
                    } catch (Exception e) {
                        System.out.println("There was an error while attempting to create your reservation. We are sorry" +
                                "for any inconvenience.");
                        return;
                    }
                    System.out.println("Success! Your reservation was created. You will be charged " +
                            costRes + " at the time of check in. Or, you may opt to pay with points. Have a nice day.");
                }
                customerQuery.close();
            }
        }
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

                            System.out.println("Please enter the number of nights you would like to stay:");

                            int numberNights = 0;
                            boolean isValidNights = false;

                            while (!isValidNights) {
                                numberNights = testValidInteger(1, 30, scanner);
                                if (numberNights > 0) {
                                    isValidNights = true;
                                } else {
                                    System.out.println("Invalid input, remember you cannot stay at any Hotel California locations for more than " +
                                            "30 days with a single reservation.");
                                }
                            }

                            System.out.println("Your check-in date is: " + inputDate.toString() + " and you will be staying with us for " +
                                    numberNights + " nights.");
                            System.out.println("Your check-out date is then " + inputDate.plusDays(numberNights).toString() + ". Continue? (Y/N)");

                            String continueInput = "";
                            boolean isValidContinue = false;
                            boolean isMainMenu = false;
                            while (!isValidContinue) {
                                scanner.nextLine();
                                continueInput = scanner.nextLine();
                                if (continueInput.equals("Y")) {
                                    isValidContinue = true;
                                } else if (continueInput.equals("N")) {
                                    System.out.println("Exiting to main menu...");
                                    isValidContinue = true;
                                    isMainMenu = true;
                                } else {
                                    System.out.println("Please only enter, 'Y' or 'N'. Try again.");
                                }
                            }

                            if (!isMainMenu) {
                                PreparedStatement preparedRoomTypesExistsQuery = null;
                                ResultSet roomTypesResult = null;
                                try {
                                    String preparedRoomTypesStatement = "SELECT DISTINCT\n" +
                                                "room_type,\n" +
                                                "capacity,\n" +
                                                "num_beds,\n" +
                                                "has_kitchen,\n" +
                                                "num_rooms,\n" +
                                                "dol_per,\n" +
                                                "pts_per\n" +
                                                "FROM room_types\n" +
                                                "WHERE\n" +
                                                "h_id = ?";
                                    preparedRoomTypesExistsQuery = connection.prepareStatement(preparedRoomTypesStatement);

                                    preparedRoomTypesExistsQuery.setString(1, selectedHotelId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                int roomSelection = 0;
                                String roomSelected = "";

                                boolean isValidRoom = false;
                                while (!isValidRoom) {
                                    roomTypesResult = preparedRoomTypesExistsQuery.executeQuery();
                                    System.out.println("\nYour Hotel California location is currently offering the following rooms: ");
                                    try {
                                        if (!roomTypesResult.next()) {
                                            System.out.println("Your location is offering no rooms at this time.");
                                        } else {
                                            System.out.println(String.format("%-15s%-10s%-10s%-10s%-10s%-10s%-10s", "Room Type", "Capacity", "Beds", "Kitchen",
                                                    "Rooms", "Dol/Night", "Pts/Night"));
                                            System.out.println("---------------------------------------------------------------------------------------------");
                                            do {
                                                System.out.println(String.format("%-15s%-10d%-10d%-10d%-10d%-10d%-10d", roomTypesResult.getString("room_type"),
                                                        roomTypesResult.getInt("capacity"), roomTypesResult.getInt("num_beds"),  roomTypesResult.getInt("has_kitchen"),
                                                        roomTypesResult.getInt("num_rooms"), roomTypesResult.getInt("dol_per"), roomTypesResult.getInt("pts_per")));
                                            } while (roomTypesResult.next());
                                        }
                                    } catch (Exception e) {
                                        System.out.println("An error has occurred.");
                                    }
                                    System.out.println("To reserve one, please enter the digit corresponding to the proper room type:\n" +
                                            "1. Single\n2. Double\n3. Suite\n4. California\n5. Exit");
                                    roomSelection = testValidInteger(1, 5, scanner);
                                    if (roomSelection > 0) {
                                        switch (roomSelection) {
                                            case 1:
                                                roomSelected = "Single";
                                                break;
                                            case 2:
                                                roomSelected = "Double";
                                                break;
                                            case 3:
                                                roomSelected = "Suite";
                                                break;
                                            case 4:
                                                roomSelected = "California";
                                                break;
                                            case 5:
                                                isMainMenu = true;
                                                isValidRoom = true;
                                                break;
                                            default:
                                                roomSelected = "A Secret Fifth Thing";
                                                break;
                                        }
                                        isValidRoom = true;
                                    } else {
                                        System.out.println("Invalid input. Please try again.");
                                    }

                                    if (roomSelection != 5) {
                                        ResultSet roomResult;
                                        System.out.println("You have selected a " + roomSelected + ".");
                                        try {
                                            String preparedRoomAvailabile = "SELECT TOTAL_AVAILABLE( ? , ? , ? , ? ) AS result FROM dual";
                                            PreparedStatement roomAvailableQuery = connection.prepareStatement(preparedRoomAvailabile);
                                            roomAvailableQuery.setDate(1, java.sql.Date.valueOf(inputDate.toString()));
                                            roomAvailableQuery.setInt(2, numberNights);
                                            roomAvailableQuery.setString(3, roomSelected);
                                            roomAvailableQuery.setString(4, selectedHotelId);

                                            roomResult = roomAvailableQuery.executeQuery();

                                            if (!roomResult.next()) {
                                                System.out.println("An error occurred fetching available rooms.");
                                            }
                                            do {
                                                int roomResultInteger = Integer.parseInt(roomResult.getString("result"));
                                                if (roomResultInteger == 0) {
                                                    System.out.println("We are sorry. There are no available rooms of that type in your hotel. Returning to room type selection...");
                                                    isValidRoom = false;
                                                } else {
                                                    System.out.println("There is an available room of that type in this hotel. Continuing to user information...");
                                                }
                                            } while (roomResult.next());
                                            roomAvailableQuery.close();
                                        } catch (Exception e) {
                                            System.out.println("An unexpected error has occurred while fetching rooms, we are sorry for the inconvenience.");
                                        }
                                    } else {
                                        System.out.println("Returning to customer menu...");
                                    }
                                    if (isValidRoom) {
                                        validateUserCreateRes(scanner, connection, selectedHotelId, roomSelected, inputDate, inputDate.plusDays(numberNights));
                                    }
                                }
                                preparedRoomTypesExistsQuery.close();
                            }
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
        userCityQuery.close();

        return;
    }
}