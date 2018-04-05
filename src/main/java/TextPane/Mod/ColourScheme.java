/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextPane.Mod;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;

/**
 * hi
 * @author saud
 */
public class ColourScheme {
    
    public static final String BACKGROUND = "Background";
    public static final String FONT = "Font";
    public static final String COMMENT = "Comment";
    public static final String STRING = "String";
    public static final String CARET = "Caret";
    public static final String SELECTION_BG = "SelectionBackground";
    public static final String SELECTION_FONT = "SelectionFont";
    public static String[] colourSchemeAtributes = new String[] {BACKGROUND, FONT, COMMENT, STRING, CARET, SELECTION_BG, SELECTION_FONT};
    
    public Color background;
    public ArrayList<Color> fonts;
    public Color comment;
    public Color string;
    public Color caret;
    public Color selectionBackground;
    public Color selectionFont;

    public ColourScheme(Color background, Color comment, Color string, Color caret, Color selectionBackground, Color selectionFont, Color... fonts) {
        this.background = background;
        this.fonts = new ArrayList<>(Arrays.asList(fonts));
        this.comment = comment;
        this.string = string;
        this.caret = caret;
        this.selectionBackground = selectionBackground;
        this.selectionFont = selectionFont;
    }
    public ColourScheme(JSONObject colourSchemeObj) {
        fonts = new ArrayList<>();
        for(Iterator iterator = colourSchemeObj.keySet().iterator(); iterator.hasNext();) {
             String key = (String) iterator.next();
             String value = (String) colourSchemeObj.get(key);
            Color c = (value==null) ? null : Color.decode(value);
            setAttribute(key, c);
        }
        if(fonts.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                        "Invalid colour scheme", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public JSONObject asJSONObject() {
        JSONObject out = new JSONObject();
        out.put(BACKGROUND, colourToHex(background));
        out.put(COMMENT, colourToHex(comment));
        out.put(STRING, colourToHex(string));
        out.put(CARET, colourToHex(caret));
        out.put(SELECTION_BG, colourToHex(selectionBackground));
        out.put(SELECTION_FONT, colourToHex(selectionFont));
        for (int i=0; i<fonts.size(); i++) {
            out.put(FONT+(i+1), colourToHex(fonts.get(i)));
        }
        return out;
    }
    private static String colourToHex(Color c) {
        if(c==null) return null;
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
    
    public void setAttribute(String attribute, Color c) {
        switch(attribute) {
            case BACKGROUND:
                background = c;
                break;
            case COMMENT:
                caret = c;
                break;
            case STRING:
                caret = c;
                break;
            case CARET:
                caret = c;
                break;
            case SELECTION_BG:
                selectionBackground = c;
                break;
            case SELECTION_FONT:
                selectionFont = c;
                break;
            default:
                if(attribute.startsWith(FONT)) {
                    int num = Integer.parseInt(attribute.substring(4));
                    fonts.add(num-1, c);
                }
                break;
        }
    }
    
}
