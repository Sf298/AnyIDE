/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.anyide;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONObject;

/**
 *
 * @author saud
 */
public class MyCompiler {
    
    public String name;
    public String language;
    public String onCompile;
    public String onClean;
    public String onRun;

    public MyCompiler() {
    }

    public MyCompiler(String name, String language, String onCompile, String onClean, String onRun) {
        this.name = name;
        this.language = language;
        this.onCompile = onCompile;
        this.onClean = onClean;
        this.onRun = onRun;
    }
    
    public MyCompiler(JSONObject myCompilerObj) {
        name = (String) myCompilerObj.get("name");
        language = (String) myCompilerObj.get("language");
        onCompile = (String) myCompilerObj.get("onCompile");
        onClean = (String) myCompilerObj.get("onClean");
        onRun = (String) myCompilerObj.get("onRun");
    }
    
    public JSONObject asJSONObject() {
        JSONObject out = new JSONObject();
        out.put("name", name);
        out.put("language", language);
        out.put("onCompile", onCompile);
        out.put("onClean", onClean);
        out.put("onRun", onRun);

        return out;
    }
    
}
