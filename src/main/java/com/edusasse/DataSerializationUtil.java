package com.edusasse;

import java.io.*;

/**
 * Utility class for serializing and deserializing float arrays and float 2D arrays to/from byte arrays.
 */
public class DataSerializationUtil {

    /**
     * Serializes a float array into a byte array.
     *
     * @param data The float array to serialize.
     * @return The serialized byte array.
     * @throws IOException If an I/O error occurs.
     */
    public static byte[] serializeData(float[] data) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            for (float value2 : data) {
                dataOutputStream.writeFloat(value2);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Serializes a float 2D array into a byte array.
     *
     * @param data The float 2D array to serialize.
     * @return The serialized byte array.
     * @throws IOException If an I/O error occurs.
     */
    public static byte[] serializeData(float[][] data) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            for (float[] value : data) {
                for (float value2 : value) {
                    dataOutputStream.writeFloat(value2);
                }
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Deserializes a byte array into a float array.
     *
     * @param serializedData The byte array to deserialize.
     * @return The deserialized float 2D array.
     * @throws IOException If an I/O error occurs.
     */
    public static float[] deserializeData(byte[] serializedData) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {

            final float[] deserializedData = new float[serializedData.length/4];

            for (int i = 0; i < deserializedData.length; i++) {
                try {
                    deserializedData[i] = dataInputStream.readFloat();
                } catch (EOFException e) {
                    e.printStackTrace();
                }
            }

            return deserializedData;
        }
    }

    /**
     * Deserializes a byte array into a float 2D array.
     *
     * @param serializedData The byte array to deserialize.
     * @param numRows        specify the number of rows in your float
     * @param numCols        specify the number of columns in your
     * @return The deserialized float 2D array.
     * @throws IOException If an I/O error occurs.
     */
    public static float[][] deserializeData(byte[] serializedData, int numRows, int numCols) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {

            final float[][] deserializedData = new float[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    try {
                        deserializedData[i][j] = dataInputStream.readFloat();
                    } catch (EOFException e) {
                        e.printStackTrace();
                    }
                }
            }

            return deserializedData;
        }
    }

}
