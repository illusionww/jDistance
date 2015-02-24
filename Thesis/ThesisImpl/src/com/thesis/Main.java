package com.thesis;

import com.thesis.adapter.parser.GraphMLParser;
import com.thesis.adapter.parser.Parser;
import com.thesis.graph.Graph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Parser parser = new GraphMLParser();

        List<Graph> graphsClust500 = new ArrayList<>();
        graphsClust500.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_1));
//        graphsClust500.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_2));
        graphsClust500.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_3));
        Scenarios.drawClustererCapabilityAllMetricsInRange(graphsClust500, 500, 5, 0.0001, 1.0, 0.05);
    }
}

