package com.thesis.cache;

import com.thesis.metric.DistanceClass;
import com.thesis.metric.Scale;
import com.thesis.workflow.Context;
import com.thesis.workflow.checker.CheckerType;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CacheItem {
    private static final Logger log = LoggerFactory.getLogger(CacheItem.class);
    public static final String regex = "^([_\\-\\w]+) (\\w+) \\((.+)\\) n=(\\d+), p_i=([\\.\\d]+), p_o=([\\.\\d]+), k=(\\d+), count=(\\d+), step=([\\.\\d]+) (\\w+) #(\\d+)\\.ser$";

    private String fileName = null;
    private HashMap<Double, Double> data = null;

    private DistanceClass distance;
    private CheckerType checker;
    private String checkerParams;
    private Integer n;
    private Double pIn;
    private Double pOut;
    private Integer k;
    private Integer count;
    private Double step;
    private Scale scale;

    public CacheItem(String fileName) {
        setFileName(fileName);
    }

    public CacheItem(Task task) {
        generateName(task);
        fillParameters();
        this.data = (HashMap) task.getResults();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        fillParameters();
    }

    public Integer getCount() {
        return count;
    }

    public Map<Double, Double> getData() {
        if (data == null) {
            if (fileName != null) {
                deserialize();
            } else {
                return null;
            }
        }
        return data;
    }

    public void setData(HashMap<Double, Double> data) {
        this.data = data;
    }

    public boolean isAppropriate(CacheItem item) {
        try {
            return this.distance.getInstance().getShortName().equals(item.distance.getInstance().getShortName())
                    && this.checker.equals(item.checker)
                    && this.checkerParams.equals(item.checkerParams)
                    && this.n.equals(item.n)
                    && this.pIn.equals(item.pIn)
                    && this.pOut.equals(item.pOut)
                    && this.k.equals(item.k)
                    && this.step.equals(item.step)
                    && this.scale.equals(item.scale);
        } catch (NullPointerException e) {
            log.error("error with {}", this.getFileName());
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        serialize();
    }

    private void generateName(Task task) {
        String basePath = Context.getInstance().CACHE_FOLDER;
        fileName = basePath + "/" + task.getName() + " #";
        int i = 0;
        while (new File(fileName + i + ".ser").exists()) {
            i++;
        }
        fileName = task.getName() + " #" + i + ".ser";
    }

    private void fillParameters() {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(fileName);
        if (m.matches()) {
            distance = DistanceClass.valueOf(m.group(1));
            checker = CheckerType.valueOf(m.group(2));
            checkerParams = m.group(3);
            n = Integer.valueOf(m.group(4));
            pIn = Double.valueOf(m.group(5));
            pOut = Double.valueOf(m.group(6));
            k = Integer.valueOf(m.group(7));
            count = Integer.valueOf(m.group(8));
            step = Double.valueOf(m.group(9));
            scale = Scale.valueOf(m.group(10));
        } else {
            throw new RuntimeException();
        }
    }

    private void serialize() {
        try {
            String basePath = Context.getInstance().CACHE_FOLDER;
            FileOutputStream fileOut = new FileOutputStream(basePath + "/" + fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deserialize() {
        try {
            String basePath = Context.getInstance().CACHE_FOLDER;
            FileInputStream fis = new FileInputStream(basePath + "/" + fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}