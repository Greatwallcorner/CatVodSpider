package com.github.catvod.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

//    public static void openFile(File file) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setDataAndType(getShareUri(file), FileUtil.getMimeType(file.getName()));
//        Init.context().startActivity(intent);
//    }

    public static void unzip(File target, File path) {
        try (ZipFile zip = new ZipFile(target.getAbsolutePath())) {
            Enumeration<?> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File out = new File(path, entry.getName());
                if (entry.isDirectory()) out.mkdirs();
                else Path.copy(zip.getInputStream(entry), out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static Uri getShareUri(File file) {
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? Uri.fromFile(file) : FileProvider.getUriForFile(Init.context(), Init.context().getPackageName() + ".provider", file);
//    }

    private static String getMimeType(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        return StringUtils.isEmpty(mimeType) ? "*/*" : mimeType;
    }
}
