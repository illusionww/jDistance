package com.jdistance.impl.workflow.task.competition;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.impl.workflow.context.ContextProvider;
import com.jdistance.impl.workflow.task.ClassifierBestParamTask;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public abstract class CompetitionTask {
    private static final Logger log = LoggerFactory.getLogger(ClassifierBestParamTask.class);

    protected List<CompetitionDTO> competitionDTOs;
    protected GraphBundle forLearning;
    protected GraphBundle forCompetitions;
    protected Integer pointsCount;
    protected String fileName;

    public CompetitionTask(List<CompetitionDTO> competitionDTOs, GraphBundle forLearning, GraphBundle forCompetitions, Integer pointsCount, String fileName) {
        this.competitionDTOs = competitionDTOs;
        this.forLearning = forLearning;
        this.forCompetitions = forCompetitions;
        this.pointsCount = pointsCount;
        this.fileName = fileName;
    }

    public CompetitionTask execute() {
        log.info("START CompetitionTask");
        log.info("LEARNING");
        learning();
        log.info("COMPETITIONS");
        competitions();

        return this;
    }

    protected void learning() {
        Stream<CompetitionDTO> stream = ContextProvider.getInstance().getContext().getParallel() ? competitionDTOs.parallelStream() : competitionDTOs.stream();
        stream.forEach(dto -> {
            log.info("{}...", dto.metricWrapper.getName());
            GridSearch gridSearch = getChecker(forLearning, dto);
            Task task = new DefaultTask(gridSearch, dto.metricWrapper, pointsCount);
            dto.pLearn = task.execute().getMaxResult();
        });
    }

    protected void competitions() {
        competitionDTOs.stream().forEach(dto -> dto.score = 0);
        forCompetitions.getGraphs().stream().forEach(graph -> {
            competitionDTOs.stream().forEach(dto -> {
                GraphBundle bundle = forCompetitions.clone();
                bundle.setGraphs(Collections.singletonList(graph));
                GridSearch gridSearch = getChecker(bundle, dto);
                dto.tempResult = gridSearch.validate(dto.metricWrapper, dto.pLearn.getKey());
            });
            Collections.sort(competitionDTOs, Comparator.comparingDouble(i -> i.tempResult));
            for (int i = 0; i < competitionDTOs.size(); i++) {
                competitionDTOs.get(i).score += i;
            }
        });
    }

    public CompetitionTask write() {
        String path = ContextProvider.getInstance().getContext().getCompetitionFolder() + File.separator + fileName + ".txt";
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path))) {
            outputWriter.write("Name\tlearnedP\tlearnedQuality\tScore\tAdditionalInfo");
            outputWriter.newLine();
            competitionDTOs.stream().sorted((o1, o2) -> o2.score - o1.score).forEach(dto -> {
                try {
                    outputWriter.write(dto.metricWrapper.getName() + "\t" + dto.pLearn.getKey() + "\t" + dto.pLearn.getValue() + "\t" + dto.score + "\t" + Arrays.toString(dto.additionalInfo.entrySet().toArray()));
                    outputWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.err.println("IOException while write results");
        }
        return this;
    }

    protected abstract GridSearch getChecker(GraphBundle graphs, CompetitionDTO dto);
}
