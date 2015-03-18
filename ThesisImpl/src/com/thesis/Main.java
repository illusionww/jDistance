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

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initEnvironment();
        Parser parser = new ParserWrapper();

        List<Distance> distances = new ArrayList<>();

        distances.add(Distance.COMMUNICABILITY);
        distances.add(Distance.LOGARITHMIC_COMMUNICABILITY);


        List<Folder> folders = new ArrayList<>(Arrays.asList(
                Context.folders.get("n500pin03pout002k5")
        ));

        for (Folder folder : folders) {
            log.info("start parse " + folder.getTitle());
            List<Graph> graphs = parser.parseInDirectory(folder.getFilePath());
            new TaskChain(new DefaultTask(new ClassifierChecker(graphs, 5, 0.3), distances, 0.01))
                    .execute().draw("classifier (k=5, p=0.3) " + folder.getTitle());
            log.info("end " + folder.getTitle());
        }
    }

    public static void initEnvironment() {
        Context.PARALLEL = true;
        Context.SCALE = Scale.ATAN;
    }
}

