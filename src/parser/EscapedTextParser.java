package parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * get the escaped test which enclose by ~
 * 
 * @author A0112823R
 *
 */
public class EscapedTextParser {
    private String textEscaped = "";
    private ArrayList<String> escapedTextList = new ArrayList<String>();

    public EscapedTextParser(String userInput) {
        escapedTextList = storeEscapedTexts(userInput);

        for (int i = 0; i < escapedTextList.size(); i++) {
            textEscaped = textEscaped + " " + escapedTextList.get(i);
        }

    }

    /**
     * get the escaped text ~....~ + ~.....~ + etc
     * 
     * @return the input left after removing all the time detected
     */
    public String getEscapedText() {
        return textEscaped;
    }

  
    public ArrayList<String> getListOfEscapedText() {
        return escapedTextList;

    }

    /**
     * store all of the escaped text into a arrayList
     * 
     * @param userInput
     * @return arraylist of escaped text
     */
    private ArrayList<String> storeEscapedTexts(String userInput) {
        ArrayList<Integer> tildeIndex = new ArrayList<Integer>();
        ArrayList<String> tildeEscapedText = new ArrayList<String>();
        Pattern tildeDetector = Pattern.compile("\\~");
        Matcher containTilde = tildeDetector.matcher(userInput);

        while (containTilde.find()) {

            tildeIndex.add(containTilde.start());

            if (tildeIndex.size() == 2) {
                tildeEscapedText.add(userInput.substring(tildeIndex.get(0),
                        tildeIndex.get(1) + 1));
                tildeIndex.clear();
            }

        }
        return tildeEscapedText;
    }
}
