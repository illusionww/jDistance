package com.jdistance.impl.workflow.task.competition;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.impl.workflow.gridsearch.classifier.KNearestNeighborsGridSearch;

import java.util.List;

public class ClassifierCompetitionTask extends CompetitionTask {
    public ClassifierCompetitionTask(List<CompetitionDTO> competitionDTOs, GraphBundle forLearning, GraphBundle forCompetitions, Integer pointsCount, String fileName) {
        super(competitionDTOs, forLearning, forCompetitions, pointsCount, fileName);
    }

    @Override
    protected GridSearch getChecker(GraphBundle graphs, CompetitionDTO dto) {
        return new KNearestNeighborsGridSearch(graphs, dto.k, 0.3, dto.x);
    }
}