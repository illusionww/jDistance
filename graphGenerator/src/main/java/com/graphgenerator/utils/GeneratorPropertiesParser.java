package com.graphgenerator.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GeneratorPropertiesParser {
    public static GeneratorPropertiesDTO parse(String pathToFile) {
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

        List<Integer> sizeOfClusters = new LinkedList<>();
        String[] str = lines.get(0).split(" ");
        for (String aStr : str) {
            sizeOfClusters.add(Integer.parseInt(aStr));
        }

        int clustersCount = sizeOfClusters.size();
        double[][] probabilityMatrix = new double[clustersCount][clustersCount];
        for (int i = 0; i < clustersCount; ++i) {
            str = lines.get(i + 1).split(" ");
            for (int j = 0; j < clustersCount; ++j) {
                probabilityMatrix[i][j] = Double.parseDouble(str[j]);
            }
        }
        return new GeneratorPropertiesDTO(sizeOfClusters, probabilityMatrix);
    }
}