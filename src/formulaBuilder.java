import java.util.ArrayList;
import java.util.Arrays;

public class formulaBuilder {

    private String input;
    private boolean isValid;
    private String complaint;
    private wff result;

    public boolean isValid() {
        return isValid;
    }

    public String getComplaint() {
        return complaint;
    }

    public wff getResult() {
        return result;
    }

    public static final char[] negationCharacters;
    public static final char[] conjunctionCharacters;
    public static final char[] disjunctionCharacters;
    public static final char[] biconditionalCharacters;
    public static final char[] conditionalCharacters;
    public static final char[] openParenthesisChars;
    public static final char[] closeParenthesisChars;
    public static final char[] existentialQuantifierChars;
    public static final char[] identityChars;
    public static final char[] antiIdentityChars;
    public static final char[] allIdentityChars;

    public static final char[] propositionalFunctionCharacters;
    public static final char[] nameCharacters;

    public static final char[] allAllowedCharacters;

    public static final char[] charactersThatGoBetweenTwoSubformulas;

    public static final int[] possibleNumCharsInQuantifierStart = {3, 4};

    static {
        String negationCharactersS = "~!¬";
        String conjunctionCharactersS = "&·•*∧^";
        String disjunctionCharactersS = "vV∨|+∥";
        String equivalenceCharactersS = "≡⇔↔";
        String implicationCharactersS = "⊃→⇒>";
        String openParenthesisCharsS = "([{";
        String closeParenthesisCharsS = ")]}";
        String existentialQuantifierCharsS = "E∃";
        String identityCharsS = "=";
        String antiIdentityCharsS = "≠";
        String allIdentityCharsS = identityCharsS + antiIdentityCharsS;

        String propositionalFunctionCharactersS = "ABCDEFGHIJKLMNOPQRSTUWXYZ";
        String nameCharactersS = "abcdefghijklmnopqrstuwxyz";

        String allAllowedCharactersS = negationCharactersS + conjunctionCharactersS +
                disjunctionCharactersS + equivalenceCharactersS + implicationCharactersS +
                openParenthesisCharsS + closeParenthesisCharsS + existentialQuantifierCharsS +
                propositionalFunctionCharactersS + identityCharsS + antiIdentityCharsS +
                nameCharactersS;

        String charactersThatGoBetweenTwoSubformulasS = conjunctionCharactersS +
                disjunctionCharactersS + equivalenceCharactersS + implicationCharactersS;

        negationCharacters = negationCharactersS.toCharArray();
        conjunctionCharacters = conjunctionCharactersS.toCharArray();
        disjunctionCharacters = disjunctionCharactersS.toCharArray();
        biconditionalCharacters = equivalenceCharactersS.toCharArray();
        conditionalCharacters = implicationCharactersS.toCharArray();
        openParenthesisChars = openParenthesisCharsS.toCharArray();
        closeParenthesisChars = closeParenthesisCharsS.toCharArray();
        existentialQuantifierChars = existentialQuantifierCharsS.toCharArray();
        identityChars = identityCharsS.toCharArray();
        antiIdentityChars = antiIdentityCharsS.toCharArray();
        allIdentityChars = allIdentityCharsS.toCharArray();

        propositionalFunctionCharacters = propositionalFunctionCharactersS.toCharArray();
        nameCharacters = nameCharactersS.toCharArray();

        allAllowedCharacters = allAllowedCharactersS.toCharArray();

        charactersThatGoBetweenTwoSubformulas = charactersThatGoBetweenTwoSubformulasS.toCharArray();

        Arrays.sort(negationCharacters);
        Arrays.sort(conjunctionCharacters);
        Arrays.sort(disjunctionCharacters);
        Arrays.sort(biconditionalCharacters);
        Arrays.sort(conditionalCharacters);
        Arrays.sort(openParenthesisChars);
        Arrays.sort(closeParenthesisChars);
        Arrays.sort(existentialQuantifierChars);
        Arrays.sort(identityChars);
        Arrays.sort(antiIdentityChars);
        Arrays.sort(allIdentityChars);

        Arrays.sort(propositionalFunctionCharacters);
        Arrays.sort(nameCharacters);

        Arrays.sort(allAllowedCharacters);

        Arrays.sort(charactersThatGoBetweenTwoSubformulas);
    }

