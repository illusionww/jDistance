package com.thesis.workflow.competition;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.metric.Distance;
import com.thesis.metric.DistanceClass;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.task.ClassifierBestParamTask;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class ClassifierCompetitionTask {
    private static final Logger log = LoggerFactory.getLogger(ClassifierBestParamTask.class);

    private ClassifierChecker checker;
    private Integer pointsCount;
    private Integer n;
    private Double pOut;
    private Double pIn;
    private Integer countForCompetition;
    private Integer k;
    private Double p;
    private String fileName;

    public ClassifierCompetitionTask(ClassifierChecker checker, Integer pointsCount, Integer n, Double pIn, Double pOut, Integer countForCompetition, Integer k, Double p, String fileName) {
        this.checker = checker;
        this.pointsCount = pointsCount;
        this.n = n;
        this.pOut = pOut;
        this.pIn = pIn;
        this.countForCompetition = countForCompetition;
        this.k = k;
        this.p = p;
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
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            try {
                for (Map.Entry<Task, Map<Double, Double>> taskMap : taskChain.getData().entrySet()) {
                    Double result = new Double(0);
                    for (int i = 0; i < countForCompetition; ++i) {
                        GraphBundle graphBundle = new GraphBundle(n, pIn, pOut, k, 1);
                        ClassifierChecker classifierCheckerForTest = new ClassifierChecker(graphBundle, k, p);
                        if (results.containsKey(taskMap.getKey().getDistance())) {
                            results.get(taskMap.getKey().getDistance()).add(classifierCheckerForTest.test(taskMap.getKey().getDistance(), taskMap.getKey().getBestResult().getKey()));
                        } else {
                            LinkedList<Double> linkedList1 = new LinkedList<>();
                            linkedList1.add(classifierCheckerForTest.test(taskMap.getKey().getDistance(), taskMap.getKey().getBestResult().getKey()));
                            results.put(taskMap.getKey().getDistance(), linkedList1);
                        }
                        result += classifierCheckerForTest.test(taskMap.getKey().getDistance(), taskMap.getKey().getBestResult().getKey());
                    }
                    output.add(taskMap.getKey().getDistance().getName() + " " + taskMap.getKey().getBestResult().getValue() + " " + taskMap.getKey().getBestResult().getKey() + " " + result);
                    bestResults.add(result);
                }

                Double maxResult;
                Integer index;
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
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}