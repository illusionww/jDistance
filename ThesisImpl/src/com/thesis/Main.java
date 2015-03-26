package com.thesis;

import com.panayotis.gnuplot.utils.FileUtils;
import com.thesis.adapter.generator.DCRGeneratorAdapter;
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
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();

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
        List<Graph> graphs = generator.generateList(50, 100, 0.3, 0.1, 5);
        new TaskChain(new DefaultTask(new ClassifierChecker(graphs, 3, 0.3), distances, 0.003))
                .execute().draw("classifier(k=3, p=0.3) count=50, n=100, p_in=0.3, p_out=0.1 exp");
    }

    private static void initContext() {
        String gnuplotPath = "c:\\cygwin64\\bin\\gnuplot.exe";
        String imgFolder = "pictures";

        String OS = System.getProperty("os.name");
        log.info("OS: {}", OS);
        gnuplotPath = OS.startsWith("Windows") && isExist(gnuplotPath) ? gnuplotPath :FileUtils.findPathExec();

        if (!isExist(imgFolder) && !new File(imgFolder).mkdirs()) {
            throw new RuntimeException("Folder " + new File(imgFolder).getAbsolutePath() + " is not exist");
        }

        if (!isExist(gnuplotPath)) {
            throw new RuntimeException("Gnuplot not found");
        }

        Context.getInstance().init(gnuplotPath, imgFolder, true, Scale.EXP);
    }

    private static boolean isExist(String path) {
        return path != null && new File(path).exists();
    }
}

