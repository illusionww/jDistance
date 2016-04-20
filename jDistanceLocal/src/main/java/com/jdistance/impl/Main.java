package com.jdistance.impl;

import com.jdistance.distance.*;
import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.adapter.graph.GraphMLWriter;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.Task;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.impl.workflow.TaskPoolResult;
import com.jdistance.learning.NullEstimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.Ward;
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
        compareDiffs();
    }

    private static void test() {
        int clustersCount = 3;
        int pointsCount = 65;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(30, 200, clustersCount, 0.3, 0.1));
        TaskPool pool = new TaskPool("test");
        for (DistanceWrapper distance : Distance.getDefaultDistances()) {
            pool.addTask(new Task(distance.getName() + " distance, Ward", new Ward(clustersCount), Scorer.RATE_INDEX, distance, graphs, pointsCount));
            pool.addTask(new Task(distance.getName() + " distance, Diff", new NullEstimator(), Scorer.DIFFUSION_ORDINAL, distance, graphs, pointsCount));
        }
        for (KernelWrapper kernel : Kernel.getAll()) {
            pool.addTask(new Task(kernel.getName() + " kernel, Ward", new Ward(clustersCount), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
            pool.addTask(new Task(kernel.getName() + " kernel, Diff", new NullEstimator(), Scorer.DIFFUSION_ORDINAL, kernel, graphs, pointsCount));
        }
        TaskPoolResult result = pool.execute();
        result.addMeasuresStatisticsToData();
        result.writeData();

        for (DistanceWrapper distance : Distance.getDefaultDistances()) {
            List<String> taskNames = new ArrayList<>();
            taskNames.add(distance.getName() + " distance, Ward");
            taskNames.add(distance.getName() + " distance, Diff");
            taskNames.add(distance.getName() + " H kernel, Ward");
            taskNames.add(distance.getName() + " H kernel, Diff");
            taskNames.add(distance.getName() + " K kernel, Ward");
            taskNames.add(distance.getName() + " K kernel, Diff");
            result.drawByName(taskNames, distance.getName() + " all, n=200, k=2, pIn=0.3, pOut=0.1 UNIQUE", "[0.3:1]", "0.2", Smooth.UNIQUE);
        }

        for (DistanceWrapper distance : Distance.getDefaultDistances()) {
            result.getData().put(distance.getName() + " distance\\_min", result.getData().get(distance.getName() + " distance, Ward_min"));
            result.getData().put(distance.getName() + " distance\\_max", result.getData().get(distance.getName() + " distance, Ward_max"));
            result.getData().put(distance.getName() + " distance\\_avg", result.getData().get(distance.getName() + " distance, Ward_avg"));
            result.getData().put(distance.getName() + " distance\\_diagavg", result.getData().get(distance.getName() + " distance, Ward_diagavg"));

            List<String> taskNames = new ArrayList<>();
            taskNames.add(distance.getName() + " distance, Ward");
            taskNames.add(distance.getName() + " distance, Diff");
            result.drawByName(taskNames, distance.getName() + " distance statistics, n=200, k=2, pIn=0.3, pOut=0.1", null, null, Smooth.UNIQUE);

            List<String> taskNames2 = new ArrayList<>();
            taskNames2.add(distance.getName() + " distance\\_min");
            taskNames2.add(distance.getName() + " distance\\_max");
            taskNames2.add(distance.getName() + " distance\\_avg");
            taskNames2.add(distance.getName() + " distance\\_diagavg");
            result.drawByName(taskNames2, distance.getName() + " distance statistics, n=200, k=2, pIn=0.3, pOut=0.1", null, null, Smooth.UNIQUE);
        }

        for (KernelWrapper kernel : Kernel.getAll()) {
            result.getData().put(kernel.getName() + " kernel\\_min", result.getData().get(kernel.getName() + " kernel, Ward_min"));
            result.getData().put(kernel.getName() + " kernel\\_max", result.getData().get(kernel.getName() + " kernel, Ward_max"));
            result.getData().put(kernel.getName() + " kernel\\_avg", result.getData().get(kernel.getName() + " kernel, Ward_avg"));
            result.getData().put(kernel.getName() + " kernel\\_diagavg", result.getData().get(kernel.getName() + " kernel, Ward_diagavg"));

            List<String> taskNames = new ArrayList<>();
            taskNames.add(kernel.getName() + " kernel, Ward");
            taskNames.add(kernel.getName() + " kernel, Diff");
            result.drawByName(taskNames, kernel.getName() + " kernel Ward and Diff, n=200, k=2, pIn=0.3, pOut=0.1", null, null, Smooth.UNIQUE);

            List<String> taskNames2 = new ArrayList<>();
            taskNames2.add(kernel.getName() + " kernel\\_min");
            taskNames2.add(kernel.getName() + " kernel\\_max");
            taskNames2.add(kernel.getName() + " kernel\\_avg");
            taskNames2.add(kernel.getName() + " kernel\\_diagavg");
            result.drawByName(taskNames2, kernel.getName() + " kernel statistics, n=200, k=2, pIn=0.3, pOut=0.1", null, null, Smooth.UNIQUE);
        }

        result.writeData("advanced");
    }

    private static void tetetest() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 200, 3, 0.3, 0.1));
        DenseMatrix A = graphs.getGraphs().get(0).getA();
        DenseMatrix H = Distance.COMM.getD(A, 0.1);
