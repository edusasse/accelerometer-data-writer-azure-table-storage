package com.edusasse;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

public class AccelerometerDataWriter {

    private static final String connectionString = "<<DefaultEndpointsProtocol=...>>";
    private static final String tableName = "SensorData";
    private static final int ACC_DATA_SIZE = 3200;

    public static void main(String[] args) throws IOException {
        // Create a TableServiceClient using the connection string
        TableServiceClient tableServiceClient = new TableServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        System.out.println("- test with x, y and z being stored in separate properties as one-dimensional arrays.");
        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        testWithSimpleArray(tableServiceClient);
        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =\n\n");

        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        System.out.println("- test with x, y and z being stored in one property with a multidimensional array.");
        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        testWith2DArray(tableServiceClient);
        System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");

    }

    private static void testWithSimpleArray(TableServiceClient tableServiceClient) throws IOException {
        // Random Accelerometer Data
        float[][] randomAccelerometerData = genenrateRandomAccelerometerData(6400, 3);
        float[] randomAccelerometerDataX = new float[6400];
        float[] randomAccelerometerDataY = new float[6400];
        float[] randomAccelerometerDataZ = new float[6400];

        for (int i = 0; i < 6400; i++) {
            randomAccelerometerDataX[i] = randomAccelerometerData[i][0];
            randomAccelerometerDataY[i] = randomAccelerometerData[i][1];
            randomAccelerometerDataZ[i] = randomAccelerometerData[i][2];
        }

        // Domain object
        final AccelerometerData data = new AccelerometerData(UUID.randomUUID().toString(), randomAccelerometerDataX, randomAccelerometerDataY, randomAccelerometerDataZ);

        // Serialize data to store float in standard 4 bytes
        final byte[] xByteArray = DataSerializationUtil.serializeData(data.getX());
        final byte[] yByteArray = DataSerializationUtil.serializeData(data.getY());
        final byte[] zByteArray = DataSerializationUtil.serializeData(data.getZ());

        // Create a table client
        tableServiceClient.createTableIfNotExists(tableName);

        // Create new Entity
        final TableEntity entity = new TableEntity(data.getPartitionKey(), data.getRowKey())
                .addProperty("x", xByteArray)
                .addProperty("y", yByteArray)
                .addProperty("z", zByteArray)
                .addProperty("timestamp", OffsetDateTime.now());

        // Get the memory usage of the array
        final long sizeInBytes = xByteArray.length + yByteArray.length + zByteArray.length;

        // Print the size of the array in bytes
        final int expectedSizeForFloats = 4 * (data.getX().length + data.getY().length + data.getZ().length);
        System.out.println(" - Size of the array in bytes [" + sizeInBytes + "] expected size [" + expectedSizeForFloats + "]");

        // retrieve table cliente
        final TableClient tableClient = tableServiceClient.getTableClient(tableName);

        // Upsert the entity into the table
        long start = System.currentTimeMillis();
        tableClient.upsertEntity(entity);
        System.out.println(" - Insert duration: " + (System.currentTimeMillis() - start));

        // Read
        System.out.println("\n - Find all");
        start = System.currentTimeMillis();
        readAndDeserializeData(tableClient);
        System.out.println("  Read duration: " + (System.currentTimeMillis() - start));


        // Define the query options with PartitionKey and Timestamp filter
        String filter = "PartitionKey eq '" + data.getPartitionKey() + "' and Timestamp ge datetime'2024-03-14T14:48:30.604Z'";
        System.out.println("\n - Search with filter [" + filter + "]");
        ListEntitiesOptions options = new ListEntitiesOptions()
                .setFilter(filter);
        start = System.currentTimeMillis();
        // Loop through the results, displaying information about the entities.
        tableClient.listEntities(options, null, null).forEach(tableEntity -> {
            readAndDeserializeDataEntity(tableEntity);
        });
        System.out.println("Read duration: " + (System.currentTimeMillis() - start));
    }

