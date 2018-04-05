/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextPane.Mod;

import com.mycompany.anyide.IDEProps;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author saud
 */
public class SyntaxHighlighting {
    
    private static final int REMOVED = -1, INSERTED = 1;
    private boolean firstRun = true;
    
    private JTextPane pane;
    private StyledDocument doc;
    private Language lang;
    private ColourScheme col;
    
    public SyntaxHighlighting(JTextPane pane, Language lang, ColourScheme col) {
        this.pane = pane;
        this.doc = pane.getStyledDocument();
        this.lang = lang;
        this.col = col;
        
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                syntaxColouring(e.getOffset()-1, e.getOffset() + e.getLength());
                resetbracket();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                syntaxColouring(e.getOffset()-1, e.getOffset()-1);
                resetbracket();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        
        pane.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                sameWordHighlighting(e.getDot());
                bracketHighlighting(e.getDot());
            }
        });
        
        pane.setText(" ");
    }
    
    private void syntaxColouring(int myStartPos, int myEndPos) {
        int[] startEndPos = getStartEndPos(myStartPos, myEndPos);
        int startPos = startEndPos[0];
        int endPos = startEndPos[1];
        String text = pane.getText();
            
        if(text.length()>0) {
            //System.out.println("startPos="+startPos+", endPos="+endPos+", len="+text.length());
            //System.out.println("'"+text.subSequence(startPos, endPos)/*+"', c = '"+text.charAt(startPos)*/+"'");
        
            // init attributes
            SimpleAttributeSet set = new SimpleAttributeSet();
            StyleConstants.setFontSize(set, IDEProps.fontSize);
            StyleConstants.setFontFamily(set, IDEProps.selectedFont);
            
            int j = -1;
            boolean searching = true;
            for(int i=startPos; i<endPos+1; i++) {
                char c = text.charAt(Math.min(i, text.length()-1));
                if((searching && isAlphanumeric(c))) {
                    j=i;
                    searching=false;
                } else if((!searching && !isAlphanumeric(c)) || i==endPos) {
                    if(j!=-1) {
                        int ti=i, tj=j;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if(firstRun)
                                    doc.setCharacterAttributes(0, text.length(), set, false);

                                String word = text.subSequence(tj, ti).toString();
                                //System.out.println("word "+word);
                                ColourScheme colours = IDEProps.colourSchemes.get(IDEProps.selectedColourScheme);
                                Color c = wordToColour(word, colours);
                                if(c!=null)
                                    StyleConstants.setForeground(set, c);
                                c = colours.background;
                                if(c!=null)
                                    StyleConstants.setBackground(set, c);

                                //System.out.println("tj="+tj+", ti="+ti);
                                doc.setCharacterAttributes(tj, word.length(), set, false);
                            }
                        });
                        searching = true;
                    }
                }
                if(searching) {
                    int ti=i;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ColourScheme colours = IDEProps.colourSchemes.get(IDEProps.selectedColourScheme);
                            Color c = colours.fonts.get(0);
                            if(c!=null)
                                StyleConstants.setForeground(set, c);
                            c = colours.background;
                            if(c!=null)
                                StyleConstants.setBackground(set, c);
                            doc.setCharacterAttributes(ti, 1, set, false);
                        }
                    });
                    searching = true;
                }
                
            }
            firstRun = false;
        }
    }
    private boolean isAlphanumeric(char c) {
        return ('A'<=c && c<='Z') || ('a'<=c && c<='z') || ('0'<=c && c<='9');
    }
    private int[] getStartEndPos(int startPos, int endPos) {
        String text = pane.getText();
        
        startPos = Math.min(startPos, text.length());
        startPos = Math.max(startPos, 0);
        endPos = Math.min(endPos, text.length());
        endPos = Math.max(endPos, 0);

        if(text.length()>0) {
            while(startPos>=0 && isAlphanumeric(text.charAt(startPos))) {
                startPos--;
            }
            startPos++;
            startPos = Math.min(startPos, text.length()-1);
            startPos = Math.max(startPos, 0);
            
            endPos++;
            while(endPos<text.length() && isAlphanumeric(text.charAt(endPos))) {
                endPos++;
            }
            endPos = Math.min(endPos, text.length());
            endPos = Math.max(endPos, 0);
            
            if(endPos < startPos) {
                int temp = startPos;
                startPos = endPos;
                endPos = temp+1;
            }
        }
        return new int[] {startPos, endPos};
    }
    
    private Color wordToColour(String word, ColourScheme colors) {
        if(lang==null) return null;
        
        int pos = lang.findWord(word)+1;
        return colors.fonts.get(pos);
    }
    
    
    private Thread sameWordThread;
    private ArrayList<Integer> lastSWHPos;
    private String lastSWHWord;
    private void sameWordHighlighting(int position) {
        int[] startEndPos = getStartEndPos(position-1, position-1);
        String word = pane.getText()
                .subSequence(startEndPos[0], startEndPos[1])
                .toString();
        String regex = "\\b"+word+"\\b";
        Pattern pattern = Pattern.compile(regex);
        
        SimpleAttributeSet set = new SimpleAttributeSet();
        ColourScheme colours = IDEProps.colourSchemes.get(IDEProps.selectedColourScheme);
        Color c = colours.selectionBackground;
        
        //System.out.println("highlighting c="+c);
        if(c!=null) {
            StyleConstants.setBackground(set, c);
            
            if(sameWordThread!=null && sameWordThread.isAlive())
                sameWordThread.interrupt();
            //System.out.println("highlighting abc");
            
            sameWordThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if(lastSWHPos!=null && !lastSWHWord.equals(word)) {
                        for(Integer pos : lastSWHPos) {
                            syntaxColouring(pos, pos+lastSWHWord.length());
                        }
                    }
                    lastSWHPos = new ArrayList<>();
                    lastSWHWord = word;
                    String text = pane.getText();
                    Matcher matcher = pattern.matcher(text);
                    while(matcher.find() == true) {
                        int start = matcher.start();
                        lastSWHPos.add(start);
                        //System.out.println("Matcher start="+start);
                        doc.setCharacterAttributes(start, word.length(), set, false);
                    }
                    //System.out.println("Matcher len="+lastSWHWord);
                }
            });
            sameWordThread.start();
        }
    }
    
    
    private String lastBracketA;
    private int lastBracketPosA;
    private String lastBracketB;
    private int lastBracketPosB;
    private void bracketHighlighting(int position) {
        position = Math.max(position-1, 0);
        String text = pane.getText();
        resetbracket();
        
        // check clicked on bracket
        int[] bracketIndx = lang.findBracketIndx(text, position);
        if(bracketIndx==null) {
            position++;
            bracketIndx = lang.findBracketIndx(text, position);
            if(bracketIndx==null)
                return;
        }
        
        ColourScheme colours = IDEProps.colourSchemes.get(IDEProps.selectedColourScheme);
        Color c = colours.selectionBackground;
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setBackground(set, c);
        
        String bracket = lang.bracketMarkers[bracketIndx[0]][bracketIndx[1]];
        String matchingBracket = lang.bracketMarkers[bracketIndx[0]][(bracketIndx[1]==0)?1:0];
        int pos2 = findPairingBracket(matchingBracket, bracket, text, position, bracketIndx[1]*-2+1);
        
        if(pos2 != -1) {
            lastBracketA = bracket;
            lastBracketPosA = position;
            lastBracketB = matchingBracket;
            lastBracketPosB = pos2;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    doc.setCharacterAttributes(lastBracketPosA, matchingBracket.length(), set, false);
                    doc.setCharacterAttributes(pos2, matchingBracket.length(), set, false);
                }
            });
        }
    }
    private void resetbracket() {
        ColourScheme colours = IDEProps.colourSchemes.get(IDEProps.selectedColourScheme);
        Color c = colours.background;
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setBackground(set, c);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(lastBracketA!=null) {
                    doc.setCharacterAttributes(lastBracketPosA, lastBracketA.length(), set, false);
                    doc.setCharacterAttributes(lastBracketPosB, lastBracketB.length(), set, false);
                    lastBracketA = null;
                    lastBracketPosA = -1;
                    lastBracketB = null;
                    lastBracketPosB = -1;
                }
            }
        });
    }
    private int findPairingBracket(String targetBracket, String pairingBracket, String text, int startPos, int direction) {
        int depth = 0;
        for(int i=startPos; 0<=i && i<text.length(); i+=direction) {
            if(startsWith(pairingBracket, text, i)) {
                depth++;
            }
            if(startsWith(targetBracket, text, i)) {
                depth--;
            }
            if(depth == 0) {
                return i;
            }
        }
        return -1;
    }
    private boolean startsWith(String str, String text, int pos) {
        for (int i=0; i<str.length(); i++) {
            if(str.charAt(i)!=text.charAt(i+pos)) {
                return false;
            }
        }
        return true;
    }
    
    /*private void updateFont() {
        
        Font font = new Font(IDEProps.selectedFont, Font.PLAIN, IDEProps.fontSize);
        System.out.println("setting font "+IDEProps.selectedFont);
        textPane.setFont(font);
        
        // set tab width
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AffineTransform affinetransform = new AffineTransform();   
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);   
        int tabWidth = (int)(font.getStringBounds(" ", frc).getWidth());
        TabSet tabs = new TabSet(new TabStop[] {new TabStop(tabWidth*IDEProps.spacesPerTab)});
        AttributeSet paraSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabs);
        textPane.setParagraphAttributes(paraSet, false);
    }*/
    
}
