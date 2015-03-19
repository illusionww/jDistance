package com.thesis;

import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.ParserWrapper;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.workflow.Context;
import com.thesis.workflow.Folder;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.checker.ClustererChecker;
import com.thesis.workflow.task.DefaultTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Context.getInstance().init("C:\\cygwin64\\bin\\gnuplot.exe", "graphs", "C:\\thesis", true, Scale.ATAN);

        List<Folder> folders = Arrays.asList(
                new Folder("n100pin03pout002k5"),
                new Folder("n100pin03pout01k5"),
                new Folder("n300pin03pout002k5"),
                new Folder("n300pin03pout003k5"),
                new Folder("n300pin03pout005k5"),
                new Folder("n300pin03pout01k5"),
                new Folder("n500pin03pout002k5"),
                new Folder("n500pin03pout003k5"),
                new Folder("n500pin03pout005k5"),
                new Folder("n500pin03pout01k5"),
                new Folder("n1000pin03pot01k5")
        );

        List<Distance> distances = Arrays.asList(
                Distance.WALK,
                Distance.LOGARITHMIC_FOREST,
                Distance.PLAIN_FOREST,
                Distance.PLAIN_WALK,
                Distance.COMMUNICABILITY,
                Distance.LOGARITHMIC_COMMUNICABILITY,
                Distance.COMBINATIONS,
                Distance.HELMHOLTZ_FREE_ENERGY
        );

        Parser parser = new ParserWrapper();
        for (Folder folder : folders) {
            log.info("start parse " + folder.getTitle());
            List<Graph> graphs = parser.parseInDirectory(folder.getFilePath());
            new TaskChain(new DefaultTask(new ClassifierChecker(graphs, 5, 0.3), distances, 0.003))
                    .execute().draw("classifier (k=5, p=0.3) " + folder.getTitle());
            log.info("end " + folder.getTitle());
        }

        for (Folder folder : folders) {
            log.info("start parse " + folder.getTitle());
            List<Graph> graphs = parser.parseInDirectory(folder.getFilePath());
            new TaskChain(new DefaultTask(new ClustererChecker(graphs, 5), distances, 0.003))
                    .execute().draw("clusterer (k=5) " + folder.getTitle());
            log.info("end " + folder.getTitle());
        }
    }
}

