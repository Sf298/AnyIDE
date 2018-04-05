/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextPane.Mod;

import java.util.Arrays;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author saud
 */
public class Language {
    
    public String name;
    public String[][] keyWords;
    public String[][] commentMarkers;
    public String[][] bracketMarkers;
    public String[][] stringMarkers;
    public boolean caseSense;
    
    public Language() {}

    public Language(String name, String[][] keyWords, String[][] commentMarkers, String[][] bracketMarkers, String[][] stringMarkers, boolean caseSense) {
        this.name = name;
        this.keyWords = keyWords;
        this.commentMarkers = commentMarkers;
        this.bracketMarkers = bracketMarkers;
        this.stringMarkers = stringMarkers;
        this.caseSense = caseSense;
    }
    public Language(JSONObject languageObj) {
        name = (String) languageObj.get("name");
        keyWords = JSONToStrArr2D(languageObj.get("keyWords"));
        commentMarkers = JSONToStrArr2D(languageObj.get("commentMarkers"));
        bracketMarkers = JSONToStrArr2D(languageObj.get("bracketMarkers"));
        stringMarkers = JSONToStrArr2D(languageObj.get("stringMarkers"));
        caseSense = (boolean) languageObj.get("caseSense");
    }
    private static String[][] JSONToStrArr2D(Object arr) {
        JSONArray jsonArr = (JSONArray) arr;
        String[][] out = new String[jsonArr.size()][];
        for(int i=0; i<out.length; i++) {
            JSONArray tempArr = (JSONArray) jsonArr.get(i);
            out[i] = new String[tempArr.size()];
            for(int j=0; j<tempArr.size(); j++) {
                out[i][j] = (String) tempArr.get(j);
            }
        }
        return out;
    }
    
    public JSONObject asJSONObject() {
        JSONObject out = new JSONObject();
        out.put("name", name);
        out.put("keyWords",  strArr2DToJSON(keyWords));
        out.put("commentMarkers",  strArr2DToJSON(commentMarkers));
        out.put("bracketMarkers",  strArr2DToJSON(bracketMarkers));
        out.put("stringMarkers",  strArr2DToJSON(stringMarkers));
        out.put("caseSense", caseSense);

        return out;
    }
    private static JSONArray strArr2DToJSON(String[][] arr) {
        JSONArray out = new JSONArray();
        for(String[] sA : arr) {
            JSONArray tempArr = new JSONArray();
            tempArr.addAll(Arrays.asList(sA));
            out.add(tempArr);
        }
        return out;
    }
    
    public int findWord(String word) {
        for(int i=0; i<keyWords.length; i++) {
            for(int j=0; j<keyWords[i].length; j++) {
                if(keyWords[i][j].equals(word))
                    return i;
            }
        }
        return -1;
    }
    
    public int[] findBracketIndx(String text, int pos) {
        text = text.substring(pos);
        for(int i=0; i<bracketMarkers.length; i++) {
            if(text.startsWith(bracketMarkers[i][0]))
                return new int[] {i,0};
            if(text.startsWith(bracketMarkers[i][1]))
                return new int[] {i,1};
        }
        return null;
    }
    
}
