package com.thesis.adapter.parser;

import com.thesis.adapter.parser.graph.Graph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ParserWrapper extends Parser {
    @Override
    public Graph parse(String path) throws ParserConfigurationException, IOException, SAXException {
        switch (getExtension(path)) {
            case "simplegraph":
                return new SimpleGraphParser().parse(path);
            case "graphml":
                return new GraphMLParser().parse(path);
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
