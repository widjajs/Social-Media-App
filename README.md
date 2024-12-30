# Social Media App - VIBRA

## Description
This is a Java based social media app that provides a user-friendly GUI to interact with a SQL-based relational database

## Features
- A GUI built using Java Swing for interacting with the database.
  - Feed: shows friend's posts
  - Search: search other users to view profiles, send friend requests, and block
  - Create Post: create posts for your profile
  - Profile: view your profile (bio, username, likes, friends) and posts as well as edit profile and posts
  - Friends: view friends and unfriend
  - Login/Create User/Logout: can login to existing account, create a new user, and logout
  - Notes: posts can be liked, disliked, edited, deleted, and commented on
- Connects to a local MySQL database to perform SQL queries.
- Supports various CRUD operations (Create, Read, Update, Delete).
- SQL queries are handled using Java's `PreparedStatement` to prevent SQL injection.
- Includes test cases for the DatabaseManager and SocialMediaObjects
  - SocialObjectTestCases.java tests that the User, Comment, and Post classes work as intended
  - DatabaseManagerTestCases.java tests that all creations, insertions, deletions, selections, etc. work as intended

## Technologies Used
- **Java** (for the application logic and GUI)
- **MySQL** (for the relational database)
- **JDBC** (for database connection and querying)
- **Maven** (check pom.xml file for more info)

## Prerequisites
Before running the project, ensure you have the following installed:
- **Java JDK** (version 8 or above)
- **MySQL** (or a MySQL-compatible database)
- **Java IDE**
- **Maven Project**

## Installation and Setup
1. Downlaod the repository into your preferred Java IDE
2. Make sure the pom.xml file is present and includes dependencies for SQL & JUnit testcases
4. Create or use a MYSQL server
5. Using the create_social_media.sql file provided create the tables and triggers
6. To run test cases create the two test users with the userUUID included at the top of the DatabaseManagerTestCases.java file
7. On the DatabaseManager.java class (line 6-8) change the username, password, and URL to your local machine's info
8. Run the DataServer.java class
9. Run the SocialMediaMain.java class
10. Enjoy!
