package com.jdistance.impl.workflow.task.competition;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.checker.KNearestNeighborsChecker;

import java.util.List;

public class ClassifierCompetitionTask extends CompetitionTask {
    public ClassifierCompetitionTask(List<CompetitionDTO> competitionDTOs, GraphBundle forLearning, GraphBundle forCompetitions, Integer pointsCount, String fileName) {
        super(competitionDTOs, forLearning, forCompetitions, pointsCount, fileName);
    }

    @Override
    protected Checker getChecker(GraphBundle graphs, CompetitionDTO dto) {
        return new KNearestNeighborsChecker(graphs, dto.k, 0.3, dto.x);
    }
}