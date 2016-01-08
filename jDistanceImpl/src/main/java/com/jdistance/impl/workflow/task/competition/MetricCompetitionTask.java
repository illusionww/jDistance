package com.jdistance.impl.workflow.task.competition;

import com.jdistance.graph.Graph;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.checker.MetricChecker;
import com.jdistance.impl.workflow.util.StandardizeHelper;
import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetricCompetitionTask extends CompetitionTask {
    public MetricCompetitionTask(List<CompetitionDTO> competitionDTOs, GraphBundle forLearning, GraphBundle forCompetitions, Integer pointsCount, String fileName) {
        super(competitionDTOs, forLearning, forCompetitions, pointsCount, fileName);
    }

    @Override
    protected Checker getChecker(GraphBundle graphs, CompetitionDTO dto) {
        return new MetricChecker(graphs, dto.k);
    }

    @Override
    protected void competitions() {
        super.competitions();
        additionalInformation();
    }

    private void additionalInformation() {
        Graph graph = forCompetitions.getGraphs().get(0);
        competitionDTOs.stream().forEach(dto -> {
            DenseMatrix A = new DenseMatrix(graph.getSparseMatrix());
            Double parameter = dto.distance.getScale().calc(A, dto.pLearn.getKey());
            DenseMatrix D = dto.distance.getD(A, parameter);
            D = StandardizeHelper.standardize(D);

            ArrayList<Double> same = new ArrayList<>(); //для одинаковых кластеров
            ArrayList<Double> different = new ArrayList<>(); //для различных

            for (int c = 0; c < D.cols; c++) {
                for (int r = c + 1; r < D.rows; r++) {
                    if (graph.getNodeData().get(c).getLabel().equals(graph.getNodeData().get(r).getLabel())) {
                        same.add(D.get(c, r));
                    } else {
                        different.add(D.get(c, r));
                    }
                }
            }

            dto.additionalInfo.put("sameMax", Collections.max(same));
            dto.additionalInfo.put("sameMin", Collections.min(same));
            dto.additionalInfo.put("sameAvg", same.stream().mapToDouble(Double::doubleValue).sum() / same.size());
            dto.additionalInfo.put("sameVariance", StandardizeHelper.getDeviation(same));

            dto.additionalInfo.put("diffMax", Collections.max(different));
            dto.additionalInfo.put("diffMin", Collections.min(different));
            dto.additionalInfo.put("diffAvg", different.stream().mapToDouble(Double::doubleValue).sum() / different.size());
            dto.additionalInfo.put("diffVariance", StandardizeHelper.getDeviation(different));
        });
    }


}
