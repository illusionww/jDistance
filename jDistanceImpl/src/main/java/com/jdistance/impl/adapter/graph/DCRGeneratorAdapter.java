package com.jdistance.impl.adapter.graph;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.graph.generator.GraphGenerator;
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
import java.util.*;

public class DCRGeneratorAdapter extends GraphGenerator {
    private static final Logger log = LoggerFactory.getLogger(DCRGeneratorAdapter.class);

    private static final String[] keys = new String[]{"t_max", "n", "p_in", "p_out", "p_inList", "k", "beta", "D_s", "p_chi", "p_nu", "eta", "p_omega", "p_mu", "sigma", "theta", "enp", "graphml", "binary", "log", "outDir", "fileName"};
    private static final String[] defaultValues = new String[]{"100", "60", "0.2", "0.01", "", "2", "1.0", "", "0.5", "0.5", "1", "0.02", "0.5", "2.0", "0.25", "false", "false", "false", "false", "", ""};
    private static final Boolean graphFilesIsTemp = false;

    private static final String PREFIX = "temp_dcr_graph";
    private static final String SUFFIX = ".graphml";
    private static final int ATTEMPTS = 5;
    private static final double DEVIATION = 0.03;

    private static DCRGeneratorAdapter instance;

    private DCRGeneratorAdapter() {
    }

    public static DCRGeneratorAdapter getInstance() {
        if (instance == null) {
            instance = new DCRGeneratorAdapter();
        }
        return instance;
    }

    @Override
    protected Graph generateGraph(GeneratorPropertiesDTO properties) {
        StandaloneDCRArguments arguments = getDefaultDCRArgs();
        arguments.t_max = 10;
        arguments.n = properties.getNodesCount();
        arguments.p_in = properties.getP_in();
        arguments.p_out = properties.getP_out();
        arguments.k = properties.getClustersCount();

        for (int i = 0; i < ATTEMPTS; i++) {
            try {
                log.debug("Graph generation attempt {}/{}", i + 1, ATTEMPTS);
                DCRGenerator generator = new DCRGenerator();
                DCRGraph dcrGraph = generator.generate(arguments);
                Graph graph = dcrGraphToGraph(dcrGraph);
                if (validate(graph, arguments.n, arguments.k, DEVIATION)) {
                    return graph;
                } else {
                    log.warn("Generated graph is not valid");
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
        arguments.t_max = Integer.parseInt((String) argMap.get("t_max"));
        arguments.n = Integer.parseInt((String) argMap.get("n"));
        arguments.p_in = Double.parseDouble((String) argMap.get("p_in"));
        arguments.p_out = Double.parseDouble((String) argMap.get("p_out"));
        arguments.p_inList = null;
        arguments.k = Integer.parseInt((String) argMap.get("k"));
        arguments.beta = Double.parseDouble((String) argMap.get("beta"));
        arguments.D_s = null;
        arguments.p_chi = Double.parseDouble((String) argMap.get("p_chi"));
        arguments.p_nu = Double.parseDouble((String) argMap.get("p_nu"));
        arguments.eta = Integer.parseInt((String) argMap.get("eta"));
        arguments.p_omega = Double.parseDouble((String) argMap.get("p_omega"));
        arguments.p_mu = Double.parseDouble((String) argMap.get("p_mu"));
        arguments.sigma = Double.parseDouble((String) argMap.get("sigma"));
        arguments.theta = Double.parseDouble((String) argMap.get("theta"));
        arguments.enp = Boolean.parseBoolean((String) argMap.get("enp"));
        return arguments;
    }

    private Graph dcrGraphToGraph(DCRGraph dcrGraph) throws IOException, ParserConfigurationException, SAXException {
        GraphJournal journal = dcrGraph.getGraphJournal();

        File tempFile = graphFilesIsTemp ? File.createTempFile(PREFIX, SUFFIX) : new File(PREFIX + new Random().nextInt(1000) + SUFFIX);
        VisoneGraphMLWriter fileOutputStream = new VisoneGraphMLWriter();
        fileOutputStream.writeGraph(journal, new FileOutputStream(tempFile));

        DCRGraphMLReager graphMLParser = new DCRGraphMLReager();
        return graphMLParser.importGraph(tempFile);
    }

    private boolean validate(Graph graph, int numOfNodes, int numOfClusters, double deviation) {
        List<Node> data = graph.getNodes();
        // validate number of nodes
        if (data.size() < numOfNodes * (1 - deviation) || data.size() > numOfNodes * (1 + deviation)) {
            log.info("Validation failed: numOfNodes - expected: {} but {} found", numOfNodes, data.size());
            return false;
        }
        // validate number of clusters
        Set<String> labels = new HashSet<>();
        data.forEach(item -> labels.add(item.getLabel()));
        if (labels.size() != numOfClusters) {
            log.info("Validation failed: numOfClusters - expected: {} but {} found", numOfClusters, labels.size());
            return false;
        }
        return true;
    }
}