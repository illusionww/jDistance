package com.jdistance.impl.workflow.task.competition;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.impl.workflow.gridsearch.clusterer.MinSpanningTreeGridSearch;

import java.util.List;

public class ClustererCompetitionTask extends CompetitionTask {
    public ClustererCompetitionTask(List<CompetitionDTO> competitionDTOs, GraphBundle forLearning, GraphBundle forCompetitions, Integer pointsCount, String fileName) {
        super(competitionDTOs, forLearning, forCompetitions, pointsCount, fileName);
    }

    @Override
    protected GridSearch getChecker(GraphBundle graphs, CompetitionDTO dto) {
        return new MinSpanningTreeGridSearch(graphs, dto.k);
    }
}
