/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.configurator;

import ivanandrianto.yaft.utils.FileUtil;
import ivanandrianto.yaft.utils.XMLValidator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Macro Manager Class.
 * @author ivanandrianto
 */
public class MacroManager {
    private String project;
    private String macroFolder;
    private static final String MACRO_FILE_FORMAT = ".xml";
    private static final String XSD_PATH = "macro.xsd";

    /**
     * Constructor.
     * @param project
     *      Nama session
     */
    public MacroManager(String project) {
        this.project = project;
        macroFolder = "projects/" + project + "/macros/";
        File macroFolderFile = new File(macroFolder);
        if (!macroFolderFile.exists()) {
            macroFolderFile.mkdir();
        }
    }

    /**
     * Method ini membuat sebuah macro berdasarkan sekumpulan HTTP request yang
     * dipilih dari sekumpulan HTTP request yang telah dikumpulkan.
     * @param name
     *      Nama macro baru
     * @param files
     *      Sekumpulan path menuju file collected request
     * @return boolean
     *      Berhasil ataut tidak
     */
    public boolean addMacroFromCollectedRequests(String name, ArrayList<String>
            files) {
        //check exist
        if (isMacroExist(name + ".xml") || files.size() < 1) {
            return false;
        }
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("requests");
            doc.appendChild(rootElement);

            //create file based on selected

            for (int i = 0; i < files.size(); i++) {
                String fileName = files.get(i);
                String collectedRequestsFolder = "projects/" + project
                        + "/collectedRequests/";
                String filePath = collectedRequestsFolder + fileName;
                File fXmlFile = new File(filePath);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc2 = dBuilder.parse(fXmlFile);
                doc2.getDocumentElement().normalize();
                Node yaftNode = doc2.getFirstChild();
                Node importedYaftNode = doc.importNode(yaftNode, true);
                NodeList yaftChildNodes = importedYaftNode.getChildNodes();
                for (int j = 0; j < yaftChildNodes.getLength(); j++) {
                    Node n = yaftChildNodes.item(j);
                    if ((n.getNodeType() == Node.ELEMENT_NODE)
                            && (n.getNodeName() == "request")) {
                        rootElement.appendChild(n);
                        break;
                    }
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}"
                    + "indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            FileUtil.createDirectoryIfNotExists(macroFolder);
            StreamResult result = new StreamResult(new File(macroFolder
                    + name + ".xml"));

            transformer.transform(source, result);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return false;
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            return false;
        } catch (SAXException ex) {
            Logger.getLogger(MacroManager.class.getName()).log(
                    Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(MacroManager.class.getName()).log(
                    Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Membuat file macro baru.
     * @param fileName
     *      Nama file macro baru yang ingin dibuat
     * @param content
     *      Isi dari file macro baru yang ingin dibuat
     * @return boolean
     *      Berhasil atau tidak

     */
    public boolean addNewMacro(String fileName, String content) {
        if (!XMLValidator.validateXMLString(XSD_PATH, content)) {
            return false;
        }
        if (isMacroExist(fileName + ".xml")) {
            return false;
        }

        String filePath = macroFolder + fileName + ".xml";
        if (!FileUtil.createNewFile(filePath, content)) {
            return false;
        }
        //Check validity juga haruse
        return true;
    }

    /**
     * Method ini memeriksa apakah macro dengan nama tertentu sudah ada.
     * @param name
     *      Nama file
     * @return boolean
     *      Apakah macro dengan nama yang diberikan sudah ada
     */
    private boolean isMacroExist(String name) {
        String filePath = macroFolder + name;
        File file = new File(filePath);
        return file.exists();
    }

//    /**
//     * Membaca sebuah file macro untuk mendapatkan request apa saja yang
//     * terdapat di file macro tersebut.
//     * @param fileName
//     *      Nama file macro yang akan dibaca
//     * @return ArrayList<Request>
//      Sekumpulan HTTPRequest yang terdapat pada suatu file macro
//     */
//    public ArrayList<HTTPRequest> getMacroRequests(String fileName) {
//        ArrayList<HTTPRequest> requests = new ArrayList<HTTPRequest>();
//
//        try {
//            File fXmlFile = new File(macroFolder + fileName);
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
//                    .newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(fXmlFile);
//            doc.getDocumentElement().normalize();
//
//            Node requestsNode = doc.getFirstChild();
//            NodeList requestsChilds = requestsNode.getChildNodes();
//
//            for (int i = 0; i < requestsChilds.getLength(); i++) {
//                Node requestNode = requestsChilds.item(i);
//                if (requestNode.getNodeType() != Node.ELEMENT_NODE) {
//                    continue;
//                }
//                HTTPRequest request = getRequest(requestNode);
//                requests.add(request);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return requests;
//    }

//    /**
//     * Memperoleh request.
//     * @param requestNode
//     *      Node request
//     * @return HTTPRequest
//      Objek HTTPRequest
//     */
//    private HTTPRequest getRequest(Node requestNode) {
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

//    /**
//     * Mem-parse isi request line dari suatu request pada macro.
//     * @param requestLineNode
//     *      Node request line
//     * @param request
//     *      Objek HTTPRequest
//     */
//    private void parseRequestLine(Node requestLineNode, HTTPRequest request) {
//        NodeList requestLineChilds = requestLineNode.getChildNodes();
//        String method = null;
//        String uri = null;
//        String httpVersion = null;
//
//        for (int i = 0; i < requestLineChilds.getLength(); i++) {
//            Node requestLineChildNode = requestLineChilds.item(i);
//            if (requestLineChildNode.getNodeType() != Node.ELEMENT_NODE) {
//                continue;
//            }
//            switch (requestLineChildNode.getNodeName().toLowerCase()) {
//                case "method":
//                    method = requestLineChildNode.getFirstChild()
//                            .getNodeValue();
//                    break;
//                case "uri":
//                    uri = requestLineChildNode.getFirstChild().getNodeValue();
//                    break;
//                case "httpVersion":
//                    httpVersion = requestLineChildNode.getFirstChild()
//                            .getNodeValue();
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        request.setMethod(method);
//        request.setURI(uri);
//        request.setHttpVersion(httpVersion);
//    }

//    /**
//     * Mem-parse isi headers dari suatu request pada macro.
//     * @param headersNode
//     *      Node headers
//     * @param request
//     *      Objek HTTPRequest
//     */
//    private void parseHeaders(Node headersNode, HTTPRequest request) {
//        NodeList headersNodeChildNodes = headersNode.getChildNodes();
//        ArrayList<HTTPRequest.RequestHeader> headers =
//                new ArrayList<HTTPRequest.RequestHeader>();
//        for (int i = 0; i < headersNodeChildNodes.getLength(); i++) {
//            Node headerNode = headersNodeChildNodes.item(i);
//            if (headerNode.getNodeType() != Node.ELEMENT_NODE) {
//                continue;
//            }
//
//            RequestHeader requestHeader = new RequestHeader();
//            NodeList headerChildNodes = headerNode.getChildNodes();
//            for (int j = 0; j < headerChildNodes.getLength(); j++) {
//
//                Node headerChildNode = headerChildNodes.item(j);
//                if (headerChildNode.getNodeType() != Node.ELEMENT_NODE) {
//                    continue;
//                }
//
//                switch (headerChildNode.getNodeName()) {
//                    case "key":
//                        requestHeader.setHeaderField(headerChildNode
//                                .getFirstChild().getNodeValue());
//                        break;
//                    case "value":
//                        requestHeader.setHeaderValue(headerChildNode
//                                .getFirstChild().getNodeValue());
//                        break;
//                    default:
//                        break;
//                }
//            }
//            headers.add(requestHeader);
//        }
//        request.setHeaders(headers);
//    }
//
//    /**
//     * Mem-parse isi body dari suatu request pada macro.
//     * @param bodyNode
//     *      Node body
//     * @param request
//     *      Objek HTTPRequest
//     */
//    private void parseBody(Node bodyNode, HTTPRequest request) {
//        request.setBody(bodyNode.getFirstChild().getNodeValue());
//    }

    /**
     * Method ini membaca isi dari sebuah file macro.
     * @param fileName
     *      Nama file macro yang ingin dibaca
     * @return Strin
     *      Isi file macro
     */
    public String readMacro(String fileName) {
        String filePath = macroFolder + fileName + MACRO_FILE_FORMAT;
        return FileUtil.read(filePath);
    }

    /**
     * Mengubah isi dari sebuah file macro.
     * @param fileName
     *      Nama file macro yang ingin diubah
     * @param content
     *      Isi baru untuk file macro.
     */
    public boolean editMacro(String fileName, String content) {
        if (!XMLValidator.validateXMLString(XSD_PATH, content)) {
            return false;
        }
        String filePath = macroFolder + fileName;
        return FileUtil.modify(filePath, content);
    }

    /**
     * Menghapus sebuah file macro.
     * @param fileName
     *      Nama file macro yang ingin dihapus
     */
    public void deleteMacro(String fileName) {
        String filePath = macroFolder + fileName;
        FileUtil.delete(filePath);
    }

    /**
     * Mendapatkan daftar macro yang ada.
     * @return ArrayList<String>
     *      Daftar macro yang ada
     */
    public ArrayList<String> listMacro() {
        return FileUtil.getFileList(macroFolder, ".xml", false);
    }

    /**
     * Mendapatkan nama folder yang menyimpan macro.
     * @return String
     *      Nama folder macro
     */
    public String getMacroFolder() {
        return macroFolder;
    }
}
