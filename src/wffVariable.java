import java.util.ArrayList;

public class wffVariable extends wff {
    public char name;

    public wffVariable(char name) {
        this.name = name;
    }

    public static final wffVariable p = new wffVariable('p');
    public static final wffVariable q = new wffVariable('q');
    public static final wffVariable r = new wffVariable('r');
    public static final wffVariable s = new wffVariable('s');


    public ArrayList<Character> getAllLettersUsedAsConstants() {
        return new ArrayList<>();
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        return new wffVariable(name);
    }

    public ArrayList<Character> getAllUsedLetters() {
        return new ArrayList<>();
    }

    public boolean equals(wff other) {
        return this == other || (other instanceof wffVariable && ((wffVariable) other).name == name);
    }

    public ArrayList<wff> getSubformulasInOrder() {
        return new ArrayList<>();
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        return this;
    }

    wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement) {
        return this;
    }

    public ArrayList<Character> getAllLettersUsedAsVariables() {
        return new ArrayList<>();
    }

    public String toString() {
        return "" + name + "?";
    }

    public wff makeNewCopyWithTheseSubformulas(ArrayList<wff> newSubformulas) {
        return this;
    }

    wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to) {
        if (this.equals(from)) {
            return to;
        }
        return this;
    }

    public static boolean isSuitableForShallowPatternMatch(wff in, wff pattern, wffVariable[] vars) {
        boolean r = true;
        for (wffVariable var : vars) {
            r = r && tryToFindFormulaCorrespondingToPatternVariable(in, pattern, var).found != searchResultFoundStatus.NONMATCH;
        }
        return r;
    }

    public static variableLocationSearchResult tryToFindFormulaCorrespondingToPatternVariable(wff in, wff pattern, wffVariable var) {
        //returns:
        //SAME_VAR: iff it found that the pattern is consistent with the input wrt the given variable
        //DIFF_VAR: iff it found no instances of the given var in the pattern
        //NONMATCH:: iff it found that the pattern is inconsistent with the input
        if (pattern.equals(var)) {
            return new variableLocationSearchResult(searchResultFoundStatus.SAME_VAR, var, in);
        }
        if (pattern instanceof wffVariable) {
            return new variableLocationSearchResult(searchResultFoundStatus.DIFF_VAR, var, null);
        }
        boolean diffClass = in.getClass() != pattern.getClass();
        if (diffClass) {
            return new variableLocationSearchResult(searchResultFoundStatus.NONMATCH, var, null);
        }
        ArrayList<wff> subformulasOfInput = in.getSubformulasInOrder();
        ArrayList<wff> subformulasOfPattern = pattern.getSubformulasInOrder();
        ArrayList<wff> matchedFormulas = new ArrayList<>();
        for (int i = 0; i < subformulasOfInput.size(); i++) {
            wff thisSubformulaOfInput = subformulasOfInput.get(i);
            wff correspondingSubformulaOfPattern = subformulasOfPattern.get(i);
            variableLocationSearchResult thisResult = tryToFindFormulaCorrespondingToPatternVariable(thisSubformulaOfInput, correspondingSubformulaOfPattern, var);
            if (thisResult.found == searchResultFoundStatus.SAME_VAR) {
                matchedFormulas.add(thisResult.linkedTo);
            } else if (thisResult.found == searchResultFoundStatus.NONMATCH) {
                return new variableLocationSearchResult(searchResultFoundStatus.NONMATCH, var, null);
            }
        }
        if (matchedFormulas.size() == 0) {
            return new variableLocationSearchResult(searchResultFoundStatus.DIFF_VAR, var, null);
        }
        boolean allMatchedFormulasAreEqual = true;
        for (int i = 1; i < matchedFormulas.size() && allMatchedFormulasAreEqual; i++) {
            if (!matchedFormulas.get(i).equals(matchedFormulas.get(i - 1))) {
                allMatchedFormulasAreEqual = false;
            }
        }
        if (!allMatchedFormulasAreEqual) {
            return new variableLocationSearchResult(searchResultFoundStatus.NONMATCH, var, null);
        }
        return new variableLocationSearchResult(searchResultFoundStatus.SAME_VAR, var, matchedFormulas.get(0));
    }

    /*
    @Deprecated
    public static wff applyLinkageToInput(replacementLinkage l, wff in, wff template)
    {
        template = applyLinkageToTemplate(l,template);
        int[] thisPath = l.pathToReplacedFormula;
        return in.makeNewCopyWithFormulaAtThisPathReplacedBy(thisPath,template);
    }
    */

    public static wff applyLinkageToTemplate(link[] links, wff template) {
        for (link thisLink : links) {
            wffVariable thisVar = thisLink.var;
            wff thisReplacement = thisLink.replaceVarWith;
            template = template.makeNewCopyWithAllInstancesOfThisReplacedBy(thisVar, thisReplacement);
        }
        return template;
    }

    @Deprecated
    public static ArrayList<replacementLinkage> getAllLinkagesForThisExchangeableFormula(wff in, wff pattern, wffVariable[] variablesInPattern, int[] pathToGetHere) {
        ArrayList<replacementLinkage> r = new ArrayList<>();
        boolean majorOperatorIsAMatch = isSuitableForShallowPatternMatch(in, pattern, variablesInPattern);
        if (majorOperatorIsAMatch) {
            link[] allLinks = new link[variablesInPattern.length];
            for (int varIndex = 0; varIndex < variablesInPattern.length; varIndex++) {
                wffVariable varInPattern = variablesInPattern[varIndex];
                wff correspondingSubformula = findSubFormulaCorrespondingToVariableInMatchedPattern(in, pattern, varInPattern);
                link thisLink = new link(varInPattern, correspondingSubformula);
                allLinks[varIndex] = thisLink;
            }
            replacementLinkage thisLinkage = new replacementLinkage(allLinks, pathToGetHere);
            r.add(thisLinkage);
        }
        ArrayList<wff> subformulasOfInput = in.getSubformulasInOrder();
        for (int indexOfSubformula = 0; indexOfSubformula < subformulasOfInput.size(); indexOfSubformula++) {
            wff thisSubformula = subformulasOfInput.get(indexOfSubformula);
            int[] newPath = new int[pathToGetHere.length + 1];
            System.arraycopy(pathToGetHere, 0, newPath, 0, pathToGetHere.length);
            newPath[newPath.length - 1] = indexOfSubformula;
            ArrayList<replacementLinkage> linkagesOfThisSubformula = getAllLinkagesForThisExchangeableFormula(thisSubformula, pattern, variablesInPattern, newPath);
            r.addAll(linkagesOfThisSubformula);
        }
        return r;
    }


    private static wff findSubFormulaCorrespondingToVariableInMatchedPattern(wff matchedFormula, wff pattern, wffVariable var) {
        if (pattern.equals(var)) {
            return matchedFormula;
        }
        ArrayList<wff> subPatterns = pattern.getSubformulasInOrder();
        ArrayList<wff> subFormulas = matchedFormula.getSubformulasInOrder();
        for (int i = 0; i < subPatterns.size(); i++) {
            wff thisSubPattern = subPatterns.get(i);
            wff correspondingSubformula = subFormulas.get(i);
            wff searchResult = findSubFormulaCorrespondingToVariableInMatchedPattern(correspondingSubformula, thisSubPattern, var);
            if (searchResult != null) {
                return searchResult;
            }
        }
        return null;
    }

    public static link[] maybeGetLinkageWithThisInputAndPattern(wff in, wff pattern, wffVariable[] allVariables) {
        link[] allLinks = new link[allVariables.length];
        for (int varIndex = 0; varIndex < allVariables.length; varIndex++) {
            wffVariable thisVar = allVariables[varIndex];
            variableLocationSearchResult result = tryToFindFormulaCorrespondingToPatternVariable(in, pattern, thisVar);
            if (result.found == searchResultFoundStatus.SAME_VAR) {
                link thisLink = new link(thisVar, result.linkedTo);
                allLinks[varIndex] = thisLink;
            } else {
                return null;
            }
        }
        return allLinks;
    }

    @Deprecated
    public static class replacementLinkage {
        public link[] allLinks;
        public int[] pathToReplacedFormula;

        public replacementLinkage(link[] allLinks, int[] path) {
            this.allLinks = allLinks;
            this.pathToReplacedFormula = path;
        }
    }

    public static class link {
        public wffVariable var;
        public wff replaceVarWith;

        public link(wffVariable var, wff replacement) {
            this.var = var;
            this.replaceVarWith = replacement;
        }
    }

    public static class variableLocationSearchResult {
        public searchResultFoundStatus found;
        public wffVariable var;
        public wff linkedTo;

        public variableLocationSearchResult(searchResultFoundStatus found, wffVariable var, wff linkedTo) {
            this.found = found;
            this.var = var;
            this.linkedTo = linkedTo;
        }
    }

    public enum searchResultFoundStatus {
        SAME_VAR, DIFF_VAR, NONMATCH
    }

}