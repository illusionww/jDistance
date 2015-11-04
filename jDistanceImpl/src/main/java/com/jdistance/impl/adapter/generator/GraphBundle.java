package com.jdistance.impl.adapter.generator;

import com.jdistance.graph.Graph;
import com.jdistance.utils.Cloneable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphBundle implements Cloneable<GraphBundle> {
    Integer n;
    Double pIn;
    Double pOut;
    Integer numOfClusters;
    List<Graph> graphs = new ArrayList<>();

    public GraphBundle(Integer n, Double pIn, Double pOut, Integer numOfClusters, List<Graph> graphs) {
        this.n = n;
        this.pIn = pIn;
        this.pOut = pOut;
        this.numOfClusters = numOfClusters;
        this.graphs = graphs;
    }

    public GraphBundle(Integer n, Double pIn, Double pOut, Integer numOfClusters, Integer count) {
        this.n = n;
        this.pIn = pIn;
        this.pOut = pOut;
        this.numOfClusters = numOfClusters;

        try {
            generate(count);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return "n=" + n + ", p_i=" + pIn + ", p_o=" + pOut + ", k=" + numOfClusters + ", count=" + getCount();
    }

    public Integer getCount() {
        return this.graphs.size();
    }

    public void setGraphs(List<Graph> graphs) {
        this.graphs = graphs;
    }

    public List<Graph> getGraphs() {
        return graphs;
    }

    public void generate(int count) throws ParserConfigurationException, SAXException, IOException {
        DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
        graphs = generator.generateList(count, n, pIn, pOut, numOfClusters);
    }

    @Override
    public GraphBundle clone() {
        List graphsCopy = new ArrayList<>(graphs);
        return new GraphBundle(n, pIn, pOut, numOfClusters, graphsCopy);
    }
}
