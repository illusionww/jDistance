package com.jdistance.impl;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.adapter.GNUPlotAdapter;
import com.jdistance.impl.adapter.graph.GraphMLWriter;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.Task;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.impl.workflow.TaskPoolResult;
import com.jdistance.learning.NullEstimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.Ward;
import com.jdistance.metric.*;
import com.panayotis.gnuplot.style.Smooth;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws SAXException, IOException, TransformerConfigurationException {
        Context.fill(false, true, true, "./results/data", "./results/img");
        compareKernelsMertric();
    }

    private static void test() {
        int clustersCount = 2;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 100, clustersCount, 0.3, 0.1));
        TaskPool pool = new TaskPool("test");
        for (MetricWrapper metric : Metric.getDefaultDistances()) {
            pool.addTask(new Task("Ward metric " + metric.getName(), new Ward(clustersCount), Scorer.RATE_INDEX, metric, graphs, 51));
            pool.addTask(new Task("Diff metric " + metric.getName(), new NullEstimator(), Scorer.DIFFUSION, metric, graphs, 51));
        }
        for (KernelWrapper kernel : Kernel.getAllK()) {
            pool.addTask(new Task("Ward kernel " + kernel.getName(), new Ward(clustersCount), Scorer.RATE_INDEX, kernel, graphs, 51));
            pool.addTask(new Task("Diff kernel " + kernel.getName(), new NullEstimator(), Scorer.DIFFUSION, kernel, graphs, 51));
        }
        TaskPoolResult result = pool.execute();
        result.writeData();

        GNUPlotAdapter gnuplot = new GNUPlotAdapter();
        for (MetricWrapper metric : Metric.getDefaultDistances()) {
            Map<String, Map<Double, Double>> dataForPicture = new TreeMap<>();
            dataForPicture.put(metric.getName() + " metric, Ward", result.getData().get("Ward metric " + metric.getName()));
            dataForPicture.put(metric.getName() + " kernel, Ward", result.getData().get("Ward kernel " + metric.getName()));
            dataForPicture.put(metric.getName() + " metric, Diff", result.getData().get("Diff metric " + metric.getName()));
            dataForPicture.put(metric.getName() + " kernel, Diff", result.getData().get("Diff kernel " + metric.getName()));
            gnuplot.draw(dataForPicture, "Ward and Diff metric and kernel " + metric.getName() + ", n=100, k=2, pIn=0.3, pOut=0.1 UNIQUE", "[0:1]", "0.2", "[0.3:1]", "0.2", Smooth.UNIQUE);
        }
    }

    private static void tetetest() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 200, 3, 0.3, 0.1));
        DenseMatrix A = graphs.getGraphs().get(0).getA();
        DenseMatrix H = Metric.COMM.getD(A, 0.1);
//        H = H.mmul(H.t());
        DenseMatrix K = Shortcuts.DtoK(Shortcuts.HtoD(H));
        System.out.println(new PearsonsCorrelation().correlation(H.getValues(), K.getValues()));
    }

    private static void terst() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 2, 0.3, 0.1));
        new TaskPool("motherfucker")
