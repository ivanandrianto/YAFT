/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.ArrayUtils;

/**
 * File Util Class.
 * @author ivanandrianto
 */
public final class FileUtil {

    /**
     * Prevent instantiation
     */
    private FileUtil() {

    }

    /**
     * Membaca isi dari suatu file.
     * @param path
     *      Path menuju file yang ingin dibca
     * @return String
     *      Isi dari file
     * @throws IOException
     *      Apabila terjadi error ketika membaca file
     */
    public static String read(String path)  {
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        try {
            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    fstream));
            String content = "";
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                content += strLine + "\n";
            }
            if (content.length() > 0) {
                content = content.substring(0, content.length() - 1);
            }
            br.close();
            return content;
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(
                    Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Memodifikasi isi dari suatu file.
     * @param path
     *      Path menuju file yang ingin dimodifikasi.
     * @param content
     *      Isi baru yang ingin dimasukkan pada file untuk mengganti isi lama.
     * @param format
     *      Apakah perlu diformat
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean modify(String path, String content) {
        File file = new File(path);
        content = content.replaceAll("\\n", System.lineSeparator());
        FileOutputStream fooStream;
        try {
            fooStream = new FileOutputStream(file, false);
            byte[] bytes = content.getBytes();
            fooStream.write(bytes);
            fooStream.close();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Menghapus suatu file.
     * @param path
     *      Path file yang ingin dihapus
     */
    public static boolean delete(String path) {
        File file = new File(path);
        if (!file.exists()) {
           return false;
        }
        return file.delete();
    }

    /**
     * Membentuk direktori apabila belum ada.
     * @param dirPath
     *      Path menuju direktori yang ingin dibentuk.
     */
    public static boolean createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdir();
        }
        return false;
    }

    /**
     * Mendapatkan daftar file dalam suatu direktori dengan akhiran tertentu.
     * @param dirPath
     *      Path menuju direktori
     * @param endsWith
     *      Diakhiri dengan
     * @boolean sort
     *      Diurutkan berdasarkan waktu atau tidak
     * @return ArrayList<String>
     *      Daftar file
     */
    public static ArrayList<String> getFileList(String dirPath,
                String endsWith, boolean sort) {
        File folder = new File(dirPath);
        ArrayList<String> fileNames = new ArrayList<String>();
        if (!folder.exists()) {
            return fileNames;
        }

        File[] files = folder.listFiles();
        if (sort) {
            Arrays.sort(files, LastModifiedFileComparator
                    .LASTMODIFIED_COMPARATOR);
            ArrayUtils.reverse(files);
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String fileName = files[i].getName();
                if (endsWith != null) {
                    if (fileName.endsWith(endsWith)) {
                        fileNames.add(fileName);
                    }
                }
            }
        }
        return fileNames;
    }

    /**
     * Mendapatkan daftar direktori di suatu path.
     * @param dirPath
     *      Path menuju direktori
     * @return ArrayList<String>
     *      Daftar direktori
     */
    public static ArrayList<String> getDirectoryList(String dirPath) {
        File folder = new File(dirPath);
        ArrayList<String> dirNames = new ArrayList<String>();
        if (!folder.exists()) {
            return dirNames;
        }

        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                String fileName = files[i].getName();
                dirNames.add(fileName);
            }
        }

        return dirNames;
    }

    /**
     * Membuat file baru beserta isinya.
     * @param filePath
     *      Path menuju file yang akan dibentuk
     * @param content
     *      Teks yang akan ditambahkan ke file
     * @param format
     *      Apakah perlu diformat
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean createNewFile(String filePath, String content) {
        File file = new File(filePath);
        content = content.replaceAll("\\n", System.lineSeparator());
        if (file.exists()) {
            return false;
        }
        try {
            if (!file.createNewFile()) {
                return false;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(content);
            Files.write(Paths.get(filePath), sb.toString().getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * Memeriksa apakah suatu file ada.
     * @param filePath
     *      Path menuju file
     * @return boolean
     *      Ada atau tidak
     */
    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Mendapatkan waktu modifikasi terakhir dari suatu file.
     * @param filePath
     *      Path menuju file
     * @return Long
     *      Waktu terakhir domodifkasi dalam Long
     */
    public static Long getLastModifiedDate(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.lastModified() : null;
    }

    /**
     * Method ini berfungsi untuk merename nama file.
     * @param oldName
     *      Nama file
     * @param newName
     *      Nama baru
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean renameFile(String oldName,
            String newName) {
        File file = new File(oldName);
        File file2 = new File(newName);

        if (file2.exists()) {
            return false;
        }

        return file.renameTo(file2);
    }

    /**
     * Menambahkan teks ke suatu file.
     * @param filePath
     *      Path menuju file
     * @param content
     *      Teks yang ingin ditambahka
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean appendTextToFile(String filePath,
            String content) {
        String c = content;
        c = c.replaceAll("%n", "%%n");
        c = c.replaceAll("\\n", "%n");
        c = String.format(c);
        try {
            Files.write(Paths.get(filePath), c.getBytes(),
                    StandardOpenOption.APPEND);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
