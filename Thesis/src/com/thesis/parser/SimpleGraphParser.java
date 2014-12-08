package com.thesis.parser;

import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import org.xml.sax.SAXException;
import sun.security.pkcs.ParsingException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleGraphParser implements Parser {
    final Pattern label = Pattern.compile("^vertex (\\d+):.*");
    final Pattern color = Pattern.compile("^color (\\d+).*");
    final Pattern edge = Pattern.compile("^(\\d+) -> (\\d+).*");

    private String path;

    public SimpleGraphParser(String path) {
        this.path = path;
    }

    @Override
    public Graph parse() throws ParserConfigurationException, IOException, SAXException {
        ArrayList<SimpleNodeData> simpleNodeData = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null && !line.matches("begin vertices:")) {
                //System.out.println("skip '" + line + "'");
                line = br.readLine();
            }
            line = br.readLine();
            //System.out.println("line: " + line);
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
                        //System.out.println("number:" + number + " group:" + group);
                        SimpleNodeData simpleNode = new SimpleNodeData(number, group);
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
            float[][] sparseM = new float[d][d];

            assert "begin edges".equals(br.readLine());
            line = br.readLine();
            while (line != null && line.matches("\\d+ -> \\d+.*")) {
                Matcher m = edge.matcher(line);
                if (m.find()) {
                    int from = Integer.parseInt(m.group(1));
                    int to = Integer.parseInt(m.group(2));
                    sparseM[from][to] = 1;
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
