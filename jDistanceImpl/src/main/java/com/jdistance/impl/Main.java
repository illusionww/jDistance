package com.jdistance.impl;

import com.graphgenerator.utils.GeneratorPropertiesDTO;
import com.graphgenerator.utils.GeneratorPropertiesParser;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.task.competition.CompetitionDTO;
import com.jdistance.impl.workflow.task.competition.MetricCompetitionTask;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        callMetricCompetitionTask("./dataForGenerator/defaultParameters.txt");
    }

    private static void initContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = "c:\\cygwin64\\bin\\gnuplot.exe";
        context.IMG_FOLDER = "pictures";
        new File("./pictures").mkdir();
        context.COMPETITION_FOLDER = "tournament";
        new File("./tournament").mkdir();
        context.PARALLEL = false;
    }

    private static void callMetricCompetitionTask(String pathToParameters) {
        GeneratorPropertiesDTO generatorPropertiesDTO = GeneratorPropertiesParser.parse(pathToParameters);
        int k = generatorPropertiesDTO.getSizeOfClusters().size();
        int graphCountForLearning = 5;
        int graphCountForCompetitions = 5;
        int pointsCount = 5;

        List<CompetitionDTO> competitionDTOs = Metric.getDefaultDistances().stream()
                .map(metric -> new CompetitionDTO(new MetricWrapper(metric), k)).collect(Collectors.toList());

        GraphBundle graphsForLearning = new GraphBundle(generatorPropertiesDTO, graphCountForLearning);
        GraphBundle graphsForCompetitions = new GraphBundle(generatorPropertiesDTO, graphCountForCompetitions);

        MetricCompetitionTask task = new MetricCompetitionTask(competitionDTOs, graphsForLearning, graphsForCompetitions, pointsCount, "test");
        task.execute().write();
    }
}

