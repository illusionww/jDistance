package com.jdistance.impl.adapter.graph;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import jeigen.DenseMatrix;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CSVReader {
    public Graph importGraph(String nodesFile, String edgesFile) throws ParserConfigurationException, IOException, SAXException {
        List<Node> nodes = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(nodesFile))) {
            stream.forEach(line -> {
                String[] rawNode = line.split("[\t;]");
                nodes.add(new Node(rawNode[0], rawNode[2]));
            });
        }
        int count = nodes.size();
        DenseMatrix sparseMatrix = DenseMatrix.zeros(count, count);
        try (Stream<String> stream = Files.lines(Paths.get(edgesFile))) {
            stream.forEach(line -> {
                String[] rawEdge = line.split("[\t;]");
                sparseMatrix.set(Integer.decode(rawEdge[0]), Integer.decode(rawEdge[1]), 1);
                sparseMatrix.set(Integer.decode(rawEdge[1]), Integer.decode(rawEdge[0]), 1);
            });
        }
        return new Graph(sparseMatrix, nodes);
    }

}
