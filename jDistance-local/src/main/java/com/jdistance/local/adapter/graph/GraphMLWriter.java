package com.jdistance.local.adapter.graph;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Vertex;
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
        UndirectedGraph<Vertex, DefaultEdge> destGraph = new SimpleGraph<>(DefaultEdge.class);
        srcGraph.getVertices().forEach(destGraph::addVertex);
        for (int row = 0; row < srcGraph.getA().rows; row++) {
            for (int col = row; col < srcGraph.getA().cols; col++) {
                double value = srcGraph.getA().get(row, col);
                if (value > 0) {
                    destGraph.addEdge(srcGraph.getVertices().get(row), srcGraph.getVertices().get(col));
                }
            }
        }

        GraphMLExporter<Vertex, DefaultEdge> exporter = new GraphMLExporter<>(node -> Integer.toString(node.getId()), node -> Integer.toString(node.getLabel()), new IntegerEdgeNameProvider<>(), null);
        exporter.export(new FileWriter(Context.getInstance().buildOutputDataFullName(fileName, "graphml")), destGraph);
    }
}
