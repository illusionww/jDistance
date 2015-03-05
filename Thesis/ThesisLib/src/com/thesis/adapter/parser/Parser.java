package com.thesis.adapter.parser;

import com.thesis.adapter.parser.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser {
    private static final Logger log = LoggerFactory.getLogger(Parser.class);
    public abstract Graph parse(String path) throws ParserConfigurationException, IOException, SAXException;

    public List<Graph> parseInDirectory(String path) throws IOException {
        List<Graph> graphs = new ArrayList<>();
        Files.walk(Paths.get(path)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                try {
                    Graph graph = parse(filePath.toString());
                    if (graph != null) {
                        log.info("Add graph {}", filePath.getFileName().toString());
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
