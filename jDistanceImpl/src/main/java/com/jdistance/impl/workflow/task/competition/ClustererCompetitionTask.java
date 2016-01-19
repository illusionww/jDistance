package com.jdistance.impl.workflow.task.competition;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.checker.MinSpanningTreeChecker;

import java.util.List;

public class ClustererCompetitionTask extends CompetitionTask {
    public ClustererCompetitionTask(List<CompetitionDTO> competitionDTOs, GraphBundle forLearning, GraphBundle forCompetitions, Integer pointsCount, String fileName) {
        super(competitionDTOs, forLearning, forCompetitions, pointsCount, fileName);
    }

    @Override
    protected Checker getChecker(GraphBundle graphs, CompetitionDTO dto) {
        return new MinSpanningTreeChecker(graphs, dto.k);
    }
}
