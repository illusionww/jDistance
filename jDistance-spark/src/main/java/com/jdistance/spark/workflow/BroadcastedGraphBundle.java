package com.jdistance.spark.workflow;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import org.apache.spark.broadcast.Broadcast;

import java.util.List;

public class BroadcastedGraphBundle extends GraphBundle {
    private Broadcast<List<Graph>> broadcastedGraphs;

    public BroadcastedGraphBundle(String name, List<Graph> graphs, GeneratorPropertiesPOJO properties) {
        super(name, null, properties);
        broadcastedGraphs = Context.getInstance().getSparkContext().broadcast(graphs);
    }

    public BroadcastedGraphBundle(GraphBundle graphBundle) {
        this(graphBundle.getName(), graphBundle.getGraphs(), graphBundle.getProperties());
    }

    @Override
    public List<Graph> getGraphs() {
        return broadcastedGraphs.value();
    }
}
