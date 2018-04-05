/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imported;

import java.io.File;

/**
 *
 * @author demon
 */
public class ApplicationConstants {
    
    public static String OS = (System.getProperty("os.name")).toUpperCase();
    
    public static String getAppDataPath() {
        String workingDirectory;
        
        if (OS.contains("WIN")) {
            workingDirectory = System.getenv("AppData");
        } else {
            workingDirectory = System.getProperty("user.home");
            workingDirectory += "/Library/Application Support";
        }
        return workingDirectory;
    }
    public static File getAppDataPath(String company, String filename) {
        return getAppDataPath(company, "", filename);
    }
    public static File getAppDataPath(String company, String subpath, String filename) {
        String path = getAppDataPath() + "/" + company + "/" + subpath + "/" + filename;
        while(path.contains("//")) {
            path = path.replaceAll("//", "/");
        }
        return new File(path);
    }
    
}
