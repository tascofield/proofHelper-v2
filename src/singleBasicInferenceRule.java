import java.util.ArrayList;

public class singleBasicInferenceRule extends basicInferenceRule {
    private wff[] premisePatterns;
    private wff conclusionForm;
    private wffVariable[] allVariablesInAllPremises;

    public singleBasicInferenceRule(String fullname, String abbreviation, wff[] premisePatterns, wff conclusionform, wffVariable... allVariables) {
        this.fullName = fullname;
        this.abbrev = abbreviation;
        this.premisePatterns = premisePatterns;
        this.conclusionForm = conclusionform;
        this.allVariablesInAllPremises = allVariables;
    }

    public ArrayList<wff> getPossibleConclusionsWithThesePremises(ArrayList<wff> premises) {
        ArrayList<wff> possibleConclusions = new ArrayList<>();
        if (premises.size() != premisePatterns.length) {
            return possibleConclusions;
        }
        int numArrangements = fac(premises.size());
        for (int thisArrangementNum = 0; thisArrangementNum < numArrangements; thisArrangementNum++) {
            wff[] thisArrangement = getThisArrangement(premises, thisArrangementNum);
            wffVariable.link[] maybeThisLinkage = thisExactOrderingIsValidPremiseArrayForm(thisArrangement);
            if (maybeThisLinkage != null) {
                wff thisConclusion = wffVariable.applyLinkageToTemplate(maybeThisLinkage, conclusionForm);
                possibleConclusions.add(thisConclusion);
            }
        }
        main.removeDuplicatesFromWffListAndSort(possibleConclusions);
        return possibleConclusions;
    }

    private wffVariable.link[] thisExactOrderingIsValidPremiseArrayForm(wff[] premisesInput) {
        if (premisesInput.length != premisePatterns.length) {
            return null;
        }
        wffVariable.link[] allLinks = new wffVariable.link[allVariablesInAllPremises.length];
        for (int varIndex = 0; varIndex < allVariablesInAllPremises.length; varIndex++) {
            wffVariable thisVariable = allVariablesInAllPremises[varIndex];
            ArrayList<wff> matches = new ArrayList<>();
            for (int premiseIndex = 0; premiseIndex < premisesInput.length; premiseIndex++) {
                wff thisInputPremise = premisesInput[premiseIndex];
                wff thisPatternPremise = premisePatterns[premiseIndex];
                wffVariable.variableLocationSearchResult thisSearch = wffVariable.tryToFindFormulaCorrespondingToPatternVariable(thisInputPremise, thisPatternPremise, thisVariable);
                if (thisSearch.found == wffVariable.searchResultFoundStatus.NONMATCH) {
                    return null;
                }
                if (thisSearch.found == wffVariable.searchResultFoundStatus.SAME_VAR) {
                    matches.add(thisSearch.linkedTo);
                }
            }
            if (matches.size() == 0) {
                return null;
            }
            for (int matchIndex = 1; matchIndex < matches.size(); matchIndex++) {
                wff thisMatch = matches.get(matchIndex);
                wff prevMatch = matches.get(matchIndex - 1);
                if (!thisMatch.equals(prevMatch)) {
                    return null;
                }
            }
            wffVariable.link thisLink = new wffVariable.link(thisVariable, matches.get(0));
            allLinks[varIndex] = thisLink;
        }
        return allLinks;
    }

    public String getComplaintOfTryingToGetConclusionFromThesePremises(ArrayList<wff> inputPremises) {
        String errorApplyingThis = "Error applying " + fullName + ": ";
        if (inputPremises.size() != premisePatterns.length) {
            return errorApplyingThis + inputPremises.size() + " premises given, " + premisePatterns.length + "expected.";
        }
        typeStatistics patternStatistics = new typeStatistics(premisePatterns);
        typeStatistics inputStatistics = new typeStatistics(inputPremises);
        StringBuilder r = new StringBuilder();
        r.append(errorApplyingThis);
        boolean typeNumError = false;
        for (int countedTypeIndex = 0; countedTypeIndex < typeStatistics.countedTypes.length - 1; countedTypeIndex++) {
            int expectedNumOfThisType = patternStatistics.ofEach[countedTypeIndex];
            int actualNumOfThisType = inputStatistics.ofEach[countedTypeIndex];
            if (actualNumOfThisType < expectedNumOfThisType) {
                typeNumError = true;
                String thisTypeName = typeStatistics.countedTypes[countedTypeIndex];
                r.append('\n');
                r.append("-Expected ");
                r.append(Integer.toString(expectedNumOfThisType));
                r.append(' ');
                r.append(thisTypeName);
                r.append(", got ");
                r.append(Integer.toString(actualNumOfThisType));
            }
        }
        if (typeNumError) {
            return r.toString();
        }
        return errorApplyingThis + " could not match with template";
    }

