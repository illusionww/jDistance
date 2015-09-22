package com.jdistance.workflow.task.competition;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.graph.Graph;
import com.jdistance.workflow.checker.Checker;
import com.jdistance.workflow.checker.MetricChecker;
import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.Arrays;
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

            // найдем и вычтем среднее
            Double sum = Arrays.stream(D.getValues()).sum();
            Double average = sum / (D.rows * (D.cols - 1));
            D = D.sub(average);
            for (int i = 0; i < A.cols; i++) {
                D.set(i, i, 0); // обнулим диагональ
            }

            // найдем среднеквадратичное отклонение и поделим на него
            Double deviationRaw = 0d;
            for (int i = 0; i < D.cols; i++) {
                for (int j = i + 1; j < D.rows; j++) {
                    deviationRaw += D.get(i, j) * D.get(i, j);
                }
            }
            Double deviation = Math.sqrt(deviationRaw / (D.rows * (D.cols - 1) / 2));
            D = D.div(deviation);

            ArrayList<Double> same = new ArrayList<>(); //для одинаковых кластеров
            ArrayList<Double> different = new ArrayList<>(); //для различных

            for (int i = 0; i < D.cols; i++) {
                for (int j = i + 1; j < D.rows; j++) {
                    if (i != j) {
                        if (graph.getSimpleNodeData().get(i).getLabel().equals(graph.getSimpleNodeData().get(j).getLabel())) {
                            same.add(D.get(i, j));
                        } else {
                            different.add(D.get(i, j));
                        }
                    }
                }
            }

            dto.additionalInfo.put("sameMax", Collections.max(same));
            dto.additionalInfo.put("sameMin", Collections.min(same));
            dto.additionalInfo.put("sameAvg", same.stream().mapToDouble(Double::doubleValue).sum() / same.size());

            dto.additionalInfo.put("diffMax", Collections.max(different));
            dto.additionalInfo.put("diffMin", Collections.min(different));
            dto.additionalInfo.put("diffAvg", different.stream().mapToDouble(Double::doubleValue).sum() / same.size());
        });
    }
}
