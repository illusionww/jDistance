package com.thesis.adapter.generator;

import com.thesis.adapter.GraphValidator;
import com.thesis.graph.Graph;
import com.thesis.adapter.parser.GraphMLParser;
import de.uka.algo.generator.accessory.StandaloneDCRArguments;
import de.uka.algo.generator.standalone.generators.DCRGenerator;
import de.uka.algo.generator.standalone.graph.DCRGraph;
import de.uka.algo.generator.standalone.graph.io.VisoneGraphMLWriter;
import de.uka.algo.generator.standalone.graph.journaling.GraphJournal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCRGeneratorAdapter {
    private static final Logger log = LoggerFactory.getLogger(DCRGeneratorAdapter.class);

    private static final String[] keys = new String[]{"t_max", "n", "p_in", "p_out", "p_inList", "k", "beta", "D_s", "p_chi", "p_nu", "eta", "p_omega", "p_mu", "sigma", "theta", "enp", "graphml", "binary", "log", "outDir", "fileName"};
    private static final String[] defaultValues = new String[]{"100", "60", "0.2", "0.01", "", "2", "1.0", "", "0.5", "0.5", "1", "0.02", "0.5", "2.0", "0.25", "false", "false", "false", "false", "", ""};

    private static final String PREFIX = "gcr2graph";
    private static final String SUFFIX = ".tmp";
    private static final int ATTEMPTS = 5;
    private static final double DEVIATION = 0.03;

    public List<Graph> generateList(int count, int n, double p_in, double p_out, int k) throws IOException, ParserConfigurationException, SAXException {
        List<Graph> graphs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            graphs.add(generate(n, p_in, p_out, k));
        }
        return graphs;
    }

    public Graph generate(int n, double p_in, double p_out, int k) throws IOException, ParserConfigurationException, SAXException {
        StandaloneDCRArguments arguments = getDefaultDCRArgs();
        arguments.t_max = 10;
        arguments.n = n;
        arguments.p_in = p_in;
        arguments.p_out = p_out;
        arguments.k = k;

        for (int i = 0; i < ATTEMPTS; i++) {
            try {
                log.debug("Graph generation attempt {}/{}", i + 1, ATTEMPTS);
                DCRGenerator generator = new DCRGenerator();
                DCRGraph dcrGraph = generator.generate(arguments);
                Graph graph = dcrGraphToGraph(dcrGraph);
                if (GraphValidator.validate(graph, n, k, DEVIATION)) {
                    return graph;
                } else {
                    log.error("Generated graph is not valid");
                }
            } catch (Exception e) {
                log.error("Graph generation attempt failed", e);
            }
        }

        return null;
    }

    private StandaloneDCRArguments getDefaultDCRArgs() {
        Map<String, Object> argMap = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            argMap.put(keys[i], defaultValues[i]);
        }

        StandaloneDCRArguments arguments = new StandaloneDCRArguments();
        arguments.t_max = Integer.parseInt((String)argMap.get("t_max"));
        arguments.n = Integer.parseInt((String)argMap.get("n"));
        arguments.p_in = Double.parseDouble((String) argMap.get("p_in"));
        arguments.p_out = Double.parseDouble((String) argMap.get("p_out"));
        arguments.p_inList = null;
        arguments.k = Integer.parseInt((String)argMap.get("k"));
        arguments.beta = Double.parseDouble((String) argMap.get("beta"));
        arguments.D_s = null;
        arguments.p_chi = Double.parseDouble((String)argMap.get("p_chi"));
        arguments.p_nu = Double.parseDouble((String)argMap.get("p_nu"));
        arguments.eta = Integer.parseInt((String)argMap.get("eta"));
        arguments.p_omega = Double.parseDouble((String)argMap.get("p_omega"));
        arguments.p_mu = Double.parseDouble((String)argMap.get("p_mu"));
        arguments.sigma = Double.parseDouble((String)argMap.get("sigma"));
        arguments.theta = Double.parseDouble((String)argMap.get("theta"));
        arguments.enp = Boolean.parseBoolean((String)argMap.get("enp"));
        return arguments;
    }

    private Graph dcrGraphToGraph(DCRGraph dcrGraph) throws IOException, ParserConfigurationException, SAXException {
        GraphJournal journal = dcrGraph.getGraphJournal();

        File tempFile = File.createTempFile(PREFIX, SUFFIX);
        VisoneGraphMLWriter fileOutputStream = new VisoneGraphMLWriter();
        fileOutputStream.writeGraph(journal, new FileOutputStream(tempFile));

        GraphMLParser graphMLParser = new GraphMLParser();
        return graphMLParser.parse(tempFile);
    }
}