//        H = H.mmul(H.t());
        DenseMatrix K = Shortcuts.DtoK(Shortcuts.HtoD(H));
        System.out.println(new PearsonsCorrelation().correlation(H.getValues(), K.getValues()));
    }

    private static void terst() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 2, 0.3, 0.1));
        new TaskPool("motherfucker")
//                .addTask(new Task("distance", new Ward(3), Scorer.RATE_INDEX, new DistanceWrapper(Distance.WALK_H), graphs, 41))
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

        for (Distance distance : Distance.values()) {
            DenseMatrix D = distance.getD(graphs.getGraphs().get(0).getA(), 0.1);

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
                System.out.println("distance " + distance.getName() + " is non-positive definite");
            } else {
                System.out.println("distance " + distance.getName() + " is OK");
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
        for (Distance distance : Distance.values()) {
            DenseMatrix D = distance.getD(A, 0.1);
            HashMap<Integer, Integer> prediction = new Ward(2).predict(D);
            List<Node> nodes = prediction.entrySet().stream()
                    .map(i -> new Node(i.getKey(), i.getValue().toString()))
                    .collect(Collectors.toList());
            Graph graph = new Graph(nodes, A);
            GraphMLWriter writer = new GraphMLWriter();
            writer.writeGraph(graph, "distance " + distance.getName());
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

    public static void dsdsd() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(3, 100, 2, 0.3, 0.1));
        new TaskPool("distances")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Distance.getDefaultDistances(), graphs, 4)
                .execute()
                .drawUniqueAndBezier("[0.49:1]", "0.1");
        new TaskPool("Kernels")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Kernel.getAllK(), graphs, 4)
                .execute()
                .drawUniqueAndBezier("[0.49:1]", "0.1");
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
                    .drawUniqueAndBezier("[0.49:1]", "0.1");
            DenseMatrix A = graphs.getGraphs().get(0).getA();
            DenseMatrix K1 = pair.getLeft().getK(A, pair.getLeft().getScale().calc(A, 0.1));
            DenseMatrix K2 = pair.getRight().getK(A, pair.getRight().getScale().calc(A, 0.1));
            double correlation = new PearsonsCorrelation().correlation(K1.getValues(), K2.getValues());
            System.out.println(pair.getLeft().getName() + " corr: " + correlation);

        }
    }

    public static void compareKernelsMertric() throws SAXException, IOException, TransformerConfigurationException {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(3, 100, 2, 0.3, 0.1));
        List<Pair<Distance, Kernel>> kernelPairs = new ArrayList<>();
        kernelPairs.add(new ImmutablePair<>(Distance.P_WALK, Kernel.P_WALK_K));
        kernelPairs.add(new ImmutablePair<>(Distance.WALK, Kernel.WALK_K));
        kernelPairs.add(new ImmutablePair<>(Distance.FOR, Kernel.FOR_K));
        kernelPairs.add(new ImmutablePair<>(Distance.LOG_FOR, Kernel.LOG_FOR_K));
        kernelPairs.add(new ImmutablePair<>(Distance.COMM, Kernel.COMM_K));
        kernelPairs.add(new ImmutablePair<>(Distance.LOG_COMM, Kernel.LOG_COMM_K));
        kernelPairs.add(new ImmutablePair<>(Distance.HEAT, Kernel.HEAT_K));
        kernelPairs.add(new ImmutablePair<>(Distance.LOG_HEAT, Kernel.LOG_HEAT_K));
        kernelPairs.add(new ImmutablePair<>(Distance.SP_CT, Kernel.SP_CT_K));

        for (Pair<Distance, Kernel> pair : kernelPairs) {
            new TaskPool(pair.getLeft().getName())
                    .addTask(new Task(new Ward(2), Scorer.RATE_INDEX, new DistanceWrapper(pair.getLeft()), graphs, 41))
                    .addTask(new Task(new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(pair.getRight()), graphs, 41))
                    .execute()
                    .drawUniqueAndBezier("[0.49:1]", "0.1");
            DenseMatrix A = graphs.getGraphs().get(0).getA();
            DenseMatrix K1 = pair.getLeft().getD(A, pair.getLeft().getScale().calc(A, 0.1));
            DenseMatrix K2 = pair.getRight().getK(A, pair.getRight().getScale().calc(A, 0.1));
            double correlation = new PearsonsCorrelation().correlation(K1.getValues(), K2.getValues());
            System.out.println(pair.getLeft().getName() + " corr: " + correlation);
        }
    }

    private static void compareDiffs() {
        int clustersCount = 2;
        int pointsCount = 51;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(10, 100, clustersCount, 0.3, 0.1));
