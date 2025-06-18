package com.attijari.bankingservices.utils;

public class CosineSimilarity {


    public static double cosineSimilarity(float[] array1, float[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("Arrays must be of the same length");
        }

        double dotProduct = 0.0;
        double normArray1 = 0.0;
        double normArray2 = 0.0;

        for (int i = 0; i < array1.length; i++) {
            dotProduct += array1[i] * array2[i];
            normArray1 += Math.pow(array1[i], 2);
            normArray2 += Math.pow(array2[i], 2);
        }

        return dotProduct / (Math.sqrt(normArray1) * Math.sqrt(normArray2));
    }
}

