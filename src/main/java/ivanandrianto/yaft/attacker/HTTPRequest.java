/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import java.util.ArrayList;

/**
 *  HTTPRequest Class.
 * @author ivanandrianto
 */
public class HTTPRequest {
    /**
     * HTTPRequest Header Class.
     */
    public static class RequestHeader {
        private String field;
        private String value;

        /**
         * Constructor.
         */
        public RequestHeader() {

        }

        /**
         * Constructor.
         * @param field
         *      Nama dari header
         * @param value
         *      Value dari header
         */
        public RequestHeader(String field, String value) {
            this.field = field;
            this.value = value;
        }

        /**
         * Copy Constructor.
         * @param requestHeader
         *      RequestHeader
         */
        public RequestHeader(RequestHeader requestHeader) {
            this.field = requestHeader.field;
            this.value = requestHeader.value;
        }

        /**
         * Menetapkan nama suatu header.
         * @param field
         *      Nama header
         */
        public void setHeaderField(String field) {
            this.field = field;
        }

        /**
         * Mendapatkan nama suatu header.
         * @return String
         *      Nama header
         */
        public String getHeaderField() {
            return field;
        }

        /**
         * Menetapkan isi suatu header.
         * @param value
         *      Isi header
         */
        public void setHeaderValue(String value) {
            this.value = value;
        }

        /**
         * Mendapatkan isi suatu header.
         * @return String
         *      Isi header
         */
        public String getHeaderValue() {
            return value;
        }
    }

    private String method;
    private String uri;
    private String httpVersion;
    private ArrayList<RequestHeader> headers;
    private String body;

    /**
     * Constructor.
     */
    public HTTPRequest() {

    }

    /**
     * Constructor.
     * @param method
     *      Method dari request
     * @param uri
     *      URI dari request
     * @param httpVersion
     *      Versi HTTP dari request
     * @param headers
     *      Headers dari request
     * @param body
     *      Body dari request
     */
    public HTTPRequest(String method, String uri, String httpVersion,
            ArrayList<RequestHeader> headers, String body) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Copy constructor.
     * @param request
     *      Request
     */
    public HTTPRequest(HTTPRequest request) {
        this.method = request.method;
        this.uri = request.uri;
        this.httpVersion = request.httpVersion;
        this.body = request.body;
        this.headers = new ArrayList<RequestHeader>(request.headers.size());
        for (RequestHeader requestHeader : request.headers) {
            this.headers.add(new RequestHeader(requestHeader));
        }
    }

    /**
     * Menetapkan method dari request.
     * @param method
     *      Method dari request
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Mendapatkan method dari request.
     * @return String
     *      Method dari request
     */
    public String getMethod() {
        return method;
    }

    /**
     * Menetapkan URI dari request.
     * @param uri
     *      URI dari request
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Mendapatkan URI dari request.
     * @return String
     *      URI dari request
     */
    public String getURI() {
        return uri;
    }

    /**
     * Menetapkan versi HTTP dari request.
     * @param httpVersion
     *      Versi HTTP dari request
     */
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    /**
     * Mendapatkan versi HTTP dari request.
     * @return String
     *      Versi HTTP dari request
     */
    public String getHttpVersion() {
        return httpVersion;
    }

    /**
     * Menetapkan headers pada indeks tertentu.
     * @param idx
     *      Indeks header yang ingin ditetapkan
     * @param requestHeader
     *      Value baru yang ingin ditetapkan
     */
    public void setHeaders(int idx, RequestHeader requestHeader) {
        this.headers.set(idx, requestHeader);
    }

    /**
     * Menetapkan headers dari request.
     * @param headers
     *      Headers dari request
     */
    public void setHeaders(ArrayList<RequestHeader> headers) {
        this.headers = headers;
    }

    /**
     * Mendapatkan headers dari request.
     * @return ArrayList<RequestHeader>
     *      Headers dari request
     */
    public ArrayList<RequestHeader> getHeaders() {
        return headers;
    }

    /**
     * Menetapkan body dari request.
     * @param body
     *      Body dari request
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Mendapatkan body dari request.
     * @return String
     *      Body dari request
     */
    public String getBody() {
        return body;
    }

    /**
     * Mendapatkan objek HTTPRequest dalam bentuk String.
     * @return String
      Obbjek HTTPRequest dalam bentuk String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method + " " + uri + " " + httpVersion + "\n");
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.get(i).getHeaderField() + ":" + headers.get(i)
                    .getHeaderValue() + "\n");
        }
        sb.append("\n");
        if ((method.toUpperCase().equals("POST")) || (method.toUpperCase()
                .equals("PUT")) || (method.toUpperCase().equals("PATCH"))) {
            sb.append(body);
        }

        return sb.toString();
    }
}
