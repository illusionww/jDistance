package com.jdistance.spark.workflow;

import com.jdistance.workflow.AbstractGridSearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

public class GridSearchResult extends AbstractGridSearchResult {
    private static final Logger log = LoggerFactory.getLogger(GridSearchResult.class);

    public GridSearchResult(String name, Map<String, Map<Double, Pair<Double, Double>>> data) {
        super(name, data);
    }

    public GridSearchResult writeData() {
        return writeData(name);
    }

    public GridSearchResult writeData(String filePath) {
        log.info("Write data to: {}", filePath);

        StringWriter stringWriter = new StringWriter();
        try {
            super.writeData(stringWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = stringWriter.toString();
        Context.getInstance().getSparkContext()
                .parallelize(Collections.singletonList(data))
                .saveAsTextFile(Context.getInstance().buildDataFullName(filePath));
        return this;
    }
}
