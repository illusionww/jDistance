package com.jdistance.workflow.task.competition;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.workflow.Context;
import com.jdistance.workflow.checker.Checker;
import com.jdistance.workflow.checker.ClassifierChecker;
import com.jdistance.workflow.task.ClassifierBestParamTask;
import com.jdistance.workflow.task.DefaultTask;
import com.jdistance.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClassifierCompetitionTask {
    private static final Logger log = LoggerFactory.getLogger(ClassifierBestParamTask.class);

    private List<CompetitionDTO> competitionDTOs;
    private GraphBundle forLearning;
    private GraphBundle forCompetitions;
    private Integer pointsCount;
    private String fileName;

    public ClassifierCompetitionTask(List<CompetitionDTO> competitionDTOs, GraphBundle forLearning, GraphBundle forCompetitions, Integer pointsCount, String fileName) {
        this.competitionDTOs = competitionDTOs;
        this.forLearning = forLearning;
        this.forCompetitions = forCompetitions;
        this.pointsCount = pointsCount;
        this.fileName = fileName;
    }

    public ClassifierCompetitionTask execute() {
        log.info("START: " + fileName);
        learning();
        log.info("competitions...");
        competitions();

        return this;
    }

    private void learning() {
        competitionDTOs.stream().forEach(dto -> {
            log.info("{}...", dto.distance.getName());
            Checker checker = new ClassifierChecker(forLearning, dto.k, 0.3, dto.x);
            Task task = new DefaultTask(checker, dto.distance, pointsCount);
            dto.pLearn = task.execute().getBestResult();
        });
    }

    private void competitions() {
        competitionDTOs.stream().forEach(dto -> dto.score = 0);
        forCompetitions.getGraphs().stream().forEach(graph -> {
            competitionDTOs.stream().forEach(dto -> {
                GraphBundle bundle = forCompetitions.clone();
                bundle.setGraphs(Collections.singletonList(graph));
                Checker checker = new ClassifierChecker(bundle, dto.k, 0.3);
                dto.tempResult = checker.test(dto.distance, dto.pLearn.getValue());
            });
            Collections.sort(competitionDTOs, Comparator.comparingDouble(i -> i.tempResult));
            for (int i = 0; i < competitionDTOs.size(); i++) {
                competitionDTOs.get(i).score += i;
            }
        });
    }

    public ClassifierCompetitionTask write() {
        String path = Context.getInstance().COMPETITION_FOLDER + File.separator + fileName + ".txt";
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path))) {
            outputWriter.write("Name\tlearnedP\tlearnedQuality\tScore");
            competitionDTOs.stream().forEach(dto -> {
                try {
                    outputWriter.write(dto.distance.getName() + "\t" + dto.pLearn.getKey() + "\t" + dto.pLearn.getValue() + "\t" + dto.score);
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
}