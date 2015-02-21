package com.thesis;

import com.thesis.classifier.Classifier;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import com.thesis.metric.Distance;
import com.thesis.parser.GraphMLParser;
import com.thesis.parser.Parser;
import org.jblas.FloatMatrix;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //Parser parser = new GraphMLParser("myRandomGraphn100k5pin0_3pout0_02_graphml.graphml");
        //Parser parser = new SimpleGraphParser("swing.simplegraph");
        Parser parser = new GraphMLParser("dcrGraph_n500k5pin03pout015.graphml");
        Graph graph = parser.parse();

        float[][] sparseM = graph.getSparseM();
        ArrayList<SimpleNodeData> simpleNodeData = graph.getSimpleNodeData();
        Map<Float, Float> result = new TreeMap<>();

        Date start = new Date();
        IntStream.range(1, 101).boxed().collect(Collectors.toList()).parallelStream().forEach(idx -> {
            float i = idx / 20f + 0.0001f;
            FloatMatrix A = new FloatMatrix(sparseM);
            float[][] D = Distance.COMBINATIONS.getD(A, i).toArray2();

            Classifier classifier = new Classifier(D, simpleNodeData);

            Integer k = 10;
            Float p = 0.4f;
            ArrayList<SimpleNodeData> data = classifier.predictLabel(k, p);

            Integer countErrors = 0;
            for (int q = 0; q < data.size(); ++q) {
                if (!simpleNodeData.get(q).getLabel().equals(data.get(q).getLabel()) && simpleNodeData.get(q).getName().equals(data.get(q).getName()))
                    countErrors += 1;
            }
            result.put(i, (float) countErrors / data.size());
        });

        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        System.out.println(diff);

        File file = new File("result.txt");
        if (file.exists() || file.createNewFile()) {
            try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                result.entrySet().forEach(entry -> {
                    out.println(entry.getKey().toString().replace('.', ',') + "\t" + entry.getValue().toString().replace('.', ','));
                });
            }
        } else {
            throw new FileNotFoundException();
        }
    }
}

