package com.jdistance.impl.workflow.context;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

@XmlRootElement
public class Context {
    private String gnuplotPath;

    private Boolean parallelTasks;
    private Boolean parallelGrid;
    private Boolean metricsStatistics;
    private String tempFolder;

    private String calculationsResultFolder;
    private String imgFolder;
    private String competitionFolder;
    private Boolean writeGnuplotScripts;

    public String getGnuplotPath() {
        return gnuplotPath;
    }

    @XmlElement
    public void setGnuplotPath(String gnuplotPath) {
        this.gnuplotPath = gnuplotPath;
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

    public Boolean getMetricsStatistics() {
        return metricsStatistics;
    }

    @XmlElement
    public void setMetricsStatistics(Boolean metricsStatistics) {
        this.metricsStatistics = metricsStatistics;
    }

    public String getTempFolder() {
        return tempFolder;
    }

    @XmlElement
    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    public String getCalculationsResultFolder() {
        return calculationsResultFolder;
    }

    @XmlElement
    public void setCalculationsResultFolder(String calculationsResultFolder) {
        this.calculationsResultFolder = calculationsResultFolder;
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

    public Boolean getWriteGnuplotScripts() {
        return writeGnuplotScripts;
    }

    @XmlElement
    public void setWriteGnuplotScripts(Boolean writeGnuplotScripts) {
        this.writeGnuplotScripts = writeGnuplotScripts;
    }

    public String buildDataFullName(String imgTitle, String extension) {
        return ContextProvider.getContext().getCalculationsResultFolder() + File.separator +
                imgTitle.replaceAll("[^\\w\\-\\.,= ]+", "_") + "." + extension;
    }

    public String buildImgFullName(String imgTitle, String extension) {
        return ContextProvider.getContext().getImgFolder() + File.separator +
                imgTitle.replaceAll("[^\\w\\-\\.,= ]+", "_") + "." + extension;
    }
}