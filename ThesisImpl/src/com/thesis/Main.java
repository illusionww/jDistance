package com.thesis;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.metric.Distance;
import com.thesis.metric.DistanceClass;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.checker.ClustererChecker;
import com.thesis.workflow.checker.DeviationChecker;
import com.thesis.workflow.competition.ClustererCompetitionTask;
import com.thesis.workflow.task.ClassifierBestParamTask;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        drawGraphsScenario();
    }

    private static void drawGraphsScenario() throws ParserConfigurationException, SAXException, IOException {
        List<Distance> distances = Arrays.asList(
                DistanceClass.COMM.getInstance(),
                DistanceClass.COMM_BD.getInstance()
        );

        Arrays.asList(2).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(0.1).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    ScenarioHelper.defaultTasks(new ClassifierChecker(graphs, 3, 0.3), distances, 20).execute().draw();

                });
            });
        });
    }

    private static void findBestClassifierParameterScenario() {
        List<DistanceClass> distances = Arrays.asList(
                DistanceClass.SP_CT,
                DistanceClass.FREE_ENERGY,
                DistanceClass.WALK,
                DistanceClass.LOG_FOREST,
                DistanceClass.FOREST,
                DistanceClass.PLAIN_WALK,
                DistanceClass.COMM,
                DistanceClass.LOG_COMM
        );

        Arrays.asList(50).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(0.1).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    distances.forEach(distanceClass -> {
                        List<Task> tasks = new ArrayList<>();
                        IntStream.range(2, 10).forEach(i -> tasks.add(new ClassifierBestParamTask(new ClassifierChecker(graphs, i, 0.3),
                                distanceClass.getInstance(Integer.toString(i)), 0.0, 3.0, 30, 250)));
                        String taskChainName = "bestParam " + distanceClass.getInstance().getName() + " n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", count=" + graphCount;
                        new TaskChain(taskChainName, tasks).execute().draw();
                    });
                });
            });
        });
    }

    public static void drawDeviationForBestSP() {
        List<DistanceClass> distances = Arrays.asList(
                DistanceClass.SP_CT,
                DistanceClass.FREE_ENERGY,
                DistanceClass.WALK,
                DistanceClass.LOG_FOREST,
                DistanceClass.FOREST,
                DistanceClass.PLAIN_WALK,
                DistanceClass.COMM,
                DistanceClass.LOG_COMM
        );

        Arrays.asList(5).forEach(graphCount -> {
            Arrays.asList(200).forEach(numOfNodes -> {
                Arrays.asList(0.1).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    List<Task> tasks = new ArrayList<>();
                    distances.forEach(distanceClass -> {
                        tasks.add(new DefaultTask(new DeviationChecker(graphs), distanceClass.getInstance(), 250));
                    });
                    String taskChainName = "deviation n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", count=" + graphCount;
                    new TaskChain(taskChainName, tasks).execute().draw();
                });
            });
        });
    }

    private static void competitions() throws ParserConfigurationException, SAXException, IOException {
        List<Distance> distances = Arrays.asList(
                DistanceClass.SP_CT.getInstance(),
                DistanceClass.FREE_ENERGY.getInstance(),
                DistanceClass.WALK.getInstance(),
                DistanceClass.LOG_FOREST.getInstance(),
                DistanceClass.FOREST.getInstance(),
                DistanceClass.PLAIN_WALK.getInstance(),
                DistanceClass.COMM.getInstance(),
                DistanceClass.LOG_COMM.getInstance()
        );

        Arrays.asList(20).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(1, 3, 5).forEach(k -> {
                    Arrays.asList(0.1).forEach(pOut -> {
                        GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                        //new ClassifierCompetitionTask(new ClassifierChecker(graphs, 5, 0.3), 250, numOfNodes, 0.3, pOut, 10, 5, 0.3, "COMPETITION n=" + numOfNodes + " countPoints=250 pIn=0.3 pOut=" + pOut + "k(nei)=" + k + " k(class)=5 p=0.3").execute();
                        new ClustererCompetitionTask(new ClustererChecker(graphs, 5), 250, numOfNodes, 0.3, pOut, 10, 5, "COMPETITION (CLUSTERER) n=" + numOfNodes + " countPoints=250 pIn=0.3 pOut=" + pOut + "k(nei)=" + k + " k(class)=5 p=0.3").execute();
                    });
                });
            });
        });
    }

    private static void initContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = "c:\\cygwin64\\bin\\gnuplot.exe";
        context.IMG_FOLDER = "pictures"; //"D:\\Dropbox\\thesis\\pictures";
        context.COMPETITION_FOLDER = "tournament";
        context.CACHE_FOLDER = "cache";
        context.PARALLEL = true;
        context.USE_CACHE = false;
    }
}

