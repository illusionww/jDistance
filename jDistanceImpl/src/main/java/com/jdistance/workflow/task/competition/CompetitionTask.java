package com.jdistance.workflow.task.competition;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.workflow.Context;
import com.jdistance.workflow.checker.Checker;
import com.jdistance.workflow.task.ClassifierBestParamTask;
import com.jdistance.workflow.task.DefaultTask;
import com.jdistance.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
        log.info("learning");
        learning();
        log.info("competitions");
        competitions();

        return this;
    }

    protected void learning() {
        competitionDTOs.stream().forEach(dto -> {
            log.info("{}...", dto.distance.getName());
            Checker checker = getChecker(forLearning, dto);
            Task task = new DefaultTask(checker, dto.distance, pointsCount);
            dto.pLearn = task.execute().getBestResult();
        });
    }

    protected void competitions() {
        competitionDTOs.stream().forEach(dto -> dto.score = 0);
        forCompetitions.getGraphs().stream().forEach(graph -> {
            competitionDTOs.stream().forEach(dto -> {
                GraphBundle bundle = forCompetitions.clone();
                bundle.setGraphs(Collections.singletonList(graph));
                Checker checker = getChecker(bundle, dto);
                dto.tempResult = checker.test(dto.distance, dto.pLearn.getKey());
            });
            Collections.sort(competitionDTOs, Comparator.comparingDouble(i -> i.tempResult));
            for (int i = 0; i < competitionDTOs.size(); i++) {
                competitionDTOs.get(i).score += i;
            }
        });
    }

    public CompetitionTask write() {
        String path = Context.getInstance().COMPETITION_FOLDER + File.separator + fileName + ".txt";
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path))) {
            outputWriter.write("Name\tlearnedP\tlearnedQuality\tScore\tAdditionalInfo");
            outputWriter.newLine();
            competitionDTOs.stream().sorted((o1, o2) -> o2.score - o1.score).forEach(dto -> {
                try {
                    outputWriter.write(dto.distance.getName() + "\t" + dto.pLearn.getKey() + "\t" + dto.pLearn.getValue() + "\t" + dto.score + "\t" + Arrays.toString(dto.additionalInfo.entrySet().toArray()));
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

    protected abstract Checker getChecker(GraphBundle graphs, CompetitionDTO dto);
}