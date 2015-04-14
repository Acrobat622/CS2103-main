package parser;

/**
 * get the description for the user input
 * 
 * @author A0112823R
 *
 */
public class DescriptionParser {
    private String description = "";

    public DescriptionParser(String userInput, String userInputLeft)
            throws Exception {

        // to match with partOfDescription since all is set to lowercase during
        // detection of time and date to prevent case sensitive
        String lowerCaseInput = switchAllToLowerCase(userInput);

        String partOfDescription = userInputLeft;

        String escapedText = getEscapedText(userInput);

        String[] eachWordInLeftOverInput = splitStringByWhitespace(partOfDescription);
        String[] eachEscapedText = splitStringByWhitespace(escapedText);
        String[] eachWordLowerCaseInput = splitStringByWhitespace(lowerCaseInput);
        String[] eachWordUserInput = splitStringByWhitespace(userInput);

        description = piecePartsOfDescription(eachWordInLeftOverInput, eachEscapedText,
                eachWordLowerCaseInput, eachWordUserInput);

        description = removeTheExtraSpace(description);
        setDescription(description);
    }

    /**
     * get the escape text which is surround with ~ eg: ~2dec~
     * 
     * @param userInput
     * @return escape text from ~~
     */
    private String getEscapedText(String userInput) {
        EscapedTextParser escapedTextParser = new EscapedTextParser(userInput);

        String escapedText = escapedTextParser.getEscapedText();

        escapedText = switchAllToLowerCase(escapedText);

        return escapedText;
    }

    private String removeTheExtraSpace(String partOfDescription) {
        partOfDescription = partOfDescription.trim();
        partOfDescription = partOfDescription.replaceAll("\\s+", " ");
        return partOfDescription;
    }

    /**
     * piece the left over user input (after extracting out time and date), the
     * escape text together and remove the conjunction that is in front of time
     * and date.
     * 
     * @param escapedText
     *            : those surround with ~ eg: ~12 dec~
     * @param partOfDescription
     *            : after extracting time, date and escaped text
     * @param lowerCaseInput
     *            : to match with part of description
     * @param userInput
     *            : to be piece together if it is not date and time in the way
     *            user typed
     * @return description
     */
    private String piecePartsOfDescription(String[] eachWordInLeftOverInput,
            String[] eachEscapedText, String[] eachWordLowerCaseInput, String[] eachWordUserInput) {

        int indexLeftOverInput = 0, indexEscapedText = 0;

        for (int i = 0; i < eachWordLowerCaseInput.length; i++) {
            boolean isByPassConjunction = isByPassConjunction(
                    indexLeftOverInput, indexEscapedText,
                    eachWordInLeftOverInput, eachEscapedText,
                    eachWordLowerCaseInput, i);

            if (isByPassConjunction) {
                indexLeftOverInput++;
                i++;
            }

            if (isWordEqualToUserInput(indexLeftOverInput,
                    eachWordInLeftOverInput, eachWordLowerCaseInput, i)) {

                indexLeftOverInput = addWordToDescription(indexLeftOverInput,
                        eachWordUserInput, i);
            } else if (isWordEqualToUserInput(indexEscapedText,
                    eachEscapedText, eachWordLowerCaseInput, i)) {

                indexEscapedText = addWordToDescription(indexEscapedText,
                        eachWordUserInput, i);
            }
        }
        return description;
    }

    /**
     * if escaped text or left over input match with the user input, add it to
     * the description
     * 
     * @param index
     * @param eachWordUserInput
     * @param i
     * @return the next index for continue detection of description
     */
    private int addWordToDescription(int index, String[] eachWordUserInput,
            int i) {
        description = description + " " + eachWordUserInput[i];
        index++;
        return index;
    }

    private boolean isWordEqualToUserInput(int index,
            String[] wordLeftOverInput, String[] eachWordLowerCaseInput, int i) {
        return index < wordLeftOverInput.length
                && eachWordLowerCaseInput[i].equals(wordLeftOverInput[index]);
    }

    /**
     * 1) change ~ to white space then split all by white space
     * 
     * @param text
     * @return array of each word in text
     */
    private String[] splitStringByWhitespace(String text) {
        text = text.replaceAll("\\~", " ");

        text = removeTheExtraSpace(text);

        String[] eachWordInDescription = text.split("\\s+|~");
        return eachWordInDescription;
    }