    public formulaBuilder(String input) {
        init(input, true);
    }

    public formulaBuilder(String input, boolean doStandardReplacements) {
        init(input, doStandardReplacements);
    }

    public void init(String input, boolean doStandardReplacements) {
        if (doStandardReplacements) {
            input = doStandardReplacements(input);
        }
        this.input = input;
        isValid = false;
        complaint = "";
        result = null;
        String unexpectedCharacterComplaint = complainAboutUnexpectedCharacters(input);
        if (unexpectedCharacterComplaint.length() != 0) {
            isValid = false;
            complaint = unexpectedCharacterComplaint;
        } else {
            int[] perCharIndentationLevelArrayOfInput = getPerCharIndentationLevelArray(input);
            String bracketComplaint = getBracketComplaint(input, perCharIndentationLevelArrayOfInput);
            if (bracketComplaint.length() != 0) {
                isValid = false;
                complaint = bracketComplaint;
            } else if (input.length() == 0) {
                isValid = false;
                complaint = "Cannot parse an empty string";
            } else {
                int[] perBetweenCharsIndentationLevelArray = getPerInBetweenCharsIndentationLevelArray(input);
                boolean isAllInsideTheSameParens = input.length() != 1;
                for (int betweenIndex = 1; betweenIndex < perBetweenCharsIndentationLevelArray.length - 1 && isAllInsideTheSameParens; betweenIndex++) {
                    int levelAtThisIndex = perBetweenCharsIndentationLevelArray[betweenIndex];
                    if (levelAtThisIndex == 0) {
                        isAllInsideTheSameParens = false;
                    }
                }
                if (isAllInsideTheSameParens) {
                    String parensContents = input.substring(1, input.length() - 1);
                    formulaBuilder parensContentsParseResult = new formulaBuilder(parensContents, false);
                    if (parensContentsParseResult.isValid()) {
                        isValid = true;
                        result = parensContentsParseResult.getResult();
                    } else {
                        isValid = false;
                        complaint = parensContentsParseResult.getComplaint();
                    }
                } else {
                    //know: it is not all inside the same parentheses
                    //step 1: separate by parenthesis groups.
                    //"abc(a(b)ds)d(g)(h)" -> "abc","(a(b)ds)","d","(g)","(h)"
                    int indexOfEndOfLastZeroIndent = 0;
                    ArrayList<String> step1Result = new ArrayList<>();
                    int searchIndex = 1;
                    ArrayList<String> step1Intermediary = new ArrayList<>();
                    while (searchIndex < perBetweenCharsIndentationLevelArray.length) {
                        int indentationAtThisIndex = perBetweenCharsIndentationLevelArray[searchIndex];
                        if (indentationAtThisIndex == 0) {
                            String toAdd = input.substring(indexOfEndOfLastZeroIndent, searchIndex);
                            step1Intermediary.add(toAdd);
                            indexOfEndOfLastZeroIndent = searchIndex;
                        }
                        searchIndex++;
                    }
                    StringBuilder prevGroupBuilder = new StringBuilder();
                    boolean stringBuilderIsNew = true;
                    for (String intermediaryString : step1Intermediary) {
                        if (intermediaryString.length() != 0) {
                            char firstCharOfThis = intermediaryString.charAt(0);
                            boolean firstCharIsOpening = isAnOpening(firstCharOfThis);
                            if (firstCharIsOpening) {
                                step1Result.add(prevGroupBuilder.toString());
                                prevGroupBuilder = new StringBuilder();
                                stringBuilderIsNew = true;
                                step1Result.add(intermediaryString);
                            } else {
                                prevGroupBuilder.append(intermediaryString);
                                stringBuilderIsNew = false;
                            }
                        }
                    }
                    if (!stringBuilderIsNew) {
                        step1Result.add(prevGroupBuilder.toString());
                    }
                    //know: it is now in groups as previously described
                    //step 2: split all groups which are not enclosed in parentheses by operators.
                    //for each: "A&~B&~C" -> "A","&","~B","&","~C"
                    ArrayList<String> step2result = new ArrayList<>();
                    for (String group : step1Result) {
                        if (group.length() != 0 && !isAnOpening(group.charAt(0))) {
                            ArrayList<Integer> indicesOfCharsThatGoBetweenTwoSubformulas = new ArrayList<>();
                            for (int i = 0; i < group.length(); i++) {
                                char thisChar = group.charAt(i);
                                if (isACharThatGoesBetweenSubformulas(thisChar)) {
                                    indicesOfCharsThatGoBetweenTwoSubformulas.add(i);
                                }
                            }
                            if (indicesOfCharsThatGoBetweenTwoSubformulas.size() != 0) {
                                int firstIndex = indicesOfCharsThatGoBetweenTwoSubformulas.get(0);
                                String firstSplit = group.substring(0, firstIndex);
                                String firstChar = "" + group.charAt(firstIndex);
                                step2result.add(firstSplit);
                                step2result.add(firstChar);
                                for (int i = 1; i < indicesOfCharsThatGoBetweenTwoSubformulas.size(); i++) {
                                    int lastIndex = indicesOfCharsThatGoBetweenTwoSubformulas.get(i - 1) + 1;
                                    int thisIndex = indicesOfCharsThatGoBetweenTwoSubformulas.get(i);
                                    String thisSplit = group.substring(lastIndex, thisIndex);
                                    String thisChar = "" + group.charAt(thisIndex);
                                    step2result.add(thisSplit);
                                    step2result.add(thisChar);
                                }
                                int lastIndex = indicesOfCharsThatGoBetweenTwoSubformulas.get(indicesOfCharsThatGoBetweenTwoSubformulas.size() - 1) + 1;
                                String lastSplit = group.substring(lastIndex);
                                step2result.add(lastSplit);
                            } else {
                                step2result.add(group);
                            }
                        } else {
                            step2result.add(group);
                        }
                    }
                    //step 3: clear out empty strings from the list
                    ArrayList<String> step3result = new ArrayList<>();
                    for (String step2group : step2result) {
                        if (step2group.length() != 0) {
                            step3result.add(step2group);
                        }
                    }
                    //step 4: "...","&","~~","(...)" -> "...","&","~(...)"
                    ArrayList<String> step4result = new ArrayList<>();
                    int negationCarry = 0;
                    for (int i = 0; i < step3result.size(); i++) {
                        //now, move any negations at the ends of things to the front of them
                        String step3group = step3result.get(i);
                        int numNegationsAtEnd = getNumOfMatchingCharsAtTheEndOfString(step3group, negationCharacters);
                        if (numNegationsAtEnd == step3group.length()) {
                            negationCarry += numNegationsAtEnd;
                        } else {
                            step4result.add(getNCharsInARow(negationCarry, main.negationSymbol) + step3group.substring(0, step3group.length() - numNegationsAtEnd));
                            negationCarry = numNegationsAtEnd;
                        }
                    }
                    if (negationCarry != 0) {
                        isValid = false;
                        complaint = "Error parsing \"" + input + "\": Unexpected negation at end of formula";
                    } else {
                        //step 5 similar to step 4, move all quantifiers (and attached negations)
                        //to the thing ahead of them that isn't a quantifier or negation
                        ArrayList<String> step5result = new ArrayList<>();
                        String quantifierCarry = "";
                        for (int i = 0; i < step4result.size(); i++) {
                            String step4group = step4result.get(i);
                            String allQuantifiersAtTheEndOfThis = getAllQuantifiersAndNegationsAtTheEndOfString(step4group);
                            if (allQuantifiersAtTheEndOfThis.length() == step4group.length()) {
                                quantifierCarry = quantifierCarry + allQuantifiersAtTheEndOfThis;
                            } else {
                                step5result.add(quantifierCarry + step4group.substring(0, step4group.length() - allQuantifiersAtTheEndOfThis.length()));
                                quantifierCarry = allQuantifiersAtTheEndOfThis;
                            }
                        }
                        if (quantifierCarry.length() != 0) {
                            isValid = false;
                            complaint = "Unexpected quantifier at end of formula";
                        } else {
                            //step 6: check whether every other group is an operator that goes between subformulas
                            isValid = true;
                            for (int i = 1; i < step5result.size() && isValid; i += 2) {
                                String thisOddGroup = step5result.get(i);
                                if (thisOddGroup.length() != 1) {
                                    isValid = false;
                                    complaint = "Error parsing \"" + input + "\": This entry should be an operator, so it should only be 1 character long: " + thisOddGroup;
                                } else {
                                    char onlyChar = thisOddGroup.charAt(0);
                                    if (!isACharThatGoesBetweenSubformulas(onlyChar)) {
                                        isValid = false;
                                        complaint = "Error parsing \"" + input + "\": This character should be an operator that goes between two subformulas";
                                    }
                                }
                            }
                            if (isValid) {
                                //know: every other character is an operator
                                boolean hasOddNumberOfGroups = step5result.size() % 2 == 1;
                                if (!hasOddNumberOfGroups) {
                                    isValid = false;
                                    complaint = "Error parsing \"" + input + "\": There doesn't seem to be an operator that isn't a negation in between every expression inside here";
                                }
                            }
                            if (isValid) {
                                //step 7: sort into groups of operators and subformulas
                                ArrayList<String> operators = new ArrayList<>();
                                ArrayList<String> subformulas = new ArrayList<>();
                                for (int i = 0; i < step5result.size(); i++) {
                                    if (i % 2 == 0) {
                                        subformulas.add(step5result.get(i));
                                    } else {
                                        operators.add(step5result.get(i));
                                    }
                                }
                                if (operators.size() == 0) {
                                    //only 1 expression, know from before that not all is enclosed in the same parens,
                                    //so it must either be a negation or quantifier, or sentence, or identity
                                    String soleFormula = subformulas.get(0);
                                    char firstChar = soleFormula.charAt(0);
                                    if (isNegationChar(firstChar)) {
                                        String everythingElse = soleFormula.substring(1);
                                        formulaBuilder negationContentsParseResult = new formulaBuilder(everythingElse, false);
                                        if (negationContentsParseResult.isValid()) {
                                            isValid = true;
                                            wff inner = negationContentsParseResult.getResult();
                                            result = new negation(inner);
                                        } else {
                                            isValid = false;
                                            complaint = negationContentsParseResult.getComplaint();
                                        }
                                    } else if (isAnUppercaseFuncLetter(firstChar)) {
                                        isValid = true;
                                        String allOtherChars = soleFormula.substring(1);
                                        for (int i = 0; i < allOtherChars.length() && isValid; i++) {
                                            char thisChar = allOtherChars.charAt(i);
                                            if (!isALowercaseVarLetter(thisChar)) {
                                                isValid = false;
                                                complaint = "Error parsing sentence \"" + soleFormula + "\": Unexpected character inside sentence: " + thisChar;
                                            }
                                        }
                                        if (isValid) {
                                            //isValid = true;
                                            result = new sentence(firstChar, allOtherChars.toCharArray());
                                        }
                                    } else if (soleFormula.length() == 3 && isAnyIdentityChar(soleFormula.charAt(1))) {
                                        char thirdChar = soleFormula.charAt(2);
                                        if (!(isALowercaseVarLetter(firstChar) || isALowercaseVarLetter(thirdChar))) {
                                            isValid = false;
                                            complaint = "Error parsing identity \"" + soleFormula + "\": characters on either side of equal sign must be lowercase letters";
                                        } else {
                                            char secondChar = soleFormula.charAt(1);
                                            if (isIdentityChar(secondChar)) {
                                                isValid = true;
                                                result = new identityFormula(firstChar, thirdChar);
                                            } else if (isAntiIdentityChar(secondChar)) {
                                                isValid = true;
                                                result = new negation(new identityFormula(firstChar, thirdChar));
                                            } else {
                                                isValid = false;
                                                complaint = "Unrecognised identity character: " + secondChar;
                                            }
                                        }
                                    } else {
                                        String possiblyQuantifierAtBeginning = getFirstQuantifierAtBeginningIfApplicable(soleFormula);
                                        if (possiblyQuantifierAtBeginning.length() == 0) {

                                            isValid = false;
                                            complaint = "Cannot find negation or quantifier or sentence or identity at beginning of formula which contains no infix operators in primary nesting level: " + soleFormula;
                                        } else {
                                            String everythingElse = soleFormula.substring(possiblyQuantifierAtBeginning.length());
                                            formulaBuilder quantifierContentsParseResult = new formulaBuilder(everythingElse, false);
                                            if (!quantifierContentsParseResult.isValid()) {
                                                isValid = false;
                                                complaint = quantifierContentsParseResult.getComplaint();
                                            } else {
                                                wff insideQuantifier = quantifierContentsParseResult.getResult();
                                                char secondCharOfQuantifier = possiblyQuantifierAtBeginning.charAt(1);
                                                char secondToLastCharOfQuantifier = possiblyQuantifierAtBeginning.charAt(possiblyQuantifierAtBeginning.length() - 2);
                                                if (isAnExistenceChar(secondCharOfQuantifier)) {
                                                    isValid = true;
                                                    result = new existentialQuantifier(secondToLastCharOfQuantifier, insideQuantifier);
                                                } else if (isALowercaseVarLetter(secondCharOfQuantifier)) {
                                                    isValid = true;
                                                    result = new universalQuantifier(secondToLastCharOfQuantifier, insideQuantifier);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //know there is at least one operator
                                    ArrayList<wff> parsedSubformulas = new ArrayList<>();
                                    ArrayList<infixOperatorType> parsedOperators = new ArrayList<>();
                                    for (String operatorStr : operators) {
                                        char thisChar = operatorStr.charAt(0);
                                        infixOperatorType thisOperatorType = charToOperatorType(thisChar);
                                        parsedOperators.add(thisOperatorType);
                                    }
                                    boolean allOperatorsAreEqual = true;
                                    for (int i = 1; i < parsedOperators.size(); i++) {
                                        allOperatorsAreEqual = allOperatorsAreEqual && parsedOperators.get(i) == parsedOperators.get(0);
                                    }
                                    boolean operatorDuplicatesAreOK = parsedOperators.get(0) != infixOperatorType.COND;
                                    if (!allOperatorsAreEqual || (!operatorDuplicatesAreOK && parsedOperators.size() > 1)) {
                                        isValid = false;
                                        complaint = "Ambiguous ordering of series of different infix operators inside same nesting level: " + input;
                                    } else {
                                        isValid = true;
                                        for (int i = 0; i < subformulas.size() && isValid; i++) {
                                            String subformulaStr = subformulas.get(i);
                                            formulaBuilder thisSubformulaParseResult = new formulaBuilder(subformulaStr, false);
                                            if (!thisSubformulaParseResult.isValid()) {
                                                isValid = false;
                                                complaint = thisSubformulaParseResult.getComplaint();
                                            } else {
                                                wff thisParsedSubformula = thisSubformulaParseResult.getResult();
                                                parsedSubformulas.add(thisParsedSubformula);
                                            }
                                        }
                                        if (isValid) {
                                            while (parsedOperators.size() != 0) {
                                                wff formula1 = parsedSubformulas.remove(0);
                                                wff formula2 = parsedSubformulas.remove(0);
                                                infixOperatorType thisOperator = parsedOperators.remove(0);
                                                wff intermediateResult = null;
                                                switch (thisOperator) {
                                                    case BIC:
                                                        intermediateResult = new biconditional(formula1, formula2);
                                                        break;
                                                    case COND:
                                                        intermediateResult = new conditional(formula1, formula2);
                                                        break;
                                                    case CONJ:
                                                        intermediateResult = new conjunction(formula1, formula2);
                                                        break;
                                                    case DISJ:
                                                        intermediateResult = new disjunction(formula1, formula2);
                                                        break;
                                                }
                                                parsedSubformulas.add(0, intermediateResult);
                                            }
                                            isValid = true;
                                            result = parsedSubformulas.get(0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private enum infixOperatorType {
        BIC, COND, CONJ, DISJ
    }

    private infixOperatorType charToOperatorType(char c) {
        if (isBiconditionalOperator(c)) {
            return infixOperatorType.BIC;
        } else if (isConditionalOperator(c)) {
            return infixOperatorType.COND;
        } else if (isConjunctionOperator(c)) {
            return infixOperatorType.CONJ;
        } else if (isDisjunctionOperator(c)) {
            return infixOperatorType.DISJ;
        } else {
            return null;
        }
    }

    private static String doStandardReplacements(String in) {
        for (char negationChar : negationCharacters) {
            in = in.replace("" + negationChar + "=", "≠");
        }
        in = in.replace("==", "\uD83C\uDF46");
        in = in.replace("=", "≡");
        in = in.replace("\uD83C\uDF46", "=");
        return in;
    }

    private static String complainAboutUnexpectedCharacters(String input) {
        char[] inputChars = input.toCharArray();
        for (char inChar : inputChars) {
            int searchIndex = Arrays.binarySearch(allAllowedCharacters, inChar);
            if (searchIndex < 0) {
                return "Unexpected character: " + inChar;
            }
        }
        return "";
    }

    public static int[] getPerCharIndentationLevelArray(String input) {
        int[] r = new int[input.length()];
        int levelOfLastChar = 0;
        for (int i = 0; i < input.length(); i++) {
            char thisChar = input.charAt(i);
            boolean isOpening = isAnOpening(thisChar);
            boolean isClosing = isAClosing(thisChar);
            if (isOpening) {
                levelOfLastChar += 1;
                r[i] = levelOfLastChar;
            } else if (isClosing) {
                r[i] = levelOfLastChar;
                levelOfLastChar -= 1;
            } else {
                r[i] = levelOfLastChar;
            }
        }
        return r;
    }

    public static int[] getPerInBetweenCharsIndentationLevelArray(String input) {
        int[] r = new int[input.length() + 1];
        r[0] = 0;
        int lastLevel = 0;
        for (int i = 1; i < input.length() + 1; i++) {
            char prevChar = input.charAt(i - 1);
            boolean isOpening = isAnOpening(prevChar);
            boolean isClosing = isAClosing(prevChar);
            if (isOpening) {
                lastLevel += 1;
            }
            if (isClosing) {
                lastLevel -= 1;
            }
            r[i] = lastLevel;
        }
        return r;
    }

    private static String getBracketComplaint(String input, int[] perCharIndentationLevelArrayOfInput) {
        for (int index = 0; index < perCharIndentationLevelArrayOfInput.length; index++) {
            int levelAtThisIndex = perCharIndentationLevelArrayOfInput[index];
            if (levelAtThisIndex < 0) {
                return "Error at index " + (index - 1) + " while parsing \"" + input + "\": unexpected closing bracket";
            }
        }
        boolean lastCharIsClosingBracket = Arrays.binarySearch(closeParenthesisChars, input.charAt(input.length() - 1)) >= 0;
        int lastIndentationLevel = perCharIndentationLevelArrayOfInput[perCharIndentationLevelArrayOfInput.length - 1];
        if (lastIndentationLevel != 0 && !lastCharIsClosingBracket) {
            int indexOfUnclosedParenthesis = -1;
            boolean foundUnclosedParenthesis = false;
            for (int backIndex = input.length(); backIndex >= 0 && !foundUnclosedParenthesis; backIndex--) {
                int levelAtThisIndex = perCharIndentationLevelArrayOfInput[backIndex];
                if (levelAtThisIndex - lastIndentationLevel == -1) {
                    indexOfUnclosedParenthesis = backIndex + 1;
                    foundUnclosedParenthesis = true;
                }
            }
            return "Error parsing \"" + input + "\": unclosed bracket at index " + indexOfUnclosedParenthesis;
        }
        return "";

        /**
         char[] inChars = input.toCharArray();
         int nesting = 0;
         int index = 0;
         ArrayList<Integer> openingBracketIndices = new ArrayList<>();
         for (char inChar: inChars)
         {
         int openingSearch = Arrays.binarySearch(openParenthesisChars,inChar);
         int closingSearch = Arrays.binarySearch(closeParenthesisChars,inChar);
         if (openingSearch >= 0)
         {
         nesting++;
         openingBracketIndices.add(index);
         }
         else if (closingSearch >= 0)
         {
         nesting--;
         if (openingBracketIndices.size() != 0)
         {
         openingBracketIndices.remove(openingBracketIndices.size()-1);
         }
         }
         if (nesting < 0)
         {
         return "Error at index " + index + " while parsing \"" + input + "\": unexpected closing bracket";
         }
         index++;
         }
         if (nesting != 0)
         {
         return "Error parsing \"" + input + "\": unclosed bracket at index " + openingBracketIndices.get(openingBracketIndices.size()-1);
         }
         return "";
         **/
    }

    private static int getNumOfMatchingCharsAtTheEndOfString(String s, char[] chars) {
        int index = s.length() - 1;
        int num = 0;
        char[] charsCopy = chars.clone();
        Arrays.sort(charsCopy);
        boolean foundNonMatch = false;
        while (!foundNonMatch && index >= 0) {
            char charAtIndex = s.charAt(index);
            if (Arrays.binarySearch(charsCopy, charAtIndex) >= 0) {
                num++;
            } else {
                foundNonMatch = true;
            }
            index--;
        }
        return num;
    }

    private static String getAllQuantifiersAndNegationsAtTheEndOfString(String s) {
        String piece = s;
        String result = "";
        boolean foundNegationOrQuantifier = true;
        while (foundNegationOrQuantifier) {
            foundNegationOrQuantifier = false;
            if (piece.length() != 0) {
                char lastChar = piece.charAt(piece.length() - 1);
                if (isNegationChar(lastChar)) {
                    result = lastChar + result;
                    piece = piece.substring(0, piece.length() - 1);
                    foundNegationOrQuantifier = true;
                } else if (isAClosing(lastChar)) {
                    for (int i = 0; i < possibleNumCharsInQuantifierStart.length && !foundNegationOrQuantifier; i++) {
                        int possibleQuantifierLength = possibleNumCharsInQuantifierStart[i];
                        if (piece.length() >= possibleQuantifierLength) {
                            int indexOfLastCharPlus1 = piece.length();
                            int indexOfPossibleBeginningOfQuantifier = indexOfLastCharPlus1 - possibleQuantifierLength;
                            String maybeWholeQuantifier = piece.substring(indexOfPossibleBeginningOfQuantifier, indexOfLastCharPlus1);
                            if (isValidQuantifier(maybeWholeQuantifier)) {
                                foundNegationOrQuantifier = true;
                                piece = piece.substring(0, indexOfPossibleBeginningOfQuantifier);
                                result = maybeWholeQuantifier + result;
                            }
                        }
                    }

                }
            }
        }
        return result;
    }

    private static boolean isValidQuantifier(String s) {
        int length = s.length();
        int lengthSearchResult = Arrays.binarySearch(possibleNumCharsInQuantifierStart, length);
        if (lengthSearchResult < 0) {
            return false;
        }
        int[] perCharIndentationLevel = getPerCharIndentationLevelArray(s);
        for (int levelOfEachChar : perCharIndentationLevel) {
            if (levelOfEachChar != 1) {
                return false;
            }
        }
        int[] perBetweenCharIndentationLevel = getPerInBetweenCharsIndentationLevelArray(s);
        if (perBetweenCharIndentationLevel[perBetweenCharIndentationLevel.length - 1] != 0) {
            return false;
        }
        char secondToLastChar = s.charAt(s.length() - 2);
        if (!isALowercaseVarLetter(secondToLastChar)) {
            return false;
        }
        if (length == 3) {
            return true;
        }
        char secondChar = s.charAt(1);
        return isAnExistenceChar(secondChar);
    }

    private static String getFirstQuantifierAtBeginningIfApplicable(String s) {
        for (int possibleLength : possibleNumCharsInQuantifierStart) {
            if (s.length() >= possibleLength) {
                String possibleQuantifier = s.substring(0, possibleLength);
                if (isValidQuantifier(possibleQuantifier)) {
                    return possibleQuantifier;
                }
            }
        }
        return "";
    }

    public static String getNCharsInARow(int n, char c) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < n; i++) {
            r.append(c);
        }
        return r.toString();
    }

    public static boolean isAnOpening(char c) {
        int search = Arrays.binarySearch(openParenthesisChars, c);
        return search >= 0;
    }

    public static boolean isAClosing(char c) {
        int search = Arrays.binarySearch(closeParenthesisChars, c);
        return search >= 0;
    }

    public static boolean isACharThatGoesBetweenSubformulas(char c) {
        int search = Arrays.binarySearch(charactersThatGoBetweenTwoSubformulas, c);
        return search >= 0;
    }

    public static boolean isNegationChar(char c) {
        int search = Arrays.binarySearch(negationCharacters, c);
        return search >= 0;
    }

    public static boolean isALowercaseVarLetter(char c) {
        int search = Arrays.binarySearch(nameCharacters, c);
        return search >= 0;
    }

    public static boolean isAnExistenceChar(char c) {
        int search = Arrays.binarySearch(existentialQuantifierChars, c);
        return search >= 0;
    }

    public static boolean isAnUppercaseFuncLetter(char c) {
        int search = Arrays.binarySearch(propositionalFunctionCharacters, c);
        return search >= 0;
    }

    public static boolean isBiconditionalOperator(char c) {
        int search = Arrays.binarySearch(biconditionalCharacters, c);
        return search >= 0;
    }

    public static boolean isConditionalOperator(char c) {
        int search = Arrays.binarySearch(conditionalCharacters, c);
        return search >= 0;
    }

    public static boolean isConjunctionOperator(char c) {
        int search = Arrays.binarySearch(conjunctionCharacters, c);
        return search >= 0;
    }

    public static boolean isDisjunctionOperator(char c) {
        int search = Arrays.binarySearch(disjunctionCharacters, c);
        return search >= 0;
    }

    public static boolean isAnyIdentityChar(char c) {
        int search = Arrays.binarySearch(allIdentityChars, c);
        return search >= 0;
    }

    public static boolean isIdentityChar(char c) {
        int search = Arrays.binarySearch(identityChars, c);
        return search >= 0;
    }

    public static boolean isAntiIdentityChar(char c) {
        int search = Arrays.binarySearch(antiIdentityChars, c);
        return search >= 0;
    }
}
