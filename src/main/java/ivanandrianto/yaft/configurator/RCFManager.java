/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.configurator;

import ivanandrianto.yaft.attacker.RCFReader;
import ivanandrianto.yaft.utils.FileUtil;
import ivanandrianto.yaft.utils.XMLValidator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Fuzz Manager Class.
 * @author ivanandrianto
 */
public class RCFManager {
    private String project;
    private static String fuzzFolder;
    private static final String XSD_PATH = "rcf.xsd";

    /**
     * Constructor.
     * @param project
     *      Nama session
     * @throws IOException
     *      Apabila terjadi kesalahan ketika membaca/menulis file
     */
    public RCFManager(String project) throws IOException {
        this.project = project;
        fuzzFolder = "projects/" + project + "/fuzzRequests/";
        File fuzzFolderFile = new File(fuzzFolder);
        if (!fuzzFolderFile.exists()) {
            fuzzFolderFile.mkdir();
        }
        File listFile = new File(fuzzFolder + "list.txt");
        if (!listFile.exists()) {
            listFile.createNewFile();
        }
    }

    /**
     * Menambahkan sekumpulan HTTP request yang ingin di fuzz,
     * berdasarkan sekumpulan HTTP request yang dipilih
     * dari sekumpulan HTTP request yang telah dikumpulkan.
     * @param files
     *      Sekumpulan nama file yang berisi HTTP request yang telah
     *      dikumpulkan, yang ingin ditambahkan menjadi RCF
     * @return boolean
     *      Berhasil atau tidak
     * @throws IOException
     *      Apabila terjadi kesalahan ketika membaca/menulis file
     */
    public boolean addRCFsFromCollectedRequests(ArrayList<String> files)
            throws IOException {
        if (files.size() < 1) {
            return false;
        }

        ArrayList<String> addedFiles = new ArrayList<String>();
        for (int i = 0; i < files.size(); i++) {
            int distinguishingNumber = 1;
            String name = files.get(i).replace(".xml$", "");
            String newFileName = name;
            while (isRCFExists(newFileName)) {
                newFileName = name + "_" + distinguishingNumber;
                distinguishingNumber++;
            }

            String originalFileName = "projects/" + project
                    + "/collectedRequests/" + files.get(i);
            Path originalFilePath = Paths.get(originalFileName);
            String destinationFile = fuzzFolder + newFileName;
            Path destinationFilePath = Paths.get(destinationFile);
            Files.copy(originalFilePath, destinationFilePath);
            addedFiles.add(newFileName);
        }

        //Add to list.txt
        StringBuilder listOfAddedFiles = new StringBuilder();
        for (int i = 0; i < addedFiles.size(); i++) {
            listOfAddedFiles.append("\n" + addedFiles.get(i));
        }
        String textToAppend = listOfAddedFiles.toString();
        String listFilePath = fuzzFolder + "list.txt";
        return FileUtil.appendTextToFile(listFilePath, textToAppend);
    }

    /**
     * Method ini digunakan untuk menambah RCF baru.
     * @param content
     *      Isi dari RCF baru
     * @return boolean
     *      Berhasil atau tidak
     */
    public boolean addNewRCF(String content) {
        if (!XMLValidator.validateXMLString(XSD_PATH, content)) {
            return false;
        }
        String currentTime = String.valueOf(System.currentTimeMillis());
        String fileName = currentTime + ".xml";
        int distinguishingNumber = 1;
        while (isRCFExists(fileName)) {
            fileName = currentTime + "_" + distinguishingNumber;
            distinguishingNumber++;
        }
        String filePath = fuzzFolder + fileName;
        if (!FileUtil.createNewFile(filePath, content)) {
            return false;
        }

        //Add to list.txt
        String textToAppend = "\n" + fileName;
        String listFilePath = fuzzFolder + "list.txt";
        return FileUtil.appendTextToFile(listFilePath, textToAppend);
    }

    /**
     * Method ini memeriksa apakah ada RCF dengan nama tertentu.
     * @param name
     *      Nama file
     * @return boolean
     *      Apakah ada RCF dengan nama tertentu.
     */
    private boolean isRCFExists(String name) {
        String filePath = fuzzFolder + name;
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Membaca isi dari sebuah RCF.
     * @param fileName
     *      Nama file
     * @return String
     *      Isi dari file RCF
     */
    public String readRCF(String fileName) {
        String filePath = fuzzFolder + fileName;
        return FileUtil.read(filePath);
    }

    /**
     * Mengubah isi dari sebuah RCF.
     * @param fileName
     *      Nama file RCF
     * @param content
     *      Isi baru untuk file RCF yang bersangkutan
     * @return boolean
     *      Behasil atau tidak
     */
    public boolean editRCF(String fileName, String content) {
        if (!XMLValidator.validateXMLString(XSD_PATH, content)) {
            return false;
        }
        String filePath = fuzzFolder + fileName;
        return FileUtil.modify(filePath, content);
    }

    /**
     * Menghapus sebuah file RCF.
     * @param fileName
     *      Nama RCF yang ingin dihapus
     * @return boolean
     *      Berhasil atau tidak
     */
    public boolean deleteRCF(String fileName) {
        String filePath = fuzzFolder + fileName;
        return FileUtil.delete(filePath);
    }

    /**
     * Mendapatkan daftar seluruh RCF sesuai urutan berdasarkan list.txt.
     * @return ArrayList<RCF.RCFInfo>
     *      Daftar RCFInfo dari RCF yang terdapat pada list.txt dan valid
     */
    public ArrayList<RCFInfo> listRCF() {
        String listFilePath = fuzzFolder + "list.txt";
        ArrayList<RCFInfo> rcfInfos = new ArrayList<RCFInfo>();

        String listFileContent = FileUtil.read(listFilePath);
        if (listFileContent == null) {
            return rcfInfos;
        }
        String[] filesInList = listFileContent.split("\n");

        ArrayList<String> existFiles = new ArrayList<String>();
        boolean allExistAndValid = true;

        for (int i = 0; i < filesInList.length; i++) {
            String fileName = filesInList[i];
            String filePath = fuzzFolder + fileName;

            if (!FileUtil.isFileExists(filePath)) {
                allExistAndValid = false;
                continue;
            }

            RCFInfo rcfInfo = RCFReader.getRCFInfo(fuzzFolder, fileName);
            if ((rcfInfo != null) && (XMLValidator
                    .validateXMLFile(XSD_PATH, filePath))) {
                rcfInfos.add(rcfInfo);
                existFiles.add(fileName);
            } else { // Not exist or not valid
                allExistAndValid = false;
            }
        }

        if (!allExistAndValid) {
            String content = "";
            for (int i = 0; i < existFiles.size(); i++) {
                content += existFiles.get(i) + "\n";
            }
            FileUtil.modify(listFilePath, content);
        }

        return rcfInfos;
    }

    /**
     * Mengupdate list.txt sehingga hanya berisi RCF yang masih valid.
     * @param fileNames
     *      Nama file RCF yang masih valid
     * @return boolean
     *      Berhasil atau tidak
     */
    public boolean updateList(ArrayList<String> fileNames) {
        String listFilePath = fuzzFolder + "list.txt";
        String content = "";
        for (int i = 0; i < fileNames.size(); i++) {
            content += fileNames.get(i) + "\n";
        }
        if (content.length() > 0) {
            content = content.substring(0, content.length() - 1);
        }
        return FileUtil.modify(listFilePath, content);
    }
}
