package com.jdistance.impl.adapter.graph;

import com.jdistance.graph.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.FileWriter;
import java.io.IOException;

public class GraphMLWriter {
    public void writeGraph(Graph srcGraph, String fileName) throws IOException, TransformerConfigurationException, SAXException {
        UndirectedGraph<String, DefaultEdge> destGraph = new SimpleGraph<>(DefaultEdge.class);
        for (int nodeId = 0; nodeId < srcGraph.getNodes().size(); nodeId++) {
            destGraph.addVertex(Integer.toString(nodeId));
        }
        for (int row = 0; row < srcGraph.getSparseMatrix().rows; row++) {
            for (int col = row; col < srcGraph.getSparseMatrix().cols; col++) {
                double value = srcGraph.getSparseMatrix().get(row, col);
                if (value > 0) {
                    destGraph.addEdge(Integer.toString(row), Integer.toString(col));
                }
            }
        }
        GraphMLExporter<String, DefaultEdge> exporter = new GraphMLExporter<>();
        exporter.export(new FileWriter(fileName), destGraph);
    }
}
