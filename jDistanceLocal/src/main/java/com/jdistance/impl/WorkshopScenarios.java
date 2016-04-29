package com.jdistance.impl;

import com.jdistance.distance.*;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.competitions.CopelandsMethod;
import com.jdistance.impl.competitions.RejectCurve;
import com.jdistance.impl.workflow.Task;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.impl.workflow.TaskPoolResult;
import com.jdistance.learning.NullEstimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.KMeans;
import com.jdistance.learning.clustering.Ward;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WorkshopScenarios {
    public void differentDI() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 100, 2, 0.3, 0.1));
        new TaskPool("Example")
                .addTask(new Task("Ward RI", new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(Kernel.LOG_COMM_K), graphs, 32))
                .addTask(new Task("DI K ordinal", new NullEstimator(), Scorer.DIFFUSION_ORDINAL, new KernelWrapper(Kernel.LOG_COMM_K), graphs, 32))
                .addTask(new Task("DI K cardinal", new NullEstimator(), Scorer.DIFFUSION_CARDINAL, new KernelWrapper(Kernel.LOG_COMM_K), graphs, 32))
                .addTask(new Task("DI dist ordinal", new NullEstimator(), Scorer.DIFFUSION_ORDINAL, new DistanceWrapper(Distance.LOG_COMM), graphs, 32))
                .addTask(new Task("DI H ordinal", new NullEstimator(), Scorer.DIFFUSION_ORDINAL, new KernelWrapper(Kernel.LOG_COMM_H), graphs, 32))

                .execute().writeData().drawUnique("[0.49:1]", "0.2");
    }

    public void countInfluence() {
        GraphBundle graphs100 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 100, 2, 0.3, 0.1));
        GraphBundle graphs1 = new GraphBundle(graphs100.getGraphs().subList(0, 1), graphs100.getProperties());
        GraphBundle graphs10 = new GraphBundle(graphs100.getGraphs().subList(0, 10), graphs100.getProperties());
        new TaskPool("countInfluence")
                .addTask(new Task("1", new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(Kernel.LOG_FOR_H), graphs1, 32))
                .addTask(new Task("10", new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(Kernel.LOG_FOR_H), graphs10, 32))
                .addTask(new Task("100", new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(Kernel.LOG_FOR_H), graphs100, 32))
                .execute().writeData();
    }

    // FIRST RESEARCH SECTION ------------------------------------------------------------------------------------------
    public void log() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(200, 100, 3, 0.3, 0.1));
        new TaskPool("log Ward 100_0.3(3)_0.1")
                .buildSimilarTasks(new Ward(3), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.WALK_H),
                        new KernelWrapper(Kernel.P_WALK_H),
                        new KernelWrapper(Kernel.FOR_H),
                        new KernelWrapper(Kernel.LOG_FOR_H),
                        new KernelWrapper(Kernel.COMM_H),
                        new KernelWrapper(Kernel.LOG_COMM_H),
                        new KernelWrapper(Kernel.HEAT_H),
                        new KernelWrapper(Kernel.LOG_HEAT_H)
                ), graphs, 32).execute().writeData();
    }

    public void logHeat() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(200, 200, 2, 0.3, 0.1));
        new TaskPool("log k-means Heat")
                .buildSimilarTasks(new KMeans(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.HEAT_H),
                        new KernelWrapper(Kernel.LOG_HEAT_H),
                        new KernelWrapper(Kernel.HEAT_K),
                        new KernelWrapper(Kernel.LOG_HEAT_K)
                ), graphs, 32).execute().writeData();
        new TaskPool("log Ward Heat")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.HEAT_H),
                        new KernelWrapper(Kernel.LOG_HEAT_H),
                        new KernelWrapper(Kernel.HEAT_K),
                        new KernelWrapper(Kernel.LOG_HEAT_K)
                ), graphs, 32).execute().writeData();
    }

    public void diffPartWalk() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 150, 2, 0.3, 0.15));
        new TaskPool("diff Ward Walk 150")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.P_WALK_H),
                        new KernelWrapper(Kernel.WALK_H)
                ), graph1, 32)
                .buildSimilarTasks(new NullEstimator(), Scorer.DIFFUSION_ORDINAL, Arrays.asList(
                        new KernelWrapper(Kernel.P_WALK_K),
                        new KernelWrapper(Kernel.WALK_K)
                ), graph1, 32).execute().writeData();
    }

    public void diffPartFor() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(150, 150, 2, 0.3, 0.15));
        new TaskPool("diff Ward For 150")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.FOR_H)), graph1, 32)
                .buildSimilarTasks(new NullEstimator(), Scorer.DIFFUSION_ORDINAL, Arrays.asList(
                        new KernelWrapper(Kernel.FOR_K)), graph1, 32)
                .execute()
                .writeData();
    }

    public void diffPartLogFor() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(150, 150, 2, 0.3, 0.15));
        new TaskPool("diff Ward For 150")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.LOG_FOR_H)), graph1, 32)
                .buildSimilarTasks(new NullEstimator(), Scorer.DIFFUSION_ORDINAL, Arrays.asList(
                        new KernelWrapper(Kernel.LOG_FOR_K)), graph1, 32)
                .execute()
                .writeData();
    }

    public void diffPartComm() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(150, 150, 2, 0.3, 0.15));
        new TaskPool("diff Ward Comm 150")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.COMM_H)), graph1, 32)
                .buildSimilarTasks(new NullEstimator(), Scorer.DIFFUSION_ORDINAL, Arrays.asList(
                        new KernelWrapper(Kernel.COMM_K)), graph1, 32)
                .execute()
                .writeData();
    }

    public void diffPartLogComm() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(150, 150, 2, 0.3, 0.15));
        new TaskPool("diff Ward logComm 150")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.LOG_COMM_H)), graph1, 32)
                .buildSimilarTasks(new NullEstimator(), Scorer.DIFFUSION_ORDINAL, Arrays.asList(
                        new KernelWrapper(Kernel.LOG_COMM_K)), graph1, 32)
                .execute()
                .writeData();
    }

    public void diffPartHeat() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 150, 2, 0.3, 0.15));
        new TaskPool("diff Ward 150")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.HEAT_H),
                        new KernelWrapper(Kernel.LOG_HEAT_H),
                        new KernelWrapper(Kernel.HEAT_K),
                        new KernelWrapper(Kernel.LOG_HEAT_K)
                ), graph1, 32)
                .buildSimilarTasks(new NullEstimator(), Scorer.DIFFUSION_ORDINAL, Arrays.asList(
                        new KernelWrapper(Kernel.HEAT_K),
                        new KernelWrapper(Kernel.LOG_HEAT_K)), graph1, 32)
                .execute()
                .writeData();
    }

    public void diffPartFE() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 150, 2, 0.3, 0.15));
        new TaskPool("diff Ward 150")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Arrays.asList(
                        new KernelWrapper(Kernel.FE_K)), graph1, 32)
                .buildSimilarTasks(new NullEstimator(), Scorer.DIFFUSION_ORDINAL, Arrays.asList(
                        new KernelWrapper(Kernel.FE_K)), graph1, 32)
                .execute()
                .writeData();
    }

    public void newsgroupsWard() throws IOException {
        GraphBundle graph1 = Datasets.getNewsgroupGraph(Datasets.newsgroups.get(0));
        new TaskPool("newsgroups1 Ward")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Kernel.getDefaultKernels(), graph1, 32)
                .execute()
                .writeData()
                .drawUnique("[0.49:1]", "0.2");
        GraphBundle graph2 = Datasets.getNewsgroupGraph(Datasets.newsgroups.get(1));

        new TaskPool("newsgroups2 Ward")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Kernel.getDefaultKernels(), graph2, 32)
                .execute()
                .writeData()
                .drawUnique("[0.49:1]", "0.2");
        GraphBundle graph3 = Datasets.getNewsgroupGraph(Datasets.newsgroups.get(2));
        new TaskPool("newsgroups3 Ward")
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Kernel.getDefaultKernels(), graph3, 32)
                .execute()
                .writeData()
                .drawUnique("[0.49:1]", "0.2");
    }

    public void newsgroupsKMeans() throws IOException {
        GraphBundle graph1 = Datasets.getNewsgroupGraph(Datasets.newsgroups.get(0));
        new TaskPool("newsgroups1 k-means")
                .buildSimilarTasks(new KMeans(2), Scorer.RATE_INDEX, Kernel.getDefaultKernels(), graph1, 32)
                .execute()
                .writeData()
                .drawUnique("[0.49:1]", "0.2");
        GraphBundle graph2 = Datasets.getNewsgroupGraph(Datasets.newsgroups.get(1));

        new TaskPool("newsgroups2 k-means")
                .buildSimilarTasks(new KMeans(2), Scorer.RATE_INDEX, Kernel.getDefaultKernels(), graph2, 32)
                .execute()
                .writeData()
                .drawUnique("[0.49:1]", "0.2");
        GraphBundle graph3 = Datasets.getNewsgroupGraph(Datasets.newsgroups.get(2));
        new TaskPool("newsgroups3 k-means")
                .buildSimilarTasks(new KMeans(2), Scorer.RATE_INDEX, Kernel.getDefaultKernels(), graph3, 32)
                .execute()
                .writeData()
                .drawUnique("[0.49:1]", "0.2");
    }

    public List<GraphBundle> generateGenerationProperties() {
        List<GraphBundle> graphs = new ArrayList<>();
        for (int nodesCount : new int[]{100, 150}) {
            for (int clustersCount : new int[]{2, 4}) {
                for (double pOut : new double[]{0.15, 0.1}) {
                    graphs.add(new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(5, nodesCount, clustersCount, 0.3, pOut)));
                }
            }
        }
        return graphs;
    }

    public void competitonsWard() {
        List<GraphBundle> graphs = generateGenerationProperties();
        CopelandsMethod copelandsMethod = new CopelandsMethod(graphs, Kernel.getDefaultKernels(), 32, 90) {
            @Override
            protected TaskPool generateTaskPool(GraphBundle graphs) {
                TaskPool pool = new TaskPool();
                for (AbstractMeasureWrapper kernel : measures) {
                    pool.addTask(new Task(kernel.getName() + ", Ward", new Ward(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                }
                return pool;
            }
        };
        copelandsMethod.execute();
        copelandsMethod.write("Ward");
    }

    public void competitonsKMeans() {
        List<GraphBundle> graphs = generateGenerationProperties();
        CopelandsMethod copelandsMethod = new CopelandsMethod(graphs, Kernel.getDefaultKernels(), 32, 90) {
            @Override
            protected TaskPool generateTaskPool(GraphBundle graphs) {
                TaskPool pool = new TaskPool();
                for (AbstractMeasureWrapper kernel : measures) {
                    pool.addTask(new Task(kernel.getName() + ", K-Means", new KMeans(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                }
                return pool;
            }
        };
        copelandsMethod.execute();
        copelandsMethod.write("K-Means");
    }

    public void competitonsWardAndKMeans() {
        List<GraphBundle> graphs = generateGenerationProperties();
        CopelandsMethod copelandsMethod = new CopelandsMethod(graphs, Kernel.getDefaultKernels(), 32, 90) {
            @Override
            protected TaskPool generateTaskPool(GraphBundle graphs) {
                TaskPool pool = new TaskPool();
                for (AbstractMeasureWrapper kernel : measures) {
                    pool.addTask(new Task(kernel.getName() + ", Ward", new Ward(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                    pool.addTask(new Task(kernel.getName() + ", K-Means", new KMeans(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                }
                return pool;
            }
        };
        copelandsMethod.execute();
        copelandsMethod.write("Ward and K-Means");
    }

    public void rejectCurve() {
        GraphBundle graph1 = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 100, 2, 0.3, 0.1));
        RejectCurve rq = new RejectCurve();

        int pointsCount = 100;

        Map<String, Map<Double, Double>> result1 = rq.calcCurve(new DistanceWrapper(Distance.LOG_COMM), 0.4, graph1, pointsCount);
        new TaskPoolResult("rq logComm", new ArrayList<>(result1.keySet()), result1, null).writeData();
        Map<String, Map<Double, Double>> result2 = rq.calcCurve(new DistanceWrapper(Distance.WALK), 0.7, graph1, pointsCount);
        new TaskPoolResult("rq Walk", new ArrayList<>(result2.keySet()), result2, null).writeData();
        Map<String, Map<Double, Double>> result3 = rq.calcCurve(new DistanceWrapper(Distance.LOG_HEAT), 0.5, graph1, pointsCount);
        new TaskPoolResult("rq logHeat", new ArrayList<>(result3.keySet()), result3, null).writeData();
        Map<String, Map<Double, Double>> result4 = rq.calcCurve(new DistanceWrapper(Distance.LOG_FOR), 0.17, graph1, pointsCount);
        new TaskPoolResult("rq logFor", new ArrayList<>(result4.keySet()), result4, null).writeData();
        Map<String, Map<Double, Double>> result5 = rq.calcCurve(new DistanceWrapper(Distance.COMM), 0.1, graph1, pointsCount);
        new TaskPoolResult("rq Comm", new ArrayList<>(result5.keySet()), result5, null).writeData();
        Map<String, Map<Double, Double>> result6 = rq.calcCurve(new DistanceWrapper(Distance.FOR), 0.9, graph1, pointsCount);
        new TaskPoolResult("rq For", new ArrayList<>(result6.keySet()), result6, null).writeData();
        Map<String, Map<Double, Double>> result7 = rq.calcCurve(new DistanceWrapper(Distance.FE), 0.7, graph1, pointsCount);
        new TaskPoolResult("rq FE", new ArrayList<>(result7.keySet()), result7, null).writeData();

        rq.writeDistributionBySP(graph1.getGraphs().get(0));
        rq.writeVerticesDegrees(graph1.getGraphs().get(0));
        rq.writeVerticesDegreesWithoutRepeat(graph1.getGraphs().get(0).getA(), graph1.getGraphs().get(0).getNodes());
    }
}
