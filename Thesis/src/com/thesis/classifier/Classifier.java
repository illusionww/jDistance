package com.thesis.classifier;


import com.thesis.graph.SimpleNodeData;

import java.util.*;

public class Classifier {

    private ArrayList<SimpleNodeData> realData; //  name and cluster

    private Float[][] matrixWithWeights;


    Classifier (Float[][] matrixWithWeights, ArrayList<SimpleNodeData> realData){
        this.matrixWithWeights = matrixWithWeights;

        //Collections.sort(realData);
        this.realData = realData;
    }

    //p - процент известных данных, т.е. те которые не надо предсказывать, или же если p > 1 то количество вершин о которых мы знаем их принадлежность
    public ArrayList<SimpleNodeData> predictLabel(Integer k, Float p){

        HashMap<String, Integer> order = new HashMap<String, Integer>();

        for (int i = 0; i < realData.size(); ++i){
            order.put(realData.get(i).getName(), new Integer(i));
        }

        //выбираем вершины о которых будем знать их принадлежность к опеределенному классу
        ArrayList<SimpleNodeData> coloredNodes = new ArrayList<SimpleNodeData>();
        if (p <= 1){
            coloredNodes = choiceOfVertices(p);
        }
        if (p > 1){
            coloredNodes = choiceOfVertices(p/realData.size());
        }

        ArrayList<SimpleNodeData> predictedDatas = new ArrayList<SimpleNodeData>();
        for (int i = 0; i < realData.size(); ++i){
            boolean flag = false;
            for (int j = 0; j < coloredNodes.size(); ++j){
                if (realData.get(i).getName().equals(coloredNodes.get(j).getName())){
                    predictedDatas.add(realData.get(i));
                    flag = true;
                    break;
                }
            }
            if (!flag){
                ArrayList<DataForClassifier> weights= new ArrayList<DataForClassifier>();
                for (int q = 0; q < coloredNodes.size(); ++q){
                    if (matrixWithWeights[i][order.get(coloredNodes.get(q).getName())] != null){
                        weights.add(new DataForClassifier(coloredNodes.get(q).getName(), matrixWithWeights[i][order.get(coloredNodes.get(q).getName())], coloredNodes.get(q).getLabel()));
                    }
                    else weights.add(new DataForClassifier(coloredNodes.get(q).getName(), Float.MAX_VALUE, coloredNodes.get(q).getLabel()));
                }
                Collections.sort(weights);
                if (k > weights.size()){
                    predictedDatas.add(new SimpleNodeData(realData.get(i).getName(), predictLabel(weights)));
                }
                else{
                    predictedDatas.add(new SimpleNodeData(realData.get(i).getName(), predictLabel(weights.subList(0, k - 1))));
                }
            }
        }
        //выбираем k ближайших о которых мы изначально знали информации о их принадлежности к определенному классу, ищем из этих k тот класс который встречается чаще всего
        //если будет случай что таких классов несколько выбираем тот у которого есть представитель ближе всего находящийся к предсказываемому


        return predictedDatas;
    }

    private String predictLabel(List<DataForClassifier> weights){
        HashMap<String, Integer> countLabels = new HashMap<String, Integer>();
        for (DataForClassifier dataForClassifier : weights){
            if (countLabels.containsKey(dataForClassifier.getLabel())){
                countLabels.put(dataForClassifier.getLabel(), countLabels.get(dataForClassifier.getLabel()) + 1);
            }
            else {
                countLabels.put(dataForClassifier.getLabel(), 1);
            }
        }
        Integer currentCount = 1;
        String label = weights.get(0).getLabel();
        for (Map.Entry<String, Integer> map : countLabels.entrySet()){
            if (map.getValue() > currentCount){
                currentCount = map.getValue();
                label = map.getKey();
            }
        }
        return label;
    }

    private ArrayList<SimpleNodeData> choiceOfVertices(Float p){  //независимо от размеров класстеров выбираем из каждого одинаковое количество
        ArrayList<SimpleNodeData> sortedRealDatas = realData;
        Collections.sort(sortedRealDatas);
        String label = sortedRealDatas.get(0).getLabel();
        ArrayList<SimpleNodeData> result = new ArrayList<SimpleNodeData>();
        ArrayList<Integer> endLabel = new ArrayList<Integer>();
            for (int i = 0; i < sortedRealDatas.size(); ++i){
                if (!label.equals(sortedRealDatas.get(i).getLabel())){
                    label = sortedRealDatas.get(i).getLabel();
                    endLabel.add(new Integer(i - 1));
                    }
                }
        endLabel.add(new Integer(sortedRealDatas.size()));
            for (int i = 0; i < endLabel.size() - 1; ++i) {
            if (i > 0) {
                for (int k = endLabel.get(i - 1); k < endLabel.get(i - 1) + (endLabel.get(i) - endLabel.get(i - 1)) * p; ++k) {   //TODO будет лучше если брать с каждого класса одинаковое количество элементов
                    result.add(sortedRealDatas.get(k + 1));   //TODO брать элементы рандомно, а не по порядку
                }
            }
            else{
                for (int k = 0; k <= endLabel.get(i) * p; ++k) {
                    result.add(sortedRealDatas.get(k));


                    }
                }
            }
        if (endLabel.size() > 1){
            for (int k = endLabel.get(endLabel.size() - 2) + 1; k < sortedRealDatas.size(); ++k){
                result.add(sortedRealDatas.get(k));
            }
        }
        return result;
    }

    private class DataForClassifier implements Comparable<DataForClassifier>{  //TODO appropriate class name
        String name;
        String label;
        Float value;

        DataForClassifier(String name, Float value, String label){
            this.name = name;
            this.value = value;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(DataForClassifier o) {
            return this.value.compareTo(o.getValue());
        }
    }

}