    private static int fac(int n) {
        int r = 1;
        for (int i = 2; i <= n; i++) {
            r = r * i;
        }
        return r;
    }

    private static wff[] getThisArrangement(ArrayList<wff> l, int arrangementNum) {
        wff[] r = new wff[l.size()];
        ArrayList<wff> lc = new ArrayList<>(l);
        //Collections.copy(lc,l);
        while (lc.size() != 0) {
            int removalIndex = arrangementNum % lc.size();
            arrangementNum = arrangementNum / lc.size();
            wff f = lc.remove(removalIndex);
            r[lc.size()] = f;
        }
        return r;
    }


    public String toString() {
        String[] premiseStrings = new String[premisePatterns.length];
        int maxPremiseStringLength = 0;
        for (int i = 0; i < premiseStrings.length; i++) {
            String thisPremiseString = premisePatterns[i].toString().replace("?", "");
            premiseStrings[i] = thisPremiseString;
            maxPremiseStringLength = Math.max(maxPremiseStringLength, thisPremiseString.length());
        }
        StringBuilder r = new StringBuilder();
        r.append(fullName);
        r.append(" (");
        r.append(abbrev);
        r.append(')');
        r.append('\n');
        if (premiseStrings.length != 0) {
            r.append(premiseStrings[0]);
            for (int j = 1; j < premiseStrings.length; j++) {
                r.append('\n');
                r.append(premiseStrings[j]);
            }
        }
        String lineSeparatingPremisesAndConclusion = main.getNCharsInARow(main.horizontalSingleBar, maxPremiseStringLength + 5);
        r.append('\n');
        r.append(lineSeparatingPremisesAndConclusion);
        r.append('\n');
        r.append(main.thereforeSymbol);
        String conclusionString = conclusionForm.toString().replace("?", "");
        r.append(conclusionString);
        return r.toString();
    }

    @Override
    String getInferenceFormString() {
        StringBuilder sb = new StringBuilder();
        int maxLength = -1;
        for (wff premise : premisePatterns) {
            String premiseString = premise.toString().replace("?", "");
            maxLength = Math.max(maxLength, premiseString.length());
            sb.append(premiseString);
            sb.append('\n');
        }
        String concl = main.thereforeSymbol + " " + conclusionForm.toString().replace("?", "");
        maxLength = Math.max(maxLength, concl.length());
        sb.append(main.getNCharsInARow(main.horizontalSingleBar, maxLength));
        sb.append('\n');
        sb.append(concl);
        return sb.toString();
    }

    private static class typeStatistics {

        public static String[] countedTypes = new String[]{"Biconditionals", "Conditionals", "Conjunctions", "Disjunctions", "Negations", "Of any formula"};

        public int[] ofEach = new int[countedTypes.length];

        public typeStatistics(ArrayList<wff> allFormulas) {
            wff[] fs = new wff[allFormulas.size()];
            fs = allFormulas.toArray(fs);
            init(fs);
        }

        public typeStatistics(wff[] allFormulas) {
            init(allFormulas);
        }

        private void init(wff[] fs) {
            for (wff f : fs) {
                if (f instanceof biconditional) {
                    ofEach[0]++;
                } else if (f instanceof conditional) {
                    ofEach[1]++;
                } else if (f instanceof conjunction) {
                    ofEach[2]++;
                } else if (f instanceof disjunction) {
                    ofEach[3]++;
                } else if (f instanceof negation) {
                    ofEach[4]++;
                } else {
                    ofEach[5]++;
                }
            }
        }
    }
}
