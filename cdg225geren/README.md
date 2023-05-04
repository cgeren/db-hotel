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
