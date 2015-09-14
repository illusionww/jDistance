package com.thesis.workflow.competition;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.clusterer.Clusterer;
import com.thesis.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.metric.DistanceClass;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClustererChecker;
import com.thesis.workflow.checker.MetricChecker;
import com.thesis.workflow.task.ClassifierBestParamTask;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import jeigen.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by vits on 27.04.2015.
 */
public class ClustererCompetitionTask {
    private static final Logger log = LoggerFactory.getLogger(ClassifierBestParamTask.class);

    private MetricChecker checker;
    private Integer pointsCount;
    private Integer n;
    private Double pOut;
    private Double pIn;
    private Integer countForCompetition;
    private Integer k;
    private String fileName;

    public ClustererCompetitionTask(MetricChecker checker, Integer pointsCount, Integer n, Double pIn, Double pOut, Integer countForCompetition, Integer k, String fileName) {
        this.checker = checker;
        this.pointsCount = pointsCount;
        this.n = n;
        this.pOut = pOut;
        this.pIn = pIn;
        this.countForCompetition = countForCompetition;
        this.k = k;
        this.fileName = fileName;
    }

    public boolean execute() {
        List<DistanceClass> distances = Arrays.asList(
                DistanceClass.COMM_D,
                DistanceClass.LOG_COMM_D,
                DistanceClass.SP_CT,
                DistanceClass.FREE_ENERGY,
                DistanceClass.WALK,
                DistanceClass.LOG_FOREST,
                DistanceClass.FOREST,
                DistanceClass.PLAIN_WALK
        );
        HashMap<Distance, LinkedList<Double>> results = new HashMap<>();
        ArrayList<Double> bestResults = new ArrayList<>();
        ArrayList<String> output = new ArrayList<>();

        LinkedList<Task> tasks = new LinkedList<>();
        for (int i = 0; i < distances.size(); ++i) {
            tasks.add(new DefaultTask(checker, distances.get(i).getInstance(distances.get(i).name()), pointsCount));
        }

        TaskChain taskChain = new TaskChain("", tasks).execute();

        File file = new File(Context.getInstance().COMPETITION_FOLDER + File.separator + fileName + ".txt");
        File file1 = new File(Context.getInstance().COMPETITION_FOLDER + File.separator + fileName + "addition information" + ".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!file1.exists()) {
                file1.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            PrintWriter out1 = new PrintWriter(file1.getAbsoluteFile());

            try {
                for (Map.Entry<Task, Map<Double, Double>> taskMap : taskChain.getData().entrySet()) {
                    Double result = new Double(0);
                    for (int i = 0; i < countForCompetition; ++i) {
                        GraphBundle graphBundle = new GraphBundle(n, pIn, pOut, k, 1);
                        MetricChecker clustererCheckerForTest = new MetricChecker(graphBundle, k);
                        if (results.containsKey(taskMap.getKey().getDistance())) {
                            results.get(taskMap.getKey().getDistance()).add(clustererCheckerForTest.test(taskMap.getKey().getDistance(), taskMap.getKey().getBestResult().getKey()));
                        } else {
                            LinkedList<Double> linkedList1 = new LinkedList<>();
                            linkedList1.add(clustererCheckerForTest.test(taskMap.getKey().getDistance(), taskMap.getKey().getBestResult().getKey()));
                            results.put(taskMap.getKey().getDistance(), linkedList1);
                        }
                        result += clustererCheckerForTest.test(taskMap.getKey().getDistance(), taskMap.getKey().getBestResult().getKey());
                    }
                    output.add(taskMap.getKey().getDistance().getName() + " " + taskMap.getKey().getBestResult().getValue() + " " + taskMap.getKey().getBestResult().getKey() + " " + result);

                    additionInformation(taskMap.getKey().getDistance(), taskMap.getKey().getBestResult().getKey(), out1);
                    bestResults.add(result);
                }

                Integer index;
                Double maxResult;
                for (int j = 0; j < bestResults.size(); ++j) {
                    index = 0;
                    maxResult = 0d;
                    for (int i = 0; i < bestResults.size(); ++i) {
                        if (maxResult < bestResults.get(i)) {
                            maxResult = bestResults.get(i);
                            index = i;
                        }
                    }
                    bestResults.set(index, -1d);
                    out.println(output.get(index));
                }

                Integer rate;
                LinkedList<Integer> score = new LinkedList<>();
                LinkedList<Distance> distanceLinkedList = new LinkedList<>();
                distanceLinkedList.addAll(results.keySet());

                for (int i = 0; i < results.size(); ++i) {
                    score.add(0);
                }

                for (int i = 0; i < countForCompetition; ++i) {
                    rate = 8;
                    for (int j = 0; j < results.size(); ++j) {
                        index = -1;
                        maxResult = 0d;
                        for (int s = 0; s < distanceLinkedList.size(); ++s) {
                            if (maxResult < results.get(distanceLinkedList.get(s)).get(i)) {
                                maxResult = results.get(distanceLinkedList.get(s)).get(i);
                                index = s;
                            }
                        }
                        if (!index.equals(-1)) {
                            results.get(distanceLinkedList.get(index)).set(i, -1d);
                            score.set(index, score.get(index) + rate);
                            rate--;
                        }
                    }
                }
                for (int j = 0; j < score.size(); ++j) {
                    Integer max_score = 0;
                    index = 0;
                    for (int i = 0; i < score.size(); ++i) {
                        if (max_score < score.get(i)) {
                            max_score = score.get(i);
                            index = i;
                        }
                    }
                    out.println(distanceLinkedList.get(index) + " " + score.get(index));
                    score.set(index, 0);
                }
            } finally {
                out.close();
                out1.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
    private void additionInformation(Distance distance, Double key, PrintWriter out) throws FileNotFoundException {
            double sum = 0d;

            GraphBundle graphs = new GraphBundle(200, 0.4, 0.1, 5, 1);
            Graph graph = graphs.getGraphs().get(0);
            DenseMatrix A = graph.getSparseMatrix();

            Double parameter = distance.getScale().calc(A, key);
            DenseMatrix D = distance.getD(A, parameter);


        for (int i = 0; i < D.cols; i++){
            for (int j = i + 1; j < D.rows; j++){
                sum += D.get(i, j);
            }
        }

        double average = sum / (D.rows*(D.cols - 1) / 2);;

        //среднеквадратичное отклонение
        double deviation = 0d;
        for (int i = 0; i < D.cols; i++){
            for (int j = i + 1; j < D.rows; j++){
                deviation += (D.get(i, j) - average) * (D.get(i, j) - average);
            }
        }

        //вычитаем среднее компонент и делим на среднеквадратичное отклонение
        if (D.rows != 0 && D.cols != 0 && deviation != 0){
            deviation = (float) Math.sqrt(deviation/ ((D.rows*(D.cols - 1) / 2) - 1));
            for (int i = 0; i < D.cols; i++){
                for (int j = 0; j < D.rows; j++){
                    if (i != j){    D.set(i, j,(D.get(i, j) - average)/deviation); }
                }
            }
        }

            //для одинаковых кластеров
            ArrayList<Double> first = new ArrayList<>();
            //для различных
            ArrayList<Double> second = new ArrayList<>();

            for (int i = 0; i < D.cols; i++){
                for (int j = i + 1; j < D.rows; j++){
                    //sum += D.get(i, j);
                    if (i != j) {
                        if (graph.getSimpleNodeData().get(i).getLabel().equals(graph.getSimpleNodeData().get(j).getLabel())) {
                            first.add(D.get(i, j));
                        } else {
                            second.add(D.get(i, j));
                        }
                    }
                }
            }

            Double firstAverage = 0d;
            Double secondAverage = 0d;
            Double secondMin = Double.MAX_VALUE;
            Double firstMin = Double.MAX_VALUE;
            Double firstMax = Double.MIN_VALUE;
            Double secondMax = Double.MIN_VALUE;
            for (int i = 0; i < first.size(); ++i){
                if (firstMin > first.get(i)){
                    firstMin = first.get(i);
                }
                if (firstMax < first.get(i)){
                    firstMax = first.get(i);
                }
                firstAverage += first.get(i);
            }
            for (int i = 0; i < second.size(); ++i){
                if (secondMin > second.get(i)){
                    secondMin = second.get(i);
                }
                if (secondMax < second.get(i)){
                    secondMax = second.get(i);
                }
                secondAverage += second.get(i);
            }
            //средние по подвекторам
            firstAverage = firstAverage / first.size();
            secondAverage = secondAverage / second.size();

            //считаем дисперсию
            Double firstVariance = 0d;
            Double secondVariance = 0d;
            for (int i = 0; i < first.size(); ++i){
                firstVariance += (first.get(i) - firstAverage) * (first.get(i) - firstAverage);
            }
            firstVariance = Math.sqrt(firstVariance /(first.size() - 1));
            for (int i = 0; i < second.size(); ++i){
                secondVariance += (second.get(i) - secondAverage) * (second.get(i) - secondAverage);
            }
            secondVariance = Math.sqrt(secondVariance /(second.size() - 1));

            //out.println(distance.getName() + " firstAverage = " + firstAverage + " firstMin = " + firstMin + " firstMax = " + firstMax + " firstVariance = " + firstVariance);
            //out.println(distance.getName() + " secondAverage = " + secondAverage + " secondMin = " + secondMin + " secondMax = " + secondMax + " secondVariance = " + secondVariance);
        if ("COMM_D".equals(distance.getName())){
            for (int i = 0; i < D.cols; ++i) {
                for (int j = 0; j < D.rows; ++j) {
                    out.print(D.get(i, j) + " ");
                }
                out.println();
            }
        }
    }
}
