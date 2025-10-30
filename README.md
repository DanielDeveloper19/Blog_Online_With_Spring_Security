📰 Blog Online – Backend Social Platform
A full backend system for a social media–style blog platform built completely from scratch with Spring Boot.
🚀 Overview

Blog Online is a backend application that simulates the core of a social media platform — users can create accounts, publish posts, comment, and interact with others.

It’s fully secured with Spring Security and OAuth2.0, supports role-based access control, and manages user relationships through a solid and scalable design.

This project was built to strengthen my understanding of backend architecture, security, and database design in real-world scenarios.

✨ Features

✅ User registration and login
✅ JWT & OAuth2.0 authentication
✅ Role-based access control (Admin, User, etc.)
✅ Create, read, update, and delete posts
✅ Comment and interact with posts
✅ Secure REST API endpoints
✅ Exception handling & validation
✅ Relational database with JPA/Hibernate

🧠 What I Learned

How to design clean and maintainable backend architecture.

Deep understanding of Spring Security and OAuth2.0.

Managing entity relationships between users, posts, and comments.

Creating reusable and testable service layers.

Best practices for structuring scalable Spring Boot projects.


⚙️ Tech Stack
Category	Technologies
Language	Java 17
Framework	Spring Boot 3
Security	Spring Security, OAuth2.0, JWT
Database	MySql
ORM	       JPA / Hibernate
Testing	  JUnit, Mockito
Build Tool 	Maven
Other	     Lombok



How to Run Locally

Clone the repository

git clone https://github.com/<your-username>/blog-online.git
cd blog-online


Configure your MySQL database

Create a database (e.g., blog_online_db)

Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password


Run the project

mvn spring-boot:run
