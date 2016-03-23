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

    public void useCustomContext(File contextFile) {
        unmarshalContext(contextFile);
        checkContext();
    }

    public Context getContext() {
        return context;
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
        if (context.getGnuplotPath() == null || context.getImgFolder() == null || context.getCompetitionFolder() == null
                || context.getParallelTasks() == null || context.getParallelGrid() == null) {
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
