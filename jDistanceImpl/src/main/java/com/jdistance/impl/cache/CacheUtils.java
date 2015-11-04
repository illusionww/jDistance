package com.jdistance.impl.cache;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class CacheUtils {
    public static List<String> getAllSerFiles(String folder) {
        File dir = new File(folder);
        FilenameFilter filter = (d, name) -> (name.endsWith(".ser"));
        return Arrays.asList(dir.list(filter));
    }
}
