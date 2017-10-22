/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivanandrianto.yaft.project;

import java.io.File;

/**
 * Tesing Project Manager Class.
 * @author ivanandrianto
 */
public final class TestingProjectManager {
    private static final String PROJECT_FOLDER = "projects/";

    /**
     * Prevent instantiation
     */
    private TestingProjectManager() {

    }

    /**
     * Membentuk testing project baru.
     * @param testingProjectName
     *      Nama tesing project baru
     * @return boolean
     *      Berhasil atai tidak
     */
    public static boolean createNewTestingProject(String testingProjectName) {
        // Create projects folder if not exists

        File projectsDir = new File(PROJECT_FOLDER);
        if (!projectsDir.exists()) {
            if (!projectsDir.mkdir()) {
                return false;
            }
        }

        boolean success = false;
        File testingProjectDir = new File(PROJECT_FOLDER + testingProjectName);
        if (!testingProjectDir.exists()) {
            if (testingProjectDir.mkdir()) {
                success = true;
            }
        }

        return success;
    }

    /**
     * Membuka testing project.
     * @param testingProjectName
     *      Nama tesing project
     * @return boolean
     *      Berhasil atau tidak
     */
    public static boolean openTestingProject(String testingProjectName) {
        boolean success = false;
        File testingProjectDir = new File(testingProjectName);
        if (testingProjectDir.exists()) {
            success = true;
        }
        return success;
    }

    /**
     * Mendapatkan path menuju folder yang menampung sekumpulan testing project.
     * @return String
     *      Path menuju folder
     */
    public static String getTestingProjectFolder() {
        return PROJECT_FOLDER;
    }
}