    private static void testWith2DArray(TableServiceClient tableServiceClient) throws IOException {
        // Random Accelerometer Data
        float[][] randomAccelerometerData = genenrateRandomAccelerometerData(ACC_DATA_SIZE, 3);

        // Domain object
        final AccelerometerData data = new AccelerometerData(UUID.randomUUID().toString(), randomAccelerometerData);

        // Serialize data to store float in standard 4 bytes
        final byte[] xyzByteArray = DataSerializationUtil.serializeData(randomAccelerometerData);

        // Create a table client
        tableServiceClient.createTableIfNotExists(tableName);

        // Create new Entity
        final TableEntity entity = new TableEntity(data.getPartitionKey(), data.getRowKey())
                .addProperty("xyz", xyzByteArray)
                .addProperty("size", ACC_DATA_SIZE)
                .addProperty("timestamp", OffsetDateTime.now());

        // Get the memory usage of the array
        final long sizeInBytes = xyzByteArray.length;

        // Print the size of the array in bytes
        final int expectedSizeForFloats = 4 * 3 * (data.getData().length);
        System.out.println(" - Size of the array in bytes [" + sizeInBytes + "] expected size [" + expectedSizeForFloats + "]");

        // retrieve table cliente
        final TableClient tableClient = tableServiceClient.getTableClient(tableName);

        // Upsert the entity into the table
        long start = System.currentTimeMillis();
        tableClient.upsertEntity(entity);
        System.out.println(" - Insert duration: " + (System.currentTimeMillis() - start));

        // Read
        System.out.println("\n - Find all");
        start = System.currentTimeMillis();
        readAndDeserializeData(tableClient);
        System.out.println(" - Read duration: " + (System.currentTimeMillis() - start));


        // Define the query options with PartitionKey and Timestamp filter
        String filter = "PartitionKey eq '" + data.getPartitionKey() + "' and Timestamp ge datetime'2024-03-14T14:48:30.604Z'";
        System.out.println("\n - Search with filter [" + filter + "]");
        ListEntitiesOptions options = new ListEntitiesOptions()
                .setFilter(filter);
        start = System.currentTimeMillis();
        // Loop through the results, displaying information about the entities.
        tableClient.listEntities(options, null, null).forEach(tableEntity -> {
            readAndDeserializeDataEntity(tableEntity);
        });
        System.out.println(" - Read duration: " + (System.currentTimeMillis() - start));
    }

    private static float[][] genenrateRandomAccelerometerData(int rows, int columns) {
        float[][] result = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = generateRandomValue();
            }
        }
        return result;
    }


    // Read and deserialize data from Azure Table Storage
    private static void readAndDeserializeData(TableClient tableClient) {
        // Retrieve entities from the table
        tableClient.listEntities().forEach(entity -> {
            readAndDeserializeDataEntity(entity);
        });
    }

    private static void readAndDeserializeDataEntity(TableEntity entity) {
        AccelerometerData data = null;
        try {
            if (entity.getProperty("x") != null && entity.getProperty("y") != null && entity.getProperty("z") != null) {
                data = new AccelerometerData(
                        entity.getPartitionKey(),
                        DataSerializationUtil.deserializeData((byte[]) entity.getProperty("x")),
                        DataSerializationUtil.deserializeData((byte[]) entity.getProperty("y")),
                        DataSerializationUtil.deserializeData((byte[]) entity.getProperty("z"))
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use the deserialized data as needed
        if (data != null) {
            System.out.println("Read and deserialized data: " + data.toString());
        }
    }

    // Read and deserialize data from Azure Table Storage
    private static void readAndDeserializeData2D(TableClient tableClient) {
        // Retrieve entities from the table
        tableClient.listEntities().forEach(entity -> {
            readAndDeserializeDataEntity2D(entity);
        });
    }

    private static void readAndDeserializeDataEntity2D(TableEntity entity) {
        AccelerometerData data = null;
        try {
            if (entity.getProperty("xyz") != null && entity.getProperty("size") != null) {
                data = new AccelerometerData(
                        entity.getPartitionKey(),
                        DataSerializationUtil.deserializeData((byte[]) entity.getProperty("xyz"), (int) entity.getProperty("size"), 3)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use the deserialized data as needed
        if (data != null) {
            System.out.println("Read and deserialized data: " + data.toString());
        }
    }

    /**
     * Simulate random accelerometer data*
     *
     * @return
     */
    private static float generateRandomValue() {
        Random rand = new Random();
        return rand.nextFloat() * 10; // You can adjust the range based on your requirements
    }
}

