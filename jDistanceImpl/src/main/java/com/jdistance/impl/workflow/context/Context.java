package com.jdistance.impl.workflow.context;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Context {
    private String gnuplotPath;
    private String imgFolder;
    private String competitionFolder;
    private String tempFolder;
    private Boolean parallelTasks;
    private Boolean parallelGrid;

    public String getGnuplotPath() {
        return gnuplotPath;
    }

    @XmlElement
    public void setGnuplotPath(String gnuplotPath) {
        this.gnuplotPath = gnuplotPath;
    }

    public String getImgFolder() {
        return imgFolder;
    }

    @XmlElement
    public void setImgFolder(String imgFolder) {
        this.imgFolder = imgFolder;
    }

    public String getCompetitionFolder() {
        return competitionFolder;
    }

    @XmlElement
    public void setCompetitionFolder(String competitionFolder) {
        this.competitionFolder = competitionFolder;
    }

    public String getTempFolder() {
        return tempFolder;
    }

    @XmlElement
    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    public Boolean getParallelTasks() {
        return parallelTasks;
    }

    @XmlElement
    public void setParallelTasks(Boolean parallelTasks) {
        this.parallelTasks = parallelTasks;
    }

    public Boolean getParallelGrid() {
        return parallelGrid;
    }

    @XmlElement
    public void setParallelGrid(Boolean parallelGrid) {
        this.parallelGrid = parallelGrid;
    }
}