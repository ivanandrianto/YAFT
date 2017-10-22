/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author ivanandrianto
 */
public class MacroReader {
    private String fileName;
    
    /**
     * Constructor.
     * @param fileName 
     *      The file name
     */
    public MacroReader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Membaca sebuah file macro untuk mendapatkan request apa saja yang
     * terdapat di file macro tersebut.
     * @return ArrayList<Request>
      Sekumpulan HTTPRequest yang terdapat pada suatu file macro
     */
    public ArrayList<HTTPRequest> getMacroRequests() {
        ArrayList<HTTPRequest> requests = new ArrayList<HTTPRequest>();

        try {
            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            Node requestsNode = doc.getFirstChild();
            NodeList requestsChilds = requestsNode.getChildNodes();

            for (int i = 0; i < requestsChilds.getLength(); i++) {
                Node requestNode = requestsChilds.item(i);
                if (requestNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                HTTPRequest request = getMacroRequest(requestNode);
                requests.add(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    /**
     * Memperoleh request dari suatu node XML.
     * @param requestNode
     *      Node request
     * @return HTTPRequest
      Objek HTTPRequest
     */
    private HTTPRequest getMacroRequest(Node requestNode) {
        HTTPRequest request = new HTTPRequest();
        NodeList requestChilds  = requestNode.getChildNodes();
        for (int i = 0; i < requestChilds.getLength(); i++) {
            Node requestChildNode = requestChilds.item(i);
            switch (requestChildNode.getNodeName()) {
                case "requestline":
                    parseRequestLine(requestChildNode, request);
                    break;
                case "headers":
                    parseHeaders(requestChildNode, request);
                    break;
                case "body":
                    parseBody(requestChildNode, request);
                    break;
                default:
                    break;
            }
        }
        return request;
    }

//    /**
//     * Memperoleh request.
//     * @param requestNode
//     *      Node request
//     * @return HTTPRequest
//      Objek HTTPRequest
//     */
//    private HTTPRequest getMacroRequest(Node requestNode) {
//        HTTPRequest request = new HTTPRequest();
//        NodeList requestChilds  = requestNode.getChildNodes();
//        for (int i = 0; i < requestChilds.getLength(); i++) {
//            Node requestChildNode = requestChilds.item(i);
//            switch (requestChildNode.getNodeName()) {
//                case "requestline":
//                    parseRequestLine(requestChildNode, request);
//                    break;
//                case "headers":
//                    parseHeaders(requestChildNode, request);
//                    break;
//                case "body":
//                    parseBody(requestChildNode, request);
//                    break;
//                default:
//                    break;
//            }
//        }
//        return request;
//    }

    /**
     * Mem-parse isi request line dari suatu Node request
     * @param requestLineNode
     *      Node request line
     * @param request
     *      Objek HTTPRequest
     */
    private void parseRequestLine(Node requestLineNode, HTTPRequest request) {
        NodeList requestLineChilds = requestLineNode.getChildNodes();
        String method = null;
        String uri = null;
        String httpVersion = null;

        for (int i = 0; i < requestLineChilds.getLength(); i++) {
            Node requestLineChildNode = requestLineChilds.item(i);
            if (requestLineChildNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (requestLineChildNode.getNodeName().toLowerCase()) {
                case "method":
                    method = requestLineChildNode.getFirstChild()
                            .getNodeValue();
                    break;
                case "uri":
                    uri = requestLineChildNode.getFirstChild().getNodeValue();
                    break;
                case "httpVersion":
                    httpVersion = requestLineChildNode.getFirstChild()
                            .getNodeValue();
                    break;
                default:
                    break;
            }
        }

        request.setMethod(method);
        request.setURI(uri);
        request.setHttpVersion(httpVersion);
    }

    /**
     * Mem-parse isi headers dari suatu node request.
     * @param headersNode
     *      Node headers
     * @param request
     *      Objek HTTPRequest
     */
    private void parseHeaders(Node headersNode, HTTPRequest request) {
        NodeList headersNodeChildNodes = headersNode.getChildNodes();
        ArrayList<HTTPRequest.RequestHeader> headers =
                new ArrayList<HTTPRequest.RequestHeader>();
        for (int i = 0; i < headersNodeChildNodes.getLength(); i++) {
            Node headerNode = headersNodeChildNodes.item(i);
            if (headerNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            HTTPRequest.RequestHeader requestHeader = new HTTPRequest.RequestHeader();
            NodeList headerChildNodes = headerNode.getChildNodes();
            for (int j = 0; j < headerChildNodes.getLength(); j++) {

                Node headerChildNode = headerChildNodes.item(j);
                if (headerChildNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                switch (headerChildNode.getNodeName()) {
                    case "key":
                        requestHeader.setHeaderField(headerChildNode
                                .getFirstChild().getNodeValue());
                        break;
                    case "value":
                        requestHeader.setHeaderValue(headerChildNode
                                .getFirstChild().getNodeValue());
                        break;
                    default:
                        break;
                }
            }
            headers.add(requestHeader);
        }
        request.setHeaders(headers);
    }

    /**
     * Mem-parse isi body dari suatu node request.
     * @param bodyNode
     *      Node body
     * @param request
     *      Objek HTTPRequest
     */
    private void parseBody(Node bodyNode, HTTPRequest request) {
        request.setBody(bodyNode.getFirstChild().getNodeValue());
    }

}
