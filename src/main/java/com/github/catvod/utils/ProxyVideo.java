package com.github.catvod.utils;

public class ProxyVideo {

//    public static NanoHTTPD.Response proxy(String url, Map<String, String> headers) throws Exception {
//        NanoHTTPD.Response.Status status = NanoHTTPD.Response.Status.PARTIAL_CONTENT;
//        if (!headers.containsKey("Range")) {
//            headers.put("Range", "bytes=0-");
//            status = NanoHTTPD.Response.Status.OK;
//        }
//        Response response = OkHttp.newCall(url, headers);
//        String contentType = response.headers().get("Content-Type");
//        String hContentLength = response.headers().get("Content-Length");
//        String contentDisposition = response.headers().get("Content-Disposition");
//        long contentLength = hContentLength != null ? Long.parseLong(hContentLength) : 0;
//        if (contentDisposition != null) contentType = Utils.getMimeType(contentDisposition);
////        new HttpResponse(HttpProtocolVersion.Companion.getHTTP_1_1(), HttpStatusCode.Companion.getFound().getValue(),
////                HttpStatusCode.Companion.getFound().getDescription(), CharArrayBuilder.)
//        return newFixedLengthResponse(status, contentType, response.body().byteStream(), contentLength);
//    }
}
