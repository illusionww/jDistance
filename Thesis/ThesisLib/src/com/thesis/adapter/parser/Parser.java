package com.thesis.adapter.parser;

import com.thesis.adapter.parser.graph.Graph;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface Parser {
    public Graph parse(String path) throws ParserConfigurationException, IOException, SAXException;
}
