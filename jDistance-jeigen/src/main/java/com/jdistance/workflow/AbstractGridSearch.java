package com.jdistance.workflow;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractGridSearch implements Serializable {
    protected String name = "Untitled";
    protected List<Task> tasks;

    public AbstractGridSearch(List<Task> tasks) {
        this.tasks = tasks;
    }

    public AbstractGridSearch(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected abstract AbstractGridSearchResult execute();
}
