package com.thesis;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.metric.Distance;
import com.thesis.metric.DistanceClass;
import com.thesis.workflow.Context;
import com.thesis.workflow.checker.ClustererChecker;
import com.thesis.workflow.competition.ClustererCompetitionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        drawGraphsScenario();
    }

    private static void drawGraphsScenario() throws ParserConfigurationException, SAXException, IOException {
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
                        new ClustererCompetitionTask(new ClustererChecker(graphs, 5), 250, numOfNodes, 0.3, pOut, 10, 5,"COMPETITION (CLUSTERER) n=" + numOfNodes + " countPoints=250 pIn=0.3 pOut=" + pOut + "k(nei)=" + k + " k(class)=5 p=0.3").execute();
                    });
                });
            });
        });
    }

    private static void findBestClassifierParameterScenario() {
        List<DistanceClass> distances = Arrays.asList(
//                DistanceClass.SP_CT,
//                DistanceClass.FREE_ENERGY,
//                DistanceClass.WALK,
//                DistanceClass.LOG_FOREST,
//                DistanceClass.FOREST,
//                DistanceClass.PLAIN_WALK,
//                DistanceClass.COMM,
                DistanceClass.LOG_COMM
        );

        Arrays.asList(3).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(0.02, 0.05, 0.1).forEach(pOut -> {

                });
            });
        });
    }

    private static void initContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = "c:\\cygwin64\\bin\\gnuplot.exe";
        context.IMG_FOLDER = "C:\\Users\\vits\\Dropbox\\pictures";
        context.CACHE_FOLDER = "C:\\Users\\vits\\Dropbox\\cache";
        context.COMPETITION_FOLDER = "C:\\Users\\vits\\Dropbox\\tournament";
        context.PARALLEL = true;
        context.USE_CACHE = false;
    }
}

