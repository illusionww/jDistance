package com.jdistance.impl.workflow.context;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Context {
    private String gnuplotPath;
    private String imgFolder;
    private String competitionFolder;
    private String tempFolder;
    private Boolean parallel;

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

    public Boolean getParallel() {
        return parallel;
    }

    @XmlElement
    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }
}