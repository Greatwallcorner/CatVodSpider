package com.github.catvod.utils;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.Init;
import com.github.catvod.spider.Proxy;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.DateUtils;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static final String patternAli = "(https:\\/\\/www\\.aliyundrive\\.com\\/s\\/[^\"]+|https:\\/\\/www\\.alipan\\.com\\/s\\/[^\"]+)";
    public static final String patternQuark = "(https:\\/\\/pan\\.quark\\.cn\\/s\\/[^\"]+)";
    public static final String patternUC = "(https:\\/\\/drive\\.uc\\.cn\\/s\\/[^\"]+)";
    public static final Pattern RULE = Pattern.compile("http((?!http).){12,}?\\.(m3u8|mp4|flv|avi|mkv|rm|wmv|mpg|m4a|mp3)\\?.*|http((?!http).){12,}\\.(m3u8|mp4|flv|avi|mkv|rm|wmv|mpg|m4a|mp3)|http((?!http).)*?video/tos*");
    public static final String CHROME = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36";
    public static final String SAFARI = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.33";
    public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7";
    public static final List<String> MEDIA = Arrays.asList("mp4", "mkv", "wmv", "flv", "avi", "iso", "mpg", "ts", "mp3", "aac", "flac", "m4a", "ape", "ogg");
    public static final List<String> SUB = Arrays.asList("srt", "ass", "ssa", "vtt");

    private static HashMap<String, String> webHttpHeaderMap;

    public static final String CLIENT_ID = "76917ccccd4441c39457a04f6084fb2f";

    public static boolean isVip(String url) {
        List<String> hosts = Arrays.asList("iqiyi.com", "v.qq.com", "youku.com", "le.com", "tudou.com", "mgtv.com", "sohu.com", "acfun.cn", "bilibili.com", "baofeng.com", "pptv.com");
        for (String host : hosts) if (url.contains(host)) return true;
        return false;
    }

    public static boolean isBlackVodUrl(String url) {
        List<String> hosts = Arrays.asList("973973.xyz", ".fit:");
        for (String host : hosts) if (url.contains(host)) return true;
        return false;
    }

    public static boolean isVideoFormat(String url) {
        if (url.contains("url=http") || url.contains(".js") || url.contains(".css") || url.contains(".html"))
            return false;
        return RULE.matcher(url).find();
    }

    public static String findByRegex(String regex, String content, Integer groupCount) {
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(regex);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(content);
        if (m.find()) {
            return m.group(groupCount);
        } else {
            return "";
        }
    }

    public static byte[] toUtf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8).getBytes();
    }

    public static boolean isSub(String ext) {
        return SUB.contains(ext);
    }

    public static boolean isMedia(String text) {
        return MEDIA.contains(getExt(text));
    }

    public static String getExt(String name) {
        return name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : name;
    }

    public static String getSize(double size) {
        if (size <= 0) return "";
        if (size > 1024 * 1024 * 1024 * 1024.0) {
            size /= (1024 * 1024 * 1024 * 1024.0);
            return String.format(Locale.getDefault(), "%.2f%s", size, "TB");
        } else if (size > 1024 * 1024 * 1024.0) {
            size /= (1024 * 1024 * 1024.0);
            return String.format(Locale.getDefault(), "%.2f%s", size, "GB");
        } else if (size > 1024 * 1024.0) {
            size /= (1024 * 1024.0);
            return String.format(Locale.getDefault(), "%.2f%s", size, "MB");
        } else {
            size /= 1024.0;
            return String.format(Locale.getDefault(), "%.2f%s", size, "KB");
        }
    }

    //todo
    public static String fixUrl(String base, String src) {
        if (src.startsWith("//")) {
            URI parse = URI.create(base);
            return parse.getScheme() + ":" + src;
        } else if (!src.contains("://")) {
            URI parse = URI.create(base);
            return parse.getScheme() + "://" + parse.getHost() + src;
        } else {
            return src;
        }
    }

    public static String removeExt(String text) {
        return text.contains(".") ? text.substring(0, text.lastIndexOf(".")) : text;
    }

    public static String substring(String text) {
        return substring(text, 1);
    }

    public static String substring(String text, int num) {
        if (text != null && text.length() > num) {
            return text.substring(0, text.length() - num);
        } else {
            return text;
        }
    }

    public static String getVar(String data, String param) {
        for (String var : data.split("var")) if (var.contains(param)) return checkVar(var);
        return "";
    }

    private static String checkVar(String var) {
        if (var.contains("'")) return var.split("'")[1];
        if (var.contains("\"")) return var.split("\"")[1];
        return "";
    }

    public static String MD5(String src) {
        return MD5(src, "UTF-8");
    }

    public static String MD5(String src, String charset) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(src.getBytes(charset));
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder sb = new StringBuilder(no.toString(16));
            while (sb.length() < 32) sb.insert(0, "0");
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    static {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @FunctionalInterface
    public interface CallBack {
        void apply(String val);
    }

    public static String ShowInputDialog(String msg, CallBack confirm) {
        JDialog jDialog = new JDialog();
        jDialog.setUndecorated(true);
        jDialog.setLocationRelativeTo(null);

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jPanel.setBackground(Color.darkGray);
        jPanel.setForeground(Color.white);
        jPanel.setBorder(getBorder(15));
        jPanel.setSize(Swings.dp2px(200), Swings.dp2px(80));

        JLabel jLabel = new JLabel(msg);
        jLabel.setForeground(Color.white);
        jPanel.add(jLabel);

        TextField textField = new TextField();
        textField.setBackground(Color.darkGray);
        textField.setForeground(Color.white);
        textField.setColumns(32);
        jPanel.add(textField);

        JButton jButton = new JButton("关闭(X)");
        jButton.setBackground(Color.darkGray);
        jButton.setForeground(Color.white);
        jButton.addActionListener((event) -> {
            confirm.apply(textField.getText());
            jDialog.setVisible(false);
            jDialog.dispose();
        });
        jPanel.add(jButton);

        jDialog.add(jPanel);
        jDialog.pack();
        jDialog.setLocation(Swings.getCenter(jPanel.getWidth(), jPanel.getHeight()));
        jDialog.setVisible(true);
        return textField.getText();
    }

    public static JDialog showDialog(JPanel jPanel, String title) {
        JDialog jDialog = new JDialog((Frame) null);
        jDialog.setUndecorated(true);
        jPanel.setBorder(getBorder(20));
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.setBackground(Color.darkGray);

        JLabel jLabel = new JLabel(String.format("TV-%s", title));
        jLabel.setAlignmentX(JLabel.CENTER);
        jLabel.setBackground(Color.DARK_GRAY);
        jLabel.setForeground(Color.white);
        jPanel.add(jLabel);

        JButton button = new JButton("关闭(X)");
        button.addActionListener((event) -> {
            jDialog.setVisible(false);
            jDialog.dispose();
        });
        button.setForeground(Color.LIGHT_GRAY);
        button.setBackground(Color.darkGray);

        jPanel.add(button);

        jDialog.setContentPane(jPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(null);
        jDialog.setLocation(Swings.getCenter(jPanel.getWidth(), jPanel.getHeight()));
        jDialog.setVisible(true);
        return jDialog;
    }

    public static void notify(String msg) {
        Init.execute(()->{
            try {
                postHttpMsg(msg);
            } catch (IOException e) {
                SpiderDebug.log(e);
            }
        });
//        NotifyEvent.post(msg);
//        showToast(msg, 2000);
    }

    private static void postHttpMsg(String msg) throws IOException {

        Response response = OkHttp.newCall(Proxy.getHostPort() + "/postMsg?msg=" + msg);

        if (!response.isSuccessful()) {
            SpiderDebug.log("send msg fail：" + msg);
        }
    }

    public static void notify(String msg, Integer timeMills) {
        showToast(msg, timeMills);
    }

    public static void showToast(String msg, Integer timeMills) {
        int height = Swings.dp2px(80);
        int width = Swings.dp2px(msg.length() > 18 ? 18 * 25 : msg.length() * 25);
        JDialog jDialog = new JDialog();
        jDialog.setUndecorated(true);
        Point point = Swings.screenRightDown(width, height);
//        RoundRectangle2D.Double shape = new RoundRectangle2D.Double();
//        int arcPx = Swings.dp2px(15);
//        shape.setRoundRect(0, 0, width, height, arcPx, arcPx);
//        jDialog.setShape(shape);
        jDialog.setBounds(point.x, point.y, width, height);
        JPanel jPanel = new JPanel();
        jPanel.setBackground(Color.darkGray);
        jPanel.setBorder(getBorder(10));
        JLabel jLabel = new JLabel(msg);
        jLabel.setBounds(0, 0, width, height);
        jLabel.setVerticalAlignment(SwingConstants.CENTER);
        jLabel.setIcon(new ImageIcon(Util.class.getResource("/TV-icon_1_s.png")));
        jLabel.setFont(jLabel.getFont().deriveFont(Float.valueOf(Swings.dp2px(25))));
        jLabel.setForeground(Color.white);
        jPanel.add(jLabel);
        jDialog.setContentPane(jPanel);
        jDialog.pack();
        jDialog.setVisible(true);

        new Timer(timeMills, (event) -> {
            jDialog.dispose();
        }).start();
    }

    private static EmptyBorder getBorder(int size) {
        int i = Swings.dp2px(size);
        return new EmptyBorder(i, i, i, i);
    }

    public static String getDigit(String text) {
        try {
            String newText = text;
            Matcher matcher = Pattern.compile(".*(1080|720|2160|4k|4K).*").matcher(text);
            if (matcher.find()) newText = matcher.group(1) + " " + text;
            matcher = Pattern.compile("^([0-9]+)").matcher(text);
            if (matcher.find()) newText = matcher.group(1) + " " + newText;
            return newText.replaceAll("\\D+", "") + " " + newText.replaceAll("\\d+", "");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getMimeType(String contentDisposition) {
        if (contentDisposition.endsWith(".mp4")) {
            return "video/mp4";
        } else if (contentDisposition.endsWith(".webm")) {
            return "video/webm";
        } else if (contentDisposition.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (contentDisposition.endsWith(".wmv")) {
            return "video/x-ms-wmv";
        } else if (contentDisposition.endsWith(".flv")) {
            return "video/x-flv";
        } else if (contentDisposition.endsWith(".mov")) {
            return "video/quicktime";
        } else if (contentDisposition.endsWith(".mkv")) {
            return "video/x-matroska";
        } else if (contentDisposition.endsWith(".mpeg")) {
            return "video/mpeg";
        } else if (contentDisposition.endsWith(".3gp")) {
            return "video/3gpp";
        } else if (contentDisposition.endsWith(".ts")) {
            return "video/MP2T";
        } else if (contentDisposition.endsWith(".mp3")) {
            return "audio/mp3";
        } else if (contentDisposition.endsWith(".wav")) {
            return "audio/wav";
        } else if (contentDisposition.endsWith(".aac")) {
            return "audio/aac";
        } else {
            return null;
        }
    }

    public static void sleep(Integer time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
    }

    /**
     * @param referer
     * @param cookie 多个cookie name=value;name2=value2
     * @return
     */
    public static HashMap<String, String> webHeaders(String referer, String cookie){
        HashMap<String, String> map = webHeaders(referer);
        map.put(com.google.common.net.HttpHeaders.COOKIE, cookie);
        return map;
    }

    public static HashMap<String, String> webHeaders(String referer) {
        if (webHttpHeaderMap == null || webHttpHeaderMap.isEmpty()) {
            synchronized (Util.class) {
                if (webHttpHeaderMap == null || webHttpHeaderMap.isEmpty()) {
                    webHttpHeaderMap = new HashMap<>();
//                    webHttpHeaderMap.put(HttpHeaders.CONTENT_TYPE, ContentType.Application.INSTANCE.getJson().getContentType());
//                    webHttpHeaderMap.put(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
                    webHttpHeaderMap.put(HttpHeaders.CONNECTION, "keep-alive");
                    webHttpHeaderMap.put(HttpHeaders.USER_AGENT, CHROME);
                    webHttpHeaderMap.put(HttpHeaders.ACCEPT, "*/*");
//                    webHttpHeaderMap.put(HttpHeaders.HOST, )
                }
            }
        }
        if(StringUtils.isNotBlank(referer)){
            URI uri = URI.create(referer);
            String u = uri.getScheme() + "://" + uri.getHost();
            webHttpHeaderMap.put(HttpHeaders.REFERER, u);
        }
//        webHttpHeaderMap.put(io.ktor.http.HttpHeaders.INSTANCE.getOrigin(), u);
        return webHttpHeaderMap;
    }

//    public static HashMap<String, String> webHeaders(String referer) {
//        if (webHttpHeaderMap == null || webHttpHeaderMap.isEmpty()) {
//            synchronized (Util.class) {
//                if (webHttpHeaderMap == null || webHttpHeaderMap.isEmpty()) {
//                    webHttpHeaderMap = new HashMap<>();
////                    webHttpHeaderMap.put(HttpHeaders.CONTENT_TYPE, ContentType.Application.INSTANCE.getJson().getContentType());
//                    webHttpHeaderMap.put(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
//                    webHttpHeaderMap.put(HttpHeaders.CONNECTION, "keep-alive");
//                    webHttpHeaderMap.put(HttpHeaders.USER_AGENT, CHROME);
//                    webHttpHeaderMap.put(HttpHeaders.ACCEPT, "*/*");
//                    webHttpHeaderMap.put(HttpHeaders.HOST, )
//                }
//            }
//        }
//        URI uri = URI.create(referer);
//        String u = uri.getScheme() + "://" + uri.getHost();
//        webHttpHeaderMap.put(HttpHeaders.REFERER, u);
////        webHttpHeaderMap.put(io.ktor.http.HttpHeaders.INSTANCE.getOrigin(), u);
//        return webHttpHeaderMap;
//    }

    public static String timestampToDateStr(Long timestamp) {
        return DateUtils.formatDate(new Date(timestamp));
    }

    public static String base64Encode(String str) {
        return new String(Base64.getEncoder().encode(str.getBytes()));
    }

    public static String base64Encode(byte[] str) {
        return new String(Base64.getEncoder().encode(str));
    }

    public static String base64Decode(String str) {
        if(StringUtils.isBlank(str)) return "";
        return new String(Base64.getDecoder().decode(str));
    }

    public static String stringJoin(String separate, List<String> list) {
        return StringUtils.join(list, separate);
    }

    public static String stringJoin(List<String> list, String separate) {
        return StringUtils.join(list, separate);
    }

    /**
     * 字符串相似度匹配
     *
     * @returns
     */

    public static LCSResult lcs(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return new LCSResult(0, "", 0);
        }

        StringBuilder sequence = new StringBuilder();
        int str1Length = str1.length();
        int str2Length = str2.length();
        int[][] num = new int[str1Length][str2Length];
        int maxlen = 0;
        int lastSubsBegin = 0;

        for (int i = 0; i < str1Length; i++) {
            for (int j = 0; j < str2Length; j++) {
                if (str1.charAt(i) != str2.charAt(j)) {
                    num[i][j] = 0;
                } else {
                    if (i == 0 || j == 0) {
                        num[i][j] = 1;
                    } else {
                        num[i][j] = 1 + num[i - 1][j - 1];
                    }

                    if (num[i][j] > maxlen) {
                        maxlen = num[i][j];
                        int thisSubsBegin = i - num[i][j] + 1;
                        if (lastSubsBegin == thisSubsBegin) {
                            // if the current LCS is the same as the last time this block ran
                            sequence.append(str1.charAt(i));
                        } else {
                            // this block resets the string builder if a different LCS is found
                            lastSubsBegin = thisSubsBegin;
                            sequence.setLength(0); // clear it
                            sequence.append(str1.substring(lastSubsBegin, i + 1));
                        }
                    }
                }
            }
        }
        return new LCSResult(maxlen, sequence.toString(), lastSubsBegin);
    }

    public static class LCSResult {
        public int length;
        public String sequence;
        public int offset;

        public LCSResult(int length, String sequence, int offset) {
            this.length = length;
            this.sequence = sequence;
            this.offset = offset;
        }
    }

    public static Integer findAllIndexes(List<String> arr, String value) {

        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }

}
