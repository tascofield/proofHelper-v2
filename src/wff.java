import java.util.ArrayList;

public abstract class wff implements Comparable<wff> {
    abstract ArrayList<Character> getAllUsedLetters();

    abstract ArrayList<Character> getAllLettersUsedAsVariables();

    abstract ArrayList<Character> getAllLettersUsedAsConstants();

    abstract wff getNewCopyWithReplacement(char from, char to);

    abstract boolean equals(wff other);

    abstract ArrayList<wff> getSubformulasInOrder();

    abstract wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to);

    abstract wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement);

    abstract wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to);

    //abstract ArrayList<wff> getAllPossibleNameReplacementChoicesWith(char from, char to);
    abstract wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs);

    public replacedNameSearchResult maybeFindTheNameThisWasReplacedTo(wff template, char from) {
        boolean diffClass = this.getClass() != template.getClass();
        if (diffClass) {
            return replacedNameSearchResult.inconsistent;
        }
        ArrayList<wff> subFormulasOfThis = getSubformulasInOrder();
        ArrayList<wff> subFormulasOfTemplate = template.getSubformulasInOrder();
        ArrayList<Character> results = new ArrayList<>();
        for (int subIndex = 0; subIndex < subFormulasOfThis.size(); subIndex++) {
            wff subformulaOfThis = subFormulasOfThis.get(subIndex);
            wff subformulaOfTemplate = subFormulasOfTemplate.get(subIndex);
            replacedNameSearchResult resultOfThese = subformulaOfThis.maybeFindTheNameThisWasReplacedTo(subformulaOfTemplate, from);
            if (!resultOfThese.hasResult) {
                return resultOfThese;
            }
            if (resultOfThese.nameThisWasReplacedWith != '?') {
                results.add(resultOfThese.nameThisWasReplacedWith);
            }
        }
        if (results.size() == 0) {
            return replacedNameSearchResult.notApplicable;
        }
        for (int i = 1; i < results.size(); i++) {
            char thisChar = results.get(i);
            char lastChar = results.get(i - 1);
            if (thisChar != lastChar) {
                return replacedNameSearchResult.inconsistent;
            }
        }
        return new replacedNameSearchResult(true, results.get(0));
    }

    public ArrayList<wff> getAllPossibleNameReplacementChoicesWith(char from, char to) {
        ArrayList<wff> inners = getSubformulasInOrder();
        ArrayList<ArrayList<wff>> possibleNameReplacementsOfEachInner = new ArrayList<>();
        int totalNumReplacements = 1;
        for (wff thisInner : inners) {
            ArrayList<wff> possibleNameReplacementsOfThisInner = thisInner.getAllPossibleNameReplacementChoicesWith(from, to);
            totalNumReplacements = totalNumReplacements * possibleNameReplacementsOfThisInner.size();
            possibleNameReplacementsOfEachInner.add(possibleNameReplacementsOfThisInner);
        }
        ArrayList<wff> r = new ArrayList<>();
        for (int thisReplacementNum = 0; thisReplacementNum < totalNumReplacements; thisReplacementNum++) {
            int n = thisReplacementNum;
            int[] indicesOfChoices = new int[inners.size()];
            for (int thisInnerChoiceIndexIndex = 0; thisInnerChoiceIndexIndex < inners.size(); thisInnerChoiceIndexIndex++) {
                ArrayList<wff> possibleReplacementsOfThisInner = possibleNameReplacementsOfEachInner.get(thisInnerChoiceIndexIndex);
                int thisSize = possibleReplacementsOfThisInner.size();
                int thisChoice = n % thisSize;
                n = n / thisSize;
                indicesOfChoices[thisInnerChoiceIndexIndex] = thisChoice;
            }
            ArrayList<wff> theseSpecificChoices = new ArrayList<>();
            for (int thisInnerIndex = 0; thisInnerIndex < inners.size(); thisInnerIndex++) {
                int thisChoiceIndex = indicesOfChoices[thisInnerIndex];
                ArrayList<wff> possibleChoicesForThisChoice = possibleNameReplacementsOfEachInner.get(thisInnerIndex);
                wff thisChoice = possibleChoicesForThisChoice.get(thisChoiceIndex);
                theseSpecificChoices.add(thisChoice);
            }
            wff thisNewReplacement = makeNewCopyWithTheseSubformulas(theseSpecificChoices);
            r.add(thisNewReplacement);
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }

    public ArrayList<wff> getAllPossibleRecursiveReplacementsWith(recursiveReplacementRule r) {
        ArrayList<wff> allInners = getSubformulasInOrder();
        ArrayList<wff> results = new ArrayList<>();
        ArrayList<wff> shallowReplacementsOfThis = r.getAllShallowReplacementsForThisFormula(this);
        results.addAll(shallowReplacementsOfThis);
        for (int thisInnerIndex = 0; thisInnerIndex < allInners.size(); thisInnerIndex++) {
            wff thisInner = allInners.get(thisInnerIndex);
            ArrayList<wff> allPossibleReplacementsOfThisInner = thisInner.getAllPossibleRecursiveReplacementsWith(r);
            for (wff thisReplacementOfThisInner : allPossibleReplacementsOfThisInner) {
                ArrayList<wff> orderedSubformulaListWithThisReplacement = new ArrayList<>(allInners);
                orderedSubformulaListWithThisReplacement.set(thisInnerIndex, thisReplacementOfThisInner);
                wff newResult = makeNewCopyWithTheseSubformulas(orderedSubformulaListWithThisReplacement);
                results.add(newResult);
            }
        }
        return results;
    }

    public boolean containsAnyAsSubformula(wff[] fs) {
        for (wff thingItMightContain : fs) {
            if (this.containsAsSubformula(thingItMightContain)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAsSubformula(wff f) {
        if (this.equals(f)) {
            return true;
        }
        ArrayList<wff> allSubformulasOfThis = this.getSubformulasInOrder();
        for (wff subformula : allSubformulasOfThis) {
            if (subformula.containsAsSubformula(f)) {
                return true;
            }
        }
        return false;
    }

    public int compareTo(wff o) {
        int classDiff = getClassDiff(this, o);
        if (classDiff != 0) {
            return classDiff;
        }
        ArrayList<wff> thisInners = this.getSubformulasInOrder();
        ArrayList<wff> otherInners = o.getSubformulasInOrder();
        for (int i = 0; i < thisInners.size(); i++) {
            wff thisInner = thisInners.get(i);
            wff otherInner = otherInners.get(i);
            int comparison = thisInner.compareTo(otherInner);
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    public static int getClassDiff(wff f1, wff f2) {
        return getClassEnum(f1) - getClassEnum(f2);
    }

    private static int getClassEnum(wff f) {
        if (f instanceof biconditional) {
            return 1;
        }
        if (f instanceof conditional) {
            return 2;
        }
        if (f instanceof conjunction) {
            return 3;
        }
        if (f instanceof disjunction) {
            return 4;
        }
        if (f instanceof existentialQuantifier) {
            return 5;
        }
        if (f instanceof identityFormula) {
            return 6;
        }
        if (f instanceof negation) {
            return 7;
        }
        if (f instanceof sentence) {
            return 8;
        }
        if (f instanceof universalQuantifier) {
            return 9;
        }
        if (f instanceof wffVariable) {
            return -9001;
        }
        return 0;
    }

    public static int[] truncatePath(int[] oldPath) {
        //make a new array with all the same elements except the first,
        //whose length is equal to the length of the old path minus one
        if (oldPath.length == 0) {
            return oldPath;
        }
        int[] newPath = new int[oldPath.length - 1];
        System.arraycopy(oldPath, 1, newPath, 0, oldPath.length - 1);
        return newPath;
    }

    public static class replacedNameSearchResult {
        public boolean hasResult;
        public char nameThisWasReplacedWith;

        public static replacedNameSearchResult inconsistent = new replacedNameSearchResult(false, '?');
        public static replacedNameSearchResult notApplicable = new replacedNameSearchResult(true, '?');

        public replacedNameSearchResult(boolean hasResult, char nameThisWasReplacedWith) {
            this.hasResult = hasResult;
            this.nameThisWasReplacedWith = nameThisWasReplacedWith;
        }
    }
}
