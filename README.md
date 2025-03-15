# Donation and Fundraising Platform

## Overview

The Donation and Fundraising Platform is a web application designed to facilitate donations and fundraising campaigns. It allows users to create and manage campaigns, make donations, and track their contributions. The platform is built using Java, Hibernate ORM with Jakarta Persistence, and a PostgreSQL database.

## Features

- **User Management**: Register, login, update profile, and delete users.
- **Campaign Management**: Create, update, and delete fundraising campaigns.
- **Donation Management**: Make donations to campaigns and track donation history.
- **Category Management**: Organize campaigns into categories for better visibility.
- **Transaction Management**: Track and manage donation transactions.

## Technologies Used

- **Backend**: Java, Hibernate ORM, Jakarta Persistence
- **Database**: PostgreSQL
- **Testing**: JUnit 5
- **Build Tool**: Maven

## Prerequisites

- Java Development Kit (JDK) 11 or later
- PostgreSQL database
- Maven (for dependency management)
- Eclipse IDE

## Setup Instructions

1.  **Clone the Repository**:
    ```bash
    git clone <repository-url>
    cd donation-fundraising-platform
    ```
    _Repository_: https://github.com/jcharles921/fund-raiser-test-case
2.  **Configure the Database**:
    -   Create a PostgreSQL database and update the database configuration in `HibernateUtil` with your database credentials.
3.  **Build the Project**:
    -   Open Eclipse and import the project as a Maven project.
    -   Right-click on the project in the Project Explorer and select `Run As > Maven Install` to build the project.
4.  **Run the Application**:
    -   Execute the main application class to start the server.
    -   Ensure that the database server is running.
5.  **Run Tests**:
    -   To run the test suite, right-click on the project in the Project Explorer and select `Run As > JUnit Test`.
    -   Ensure that all tests pass successfully.

## Project Structure

-   `model`: Contains the entity classes representing the database tables.
-   `controller`: Contains the controller classes handling business logic.
-   `util`: Contains utility classes, such as `HibernateUtil` for session management.
-   `test`: Contains JUnit test cases for the application.

## Contributing

Contributions are welcome! Please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Commit your changes and push to your fork.
4.  Submit a pull request with a detailed description of your changes.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.