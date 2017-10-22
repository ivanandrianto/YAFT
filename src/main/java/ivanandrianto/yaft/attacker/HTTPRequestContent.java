/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import ivanandrianto.yaft.attacker.HTTPRequestPart.Content;
import java.util.ArrayList;

/**
 *
 * @author ivanandrianto
 */
public class HTTPRequestContent {
    /**
     * HTTPRequest Header Class.
     */
    public static class RequestHeaderContent {
        private HTTPRequestPart field;
        private HTTPRequestPart value;

        /**
         * Constructor.
         */
        public RequestHeaderContent() {

        }

        /**
         * Constructor.
         * @param field
         *      Nama dari header
         * @param value
         *      Value dari header
         */
        public RequestHeaderContent(HTTPRequestPart field, HTTPRequestPart value) {
            this.field = field;
            this.value = value;
        }

        /**
         * Copy Constructor.
         * @param requestHeader
         *      RequestHeader
         */
        public RequestHeaderContent(RequestHeaderContent requestHeader) {
            this.field = requestHeader.field;
            this.value = requestHeader.value;
        }

        /**
         * Menetapkan nama suatu header.
         * @param field
         *      Nama header
         */
        public void setHeaderField(HTTPRequestPart field) {
            this.field = field;
        }

        /**
         * Mendapatkan nama suatu header.
         * @return String
         *      Nama header
         */
        public HTTPRequestPart getHeaderField() {
            return field;
        }

        /**
         * Menetapkan isi suatu header.
         * @param value
         *      Isi header
         */
        public void setHeaderValue(HTTPRequestPart value) {
            this.value = value;
        }

        /**
         * Mendapatkan isi suatu header.
         * @return String
         *      Isi header
         */
        public HTTPRequestPart getHeaderValue() {
            return value;
        }
    }

    private HTTPRequestPart method;
    private HTTPRequestPart uri;
    private HTTPRequestPart httpVersion;
    private ArrayList<RequestHeaderContent> headers;
    private HTTPRequestPart body;

    /**
     * Constructor.
     */
    public HTTPRequestContent() {

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
    public HTTPRequestContent(HTTPRequestPart method, HTTPRequestPart uri, HTTPRequestPart httpVersion,
            ArrayList<RequestHeaderContent> headers, HTTPRequestPart body) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Copy constructor.
     * @param requestContent
     *      HTTPRequest
     */
    public HTTPRequestContent(HTTPRequestContent requestContent) {
        this.method = requestContent.method;
        this.uri = requestContent.uri;
        this.httpVersion = requestContent.httpVersion;
        this.body = requestContent.body;
        this.headers = new ArrayList<RequestHeaderContent>(requestContent.headers.size());
        for (RequestHeaderContent requestHeader : requestContent.headers) {
            this.headers.add(new RequestHeaderContent(requestHeader));
        }
    }

    /**
     * Menetapkan method dari request.
     * @param method
     *      Method dari request
     */
    public void setMethod(HTTPRequestPart method) {
        this.method = method;
    }

    /**
     * Mendapatkan method dari request.
     * @return HTTPRequestPart
      Method dari request
     */
    public HTTPRequestPart getMethod() {
        return method;
    }

    /**
     * Menetapkan URI dari request.
     * @param uri
     *      URI dari request
     */
    public void setURI(HTTPRequestPart uri) {
        this.uri = uri;
    }

    /**
     * Mendapatkan URI dari request.
     * @return HTTPRequestPart
      URI dari request
     */
    public HTTPRequestPart getURI() {
        return uri;
    }

    /**
     * Menetapkan versi HTTP dari request.
     * @param httpVersion
     *      Versi HTTP dari request
     */
    public void setHttpVersion(HTTPRequestPart httpVersion) {
        this.httpVersion = httpVersion;
    }

    /**
     * Mendapatkan versi HTTP dari request.
     * @return HTTPRequestPart
      Versi HTTP dari request
     */
    public HTTPRequestPart getHttpVersion() {
        return httpVersion;
    }

    /**
     * Menetapkan header pada indeks tertentu.
     * @param idx
     *      Indeks header yang ingin ditetapkan
     * @param requestHeader
     *      Value baru yang ingin ditetapkan
     */
    public void setHeaders(int idx, RequestHeaderContent requestHeader) {
        this.headers.set(idx, requestHeader);
    }

    /**
     * Menetapkan headers dari request.
     * @param headers
     *      Headers dari request
     */
    public void setHeaders(ArrayList<RequestHeaderContent> headers) {
        this.headers = headers;
    }

    /**
     * Mendapatkan headers dari request.
     * @return ArrayList<RequestHeader>
     *      Headers dari request
     */
    public ArrayList<RequestHeaderContent> getHeaders() {
        return headers;
    }

    /**
     * Menetapkan body dari request.
     * @param body
     *      Body dari request
     */
    public void setBody(HTTPRequestPart body) {
        this.body = body;
    }

    /**
     * Mendapatkan body dari request.
     * @return HTTPRequestPart
      Body dari request
     */
    public HTTPRequestPart getBody() {
        return body;
    }

    public HTTPRequest getRequest() {
        HTTPRequest request = new HTTPRequest();
        request.setMethod(method.getContentText());
        request.setURI(uri.getContentText());
        if (body != null) {
            request.setBody(body.getContentText());
        }
        request.setHttpVersion(httpVersion.getContentText());
        ArrayList<HTTPRequest.RequestHeader> requestHeaders = new ArrayList<>();
        for (int i = 0; i < headers.size(); i++) {
            HTTPRequest.RequestHeader requestHeader = new HTTPRequest.RequestHeader();
            requestHeader.setHeaderField(headers.get(i).getHeaderField().getContentText());
            requestHeader.setHeaderValue(headers.get(i).getHeaderValue().getContentText());
            requestHeaders.add(requestHeader);
        }
        request.setHeaders(requestHeaders);
        return request;
    }
}
