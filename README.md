# GitHub User Information Terminal Application

A Kotlin terminal application that fetches and stores GitHub user information using the GitHub API.

## Features

- Fetch GitHub user information using username
- Store user data locally to prevent duplicate API requests
- Display list of stored users
- Search users by username
- Search repositories by name
- Store and display user information including:
  - Username
  - Number of followers
  - Number of followings
  - Account creation date
  - List of public repositories

## Requirements

- Java 8 or higher
- Maven
- GitHub API access

## Setup

1. Clone the repository
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.github.userinfo.Main"
   ```

## Usage

The application provides a menu-driven interface with the following options:

1. Get user information using username
2. Show list of stored users
3. Search users by username
4. Search repositories by name
5. Exit application

## Data Storage

User information is stored locally in a JSON file to minimize API requests and improve performance.
