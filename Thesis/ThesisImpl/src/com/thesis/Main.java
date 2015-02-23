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

        List<Graph> graphsClust100init = new ArrayList<>();
        graphsClust100init.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_1));
        graphsClust100init.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_2));
        graphsClust100init.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_3));
        Scenarios.drawClustererCapabilityAllMetricsInRange(graphsClust100init, 1000, 5, 0.0001, 3.0, 0.2);

        List<Graph> graphsClust100 = new ArrayList<>();
        graphsClust100.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_1));
        graphsClust100.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_2));
        graphsClust100.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_3));
        Scenarios.drawClustererCapabilityAllMetricsInRange(graphsClust100, 100, 5, 0.0001, 3.0, 0.02);


        List<Graph> graphsClust500 = new ArrayList<>();
        graphsClust500.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_1));
        graphsClust500.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_2));
        graphsClust500.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_3));
        Scenarios.drawClustererCapabilityAllMetricsInRange(graphsClust500, 500, 5, 0.0001, 3.0, 0.02);

        List<Graph> graphsClass100_1start = new ArrayList<>();
        graphsClass100_1start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_1));
        graphsClass100_1start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_2));
        graphsClass100_1start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_3));
        Scenarios.drawClassifierCapabilityAllMetricsInRange(graphsClass100_1start, 100, 1, 0.3, 0.0001, 0.4, 0.01);

        List<Graph> graphsClass100_10start = new ArrayList<>();
        graphsClass100_10start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_1));
        graphsClass100_10start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_2));
        graphsClass100_10start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_3));
        Scenarios.drawClassifierCapabilityAllMetricsInRange(graphsClass100_10start, 100, 10, 0.3, 0.0001, 0.4, 0.01);

        List<Graph> graphsClass500_1start = new ArrayList<>();
        graphsClass500_1start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_1));
        graphsClass500_1start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_2));
        graphsClass500_1start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_3));
        Scenarios.drawClassifierCapabilityAllMetricsInRange(graphsClass500_1start, 500, 1, 0.3, 0.0001, 0.4, 0.01);

        List<Graph> graphsClass500_10start = new ArrayList<>();
        graphsClass500_10start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_1));
        graphsClass500_10start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_2));
        graphsClass500_10start.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_2_3));
        Scenarios.drawClassifierCapabilityAllMetricsInRange(graphsClass500_10start, 500, 10, 0.3, 0.0001, 0.4, 0.01);
    }


}

