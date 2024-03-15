package com.edusasse;

import java.util.Arrays;

class AccelerometerData {
    private String partitionKey;
    private String rowKey;
    private float[] x;
    private float[] y;
    private float[] z;
    private float[][] data;

    public AccelerometerData(String deviceId, float[] x, float[] y, float[] z) {
        this.partitionKey = deviceId;
        this.rowKey = String.valueOf(System.currentTimeMillis());
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AccelerometerData(String deviceId, float[][] data) {
        this.partitionKey = deviceId;
        this.rowKey = String.valueOf(System.currentTimeMillis());
        this.data = data;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public String getRowKey() {
        return rowKey;
    }

    public float[] getX() {
        return x;
    }

    public float[] getY() {
        return y;
    }

    public float[] getZ() {
        return z;
    }

    public float[][] getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccelerometerData{");
        sb.append("partitionKey='").append(partitionKey).append('\'');
        sb.append(", rowKey='").append(rowKey).append('\'');
        sb.append(", x=").append(Arrays.toString(getPartialArray(x)));
        sb.append(", y=").append(Arrays.toString(getPartialArray(y)));
        sb.append(", z=").append(Arrays.toString(getPartialArray(z)));
        sb.append(", data=").append(Arrays.deepToString(getPartial2DArray(data)));
        sb.append('}');
        return sb.toString();
    }

    private float[] getPartialArray(float[] array) {
        if (array == null || array.length <= 5) {
            return array;
        } else {
            return Arrays.copyOfRange(array, 0, 5);
        }
    }

    private float[][] getPartial2DArray(float[][] array) {
        if (array == null || array.length == 0 || array[0].length <= 5) {
            return array;
        } else {
            float[][] partialArray = new float[array.length][5];
            for (int i = 0; i < array.length; i++) {
                partialArray[i] = Arrays.copyOfRange(array[i], 0, 5);
            }
            return partialArray;
        }
    }

}
