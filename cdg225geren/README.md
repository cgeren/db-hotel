# db-hotel
Database for the luxurious Hotel California chain of hotels.

CSV
----------------------------------------------------------------------------------------

All CSV test data was generated from and JavaScript functions defined within: https://extendsclass.com/csv-generator.html. CSV files can be found within the files directory. The only data not generated from this website is the data from 8 columns within RESERVATIONS and TRANSACTIONS. These columns were start_date, end_date, check_in, check_out, and cost for the reservations table; tx_received, amount_d, and amount_p for the transactions table. 

The reason these specific columns were generated from SQL queries rather than CSV generation tools is first and foremost because the data in these tables is heavily dependent upon data generated from the CSV files. For example, if a customer's c_status is currently set to 'Past', then their must not be a reservation associated with their c_id with a start_date either in the future or where start_date = SYSDATE. Similarly, cost is dependent upon several attributes defined within other tables. Programming JavaScript into functions within https://extendsclass.com/csv-generator.html where fields from other tables must be referenced is nigh-impossible, another minor reason that these columns were generated with PL/SQL and other user-defined functions. 

All PL/SQL and queries used to generate these data sets can also be found within the files directory. The astute peruser will note that customers where c_status = 'Current' all have reservations within the range of 2023-02-15 and 2023-02-22. This is currently being used as a "present" date range in the database, until a more elegant solution with managing time is implemented.
