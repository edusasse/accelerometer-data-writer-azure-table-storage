# Accelerometer Data Writer (Azure Table Storage) [PoC]

This project contains Java classes for writing accelerometer data to Azure Table Storage.

## Project Structure

- `AccelerometerDataWriter.java`: Java class for generating random accelerometer data and writing it to Azure Table Storage.
- `DataSerializationUtil.java`: Utility class for serializing and deserializing float arrays and float 2D arrays to/from byte arrays.
- `AccelerometerData.java`: Domain class representing accelerometer data.
- `pom.xml`: Maven project configuration file.

## Usage

### Prerequisites

- Java Development Kit (JDK) 8 or higher installed
- Apache Maven installed
- Azure Table Storage account with the required connection string
- Azure SDK for Java added as a dependency (included in the provided pom.xml)

### Instructions

1. Clone this repository to your local machine.
2. Open the project in your favorite Java IDE.
3. Update the `connectionString` variable in `AccelerometerDataWriter.java` with your Azure Storage account connection string.
4. Replace the `AccountName`, `AccountKey`, and `EndpointSuffix` values in the connection string with your Azure Storage account details.
5. Build the project using Maven (`mvn clean package`).
6. Run the `AccelerometerDataWriter` class to start writing accelerometer data to Azure Table Storage.

## Dependencies

- Azure SDK for Java (version 12.3.17)
- Maven Compiler Plugin (version 3.8.1)

## License

This project is licensed under the MIT License