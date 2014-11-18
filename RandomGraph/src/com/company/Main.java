package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        File file = new File("myGraph.graphml");
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
        System.out.println(activeEdgeDatas.size());
        System.out.println(activeNodeDatas.size());
        System.out.println(doc.getElementsByTagName("node").getLength());
    }
}

