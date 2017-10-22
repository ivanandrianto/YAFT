/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.attacker;

/**
 *
 * @author ivanandrianto
 */

import ivanandrianto.yaft.attacker.HTTPRequestPart.Content;
import ivanandrianto.yaft.configurator.RCFInfo;
import ivanandrianto.yaft.utils.FileUtil;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * RCF Class.
 * @author ivanandrianto
 */
public class RCFReader {

    private HTTPRequestConfig requestConfig;
    private String filePath;
    private Map<String, Integer> combinations;

    /**
     * Constructor.
     * @param filePath
     *      Path menuju file RCF
     */
    public RCFReader(String filePath) {
        requestConfig = new HTTPRequestConfig();
        this.filePath = filePath;
        combinations = new HashMap<String, Integer>();
    }

    /**
     * Membaca request configuration file.
     * @return HTTPRequestConfig
      Konfigurasi request
     */
    public HTTPRequestConfig read() {
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            Node rootNode = doc.getFirstChild();
            NodeList rootChilds = rootNode.getChildNodes();

            for (int i = 0; i < rootChilds.getLength(); i++) {
                Node rootChildNode = rootChilds.item(i);
                if (rootChildNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                switch (rootChildNode.getNodeName()) {
                    case "before":
                        readMacroConfig(rootChildNode);
                        break;
                    case "request":
                        readRequestNodeConfig(rootChildNode);
                        break;
                    case "after":
//                        readAfterNodeConfig(rootChildNode);
                        break;
                    case "expects":
                        readExpectConfig(rootChildNode);
                        break;
                    case "timeout":
                        int timeout = Integer.parseInt(rootChildNode
                                .getFirstChild().getNodeValue());
                        requestConfig.setTimeout(timeout);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestConfig;
    }

//    private void readBeforeNodeConfig(Node beforeNode) {
//        NodeList beforeNodeChilds = beforeNode.getChildNodes();
//        for (int i = 0; i < beforeNodeChilds.getLength(); i++) {
//            Node beforeChildNode = beforeNodeChilds.item(i);
//            if (beforeChildNode.getNodeType() != Node.ELEMENT_NODE) {
//                continue;
//            }
//            switch (beforeChildNode.getNodeName()) {
//                case "macros":
//                    readMacroConfig(beforeChildNode);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    /**
     * Membaca konfigurasi request.
     * @param requestNode
     *      Node request.
     */
    private void readRequestNodeConfig(Node requestNode) {
        NodeList requestNodeChilds = requestNode.getChildNodes();
        for (int i = 0; i < requestNodeChilds.getLength(); i++) {
            Node requestChildNode = requestNodeChilds.item(i);
            if (requestChildNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (requestChildNode.getNodeName()) {
                case "requestline":
                    readRequestLineConfig(requestChildNode);
                    break;
                case "headers":
                    readHeadersConfig(requestChildNode);
                    break;
                case "body":
                    readBodyConfig(requestChildNode);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Membaca konfigurasi macro.
     * @param beforeNode
     *      Before node
     */
    private void readMacroConfig(Node beforeNode) {
        NodeList macrosChildNodes = beforeNode.getChildNodes();
        ArrayList<HTTPRequestConfig.MacroConfig> macrosConfig =
                new ArrayList<HTTPRequestConfig.MacroConfig>();

        for (int i = 0; i < macrosChildNodes.getLength(); i++) {
            Node macroNode = macrosChildNodes.item(i);
            if (macroNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String fileName = macroNode.getChildNodes().item(0).getNodeValue();
            Node idNode = macroNode.getAttributes().getNamedItem("id");
            String id = null;
            if (idNode != null) {
                id = idNode.getNodeValue();
            }
            Node partNode = macroNode.getAttributes().getNamedItem("part");
            String part = null;
            if (partNode != null) {
                part = partNode.getNodeValue();
            }
            HTTPRequestConfig.MacroConfig macroConfig = new HTTPRequestConfig
                    .MacroConfig(id, fileName, part);
            macrosConfig.add(macroConfig);
        }

        requestConfig.setMacrosConfig(macrosConfig);
    }

    /**
     * Membaca konfigursi request line.
     * @param requestLineNode
     *      Node request line
     */
    private void readRequestLineConfig(Node requestLineNode) {
        NodeList requestLineChildNodes = requestLineNode.getChildNodes();

        for (int i = 0; i < requestLineChildNodes.getLength(); i++) {
            Node n = requestLineChildNodes.item(i);
            switch (n.getNodeName()) {
                case "method":
                    HTTPRequestPart methodRequestPart = getTextAndFuzzedParts(
                            n.getChildNodes());
                    requestConfig.setMethodConfig(methodRequestPart);
                    break;
                case "uri":
                    HTTPRequestPart uriRequestPart = getTextAndFuzzedParts(
                            n.getChildNodes());
                    requestConfig.setUriConfig(uriRequestPart);
                    break;
                case "httpVersion":
                    HTTPRequestPart httpVersionRequestPart = getTextAndFuzzedParts(
                            n.getChildNodes());
                    requestConfig.setHttpVersionConfig(httpVersionRequestPart);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Membaca konfigurasi headers.
     * @param headersNode
     *      Node headers
     */
    private void readHeadersConfig(Node headersNode) {
        NodeList headersNodeChildNodes = headersNode.getChildNodes();
        ArrayList<HTTPRequestConfig.RequestHeadersConfig> requestHeadersConfig =
                new ArrayList<HTTPRequestConfig.RequestHeadersConfig>();
        for (int i = 0; i < headersNodeChildNodes.getLength(); i++) {
            Node headerNode = headersNodeChildNodes.item(i);
            if (headerNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            NodeList headerNodeChildNodes = headerNode.getChildNodes();
            HTTPRequestConfig.RequestHeadersConfig requestHeaderConfig =
                    new HTTPRequestConfig.RequestHeadersConfig();
            for (int j = 0; j < headerNodeChildNodes.getLength(); j++) {
                Node n = headerNodeChildNodes.item(j);
                switch (n.getNodeName()) {
                    case "key":
                        HTTPRequestPart headerKeyRequestPart =
                                getTextAndFuzzedParts(n.getChildNodes());
                        requestHeaderConfig.setHeaderField(
                                headerKeyRequestPart);
                        break;
                    case "value":
                        HTTPRequestPart headerValueRequestPart =
                                getTextAndFuzzedParts(n.getChildNodes());
                        requestHeaderConfig.setHeaderValue(
                                headerValueRequestPart);
                        break;
                    default:
                        break;
                }
            }
            requestHeadersConfig.add(requestHeaderConfig);
        }
        requestConfig.setHeadersConfig(requestHeadersConfig);
    }

    /**
     * Membaca konfigurasi body.
     * @param bodyNode
     *      Node body
     */
    private void readBodyConfig(Node bodyNode) {
        HTTPRequestPart bodyRequestPart = getTextAndFuzzedParts(bodyNode
                .getChildNodes());
        requestConfig.setBodyConfig(bodyRequestPart);
    }

    /**
     * Membaca konfigurasi expects.
     * @param expectsNode
     *      Node expects
     */
    private void readExpectConfig(Node expectsNode) {
        NodeList expectsChildNodes = expectsNode.getChildNodes();
        ArrayList<HTTPRequestConfig.ResponseExpect> responseExpects =
                new ArrayList<HTTPRequestConfig.ResponseExpect>();

        for (int i = 0; i < expectsChildNodes.getLength(); i++) {
            Node expectNode = expectsChildNodes.item(i);
            if ((expectNode.getNodeType() != Node.ELEMENT_NODE)
                    || (expectNode.getNodeName() != "expect")) {
                continue;
            }
            HTTPRequestConfig.ResponseExpect responseExpect = new HTTPRequestConfig
                    .ResponseExpect();
            responseExpect.setMatch(true); //default
            NodeList expectChildNodes = expectNode.getChildNodes();

            for (int j = 0; j < expectChildNodes.getLength(); j++) {
                Node n = expectChildNodes.item(j);
                switch (n.getNodeName()) {
                    case "responseBodyFormat":
                        String responseBodyFormat = n.getFirstChild()
                                .getNodeValue();
                        responseExpect.setResponseBodyFormat(responseBodyFormat);
                        break;
                    case "responseBodyElement":
                        String responseBodyElement = n.getFirstChild()
                                .getNodeValue();
                        responseExpect.setResponseBodyElement(responseBodyElement);
                        break;
                    case "validationType":
                        String validationType = n.getFirstChild()
                                .getNodeValue();
                        responseExpect.setValidationType(validationType);
                        break;
                    case "content":
                        String content = n.getFirstChild().getNodeValue();
                        responseExpect.setContent(content);
                        break;
                    case "match":
                        boolean exist = (n.getFirstChild().getNodeValue()
                                .equals("true")) ? true : false;
                        responseExpect.setMatch(exist);
                        break;
                    case "exceptionType":
                        String exceptionType = n.getFirstChild().getNodeValue();
                        responseExpect.setExceptionType(exceptionType);
                        break;
                    case "exceptionContent":
                        String exceptionContent = n.getFirstChild()
                                .getNodeValue();
                        responseExpect.setExceptionContent(exceptionContent);
                        break;
                    default:
                        break;
                }
            }
            responseExpects.add(responseExpect);
        }
        requestConfig.setResponseExpect(responseExpects);
    }

    /**
     * Mendapatkan teks dan daftar bagian yang memerlukan pembangkitan input.
     * @param nodeList
     *      Daftar node
     * @return HTTPRequestPart
      RequesPart
     */
    private HTTPRequestPart getTextAndFuzzedParts(NodeList nodeList) {
        ArrayList<FuzzedPart> fuzzedParts = new ArrayList<FuzzedPart>();
        ArrayList<Content> contents = new ArrayList<Content>();
        String content = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n2 = nodeList.item(i);
            if (n2.getNodeType() == Node.TEXT_NODE) {
                String text = n2.getNodeValue();
                contents.add(new Content(text, null));
                content += n2.getNodeValue().replaceAll("%s", "%%s");
            } else if ((n2.getNodeType() == Node.ELEMENT_NODE) && (n2
                    .getNodeName().equals("fuzz"))) {
                String cname = null;
                int cid = -1;
                String generationType = n2.getAttributes().getNamedItem("type")
                        .getNodeValue();
                JSONObject generationOptions = new JSONObject();
                for (int j = 0; j < n2.getAttributes().getLength(); j++) {
                    String key = n2.getAttributes().item(j).getNodeName();
                    if (!key.equals("type")) {
                        String atrValue = n2.getAttributes().item(j)
                                .getNodeValue().toLowerCase();
                        if (key.equals("combination")) {
                            if (combinations.containsKey(atrValue)) {
                                combinations.put(atrValue, combinations
                                        .get(atrValue) + 1);
                            } else {
                                combinations.put(atrValue, 1);
                            }
                            cname = atrValue;
                            cid = combinations.get(cname);
                        } else {
                            generationOptions.put(key, atrValue);
                        }
                    }
                }
                Node defaultValueNode = n2.getFirstChild();
                String defaultValue = (defaultValueNode != null)
                        ? defaultValueNode.getNodeValue() : null;
                content += "%s";
                FuzzedPart.Generation generation = new FuzzedPart.Generation(
                        generationType, generationOptions);
                FuzzedPart fuzzedPart = new FuzzedPart(defaultValue,
                        generation);
                if ((cname != null) && (cid != -1)) {
                    fuzzedPart.setCombinationName(cname);
                    fuzzedPart.setCid(cid);
                }
                contents.add(new Content(null, fuzzedPart));
                fuzzedParts.add(fuzzedPart);
            } else if ((n2.getNodeType() == Node.ELEMENT_NODE) && (n2
                    .getNodeName().equals("macro"))) {
                String id = n2.getAttributes().getNamedItem("id")
                        .getNodeValue();
                
                Node defaultValueNode = n2.getFirstChild();
                String defaultValue = (defaultValueNode != null)
                        ? defaultValueNode.getNodeValue() : null;
                content += "%s";
                JSONObject generationOptions = new JSONObject();
                generationOptions.put("id", id);
                FuzzedPart.Generation generation = new FuzzedPart.Generation(
                        "macro", generationOptions);
                FuzzedPart fuzzedPart = new FuzzedPart(defaultValue,
                        generation);
                contents.add(new Content(null, fuzzedPart));
                fuzzedParts.add(fuzzedPart);
            }
        }
        HTTPRequestPart requestPart = new HTTPRequestPart(content, fuzzedParts,
                contents);
        return requestPart;
    }

    /**
     * Mendapatkan informasi mengenai suatu request configuration file.
     * @param fuzzFolder
     *      Path menuju folder yang berisi sekumpulan RCF dari suatu session
     * @param fileName
     *      Nama file
     * @return RCFInfo
     *      Informasi mengenai suatu request configuration file
     */
    public static RCFInfo getRCFInfo(String fuzzFolder, String fileName) {
        String filePath = fuzzFolder + fileName;
        if (!FileUtil.isFileExists(filePath)) {
            return null;
        }
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            Node rootNode = doc.getFirstChild();
            NodeList rootChilds = rootNode.getChildNodes();

            String method = null;
            String uri = null;

            for (int i = 0; i < rootChilds.getLength(); i++) {
                Node rootChildNode = rootChilds.item(i);

                if ((rootChildNode.getNodeType() != Node.ELEMENT_NODE)
                        && (rootChildNode.getNodeName().equals("request"))) {
                    continue;
                }

                NodeList requestChilds = rootChildNode.getChildNodes();

                for (int j = 0; j < requestChilds.getLength(); j++) {
                    Node requestChildNode = requestChilds.item(j);
                    if (requestChildNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    switch (requestChildNode.getNodeName()) {
                        case "requestline":
                            NodeList requestLineChilds = requestChildNode
                                    .getChildNodes();
                            for (int k = 0; k < requestLineChilds.getLength();
                                    k++) {
                                Node requestLineChildNode = requestLineChilds
                                        .item(k);
                                if (requestLineChildNode.getNodeType()
                                        != Node.ELEMENT_NODE) {
                                    continue;
                                }
                                switch (requestLineChildNode.getNodeName()
                                        .toLowerCase()) {
                                    case "method":
                                        method = requestLineChildNode
                                                .getFirstChild().getNodeValue();
                                        break;
                                    case "uri":
                                        uri = requestLineChildNode
                                                .getFirstChild().getNodeValue();
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        default:
                            break;
                    }

                    if ((method != null) && (uri != null)) {
                        return new RCFInfo(method, uri, fileName);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Memperoleh daftar kombinasi input beserta jumlahnya masing-masing.
     * @return Map<String, Integer>
     *      Kombinasi input beserta jumlahnya masing-masing
     */
    public Map<String, Integer> getCombinations() {
        return combinations;
    }
}
