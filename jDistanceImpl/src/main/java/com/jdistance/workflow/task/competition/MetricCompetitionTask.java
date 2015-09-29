package com.jdistance.workflow.task.competition;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.graph.Graph;
import com.jdistance.workflow.checker.Checker;
import com.jdistance.workflow.checker.MetricChecker;
import com.jdistance.workflow.util.TaskHelper;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.ArrayUtils;

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
            for (int i = 0; i < D.cols; i++) {
                D.set(i, i, 0); // обнулим диагональ
            }

            // найдем среднеквадратичное отклонение и поделим на него
            Double deviation = getDeviation(D);
            D = D.div(deviation);

            ArrayList<Double> same = new ArrayList<>(); //для одинаковых кластеров
            ArrayList<Double> different = new ArrayList<>(); //для различных

            for (int c = 0; c < D.cols; c++) {
                for (int r = c + 1; r < D.rows; r++) {
                    if (graph.getSimpleNodeData().get(c).getLabel().equals(graph.getSimpleNodeData().get(r).getLabel())) {
                        same.add(D.get(c, r));
                    } else {
                        different.add(D.get(c, r));
                    }
                }
            }

            dto.additionalInfo.put("sameMax", Collections.max(same));
            dto.additionalInfo.put("sameMin", Collections.min(same));
            dto.additionalInfo.put("sameAvg", same.stream().mapToDouble(Double::doubleValue).sum() / same.size());
            dto.additionalInfo.put("sameVariance", TaskHelper.deviation(ArrayUtils.toPrimitive(same.toArray(new Double[same.size()]))));

            dto.additionalInfo.put("diffMax", Collections.max(different));
            dto.additionalInfo.put("diffMin", Collections.min(different));
            dto.additionalInfo.put("diffAvg", different.stream().mapToDouble(Double::doubleValue).sum() / different.size());
            dto.additionalInfo.put("diffVariance", TaskHelper.deviation(ArrayUtils.toPrimitive(different.toArray(new Double[same.size()]))));
        });
    }

    private double getDeviation(DenseMatrix D) {
        Double deviationRaw = 0d;
        for (int c = 0; c < D.cols; c++) {
            for (int r = c + 1; r < D.rows; r++) {
                deviationRaw += D.get(c, r) * D.get(c, r);
            }
        }
        return Math.sqrt(deviationRaw / (D.rows * (D.cols - 1) / 2));
    }
}
