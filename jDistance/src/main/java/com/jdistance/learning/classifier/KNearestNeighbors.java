package com.jdistance.learning.classifier;

import com.jdistance.graph.Node;
import com.jdistance.utils.MatrixUtils;
import jeigen.DenseMatrix;

import java.util.*;

public class KNearestNeighbors {
    private ArrayList<Node> realData; //  id and cluster
    private double[][] matrixWithWeights;
    private int countColoredNodes;


    public KNearestNeighbors(DenseMatrix matrixWithWeights, List<Node> realData) {
        this.matrixWithWeights = MatrixUtils.toArray2(matrixWithWeights);
        this.realData = new ArrayList<>(realData);
        countColoredNodes = 0;
    }

    //p - процент известных данных, т.е. те которые не надо предсказывать, или же если p > 1 то количество вершин о которых мы знаем их принадлежность
    public ArrayList<Node> predictLabel(Integer k, Double p, Double x) {
        HashMap<Integer, Integer> order = new HashMap<>();

        for (int i = 0; i < realData.size(); ++i) {
            order.put(realData.get(i).getId(), i);
        }

        //выбираем вершины о которых будем знать их принадлежность к опеределенному классу
        ArrayList<Node> coloredNodes = new ArrayList<>();
        if (p <= 1) {
            coloredNodes = choiceOfVertices(p);
        }
        if (p > 1) {
            coloredNodes = choiceOfVertices(p / realData.size());
        }

        ArrayList<Node> predictedDatas = new ArrayList<>();
        for (int i = 0; i < realData.size(); ++i) {
            boolean flag = false;
            for (Node coloredNode : coloredNodes) {
                if (realData.get(i).getId().equals(coloredNode.getId())) {
                    predictedDatas.add(realData.get(i));
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                ArrayList<DataForClassifier> weights = new ArrayList<>();
                for (Node coloredNode : coloredNodes) {
                    if (matrixWithWeights[i][order.get(coloredNode.getId())] != 0) {
                        weights.add(new DataForClassifier(coloredNode.getId(), matrixWithWeights[i][order.get(coloredNode.getId())], coloredNode.getLabel()));
                    } else
                        weights.add(new DataForClassifier(coloredNode.getId(), Double.MAX_VALUE, coloredNode.getLabel()));
                }
                Collections.sort(weights);
                if (k > weights.size()) {
                    predictedDatas.add(new Node(realData.get(i).getId(), predictLabel(weights, x)));
                } else {
                    predictedDatas.add(new Node(realData.get(i).getId(), predictLabel(weights.subList(0, k), x)));
                }
            }
        }
        //выбираем k ближайших о которых мы изначально знали информации о их принадлежности к определенному классу, ищем из этих k тот класс который встречается чаще всего
        //если будет случай что таких классов несколько выбираем тот у которого есть представитель ближе всего находящийся к предсказываемому

        return predictedDatas;
    }

    private String predictLabel(List<DataForClassifier> weights, Double x) {
        HashMap<String, Double> countLabels = new HashMap<>();
        ListIterator iteratorWeights = weights.listIterator();
        int i = 0;
        while (iteratorWeights.hasNext()) {
            DataForClassifier dataForClassifier = (DataForClassifier) iteratorWeights.next();
            if (countLabels.containsKey(dataForClassifier.getLabel())) {
                countLabels.put(dataForClassifier.getLabel(), countLabels.get(dataForClassifier.getLabel()) + Math.pow((weights.size() - i), x));
            } else {
                countLabels.put(dataForClassifier.getLabel(), Math.pow((weights.size() - i), x));
            }
            i++;
        }
        Double currentCount = 1.0;
        String label = weights.get(0).getLabel();
        for (Map.Entry<String, Double> map : countLabels.entrySet()) {
            if (map.getValue() > currentCount) {
                currentCount = map.getValue();
                label = map.getKey();
            }
        }
        return label;
    }

    private ArrayList<Node> choiceOfVertices(Double p) {  //независимо от размеров класстеров выбираем из каждого одинаковое количество
        ArrayList<Node> sortedRealDatas = realData;
        Collections.sort(sortedRealDatas);
        String label = sortedRealDatas.get(0).getLabel();
        ArrayList<Node> result = new ArrayList<>();
        ArrayList<Integer> endLabel = new ArrayList<>();
        for (int i = 0; i < sortedRealDatas.size(); ++i) {
            if (!label.equals(sortedRealDatas.get(i).getLabel())) {
                label = sortedRealDatas.get(i).getLabel();
                endLabel.add(i - 1);
            }
        }
        endLabel.add(sortedRealDatas.size());
        for (int i = 0; i < endLabel.size() - 1; ++i) {
            if (i > 0) {
                countColoredNodes += (endLabel.get(i - 1) + (endLabel.get(i) - endLabel.get(i - 1)) * p) - endLabel.get(i - 1);
                for (int k = endLabel.get(i - 1); k < endLabel.get(i - 1) + (endLabel.get(i) - endLabel.get(i - 1)) * p; ++k) {   //TODO будет лучше если брать с каждого класса одинаковое количество элементов
                    result.add(sortedRealDatas.get(k + 1));   //TODO брать элементы рандомно, а не по порядку
                }
            } else {
                countColoredNodes += endLabel.get(i) * p;
                for (int k = 0; k <= endLabel.get(i) * p; ++k) {
                    result.add(sortedRealDatas.get(k));
                }
            }
        }
        if (endLabel.size() > 1) {
            for (int k = endLabel.get(endLabel.size() - 2) + 1; k < sortedRealDatas.size(); ++k) {
                result.add(sortedRealDatas.get(k));
            }
        }
        return result;
    }

    public int getCountColoredNodes() {
        return countColoredNodes;
    }

    private class DataForClassifier implements Comparable<DataForClassifier> {
        Integer id;
        Double value;
        String label;

        DataForClassifier(Integer id, Double value, String label) {
            this.id = id;
            this.value = value;
            this.label = label;
        }

        String getLabel() {
            return label;
        }

        Double getValue() {
            return value;
        }

        Integer getId() {
            return id;
        }

        void setId(Integer id) {
            this.id = id;
        }

        @Override
        public int compareTo(DataForClassifier o) {
            return this.value.compareTo(o.getValue());
        }
    }

}
