/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 * XML Validator Class.
 * @author ivanandrianto
 */
public final class XMLValidator {

    /**
     * Prevent instantiation.
     */
    private XMLValidator() {

    }

    /**
     * Memeriksa apakah sebuah file XML valid terhadap XSD.
     * @param xsdPath
     *      The XSD file path
     * @param xmlPath
     *      The XML file path
     * @return boolean
     *      Valid or not
     */
    public static boolean validateXMLFile(String xsdPath, String xmlPath) {
        boolean isValid = true;
        Source xmlFile = new StreamSource(new File(xmlPath));
        try {
            File schemaFile = new File(xsdPath);
            SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
        } catch (SAXException e) {
            isValid = false;
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:"
            + e);
        } catch (IOException e) {
            isValid = false;
            e.printStackTrace();
        }
        return isValid;
    }

    /**
     * Memeriksa apakah sebuah string XML valid terhadap XSD.
     * @param xsdPath
     *      The XSD file path
     * @param xmlContent
     *      The XML content
     * @return boolean
     *      Valid or not
     */
    public static boolean validateXMLString(String xsdPath, String xmlContent) {
        StringReader reader = new StringReader(xmlContent);
        File schemaFile = new File(xsdPath);
        boolean isValid = true;
        try {
            SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator val = schema.newValidator();
            val.validate(new StreamSource(reader));
        } catch (SAXException e) {
            isValid = false;
            System.out.println("NOT valid reason:" + e);
        } catch (IOException e) {
            isValid = false;
            e.printStackTrace();
        }
        return isValid;
    }
}
