package com.thesis.cache;

import com.thesis.workflow.Context;
import com.thesis.workflow.task.DefaultTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CacheManager {
    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);

    private static volatile CacheManager instance;
    private List<CacheItem> items;

    private CacheManager() {
        indexCache();
    }

    public static CacheManager getInstance() {
        CacheManager localInstance = instance;
        if (localInstance == null) {
            synchronized (CacheManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new CacheManager();
                }
            }
        }
        return localInstance;
    }

    public void push(CacheItem item) {
        item.flush();
    }

    public List<CacheItem> pop(DefaultTask task) {
        CacheItem needle = new CacheItem(task);
        List<CacheItem> rawItems = getAppropriateList(needle);

        int count = 0;
        List<CacheItem> items = new ArrayList<>();
        for (CacheItem item : rawItems) {
            if (item.getCount() <= needle.getCount() - count) {
                items.add(item);
                count += item.getCount();
            }
        }
        log.info("get from cache {}/{}", count, needle.getCount());
        return items;
    }

    public void reconciliation() {
        indexCache();
    }

    private void indexCache() {
        items = new ArrayList<>();
        String basePath = Context.getInstance().CACHE_FOLDER;
        try {
            Files.walk(Paths.get(basePath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && isCacheFile(filePath.getFileName().toString())) {
                    CacheItem item = new CacheItem(filePath.getFileName().toString());
                    items.add(item);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<CacheItem> getAppropriateList(CacheItem task) {
        return items.stream().filter(item -> item.isAppropriate(task)).collect(Collectors.toList());
    }

    private boolean isCacheFile(String fileName) {
        Pattern p = Pattern.compile(CacheItem.regex);
        Matcher m = p.matcher(fileName);
        return m.matches();
    }
}
