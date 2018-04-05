/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.anyide;

import TextPane.Mod.ColourScheme;
import TextPane.Mod.Language;
import gui.UpdateListener;
import imported.ApplicationConstants;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InvalidNameException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Load and save language, compiler and program properties
 * @author saud
 */
public class IDEProps {
    
    public static String companyName = "SaudsProjects";
    public static String applicationName = "AnyIDE";
    public static File settingsFile;
    public static File colourSchemesFile;
    public static File langFile;
    public static File compilersFile;
    
    public static String selectedFont;
    public static int fontSize;
    public static String selectedColourScheme;
    public static int spacesPerTab; ///////////////////////////
    public static double vertScroll;
    public static double horrizScroll;
    public static boolean isFullScreen;
    public static int screenW;
    public static int screenH;
    public static int screenPosX;
    public static int screenPosY;
    public static ArrayList<String[]> openClasses; // [projName][class]
    public static ArrayList<Project> storedProjects; // [stored projects]
    
    public static HashMap<String, ColourScheme> colourSchemes; // [background, font]
    public static HashMap<String, Language> languages;
    public static HashMap<String, MyCompiler> compilers;
    public static IDEFrame mainFrame;
    
    public static void initAll() {
        settingsFile = ApplicationConstants.getAppDataPath(companyName, applicationName, "Settings.txt");
        colourSchemesFile = ApplicationConstants.getAppDataPath(companyName, applicationName, "ColourSchemes.txt");
        langFile = ApplicationConstants.getAppDataPath(companyName, applicationName, "Languages.txt");
        compilersFile = ApplicationConstants.getAppDataPath(companyName, applicationName, "Compilers.txt");
        
        ensureInit();
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                runOnExitListeners();
                saveAll();
                System.out.println("Saving... ");
            }
        }, "Save-on-Shutdown"));
    }
    public static void saveAll() {
        saveColourSchemes();
        saveCompilers();
        saveLanguages();
        saveSettings();
    }
    
    private static void ensureInit() {
        // <editor-fold defaultstate="expanded" desc="colourSchemesFile">
        if(colourSchemesFile.exists()) {
            colourSchemes = new HashMap<>();
            JSONObject obj = (JSONObject) readJSON(colourSchemesFile);
            for(Iterator iterator = obj.keySet().iterator(); iterator.hasNext();) {
                 String key = (String) iterator.next();
                 JSONObject value = (JSONObject) obj.get(key);
                colourSchemes.put(key, new ColourScheme(value));
            }
        } else {
            initColourSchemes();
            saveColourSchemes();
        }
        // </editor-fold>
        // <editor-fold defaultstate="expanded" desc="langFile">
        if(langFile.exists()) {
            languages = new HashMap<>();
            JSONObject obj = (JSONObject) readJSON(langFile);
            for(Iterator iterator = obj.keySet().iterator(); iterator.hasNext();) {
                 String key = (String) iterator.next();
                 JSONObject value = (JSONObject) obj.get(key);
                Language tempLang = new Language(value);
                languages.put(key, tempLang);
            }
        } else {
            initLanguages();
            saveLanguages();
        }
        // </editor-fold>
        // <editor-fold defaultstate="expanded" desc="compilersFile">
        if(compilersFile.exists()) {
            compilers = new HashMap<>();
            JSONObject obj = (JSONObject) readJSON(compilersFile);
            for(Iterator iterator = obj.keySet().iterator(); iterator.hasNext();) {
                 String key = (String) iterator.next();
                 JSONObject value = (JSONObject) obj.get(key);
                MyCompiler tempComp = new MyCompiler(value);
                compilers.put(key, tempComp);
            }
        } else {
            initCompilers();
            saveCompilers();
        }
        // </editor-fold>
        // <editor-fold defaultstate="expanded" desc="settignsFile">
        if(settingsFile.exists()) {
            JSONObject obj = (JSONObject) readJSON(settingsFile); // load JSON object
            
            // extract map data
            selectedFont = (String) obj.get("selectedFont");
            System.out.println("loaded "+IDEProps.selectedFont);
            fontSize = (int)(long) obj.get("fontSize");
            selectedColourScheme = (String) obj.get("selectedColourScheme");
            spacesPerTab = (int)(long) obj.get("spacesPerTab");
            vertScroll = (double) obj.get("vertScroll");
            horrizScroll = (double) obj.get("horrizScroll");
            isFullScreen = (boolean) obj.get("isFullScreen");
            screenW = (int)(long) obj.get("screenW");
            screenH = (int)(long) obj.get("screenH");
            screenPosX = (int)(long) obj.get("screenPosX");
            screenPosY = (int)(long) obj.get("screenPosY");
            openClasses = JSONToStrArrList2D((JSONArray)obj.get("openClasses"));
            storedProjects = JSONToProjArr((JSONArray)obj.get("storedProjects"));
        } else {
            // init variables
            selectedFont = "Monospaced";
            fontSize = 12;
            selectedColourScheme = "NetBeans";
            spacesPerTab = 4;
            vertScroll = 0.5;
            horrizScroll = 0.2;
            isFullScreen = true;
            screenW = 500;
            screenH = 500;
            screenPosX = 0;
            screenPosY = 0;
            openClasses = new ArrayList<>();
            storedProjects = new ArrayList<>();
            
            saveSettings();
        }
        // </editor-fold>
    }
    
    private static void saveSettings() {
        JSONObject obj = new JSONObject();
        obj.put("selectedFont", selectedFont);
        System.out.println("saved "+IDEProps.selectedFont);
        obj.put("fontSize", fontSize);
        obj.put("selectedColourScheme", selectedColourScheme);
        obj.put("spacesPerTab", spacesPerTab);
        obj.put("vertScroll", vertScroll);
        obj.put("horrizScroll", horrizScroll);
        obj.put("isFullScreen", isFullScreen);
        obj.put("screenW", screenW);
        obj.put("screenH", screenH);
        obj.put("screenPosX", screenPosX);
        obj.put("screenPosY", screenPosY);
        obj.put("openClasses", strArrList2DToJSON(openClasses));
        obj.put("storedProjects", projArrToJSON(storedProjects));

        writeString(settingsFile, obj.toJSONString());
    }
    private static void saveColourSchemes() {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, ColourScheme> entry : colourSchemes.entrySet()) {
            String key = entry.getKey();
            ColourScheme value = entry.getValue();
            obj.put(key, value.asJSONObject());
        }

        writeString(colourSchemesFile, obj.toJSONString());
    }
    private static void saveLanguages() {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Language> entry : languages.entrySet()) {
             Language value = entry.getValue();
             JSONObject currObj = value.asJSONObject();

            obj.put(entry.getKey(), currObj);
        }
        writeString(langFile, obj.toJSONString());
    }
    private static void saveCompilers() {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, MyCompiler> entry : compilers.entrySet()) {
             MyCompiler value = entry.getValue();
             JSONObject currObj = value.asJSONObject();
            obj.put(entry.getKey(), currObj);
        }
        writeString(compilersFile, obj.toJSONString());
    }
    
    
    private static void initColourSchemes() {
        colourSchemes = new HashMap<>();
        
        ColourScheme none = new ColourScheme(
                Color.WHITE, // background
                null, // comment
                null, // string
                null, // caret
                null, // selection bg
                null, // selection font
                Color.BLACK // fonts
        );
        colourSchemes.put("None", none);
        
        ColourScheme netBeans = new ColourScheme(
                Color.WHITE, // background
                null, // comment
                null,
                null,
                new Color(236, 235, 163), // string
                null, // caret
                Color.BLACK, // selection bg
                Color.BLUE // selection font
        );
        colourSchemes.put("NetBeans", netBeans);
    }
    private static void initLanguages() {
        languages = new HashMap<>();
        Language lang = new Language(
                "None",
                new String[][]{new String[] {""}},
                new String[][]{new String[] {""}},
                new String[][]{new String[] {""}},
                new String[][]{new String[] {""}},
                true);
        Language java = new Language(
                "Java",
                new String[][]{
                    new String[] {"for", "while", "do", "if", "else",
                        "switch", "case", "public", "private", "static",
                        "boolean", "int", "long", "float", "double", "class",
                        "null", "void", "final", "new", "try", "catch",
                        "import", "package", "instanceof"}
                },
                new String[][]{
                    new String[] {"//"},
                    new String[] {"/*","*/"}
                },
                new String[][]{
                    new String[] {"(",")"},
                    new String[] {"[","]"},
                    new String[] {"{","}"}
                },
                new String[][]{
                    new String[] {"'","'", "\\"},
                    new String[] {"\"","\"", "\\"}
                },
                true);
        
        languages.put(lang.name, lang);
        languages.put(java.name, java);
    }
    private static void initCompilers() {
        compilers = new HashMap<>();
        MyCompiler comp = new MyCompiler(
                "None", // name
                "None", // language
                "",
                "",
                "");
        compilers.put(comp.name, comp);
        MyCompiler java8 = new MyCompiler(
                "Java8",
                "Java", // compiler file
                "",
                "",
                "");
        compilers.put(java8.name, java8);
    }
    
    public static void writeString(File f, String str) {
        try {
            f.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(f);
            fw.write(str);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(IDEProps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static Object readJSON(File f) {
        try {
            JSONParser parser = new JSONParser();
            return parser.parse(new FileReader(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IDEProps.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IDEProps.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(IDEProps.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private static JSONArray strArrList2DToJSON(ArrayList<String[]> arr) {
        JSONArray out = new JSONArray();
        for(String[] sA : arr) {
            JSONArray tempArr = new JSONArray();
            tempArr.addAll(Arrays.asList(sA));
            out.add(tempArr);
        }
        return out;
    }
    private static ArrayList<String[]> JSONToStrArrList2D(JSONArray arr) {
        ArrayList<String[]> out = new ArrayList<>();
        for(int i=0; i<arr.size(); i++) {
            JSONArray tempArr = (JSONArray) arr.get(i);
            out.add(new String[tempArr.size()]);
            for(int j=0; j<tempArr.size(); j++) {
                out.get(i)[j] = (String) tempArr.get(j);
            }
        }
        return out;
    }
    private static JSONArray projArrToJSON(ArrayList<Project> arr) {
        JSONArray out = new JSONArray();
        for(Project p : arr) {
            out.add(p.projectFolder.getPath());
        }
        return out;
    }
    private static ArrayList<Project> JSONToProjArr(JSONArray arr) {
        ArrayList<Project> out = new ArrayList<>();
        for(int i=0; i<arr.size(); i++) {
            File value = new File((String) arr.get(i));
            try {
                if(value.exists())
                    out.add(Project.load(value));
            } catch (FileNotFoundException | InvalidNameException ex) {
                Logger.getLogger(IDEProps.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return out;
    }
    
    public static void runOnExitListeners() {
        for (UpdateListener l : updateListeners) {
            l.onExit();
        }
    }
    public static void runOnPostLoadListeners() {
        for (UpdateListener l : updateListeners) {
            l.onPostLoad();
        }
    }
    private static ArrayList<UpdateListener> updateListeners = new ArrayList<>();
    public static void addUpdateListener(UpdateListener l) {
        updateListeners.add(l);
    }
    public static void removeUpdateListener(UpdateListener l) {
        updateListeners.remove(l);
    }
}

