package com.graphgenerator.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ParseInput {
    public static Input parse(String pathToFile){
        BufferedReader reader;
        List<String> lines = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(pathToFile));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Integer> sizeOfVertices = new LinkedList<>();
        String[] str = lines.get(0).split(" ");
        for (int i = 0; i < str.length; ++i){
            sizeOfVertices.add(Integer.parseInt(str[i]));
        }

        int numberOfClusters = sizeOfVertices.size();
        double[][] probabilityMatrix = new double[numberOfClusters][numberOfClusters];
        for (int i = 0; i < numberOfClusters; ++i){
            str = lines.get(i + 1).split(" ");
            for (int j = 0; j < numberOfClusters; ++j) {
                probabilityMatrix[i][j] = Double.parseDouble(str[j]);
            }
        }
        return new Input(sizeOfVertices, probabilityMatrix);
    }
}