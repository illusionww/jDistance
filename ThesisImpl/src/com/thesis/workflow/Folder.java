package com.thesis.workflow;

import java.io.File;

public class Folder {
    private String fileName;
    private String title;

    public Folder(String fileName) {
        this.fileName = fileName;
        this.title = fileNameToTitle(fileName);
    }

    public Folder(String fileName, String title) {
        this.fileName = fileName;
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return Context.getInstance().GRAPH_FOLDER + File.separator + fileName;
    }

    public String getTitle() {
        return title;
    }

    private String fileNameToTitle(String fileName) {
        char[] chars = fileName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i > 0) {
                if (Character.isAlphabetic(chars[i - 1]) && Character.isDigit(chars[i])) {
                    sb.append("=");
                } else if (Character.isDigit(chars[i - 1]) && Character.isAlphabetic(chars[i])) {
                    sb.append(" ");
                }
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }
}
