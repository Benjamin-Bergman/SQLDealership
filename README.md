# Car Dealership App

An app to manage cars at a dealership. It will automatically save to
and load from a MySQL database. It also manages contracts for sold or
leased vehicles.

## Setup

Instructions on how to set up and run the project using IntelliJ IDEA.

### Prerequisites

- IntelliJ IDEA: Ensure you have IntelliJ IDEA installed, which you can download
  from [here](https://www.jetbrains.com/idea/download/).
- Java SDK: Make sure Java SDK is installed and configured in IntelliJ.
- IntelliJ Manifold plugin: The program will compile and run without this, but it provides a better editor experience.
  You can download it [here](https://plugins.jetbrains.com/plugin/10057-manifold).
- A running MySQL server on `localhost:3306` with the `car_dealership` schema installed.
- Environment variables `MYSQL_USERNAME` and `MYSQL_PASSWORD` with the relevant credentials
  for your server.

### Running the Application in IntelliJ

Follow these steps to get your application running within IntelliJ IDEA:

1. Open IntelliJ IDEA.
2. Select "Open" and navigate to the directory where you cloned or downloaded the project.
3. After the project opens, wait for IntelliJ to index the files and set up the project.
4. Find `Program.java` (with the `public static void main(String[] args)` method).
5. Right-click on the file and select 'Run 'Program.main()'' to start the application.

## Technologies Used

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Manifold](https://github.com/manifold-systems/manifold)
