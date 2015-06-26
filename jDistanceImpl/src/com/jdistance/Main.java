package com.jdistance;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import com.jdistance.workflow.Context;
import com.jdistance.workflow.TaskChain;
import com.jdistance.workflow.checker.ClassifierChecker;
import com.jdistance.workflow.checker.DeviationChecker;
import com.jdistance.workflow.task.ClassifierBestParamTask;
import com.jdistance.workflow.task.CustomTask;
import com.jdistance.workflow.task.Task;
import com.jdistance.workflow.task.competition.ClassifierCompetitionTask;
import com.jdistance.workflow.task.competition.CompetitionDTO;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        competitions();
    }

    private static void drawGraphsScenario() throws ParserConfigurationException, SAXException, IOException {
        List<Distance> distances = Arrays.asList(
                DistanceClass.COMM_D.getInstance(),
                DistanceClass.LOG_COMM_D.getInstance(),
                DistanceClass.SP_CT.getInstance(),
                DistanceClass.FREE_ENERGY.getInstance(),
                DistanceClass.WALK.getInstance(),
                DistanceClass.LOG_FOREST.getInstance(),
                DistanceClass.FOREST.getInstance(),
                DistanceClass.PLAIN_WALK.getInstance()
        );

        Arrays.asList(50).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(0.2).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    ScenarioHelper.defaultTasks(new ClassifierChecker(graphs, 3, 0.3), distances, 50).execute().draw();
                });
            });
        });
    }

    private static void findBestClassifierParameterScenario() {
        List<DistanceClass> distances = Arrays.asList(
                DistanceClass.COMM_D,
                DistanceClass.LOG_COMM_D,
                DistanceClass.SP_CT,
                DistanceClass.FREE_ENERGY,
                DistanceClass.WALK,
                DistanceClass.LOG_FOREST,
                DistanceClass.FOREST,
                DistanceClass.PLAIN_WALK
        );

        Arrays.asList(30).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(0.15).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    distances.forEach(distanceClass -> {
                        List<Task> tasks = new ArrayList<>();
                        IntStream.range(2, 11).forEach(i -> tasks.add(new ClassifierBestParamTask(new ClassifierChecker(graphs, i, 0.3),
                                distanceClass.getInstance(Integer.toString(i)), 0.0, 4.0, 30, 60)));
                        String taskChainName = "bestParam " + distanceClass.getInstance().getName() + " n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", count=" + graphCount;
                        new TaskChain(taskChainName, tasks).execute().draw();
                    });
                });
            });
        });

        Arrays.asList(10).forEach(graphCount -> {
            Arrays.asList(200).forEach(numOfNodes -> {
                Arrays.asList(0.05, 0.15).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    distances.forEach(distanceClass -> {
                        List<Task> tasks = new ArrayList<>();
                        IntStream.range(2, 11).forEach(i -> tasks.add(new ClassifierBestParamTask(new ClassifierChecker(graphs, i, 0.3),
                                distanceClass.getInstance(Integer.toString(i)), 0.0, 4.0, 30, 60)));
                        String taskChainName = "bestParam " + distanceClass.getInstance().getName() + " n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", count=" + graphCount;
                        new TaskChain(taskChainName, tasks).execute().draw();
                    });
                });
            });
        });
    }

    public static void drawDeviationForBestSP() {
        List<DistanceClass> distances = Arrays.asList(
                DistanceClass.COMM_D,
                DistanceClass.LOG_COMM_D,
                DistanceClass.SP_CT,
                DistanceClass.FREE_ENERGY,
                DistanceClass.WALK,
                DistanceClass.LOG_FOREST,
                DistanceClass.FOREST,
                DistanceClass.PLAIN_WALK
        );

        Arrays.asList(10).forEach(graphCount -> {
            Arrays.asList(200).forEach(numOfNodes -> {
                Arrays.asList(0.1).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    List<Task> tasks = new ArrayList<>();
                    distances.forEach(distanceClass -> {
                        tasks.add(new CustomTask(new DeviationChecker(graphs), distanceClass.getInstance(), 0.9, 1.0, 300));
                    });
                    String taskChainName = "deviation n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", count=" + graphCount;
                    new TaskChain(taskChainName, tasks).execute().draw();
                });
            });
        });
    }

    private static void competitions() throws ParserConfigurationException, SAXException, IOException {
        int graphCountForLearning = 50;
        int grapsCountForCompetitions = 10;
        int pointsCountForLearning = 100;

        Arrays.asList(100).forEach(numOfNodes -> {
            Arrays.asList(0.05).forEach(pOut -> {
                GraphBundle forLearning = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCountForLearning);
                GraphBundle forCompetitions = new GraphBundle(numOfNodes, 0.3, pOut, 5, grapsCountForCompetitions);

                List<CompetitionDTO> competitionDTOs = Arrays.asList(
                        new CompetitionDTO(DistanceClass.COMM_D.getInstance(), 6, 1.5),
                        new CompetitionDTO(DistanceClass.LOG_COMM_D.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.SP_CT.getInstance(), 4, 0.2),
                        new CompetitionDTO(DistanceClass.FREE_ENERGY.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.WALK.getInstance(), 8, 1.4),
                        new CompetitionDTO(DistanceClass.LOG_FOREST.getInstance(), 8, 1.9),
                        new CompetitionDTO(DistanceClass.FOREST.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.PLAIN_WALK.getInstance(), 10, 1.6)
                );

                String fileName = "competitions n=" + numOfNodes + ", p_o=" + pOut + ", forLearning=" + graphCountForLearning + ", forCompetioions=" + grapsCountForCompetitions;
                new ClassifierCompetitionTask(competitionDTOs, forLearning, forCompetitions, pointsCountForLearning, fileName).execute().write();
            });
        });

        Arrays.asList(200).forEach(numOfNodes -> {
            Arrays.asList(0.05).forEach(pOut -> {
                GraphBundle forLearning = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCountForLearning);
                GraphBundle forCompetitions = new GraphBundle(numOfNodes, 0.3, pOut, 5, grapsCountForCompetitions);

                List<CompetitionDTO> competitionDTOs = Arrays.asList(
                        new CompetitionDTO(DistanceClass.COMM_D.getInstance(), 10, 0.8),
                        new CompetitionDTO(DistanceClass.LOG_COMM_D.getInstance(), 8, 2.6),
                        new CompetitionDTO(DistanceClass.SP_CT.getInstance(), 7, 0.0),
                        new CompetitionDTO(DistanceClass.FREE_ENERGY.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.WALK.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.LOG_FOREST.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.FOREST.getInstance(), 6, 0.0),
                        new CompetitionDTO(DistanceClass.PLAIN_WALK.getInstance(), 10, 4.0)
                );

                String fileName = "competitions n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", forLearning=" + graphCountForLearning + ", forCompetioions=" + grapsCountForCompetitions;
                new ClassifierCompetitionTask(competitionDTOs, forLearning, forCompetitions, pointsCountForLearning, fileName).execute().write();
            });
            Arrays.asList(0.15).forEach(pOut -> {
                GraphBundle forLearning = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCountForLearning);
                GraphBundle forCompetitions = new GraphBundle(numOfNodes, 0.3, pOut, 5, grapsCountForCompetitions);

                List<CompetitionDTO> competitionDTOs = Arrays.asList(
                        new CompetitionDTO(DistanceClass.COMM_D.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.LOG_COMM_D.getInstance(), 10, 2.0),
                        new CompetitionDTO(DistanceClass.SP_CT.getInstance(), 5, 0.0),
                        new CompetitionDTO(DistanceClass.FREE_ENERGY.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.WALK.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.LOG_FOREST.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.FOREST.getInstance(), 1, 0.0),
                        new CompetitionDTO(DistanceClass.PLAIN_WALK.getInstance(), 1, 0.0)
                );

                String fileName = "competitions n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", forLearning=" + graphCountForLearning + ", forCompetioions=" + grapsCountForCompetitions;
                new ClassifierCompetitionTask(competitionDTOs, forLearning, forCompetitions, pointsCountForLearning, fileName).execute().write();
            });
        });
    }

    private static void initContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = "c:\\cygwin64\\bin\\gnuplot.exe";
        context.IMG_FOLDER = "pictures"; //"D:\\Dropbox\\jdistance\\pictures";
        context.COMPETITION_FOLDER = "tournament";
        context.CACHE_FOLDER = "cache";
        context.PARALLEL = true;
        context.USE_CACHE = false;
    }
}

