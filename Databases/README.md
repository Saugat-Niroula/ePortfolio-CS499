# Databases Artifact
Inventory Management Application

## Description and Origin
The Inventory Management Application was initially developed for the CS360 Mobile Architecture and Programming course in September 2023. The app streamlines inventory management in warehouse environments by providing features such as user authentication, inventory display, item addition and removal, quantity adjustment, and low inventory notifications.

## Justification
This artifact was chosen for inclusion in my ePortfolio because it demonstrates key skills and abilities in database management. The enhancements included integrating Firebase Firestore for real-time data synchronization, implementing complex queries and triggers, encrypting user credentials, and adding tests for database interactions.

## Enhancements
Replaced the local SQLite database with Firebase Firestore in `DataController` for real-time data synchronization. Created `DataMigration` class to transition data from SQLite to Firestore without data loss. Implemented encryption for user credentials in `DataMigration` class using AES encryption. Added unit and instrumentation tests in `ExampleInstrumentedTest` to ensure reliability of Firestore integration and encryption functionality.

## Learning and Challenges
Enhancing this artifact involved understanding cloud-based databases and real-time data synchronization. Implementing encryption techniques expanded my knowledge of data security. Developing data migration scripts required ensuring data integrity throughout the transition. Challenges included managing data consistency and ensuring secure data storage in Firestore.

[For more detailed information on the above enhancement, click here for reading my artifact enhancement document](https://github.com/Saugat-Niroula/ePortfolio-CS499/blob/main/Databases/CS%20499%20Milestone_4_Narratives%20for%20Database%20Enhancement_Saugat_Niroula.pdf)

[Also, click on this link if you want to view my initial code before this enhancement.](https://github.com/Saugat-Niroula/ePortfolio-CS499/blob/main/Initial%20Code.zip)

[And, click on this link if you want to view my enhanced code for this category.](https://github.com/Saugat-Niroula/ePortfolio-CS499/blob/main/Enhanced%20code%20for%20Database-Milestone%20Four.zip)
