package com.jdistance.graph.parser;

import com.jdistance.graph.Graph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser {
    public Graph parse(String path) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(path);
        return parse(file);
    }

    public abstract Graph parse(File file) throws ParserConfigurationException, IOException, SAXException;

    public List<Graph> parseInDirectory(String path) throws IOException {
        List<Graph> graphs = new ArrayList<>();
        Files.walk(Paths.get(path)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                try {
                    Graph graph = parse(filePath.toString());
                    if (graph != null) {
                        graphs.add(graph);
                    }
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    e.printStackTrace();
                }
            }
        });
        return graphs;
    }
}