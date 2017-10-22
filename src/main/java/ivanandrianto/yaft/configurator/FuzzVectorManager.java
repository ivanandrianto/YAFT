/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.configurator;

import ivanandrianto.yaft.inputGenerator.InputGenerator;
import ivanandrianto.yaft.utils.FileUtil;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fuzz Vector Manager Class.
 * @author ivanandrianto
 */
public final class FuzzVectorManager {
    private static String fuzzVectorFolder = "fuzzVectors/";
    private static final String[] BUILT_IN_FUZZ_VECTORS = {"activeSQL",
        "passiveSQL", "LDAP", "pathTraversal", "XSS", "XML", "XPath", "SSI"
    };
    private static final String FUZZ_VECTOR_FILE_FORMAT = ".txt";

    /**
     * Prevent instantiation.
     */
    private FuzzVectorManager() {

    }

    /**
     * Mendapatkan daftar fuzz vector.
     * @return ArrayList<String>
     *      Daftar fuzz vector
     */
    public static ArrayList<String> getFuzzVectorList() {
        return FileUtil.getFileList(fuzzVectorFolder, FUZZ_VECTOR_FILE_FORMAT
                , false);
    }

    /**
     * Mendapatkan isi dari suatu fuzz vector.
     * @param fileName
     *      Nama file yang berisi fuzz vector
     * @return String
     *      Fuzz vector
     */
    public static String getContent(String fileName) {
        fileName = formatFileName(fileName);
        ArrayList<String> fuzzVectorList = getFuzzVectorList();
        for (int i = 0; i < fuzzVectorList.size(); i++) {
            if (fileName.equalsIgnoreCase(fuzzVectorList.get(i))) {
                return FileUtil.read(fuzzVectorFolder + fileName);
            }
        }
        return "";
    }

    /**
     * Mendapatkan isi dari suatu fuzz vector dalam bentuk ArrayList
     * bentuk list.
     * @param fileName
     *      Nama file
     * @return ArrayList<String>
     *      Isi dari suatu fuzz vector dalam bentuk ArrayList.
     */
    public static ArrayList<String> getContentAsList(String fileName) {
        ArrayList<String> content = new ArrayList<String>();
        fileName = formatFileName(fileName);
        String filePath = fuzzVectorFolder + fileName;

        if (!FileUtil.isFileExists(filePath)) {
            fileName = getFileNameIgnoreCase(fileName);
            if (fileName == null) {
                return content;
            }
            filePath = fuzzVectorFolder + fileName;
        }

        FileInputStream fstream;
        try {
            fstream = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    fstream));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                content.add(strLine);
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(FuzzVectorManager.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return content;
    }

    /**
     * Method ini berfungsi untuk mendapatkan nama file sebenarnya
     * dengan mengabaikan kapitalisasi.
     * @param fileName
     *      Nama file (case insensitive)
     * @return String
     *      Nama file sebenarnya (case sensitive)
     */
    private static String getFileNameIgnoreCase(String fileName) {
        fileName = formatFileName(fileName);
        ArrayList<String> fuzzVectorList = getFuzzVectorList();
        String realFileName = null;
        for (int i = 0; i < fuzzVectorList.size(); i++) {
            String n = fuzzVectorList.get(i);
            if (n.equalsIgnoreCase(fileName)) {
                realFileName = n;
                break;
            }
        }
        return realFileName;
    }

    /**
     * Membuat fuzz vector baru.
     * @param fileName
     *      Nama file
     * @param content
     *      Isi file
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean createNewFuzzVector(String fileName, String content) {
        if (InputGenerator.matchAnyInputGeneration(fileName)) {
            return false;
        }
        fileName = formatFileName(fileName);
        return FileUtil.createNewFile(fuzzVectorFolder + fileName, content);
    }

    /**
     * Method ini berfungsi untuk mengubah isi suatu fuzz vector.
     * @param fileName
     *      Nama file fuzz vector
     * @param content
     *      Isi baru untuk file fuzz vector yang bersangkutan
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean editFuzzVector(String fileName, String content) {
        if (!isEditable(fileName)) {
            return false;
        }
        return FileUtil.modify(fuzzVectorFolder + fileName, content);
    }

    /**
     * Menghapus sebuah file fuzz vector.
     * @param fileName
     *      Nama file fuzz vector
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean deleteFuzzVector(String fileName) {
        fileName = formatFileName(fileName);
        return FileUtil.delete(fuzzVectorFolder + fileName);
    }

    /**
     * Mengubah nama sebuah file fuzz vector.
     * @param oldName
     *      Nama lama
     * @param newName
     *      Nama baru
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean renameFuzzVector(String oldName, String newName) {
        if (InputGenerator.matchAnyInputGeneration(newName)) {
            return false;
        }
        if (!isEditable(oldName)) {
            return false;
        }
        newName = formatFileName(newName);
        oldName = formatFileName(oldName);
        return FileUtil.renameFile(fuzzVectorFolder + oldName,
                fuzzVectorFolder + newName);
    }

    /**
     * Memeriksa apakah sudah ada suatu fuzz vector dengan nama tertentu.
     * @param fileName
     *      Nama file fuzz vector
     * @return boolean
     *      Apakah sudah ada suatu fuzz vector dengan nama tertentu
     */
    public static boolean isNameExist(String fileName) {
        fileName = formatFileName(fileName);
        ArrayList<String> fuzzVectorList = FileUtil.getFileList(
                fuzzVectorFolder, FUZZ_VECTOR_FILE_FORMAT, false);
        boolean exists = false;
        for (int i = 0; i < fuzzVectorList.size(); i++) {
            if (fileName.equalsIgnoreCase(fuzzVectorList.get(i))) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    /**
     * Memeriksa apakah suatu file fuzz vector boleh diubah/dihapus atau tidak.
     * @param fileName
     *      Nama file
     * @return boolean
     *      Apakah suatu file fuzz vector boleh diubah/dihapus atau tidak
     */
    public static boolean isEditable(String fileName) {
        fileName = fileName.replaceAll(FUZZ_VECTOR_FILE_FORMAT, "");        
        boolean editable = true;
        for (int i = 0; i < BUILT_IN_FUZZ_VECTORS.length; i++) {
            if (BUILT_IN_FUZZ_VECTORS[i].equalsIgnoreCase(fileName)) {
                editable = false;
                break;
            }
        }
        return editable;
    }

    /**
     * Mengubah nama file agar sesuai dengan ekstensi.
     * @param fileName
     *      Nama file
     * @return String
     *      Nama file yang telah diformat
     */
    private static String formatFileName(String fileName) {
        if (!fileName.endsWith(FUZZ_VECTOR_FILE_FORMAT)) {
            fileName += FUZZ_VECTOR_FILE_FORMAT;
        }
        return fileName;
    }

}
