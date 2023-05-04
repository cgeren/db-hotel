# db-hotel
GitHub containing the database for the luxurious Hotel California chain of hotels.
----------------------------------------------------------------------------------------


                                                            /T /I
                                                            / |/ | .-~/
                                                        T\ Y  I  |/  /  _
                                       /T               | \I  |  I  Y.-~/
                                      I l   /I       T\ |  |  l  |  T  /
                                   T\ |  \ Y l  /T   | \I  l   \ `  l Y
                               __  | \l   \l  \I l __l  l   \   `  _. |
                               \ ~-l  `\   `\  \  \\ ~\  \   `. .-~   |
                                \   ~-. "-.  `  \  ^._ ^. "-.  /  \   |           
                              .--~-._  ~-  `  _  ~-_.-"-." ._ /._ ." ./
                               >--.  ~-.   ._  ~>-"    "\\   7   7   ]
                              ^.___~"--._    ~-{  .-~ .  `\ Y . /    |
                               <__ ~"-.  ~       /_/   \   \I  Y   : |
                                 ^-.__           ~(_/   \   >._:   | l______
                                     ^--.,___.-~"  /_/   !  `-.~"--l_ /     ~"-.
                                            (_/ .  ~(   /'     "~"--,Y   -==-. _)
                                             (_/ .  \  :           / l      -"~- \
                                              \ /    `.    .     .^   \_.-~"~--.  )
                                               (_/ .   `  /     /       !       )/
                                                / / _.   '.   .':      /        '
                                                ~(_/ .   /    _  `  .-<_
                                                  /_/ . ' .-~" `.  / \  \          ,z=.
                                                  ~( /   '  :   | K   "-.~-.______//
                                                    "-,.    l   I/ \_    __{--->._(==.
                                                     //(     \  <    ~"~"     //
                                                    /' /\     \  \     ,v=.  ((
                                                  .^. / /\     "  }__ //===-  `
                                                 / / ' '  "-.,__ {---(==-
                                               .^ '       :  T  ~"   ll
                                              / .  .  . : | :!        \\
                                             (_/  /   | | j-"          ~^
                                               ~-<_(_.^-~"




----------------------------------------------------------------------------------------
Build Instructions
----------------------------------------------------------------------------------------
Build instructions are as follows for a freshly unzipped program:
  - Ensure you are in the cdg225geren directory
  - 'java -jar cdg225.jar'

If one wishes to recompile the project:

  - Ensure you are in the cdg225geren directory
  - 'make clean'
  - 'make'
  - 'java -jar cdg225.jar'
  
----------------------------------------------------------------------------------------
Getting Into It
----------------------------------------------------------------------------------------
There are many avenues one can take while diving into the Hotel California user interface. However, a gentler introduction to the full user experience may  be readily attained through the following series of steps:

  1. One must login to the database to gain access to the Hotel California servers. To do this, please enter your userid (cdg225 for me) and                    subsequently enter your password. Note that if you enter these incorrectly, the program will terminate. This is the only step that will force the          program to terminate if the user enters incorrect information. This is to ensure that non-authenticated users cannot flood the database with malicious      login requests.
  2. Upon successful connection to the database, the user will be prompted to enter a digit 1-4:
       - 1\. Customer Interface
       - 2\. Front-Desk Interface
       - 3\. Housekeeping Interface
       - 4\. Exit Program
      
      To begin, the developer recommendation is to select '1' for the customer interface. Front-desk and housekeeping both assume a certain level of             familiarity with the hotel that any Hotel California staff would be expected to know. The customer interface makes no such assumptions.
      
  3. Once the user enters the customer interface, they will be able to select a digit 1-3:
       - 1\. Make a Reservation
       - 2\. View List of Locations
       - 3\. Back to Main Menu
       
      The developer recommendation here is to select '2' in order to view the list of locations. Pressing '1' will prompt the user for their desired             destination city, and while the Hotel California conglomeration is set on world domination, we only have locations in 20 U.S. cities as of now.             Pressing '2' will allow the user to choose an available city to stay at. 
      
   4. After the user has a city in mind, press '1' and enter the name of the city as directed by the prompt. 
   
          Note: ALL USER INPUTS ARE CASE SENSITIVE. 
                                                    
   6. From here, the interface is somewhat self evident, the only other suggestion is that if you are a new customer, it would be wise to select new             customer, unless you are familiar with another users last name, phone number, and reservation info. After your customer profile is created, you may         easily navigate throughout all other aspects of the program in order to manipulate your reservation, customer status, transactions, etc.

Happy breaking! (If you can...)
   
   
----------------------------------------------------------------------------------------
Things to Note
----------------------------------------------------------------------------------------
While perusing the source code, one may note that there seem to be missing some features, such as customer statuses being properly updated, points being deducted, etc. This is all taken care of by the user of several triggers in the database, these are:
- UPDATECUSTOMERSTATUS
- UPDATEPOINTBALANCE
- UPDATEROOMSTATUS

Room designation at the time of check-in was specifically requested to be implemented. There is a little story to note about this because of my design. I store, in each reservation, a room_num variable that holds the customers room_number, intended to be updated upon check_in and null beforehand. Obviously, room number is dependent upon the type of room that the user requested. I do not hold a room_type attribute in my reservations, but instead the room_num and h_id (hotel id) foreign keys are meant to allow the room_types relation toeb 

----------------------------------------------------------------------------------------
Design
----------------------------------------------------------------------------------------
Link to ER design diagram:
https://docs.google.com/drawings/d/1nq6BD-3gpen4M1sLHo_Ir-e9NnSvlMbwSP5PZg8fmvc/edit?usp=sharing

A very minimal design was used for the database. With only seven relations the entire database was able to be constructed through extensive use of foreign keys, integrity constraints, and various candidate keys within a single relation. This enabled, at multiple stages of creating reservations, transactions, etc. for the retrieval of primary keys without knowing them beforehand. For example, while checking in customers to their reservations, the customer obviously does not know their reservation id, r_id in the design, and neither does the clerk at the front desk. However, there may only exist one reservation with one start date for one customer that has a last name and a unique phone number. Therefore, the r_id may be fetched through the use of the customer optionally knowing their name, and having their phone number, and the day on which their reservation begins in hand. Many such cases of leveraging candidate keys that are not primary keys are exploited throughout the program.



----------------------------------------------------------------------------------------
CSV
----------------------------------------------------------------------------------------

All CSV test data was generated from and JavaScript functions defined within: https://extendsclass.com/csv-generator.html. CSV files can be found within the files directory. The only data not generated from this website is the data from 8 columns within RESERVATIONS and TRANSACTIONS. These columns were start_date, end_date, check_in, check_out, and cost for the reservations table; tx_received, amount_d, and amount_p for the transactions table. 

The reason these specific columns were generated from SQL queries rather than CSV generation tools is first and foremost because the data in these tables is heavily dependent upon data generated from the CSV files. For example, if a customer's c_status is currently set to 'Past', then their must not be a reservation associated with their c_id with a start_date either in the future or where start_date = SYSDATE. Similarly, cost is dependent upon several attributes defined within other tables. Programming JavaScript into functions within https://extendsclass.com/csv-generator.html where fields from other tables must be referenced is nigh-impossible, another minor reason that these columns were generated with PL/SQL and other user-defined functions. 

All PL/SQL and queries used to generate these data sets can also be found within the files directory. The astute peruser will note that many customers where c_status = 'Current' have reservations within the range of 2023-02-15 and 2023-02-22. This was used during testing as the present date, however new customers added to the system through the user interface will have proper Current/Past/Future statuses updated according to the current time for the time zone "America/Montreal" as defined by the java.time class.
