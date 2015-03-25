package com.thesis;

import com.thesis.adapter.generator.DCRGeneratorAdapter;
import com.thesis.adapter.GraphValidator;
import com.thesis.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
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
        Context.getInstance().init("C:\\cygwin64\\bin\\gnuplot.exe", "graphs", "C:\\thesis", true, Scale.ATAN);

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

        DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
        List<Graph> graphs = generator.generateList(10, 100, 0.3, 0.1, 5);
        new TaskChain(new DefaultTask(new ClassifierChecker(graphs, 5, 0.3), distances, 0.003))
                .execute().draw("classifier (k=5, p=0.3) n=100, p_in=0.3, p_out=0.1");
    }
}

