# Finanx API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%23316192.svg?style=for-the-badge&logo=mysql&logoColor=white)

This project is an API built using **Java, Spring Boot, Flyway Migrations, MySQL as the database, and Spring Security for authentication control.** 
The API was developed to concretize the knowledge accquired in my 8 months journey of learning Java, and some concepts as well,
like Oriented Object Programming,Spring Annotations,Databases/SQL, code versioning with Git, and finally API REST. 

The goal of this API is to help people take control of their financial life. Users can register themselves into the app, and logged-in users can read, create, update and delete expenses.
As someone who has used other finance apps before, I missed some features in the apps, such as: the system should provide a monthly and yearly balance (projection). 
The user can set a monthly spending limit. The user can add cards by specifying the bank, card network, and card name to avoid using sensitive data. Among other things.

DISCLAIMER: currently the API can only read, update, register, and delete users and expenses, I am currently working to improve the codebase as well as implementing the features mentioned above.  
## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Contributing](#contributing)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/nslucas/finanx.git
```

2. Install dependencies with Maven

3. Install [MySQL](https://www.mysql.com/downloads/)

## Usage

1. Make sure that MySQL service is running, if it is not, start the service in Task Explorer -> Services -> MySQL80
2. The API will be accessible at http://localhost:8080


## API Endpoints
The API provides the following endpoints:

```markdown
GET /users - Retrieve a list of all users.

GET /users/{userId} - Retrieve a specific user by ID.

POST /users - Register a new user.

PUT /users/{userId} - Update an already registered user.

DELETE /users/{userId}

GET /expenses - Retrieve all expenses registered into the API.

GET /expenses/{expenseId} - Retrieve a specific expense by ID.

POST /expenses - Register a new expense.

DELETE /expenses/{expenseId}

```


## Database
The project utilizes [MySQL](https://www.mysql.com/) as the database. The necessary database migrations are managed using Flyway.


## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request to the repository.

When contributing to this project, please follow the existing code style, [commit conventions](https://www.conventionalcommits.org/en/v1.0.0/), and submit your changes in a separate branch.

## License

This project is licensed under the [MIT License](LICENSE).
