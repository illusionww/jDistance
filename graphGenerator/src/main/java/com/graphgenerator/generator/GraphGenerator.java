package com.graphgenerator.generator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.graphgenerator.utils.Input;
import com.jdistance.graph.Graph;
import com.jdistance.graph.SimpleNodeData;

public class GraphGenerator {
    private boolean biDirectional;
    private final Random random;
    private static GraphGenerator graphGenerator;

    private GraphGenerator(){
        random = new Random();
        biDirectional = false;
    }

    public void setBiDirectional(boolean b){
        biDirectional = b;
    }

    public static GraphGenerator getInstance(){
        if (graphGenerator == null){
            graphGenerator = new GraphGenerator();
        }
        return graphGenerator;
    }

    public Graph generateGraph(List<Integer> sizeClusters, double[][] probabilityMatrix){
        int numberOfVertices = 0;
        List <Integer> borderClusters = new LinkedList<>(sizeClusters);
        for (int i = 0; i < sizeClusters.size(); ++i){
            numberOfVertices += sizeClusters.get(i);
            if (i > 0){
                borderClusters.set(i, borderClusters.get(i - 1) + borderClusters.get(i));
            }
        }


        double[][] sparseMatrix = new double[numberOfVertices][numberOfVertices];
        for (int i = 0; i < numberOfVertices; ++i){
            for (int j = i + 1; j < numberOfVertices; ++j){
                sparseMatrix[i][j] =
                        (getProbabilityEdge(borderClusters, probabilityMatrix, i, j) < random.nextDouble()) ? 1 : 0;
                if (biDirectional){
                    sparseMatrix[j][i] = sparseMatrix[i][j];
                }
                else {
                    sparseMatrix[j][i] =
                            (getProbabilityEdge(borderClusters, probabilityMatrix, j, i) < random.nextDouble()) ? 1 : 0;
                }
            }
        }
        return new Graph(sparseMatrix, generateSimpleNodeDatas(sizeClusters));
    }

    public Graph generateGraph(Input input){
        return generateGraph(input.getSizeOfVertices(), input.getProbabilityMatrix());
    }

    private double getProbabilityEdge(List<Integer> borderClusters, double[][] probabilityMatrix, int from, int to){
        int fromCluster = -1;
        int toCluster = -1;
        for (int i = 0; i < borderClusters.size(); ++i){
            if (borderClusters.get(i) > from){
                fromCluster = i;
            }
            if (borderClusters.get(i) > to){
                toCluster = i;
            }
            if (fromCluster != -1 && toCluster != -1){
                break;
            }
        }
        return probabilityMatrix[fromCluster][toCluster];
    }

    private ArrayList<SimpleNodeData> generateSimpleNodeDatas(List<Integer> sizeClusters){
        ArrayList<SimpleNodeData> simpleNodeDatas = new ArrayList<>();
        Integer vertex = 0;
        for (Integer i = 0; i < sizeClusters.size(); ++i){
            for (int j = 0; j < sizeClusters.get(i); ++j){
                simpleNodeDatas.add(new SimpleNodeData(vertex.toString(), i.toString()));
                vertex++;
            }
        }
        return simpleNodeDatas;
    }

}