package com.jdistance.graph.parser;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import org.xml.sax.SAXException;
import sun.security.pkcs.ParsingException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleGraphParser extends Parser {
    private static final Pattern label = Pattern.compile("^vertex (\\d+):.*");
    private static final Pattern color = Pattern.compile("^color (\\d+).*");
    private static final Pattern edge = Pattern.compile("^(\\d+) -> (\\d+).*");

    @Override
    public Graph parse(File file) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<Node> simpleNodeData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while (line != null && !line.matches("begin vertices:")) {
                line = br.readLine();
            }
            line = br.readLine();
            while (line != null && line.matches("^vertex \\d+:")) {
                Matcher nm = label.matcher(line);
                if (nm.find()) {
                    String number = nm.group(1);
                    br.readLine();
                    br.readLine();
                    line = br.readLine();
                    Matcher cm = color.matcher(line);
                    if (cm.find()) {
                        String group = cm.group(1);
                        Node simpleNode = new Node(number, group);
                        simpleNodeData.add(simpleNode);
                        br.readLine();
                        line = br.readLine();
                    } else {
                        throw new ParsingException("no matches color with: " + line);
                    }
                } else {
                    throw new ParsingException("no matches label with: " + line);
                }
            }

            int d = simpleNodeData.size();
            double[][] sparseM = new double[d][d];

            assert "begin edges".equals(br.readLine());
            line = br.readLine();
            while (line != null && line.matches("\\d+ -> \\d+.*")) {
                Matcher m = edge.matcher(line);
                if (m.find()) {
                    int from = Integer.parseInt(m.group(1));
                    int to = Integer.parseInt(m.group(2));
                    sparseM[from][to] = 1;
                    sparseM[to][from] = 1;
                    br.readLine();
                    br.readLine();
                    br.readLine();
                    line = br.readLine();
                } else {
                    throw new ParsingException("no matches edge with: " + line);
                }
            }

            return new Graph(sparseM, simpleNodeData);
        }
    }
}