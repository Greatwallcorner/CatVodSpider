package com.github.catvod.spider;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Util;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Init {

    private final ExecutorService executor;

    public static void run(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
//    private final Handler handler;
//    private Application app;

    private static class Loader {
        static volatile Init INSTANCE = new Init();
    }

    public static Init get() {
        return Loader.INSTANCE;
    }

    public Init() {
//        this.handler = new Handler(Looper.getMainLooper());
        this.executor = Executors.newFixedThreadPool(5);
    }

    public static void init() {
        SpiderDebug.log("自定義爬蟲代碼載入成功！");
        Util.notify("加载成功");
    }

    public static void execute(Runnable runnable) {
        get().executor.execute(runnable);
    }

//    public static void run(Runnable runnable) {
//        get().handler.post(runnable);
//    }

//    public static void run(Runnable runnable, int delay) {
//        get().handler.postDelayed(runnable, delay);
//    }
}
