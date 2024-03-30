package com.github.catvod.crawler;

import org.slf4j.LoggerFactory;

public class SpiderDebug {

    private static final String TAG = SpiderDebug.class.getSimpleName();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TAG);

    public static void log(Throwable e) {
        LOGGER.error("error:", e);
    }

    public static void log(String msg) {
        LOGGER.debug(msg);
    }
}