//                .addTask(new Task("metric", new Ward(3), Scorer.RATE_INDEX, new MetricWrapper(Metric.WALK_H), graphs, 41))
                .addTask(new Task("kernel", new Ward(3), Scorer.RATE_INDEX, new KernelWrapper(Kernel.WALK_H), graphs, 41))
                .addTask(new Task("0-diagonal kernel", new Ward(3), Scorer.RATE_INDEX, new KernelWrapper(Kernel.WALK_H) {
                    @Override
                    public DenseMatrix calc(DenseMatrix A, double param) {
                        DenseMatrix D = super.calc(A, param);
                        int d = D.rows;
                        for (int i = 0; i < d; i++) {
                            D.set(i, i, 0);
                        }
                        return D;
                    }
                }, graphs, 41))
                .execute().drawUniqueAndBezier("[0.49:1]", "0.1");
    }

    private static void hui() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, 2, 0.3, 0.1));

        for (Metric metric : Metric.values()) {
            DenseMatrix D = metric.getD(graphs.getGraphs().get(0).getA(), 0.1);

            List<Double> determinants = new ArrayList<>();
            for (int i = 1; i < D.rows; i++) {
                double[][] DMatrix = new double[i][i];
                for (int j = 0; j < i; j++) {
                    for (int k = 0; k < i; k++) {
                        DMatrix[j][k] = D.get(j, k);
                    }
                }
                RealMatrix apacheMatrix = new BlockRealMatrix(DMatrix);
                determinants.add(apacheMatrix.getDeterminant());
            }
            if (determinants.stream().allMatch(i -> i > 0)) {
                System.out.println("metric " + metric.getName() + " is non-positive definite");
            } else {
                System.out.println("metric " + metric.getName() + " is OK");
            }
        }

        for (Kernel kernel : Kernel.values()) {
            DenseMatrix H = kernel.getK(graphs.getGraphs().get(0).getA(), 0.1);

            List<Double> determinants = new ArrayList<>();
            for (int i = 1; i < H.rows; i++) {
                double[][] DMatrix = new double[i][i];
                for (int j = 0; j < i; j++) {
                    for (int k = 0; k < i; k++) {
                        DMatrix[j][k] = H.get(j, k);
                    }
                }
                RealMatrix apacheMatrix = new BlockRealMatrix(DMatrix);
                determinants.add(apacheMatrix.getDeterminant());
            }
            if (determinants.stream().allMatch(i -> i > 0)) {
                System.out.println("kernel " + kernel.getName() + " is non-positive definite");
            } else {
                System.out.println("kernel " + kernel.getName() + " is OK");
            }
        }
    }

    public static void ere() throws SAXException, IOException, TransformerConfigurationException {
        GraphBundle graphs = Datasets.getTwoStars();
        DenseMatrix A = graphs.getGraphs().get(0).getA();
        for (Metric metric : Metric.values()) {
            DenseMatrix D = metric.getD(A, 0.1);
            HashMap<Integer, Integer> prediction = new Ward(2).predict(D);
            List<Node> nodes = prediction.entrySet().stream()
                    .map(i -> new Node(i.getKey(), i.getValue().toString()))
                    .collect(Collectors.toList());
            Graph graph = new Graph(nodes, A);
            GraphMLWriter writer = new GraphMLWriter();
            writer.writeGraph(graph, "metric " + metric.getName());
        }
        for (Kernel kernel : Kernel.values()) {
            DenseMatrix D = new KernelWrapper(kernel).calc(A, 0.1);
            HashMap<Integer, Integer> prediction = new Ward(2).predict(D);
            List<Node> nodes = prediction.entrySet().stream()
                    .map(i -> new Node(i.getKey(), i.getValue().toString()))
                    .collect(Collectors.toList());
            Graph graph = new Graph(nodes, A);
            GraphMLWriter writer = new GraphMLWriter();
            writer.writeGraph(graph, "kernel " + kernel.getName());
        }
    }

    public static void compareKernelsHandK() throws SAXException, IOException, TransformerConfigurationException {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(3, 100, 2, 0.3, 0.1));
        List<Pair<Kernel, Kernel>> kernelPairs = new ArrayList<>();
        kernelPairs.add(new ImmutablePair<>(Kernel.P_WALK_H, Kernel.P_WALK_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.WALK_H, Kernel.WALK_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.FOR_H, Kernel.FOR_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.LOG_FOR_H, Kernel.LOG_FOR_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.COMM_H, Kernel.COMM_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.LOG_COMM_H, Kernel.LOG_COMM_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.HEAT_H, Kernel.HEAT_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.LOG_HEAT_H, Kernel.LOG_HEAT_K));
        kernelPairs.add(new ImmutablePair<>(Kernel.SP_CT_H, Kernel.SP_CT_K));

        for (Pair<Kernel, Kernel> pair : kernelPairs) {
            new TaskPool(pair.getLeft().getName())
                    .addTask(new Task(new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(pair.getLeft()), graphs, 41))
                    .addTask(new Task(new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(pair.getRight()), graphs, 41))
                    .execute()
                    .drawUniqueAndBezier("[0.49:1]", "0.1")
                    .writeStatistics();
            DenseMatrix A = graphs.getGraphs().get(0).getA();
            DenseMatrix K1 = pair.getLeft().getK(A, pair.getLeft().getScale().calc(A, 0.1));
            DenseMatrix K2 = pair.getRight().getK(A, pair.getRight().getScale().calc(A, 0.1));
            double correlation = new PearsonsCorrelation().correlation(K1.getValues(), K2.getValues());
            System.out.println(pair.getLeft().getName() + " corr: " + correlation);

        }
    }

    public static void compareKernelsMertric() throws SAXException, IOException, TransformerConfigurationException {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(3, 100, 2, 0.3, 0.1));
        List<Pair<Metric, Kernel>> kernelPairs = new ArrayList<>();
        kernelPairs.add(new ImmutablePair<>(Metric.P_WALK, Kernel.P_WALK_K));
        kernelPairs.add(new ImmutablePair<>(Metric.WALK, Kernel.WALK_K));
        kernelPairs.add(new ImmutablePair<>(Metric.FOR, Kernel.FOR_K));
        kernelPairs.add(new ImmutablePair<>(Metric.LOG_FOR, Kernel.LOG_FOR_K));
        kernelPairs.add(new ImmutablePair<>(Metric.COMM, Kernel.COMM_K));
        kernelPairs.add(new ImmutablePair<>(Metric.LOG_COMM, Kernel.LOG_COMM_K));
        kernelPairs.add(new ImmutablePair<>(Metric.HEAT, Kernel.HEAT_K));
        kernelPairs.add(new ImmutablePair<>(Metric.LOG_HEAT, Kernel.LOG_HEAT_K));
        kernelPairs.add(new ImmutablePair<>(Metric.SP_CT, Kernel.SP_CT_K));

        for (Pair<Metric, Kernel> pair : kernelPairs) {
            new TaskPool(pair.getLeft().getName())
                    .addTask(new Task(new Ward(2), Scorer.RATE_INDEX, new MetricWrapper(pair.getLeft()), graphs, 41))
                    .addTask(new Task(new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(pair.getRight()), graphs, 41))
                    .execute()
                    .drawUniqueAndBezier("[0.49:1]", "0.1")
                    .writeStatistics();
            DenseMatrix A = graphs.getGraphs().get(0).getA();
            DenseMatrix K1 = pair.getLeft().getD(A, pair.getLeft().getScale().calc(A, 0.1));
            DenseMatrix K2 = pair.getRight().getK(A, pair.getRight().getScale().calc(A, 0.1));
            double correlation = new PearsonsCorrelation().correlation(K1.getValues(), K2.getValues());
            System.out.println(pair.getLeft().getName() + " corr: " + correlation);
        }
    }
}

