/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import java.util.ArrayList;

/**
 * HTTPResponse Class.
 * @author ivanandrianto
 */
public class HTTPResponse {

    /**
     * HTTPResponse Header Class.
     */
    public static class ResponseHeader {
        private String field;
        private String value;

        /**
         * Constructor.
         * @param field
         *      Nama header
         * @param value
         *      Value header
         */
        public ResponseHeader(String field, String value) {
            this.field = field;
            this.value = value;
        }

        /**
         * Mendapatkan nama dari suatu header.
         * @return String
         *      Nama dari suatu header
         */
        public String getHeaderField() {
            return field;
        }

        /**
         * Mendapatkan isi dari suatu header.
         * @return String
         *      Isi dari suatu header
         */
        public String getHeaderValue() {
            return value;
        }
    }

    private String httpVersion;
    private int statusCode;
    private String reasonPhrase;
    private String body;
    private String lastUri;
    private ArrayList<ResponseHeader> responseHeaders;
    private boolean isTimeout;
    private ArrayList<String> alerts;

    /**
     * Constructor.
     */
    public HTTPResponse() {

    }

//    /**
//     * Constructor.
//     * @param httpVersion
//     *      Versi HTTP dari response
//     * @param statusCode
//     *      Status code dari response
//     * @param reasonPhrase
//     *      Reason phrase dari response
//     * @param body
//     *      Body dari response
//     * @param lastURI
//     *      URI terakhir dari response
//     * @param responseHeaders
//     *      Headers dari response
//     */
//    public HTTPResponse(String httpVersion, int statusCode, String reasonPhrase,
//            String body, String lastURI, ArrayList<ResponseHeader>
//            responseHeaders) {
//        this.httpVersion = httpVersion;
//        this.statusCode = statusCode;
//        this.reasonPhrase = reasonPhrase;
//        this.body = body;
//        this.lastUri = lastURI;
//        this.responseHeaders = responseHeaders;
//        isTimeout = false;
//    }

    /**
     * Menetapkan versi HTTP dari response.
     * @param httpVersion
     *      Versi HTTP dari response
     */
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    /**
     * Mendapatkan versi HTTP dari response.
     * @return String
     *      Versi HTTP dari response
     */
    public String getHttpVersion() {
        return httpVersion;
    }

    /**
     * Menetapkan status code dari response.
     * @param statusCode
     *      Status code dari response
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Mendapatkan status code dari response.
     * @return int
     *      Status code dari response
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Mengeset reason phrase dari response.
     * @param reasonPhrase
     *      Reason phrase dari response
     */
    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * Mendapatkan reason phrase dari response.
     * @return String
     *      Reason phrase dari response
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Mentepakan body dari response.
     * @param body
     *      Body dari response
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Mendapatkan body dari reponse.
     * @return String
     *      Body dari response
     */
    public String getBody() {
        return body;
    }

    /**
     * Menetapkan URI terakhir yang diperoleh.
     * @param lastUri
     *      URI terakhir yang diperoleh
     */
    public void setLastUri(String lastUri) {
        this.lastUri = lastUri;
    }

    /**
     * Mendapatkan URI terakhir yang diperoleh.
     * @return String
     *      URI terakhir yang diperoleh
     */
    public String getLastUri() {
        return lastUri;
    }

    /**
     * Menetapkan headers dari response.
     * @param responseHeaders
     *      Sekumpulan header dari response
     */
    public void setResponseHeaders(ArrayList<ResponseHeader> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * Mendapatkan headers dari response.
     * @return ArrayList<ResponseHeader>
     *      Sekumpulan header dari response
     */
    public ArrayList<ResponseHeader> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Menetapkan apakah terkena timeout atau tidak.
     * @param isTimeout
     *      Apakah terkena timeout atau tidak
     */
    public void setIsTimeout(boolean isTimeout) {
        this.isTimeout = isTimeout;
    }

    /**
     * Mendapatkan apakah terkena timeout atau tidak.
     * @return boolean
     *      Apakah terkena timeout atau tidak
     */
    public boolean getIsTimeout() {
        return isTimeout;
    }

    /**
     * Mengembalikan response dalam bentuk String.
     * @return String
      Objek HTTPResponse dalam bentuk String.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(httpVersion + " " + statusCode + " " + reasonPhrase + "\n");
        for (int i = 0; i < responseHeaders.size(); i++) {
            sb.append(responseHeaders.get(i).getHeaderField() + ":"
                    + responseHeaders.get(i).getHeaderValue() + "\n");
        }
        sb.append("\n");
        sb.append(body);

        return sb.toString();
    }

    /**
     * Mengembalikan response tanpa body.
     * @return String
      HTTPResponse tanpa body.
     */
    public String getResponseWithoutBody() {
        if (isTimeout) {
            return "Timeout";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(httpVersion + " " + statusCode + " " + reasonPhrase + "\n");
        for (int i = 0; i < responseHeaders.size(); i++) {
            sb.append(responseHeaders.get(i).getHeaderField() + ":"
                    + responseHeaders.get(i).getHeaderValue() + "\n");
        }
        return sb.toString();
    }

    /**
     * Menetapkan isi dari alert yang diperoleh.
     * @param alerts
     *      The alerts
     */
    public void setAlerts(ArrayList<String> alerts) {
        this.alerts = alerts;
    }

    /**
     * Mengembalikan isi dari alert yang diperoleh.
     * @return ArrayList<String>
     *      The alerts
     */
    public ArrayList<String> getAlerts() {
        return alerts;
    }
}
