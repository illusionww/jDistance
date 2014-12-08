package com.thesis;

import com.thesis.classifier.Classifier;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import com.thesis.metric.Distances;
import com.thesis.parser.GraphMLParser;
import com.thesis.parser.Parser;
import com.thesis.parser.SimpleGraphParser;
import com.thesis.utils.PrintUtils;
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
        Parser parser = new GraphMLParser("myRandomGraphn100k5pin0_3pout0_02_graphml.graphml");
        //Parser parser = new SimpleGraphParser("swing.simplegraph");
        //Parser parser = new GraphMLParser("dcrGraph_n500k5pin03pout015.graphml");
        Graph graph = parser.parse();


        File file = new File("result1column.txt");
        File file2 = new File("result2column.txt");
        float[][] sparseM = graph.getSparseM();
        ArrayList<SimpleNodeData> simpleNodeData = graph.getSimpleNodeData();
        try {
            if (file.exists() || file.createNewFile() && (file2.exists() || file2.createNewFile())) {
                try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                    try (PrintWriter out2 = new PrintWriter(file2.getAbsoluteFile())) {
                        for (float i = 0.001f; i < 100.1f; ++i) {


                            FloatMatrix A = new FloatMatrix(sparseM);
                            float[][] D = Distances.PLAIN_FOREST.getD(A, i/10).toArray2();

                            Classifier classifier = new Classifier(D, simpleNodeData);

                            Integer k = 1;
                            Float p = 0.4f;
                            ArrayList<SimpleNodeData> data = classifier.predictLabel(k, p);

                            Integer countErrors = 0;
                            for (int q = 0; q < data.size(); ++q) {
                                if (!simpleNodeData.get(q).getLabel().equals(data.get(q).getLabel()) && simpleNodeData.get(q).getName().equals(data.get(q).getName()))
                                    countErrors += 1;
                            }


                            out.println(i/10);
                            out2.println((float) countErrors / data.size());
                        }
                    }
                }
            }
            else {
                throw new FileNotFoundException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

