package com.jdistance.impl.adapter.parser;

import com.jdistance.graph.Graph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ParserWrapper extends Parser {
    @Override
    public Graph parse(File file) throws ParserConfigurationException, IOException, SAXException {
        switch (getExtension(file.getAbsolutePath())) {
            case "simplegraph":
                return new SimpleGraphParser().parse(file);
            case "graphml":
                return new GraphMLParser().parse(file);
            default:
                return null;
        }
    }

    private String getExtension(String path) {
        String extension = "";

        int i = path.lastIndexOf('.');
        int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        if (i > p) {
            extension = path.substring(i + 1);
        }

        return extension;
    }
}