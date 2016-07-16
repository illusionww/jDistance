package com.jdistance.local.adapter.graph;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import com.jdistance.local.workflow.Context;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.FileWriter;
import java.io.IOException;

public class GraphMLWriter {
    public void writeGraph(Graph srcGraph, String fileName) throws IOException, TransformerConfigurationException, SAXException {
        UndirectedGraph<Node, DefaultEdge> destGraph = new SimpleGraph<>(DefaultEdge.class);
        srcGraph.getNodes().forEach(destGraph::addVertex);
        for (int row = 0; row < srcGraph.getA().rows; row++) {
            for (int col = row; col < srcGraph.getA().cols; col++) {
                double value = srcGraph.getA().get(row, col);
                if (value > 0) {
                    destGraph.addEdge(srcGraph.getNodes().get(row), srcGraph.getNodes().get(col));
                }
            }
        }

        GraphMLExporter<Node, DefaultEdge> exporter = new GraphMLExporter<>(node -> Integer.toString(node.getId()), node -> Integer.toString(node.getLabel()), new IntegerEdgeNameProvider<>(), null);
        exporter.export(new FileWriter(Context.getInstance().buildOutputDataFullName(fileName, "graphml")), destGraph);
    }
}
