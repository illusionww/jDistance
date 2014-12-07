package com.thesis.graph;

import com.thesis.classifier.Classifier;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;


public class Main {
//парсинг формата graphml получение матрицы смежности
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        File file = new File("myRandomGraphn100k5pin0.3pout0.02.graphml");
        LinkedList<NodeData> linkedList = new LinkedList<NodeData>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        NodeList nodeList = doc.getElementsByTagName("node");
        for (int i = 0; i < nodeList.getLength(); ++i){
            NodeData nodeData = new NodeData();
            NodeList childNodes = nodeList.item(i).getChildNodes();
            for (int k = 0; k < childNodes.getLength(); ++k){
                if (childNodes.item(k).getAttributes() != null){
                    String prefix = childNodes.item(k).getAttributes().getNamedItem("key").getNodeValue();
                    String value = childNodes.item(k).getFirstChild().getNodeValue();
                    if ("d102".equals(prefix)){
                        nodeData.setIdNode(value);
                    }
                    else if ("d7".equals(prefix)){
                        nodeData.setActive(new Boolean(value));
                    }
                    else if ("d100".equals(prefix)){
                        nodeData.setCluster(value);
                    }
                    else if ("d5".equals(prefix)){
                        nodeData.setBorderColor(value);
                    }
                    else if ("d101".equals(prefix)){
                        nodeData.setReferenceCluster(value);
                    }
                    else if ("d4".equals(prefix)){
                        nodeData.setColor(value);
                    }
                }
            }
            linkedList.add(nodeData);
        }
        LinkedList<NodeData> activeNodeDatas = new LinkedList<NodeData>();
        LinkedList<EdgeData> activeEdgeDatas = new LinkedList<EdgeData>();

        for (NodeData nodeData : linkedList){
            if (nodeData.getActive())
                activeNodeDatas.add(nodeData);
        }
        NodeList edgeList = doc.getElementsByTagName("edge");
        for (int i = 0; i < edgeList.getLength(); ++i) {
            EdgeData edgeData = new EdgeData();
            NodeList childEdges = edgeList.item(i).getChildNodes();
            for (int k = 0; k < childEdges.getLength(); ++k) {
                if (childEdges.item(k).getAttributes() != null) {
                    String prefix = childEdges.item(k).getAttributes().getNamedItem("key").getNodeValue();
                    String value = childEdges.item(k).getFirstChild().getNodeValue();
                    if ("d15".equals(prefix)) {
                        edgeData.setActive(new Boolean(value));
                    }
                }
            }
            if (edgeData.getActive()){
                edgeData.setSource(edgeList.item(i).getAttributes().getNamedItem("source").getNodeValue());
                edgeData.setTarget(edgeList.item(i).getAttributes().getNamedItem("target").getNodeValue());
                activeEdgeDatas.add(edgeData);
            }

        }
        /*System.out.println(activeEdgeDatas.size());
        System.out.println(activeNodeDatas.size());
        System.out.println(doc.getElementsByTagName("node").getLength());*/

        Collections.sort(activeNodeDatas);

        String color = "";
        if (activeNodeDatas.size() > 0) {
            color = activeNodeDatas.get(0).getColor();
        }
        for (int i = 0; i < activeNodeDatas.size(); ++i){
            //System.out.println(activeNodeDatas.get(i).getColor());
            if (!color.equals(activeNodeDatas.get(i).getColor())){
              //  System.out.println(i);

                color = activeNodeDatas.get(i).getColor();
            }
        }
        HashMap<String, Integer> order = new HashMap<String, Integer>();
        for (int i = 0; i < activeNodeDatas.size(); ++i) {
            order.put('n' + activeNodeDatas.get(i).getIdNode(), new Integer(i));
        }

        HashMap<String, HashMap<String, Boolean>> sparseMatrix = new HashMap<String, HashMap<String, Boolean>>();

      /*  for (int i = 0; i < activeNodeDatas.size(); ++i) {
                sparseMatrix.put('n' + activeNodeDatas.get(i).getIdNode(), new HashMap<String, Boolean>());
        }
        for (int i = 0; i < activeEdgeDatas.size(); ++i){
            sparseMatrix.get(activeEdgeDatas.get(i).getSource()).put(activeEdgeDatas.get(i).getTarget(), new Boolean(true));
        }*/

        //String sparseMatrixForFile = "";
        Float[][] sparseM = new Float[activeNodeDatas.size()][activeNodeDatas.size()];
        for (int i = 0; i < activeEdgeDatas.size(); ++i){
            String sourse = activeEdgeDatas.get(i).getSource();
            String target = activeEdgeDatas.get(i).getTarget();
            Integer intSourse = order.get(sourse);
            Integer intTarget = order.get(target);
            sparseM[intSourse][intTarget] = new Float(1);
            sparseM[intTarget][intSourse] = new Float(1);
        }

        ArrayList<SimpleNodeData> simpleNodeDatas = new ArrayList<SimpleNodeData>();
        for (int i = 0; i < activeNodeDatas.size(); ++i){
            simpleNodeDatas.add(new SimpleNodeData(activeNodeDatas.get(i).getIdNode(), activeNodeDatas.get(i).getColor()));
        }

//Работа с классификатором
        Classifier classifier = new Classifier(sparseM, simpleNodeDatas);

        Integer k = 9;
        Float p = 0.9f;
        ArrayList<SimpleNodeData> datas= classifier.predictLabel(k, p);
        Integer countErrors = 0;
        for (int i = 0; i < datas.size(); ++i){
            if (!simpleNodeDatas.get(i).getLabel().equals(datas.get(i).getLabel()) && simpleNodeDatas.get(i).getName().equals(datas.get(i).getName()))
                countErrors += 1;
        }

        System.out.println(countErrors);
        System.out.println(datas.size());

        //Main.write("sparseMatrix.mat", sparseM);



    }

    public static void write(String fileName, int[][] sparseMatrix) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем массив в файл
                for (int i = 0; i < sparseMatrix.length; ++i){
                    for (int k = 0; k < sparseMatrix[i].length; ++k){
                        out.print(sparseMatrix[i][k] + " ");
                    }
                    out.println();
                }
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}

