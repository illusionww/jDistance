package com.jdistance.impl.workflow.context;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class ContextProvider {
    private static ContextProvider instance;
    private Context context;

    private ContextProvider() {
        File contextFile = new File("context.xml");
        unmarshalContext(contextFile);
        checkContext();
    }

    public static ContextProvider getInstance() {
        if (instance == null) {
            instance = new ContextProvider();
        }
        return instance;
    }

    public static Context getContext() {
        return getInstance().context;
    }

    public void useCustomContext(File contextFile) {
        unmarshalContext(contextFile);
        checkContext();
    }

    private void unmarshalContext(File file) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Context.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            context = (Context) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new RuntimeException("Cannot unmarshal context file", e);
        }

    }

    private void checkContext() {
        if (context.getGnuplotPath() == null || context.getParallelTasks() == null || context.getParallelGrid() == null
                || context.getTempFolder() == null || context.getCalculationsResultFolder() == null
                || context.getImgFolder() == null || context.getCompetitionFolder() == null
                || context.getWriteGnuplotScripts() == null) {
            throw new RuntimeException("Context is not filled properly");
        }

        if (!"auto".equals(context.getGnuplotPath())) {
            File gnuplotPathFile = new File(context.getGnuplotPath());
            if (!gnuplotPathFile.exists()) {
                throw new RuntimeException("Gnuplot not found");
            }
        } else {
            context.setGnuplotPath(null);
        }

        File dataFolderFile = new File(context.getCalculationsResultFolder());
        if (!dataFolderFile.exists() && !dataFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + dataFolderFile.getAbsolutePath() + " is not exist");
        }

        File imgFolderFile = new File(context.getImgFolder());
        if (!imgFolderFile.exists() && !imgFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + imgFolderFile.getAbsolutePath() + " is not exist");
        }

        File competitionFolderFile = new File(context.getCompetitionFolder());
        if (!competitionFolderFile.exists() && !competitionFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + competitionFolderFile.getAbsolutePath() + " is not exist");
        }
    }
}