    /**
     * by pass the conjunction if it is link with date or time
     * 
     * @param indexLeftOverInput
     *            : index of eachWordInLeftOverInput
     * @param indexEscapedText
     *            : index of eachEscapedText
     * @param eachWordInLeftOverInput
     *            : left over input after extracting time and date
     * @param eachEscapedText
     *            : escaped text detected
     * @param eachWordLowerCaseInput
     *            : user input in lower case
     * @param i
     *            : index of eachWordLowerCaseInput
     * @return
     */
    private boolean isByPassConjunction(int indexLeftOverInput,
            int indexEscapedText, String[] eachWordInLeftOverInput,
            String[] eachEscapedText, String[] eachWordLowerCaseInput, int i) {

        boolean isByPassConjunction = false;
        if (indexLeftOverInput < eachWordInLeftOverInput.length
                && isConjunction(eachWordLowerCaseInput[i])
                && eachWordLowerCaseInput[i]
                        .equals(eachWordInLeftOverInput[indexLeftOverInput])) {

            if (isTheNextWordDate(indexLeftOverInput, indexEscapedText,
                    eachWordInLeftOverInput, eachEscapedText,
                    eachWordLowerCaseInput, i)
                    || isNextWordEmpty(indexLeftOverInput, indexEscapedText,
                            eachWordInLeftOverInput, eachEscapedText,
                            eachWordLowerCaseInput, i)) {
                isByPassConjunction = true;
            }

        }
        return isByPassConjunction;
    }

    /**
     * check if the next word of the left over input empty and it is empty not
     * because of escaped text but is because of the time and date extracted
     * out.
     * 
     * @param indexLeftOverInput
     *            : index of eachWordInLeftOverInput
     * @param indexEscapedText
     *            : index of eachEscapedText
     * @param eachWordInLeftOverInput
     *            : left over input after extracting time and date
     * @param eachEscapedText
     *            : escaped text detected
     * @param eachWordLowerCaseInput
     *            : user input in lower case
     * @param indexUserInput
     *            : index of eachWordLowerCaseInput
     * @return true if the next word is empty otherwise false.
     */
    private boolean isNextWordEmpty(int indexLeftOverInput,
            int indexEscapedText, String[] eachWordInLeftOverInput,
            String[] eachEscapedText, String[] eachWordLowerCaseInput,
            int indexUserInput) {

        return indexLeftOverInput == eachWordInLeftOverInput.length - 1
                && indexUserInput + 1 < eachWordLowerCaseInput.length
                && (indexEscapedText < eachEscapedText.length
                        && !eachWordLowerCaseInput[indexUserInput + 1]
                                .equals(eachEscapedText[indexEscapedText]) || indexEscapedText == eachEscapedText.length);
    }

    /**
     * check if the next word in the user input is time or date If yes, the next
     * word in left over input will not be equal to the next word in user input
     * and next word in user input will not be equal to escaped text.
     * 
     * @param indexLeftOverInput
     *            : index of eachWordInLeftOverInput
     * @param indexEscapedText
     *            : index of eachEscapedText
     * @param eachWordInLeftOverInput
     *            : left over input after extracting time and date
     * @param eachEscapedText
     *            : escaped text detected
     * @param eachWordLowerCaseInput
     *            : user input in lower case
     * @param indexUserInput
     *            : index of eachWordLowerCaseInput
     * @return true if next word contain time and date otherwise false
     */
    private boolean isTheNextWordDate(int indexLeftOverInput,
            int indexEscapedText, String[] eachWordInLeftOverInput,
            String[] eachEscapedText, String[] eachWordLowerCaseInput,
            int indexUserInput) {

        return indexLeftOverInput + 1 < eachWordInLeftOverInput.length

                && indexUserInput + 1 < eachWordLowerCaseInput.length

                && !eachWordInLeftOverInput[indexLeftOverInput + 1]
                        .equals(eachWordLowerCaseInput[indexUserInput + 1])

                && (indexEscapedText == eachEscapedText.length || (indexEscapedText < eachEscapedText.length && !eachWordLowerCaseInput[indexUserInput + 1]
                        .equals(eachEscapedText[indexEscapedText])));

    }

    private boolean isConjunction(String text) {
        return text.matches("\\b(@|due on|on|at|from|to|by|due)\\b");
    }

    /**
     * to prevent case sensitive, switch all to lower case
     * 
     * @param userInput
     * @return the user input all in lower case.
     */
    private String switchAllToLowerCase(String userInput) {
        userInput = userInput.toLowerCase();
        return userInput;
    }

    /**
     * set description
     * 
     * @param detectedDescription
     */
    private void setDescription(String detectedDescription) {
        description = detectedDescription;

    }

    /**
     * Get the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
