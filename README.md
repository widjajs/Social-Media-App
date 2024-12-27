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
2. Create or use a MYSQL server
3. Using the create_social_media.sql file provided create the tables and triggers
4. On the DatabaseManager.java class (line 6-8) change the username, password, and URL to your local machine's info
5. Run the DataServer.java class
6. Run the SocialMediaMain.java class
7. Enjoy!
8. Run the SocialMediaMain.java class
