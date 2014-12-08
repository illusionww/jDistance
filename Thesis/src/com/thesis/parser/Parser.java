package com.thesis.parser;

import com.thesis.graph.Graph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface Parser {
    public Graph parse(String path) throws ParserConfigurationException, IOException, SAXException;
}
