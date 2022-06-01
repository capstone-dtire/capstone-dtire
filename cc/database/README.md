# database

## How to use the database
1. Install PostgreSQL
2. Create a database dtire with user dtire
3. Import dtire.sql to your machine
4. Apply all changes to the database from the perubahan-db folder


## Database Changelog
1. 2022-05-19: Initial version.
2. 2022-05-26: Change date_of_check column in detection_history from timestamp to bigint (The timestamp is stored as epoch time now because some problems from Cloud SQL is caused by the timestamp type)
3. 2022-05-28: Add column for public.user url_picture to store the url of the user's picture.
4. 2022-05-29: Merge public.user and user_details into public.user to simplify the database.


## Author
Allief Nuriman
