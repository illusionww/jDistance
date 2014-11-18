package com.company;

import java.util.LinkedList;

/**
 * Created by 111 on 17.11.2014.
 */
public class NodeData {
    private String idNode;
    private String color;
    private String cluster;
    private String referenceCluster;
    private Boolean active;
    private String borderColor;

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getReferenceCluster() {
        return referenceCluster;
    }

    public void setReferenceCluster(String referenceCluster) {
        this.referenceCluster = referenceCluster;
    }

    public LinkedList getList() {
        return list;
    }

    public void setList(LinkedList list) {
        this.list = list;
    }

    private LinkedList list;

    public String getIdNode() {
        return idNode;
    }

    public void setIdNode(String idNode) {
        this.idNode = idNode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
