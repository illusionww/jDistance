package com.thesis;

import com.thesis.classifier.Classifier;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import com.thesis.metric.Distances;
import com.thesis.parser.GraphMLParser;
import org.jblas.FloatMatrix;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Graph graph = GraphMLParser.parse("myRandomGraphn100k5pin0_3pout0_02_graphml.graphml");

        float[][] sparseM = graph.getSparseM();
        ArrayList<SimpleNodeData> simpleNodeData = graph.getSimpleNodeData();

        FloatMatrix A = new FloatMatrix(sparseM);
        float[][] D = Distances.WALK.getD(A, (float) 0.5).toArray2();

        Classifier classifier = new Classifier(D, simpleNodeData);

        Integer k = 9;
        Float p = 0.9f;
        ArrayList<SimpleNodeData> data = classifier.predictLabel(k, p);
        Integer countErrors = 0;
        for (int i = 0; i < data.size(); ++i) {
            if (!simpleNodeData.get(i).getLabel().equals(data.get(i).getLabel()) && simpleNodeData.get(i).getName().equals(data.get(i).getName()))
                countErrors += 1;
        }

        System.out.println(countErrors);
        System.out.println(data.size());
    }

    public static void write(String fileName, int[][] sparseMatrix) {
        File file = new File(fileName);

        try {
            if (file.exists() || file.createNewFile()) {
                try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                    for (int[] aSparseMatrix : sparseMatrix) {
                        for (int anASparseMatrix : aSparseMatrix) {
                            out.print(anASparseMatrix + " ");
                        }
                        out.println();
                    }
                }
            } else {
                throw new FileNotFoundException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