//        for (DistanceWrapper distance : Distance.getDefaultDistances()) {
//            TaskPool pool = new TaskPool(distance.getName() + " distance");
//            pool.addTask(new Task(distance.getName() + " distance, Ward", new Ward(clustersCount), Scorer.RATE_INDEX, distance, graphs, pointsCount));
//            pool.addTask(new Task(distance.getName() + ", Diff ordinal", new NullEstimator(), Scorer.DIFFUSION_ORDINAL, distance, graphs, pointsCount));
//            pool.addTask(new Task(distance.getName() + ", Diff cardinal", new NullEstimator(), Scorer.DIFFUSION_CARDINAL, distance, graphs, pointsCount));
//            pool.addTask(new Task(distance.getName() + ", Diff cardinal control", new NullEstimator(), Scorer.DIFFUSION_CARDINAL_CONTROL, distance, graphs, pointsCount));
//            TaskPoolResult result = pool.execute();
//            result.writeData();
//            result.drawUnique("[0.2:1]", "0.2");
//            result.addMeasuresStatisticsToData();
//            result.writeData(distance.getName() + " distance advanced");
//        }
        for (KernelWrapper kernel : Collections.singletonList(new KernelWrapper(Kernel.HEAT_K))) {
            TaskPool pool = new TaskPool(kernel.getName() + " kernel");
            pool.addTask(new Task(kernel.getName() + " kernel, Ward", new Ward(clustersCount), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
            pool.addTask(new Task(kernel.getName() + ", Diff ordinal", new NullEstimator(), Scorer.DIFFUSION_ORDINAL, kernel, graphs, pointsCount));
            pool.addTask(new Task(kernel.getName() + ", Diff cardinal", new NullEstimator(), Scorer.DIFFUSION_CARDINAL, kernel, graphs, pointsCount));
//            pool.addTask(new Task(kernel.getName() + ", Diff cardinal control", new NullEstimator(), Scorer.DIFFUSION_CARDINAL_CONTROL, kernel, graphs, pointsCount));
            pool.addTask(new Task(kernel.getName() + ", Diff cardinal no sqrt", new NullEstimator(), Scorer.DIFFUSION_CARDINAL_WITHOUT_SQRT, kernel, graphs, pointsCount));
            TaskPoolResult result = pool.execute();
            result.writeData();
            result.drawUnique("[0.2:1]", "0.2");
            result.addMeasuresStatisticsToData();
            result.writeData(kernel.getName() + " kernel advanced");
        }
    }
}

