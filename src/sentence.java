import java.util.ArrayList;

public class
sentence extends wff {
    private char func;
    private char[] namesOrd;

    public sentence(char propositionalFunciton, char[] namesOrd) {
        this.func = propositionalFunciton;
        this.namesOrd = namesOrd;
    }

    public char getPropositionalFunctionChar() {
        return func;
    }

    public char[] getNamesOrdering() {
        return namesOrd;
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        char[] newNamesOrd = new char[namesOrd.length];
        for (int i = 0; i < namesOrd.length; i++) {
            char name = namesOrd[i];
            if (name == from) {
                newNamesOrd[i] = to;
            } else {
                newNamesOrd[i] = name;
            }
        }
        return new sentence(func, newNamesOrd);
    }

    public ArrayList<Character> getAllUsedLetters() {
        return getAllLettersUsedAsConstants();
    }

    public ArrayList<Character> getAllLettersUsedAsConstants() {
        ArrayList<Character> r = new ArrayList<>();
        for (char name : namesOrd) {
            r.add(name);
        }
        return r;
    }

    public ArrayList<Character> getAllLettersUsedAsVariables() {
        return new ArrayList<>();
    }

    public boolean equals(wff other) {
        if (!(other instanceof sentence)) {
            return false;
        }
        char otherFunc = ((sentence) other).getPropositionalFunctionChar();
        if (otherFunc != func) {
            return false;
        }
        char[] otherNamesOrd = ((sentence) other).getNamesOrdering();
        if (otherNamesOrd.length != namesOrd.length) {
            return false;
        }
        for (int i = 0; i < namesOrd.length; i++) {
            if (otherNamesOrd[i] != namesOrd[i]) {
                return false;
            }
        }
        return true;
    }

    ArrayList<wff> getSubformulasInOrder() {
        return new ArrayList<>();
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        return this;
    }

    @Override
    wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement) {
        if (path.length == 0) {
            return replacement;
        }
        return this;
    }

    wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to) {
        if (this.equals(from)) {
            return to;
        }
        return this;
    }

    public ArrayList<wff> getAllPossibleNameReplacementChoicesWith(char from, char to) {
        int numMatches = 0;
        for (char thisName : namesOrd) {
            if (thisName == from) {
                numMatches++;
            }
        }
        int numResults = 1 << numMatches;
        ArrayList<wff> r = new ArrayList<>();
        if (numResults == 1) {
            r.add(this);
            return r;
        }
        for (int thisResultNum = 0; thisResultNum < numResults; thisResultNum++) {
            char[] newNamesOrd = new char[namesOrd.length];
            int n = thisResultNum;
            for (int nameIndex = 0; nameIndex < namesOrd.length; nameIndex++) {
                char thisName = namesOrd[nameIndex];
                if (thisName == from) {
                    boolean swapThis = n % 2 == 1;
                    n = n >> 1;
                    char setTo;
                    if (swapThis) {
                        setTo = to;
                    } else {
                        setTo = thisName;
                    }
                    newNamesOrd[nameIndex] = setTo;
                } else {
                    newNamesOrd[nameIndex] = thisName;
                }
            }
            sentence newSentence = new sentence(func, newNamesOrd);
            r.add(newSentence);
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }

    public replacedNameSearchResult maybeFindTheNameThisWasReplacedTo(wff template, char from) {
        if (!(template instanceof sentence)) {
            return replacedNameSearchResult.inconsistent;
        }
        if (((sentence) template).getPropositionalFunctionChar() != func) {
            return replacedNameSearchResult.inconsistent;
        }
        ArrayList<Character> changedTo = new ArrayList<>();
        char[] tempNamesOrd = ((sentence) template).getNamesOrdering();
        for (int i = 0; i < namesOrd.length; i++) {
            char thisName = namesOrd[i];
            char tempName = tempNamesOrd[i];
            if (thisName == from) {
                changedTo.add(tempName);
            } else if (thisName != tempName) {
                return replacedNameSearchResult.inconsistent;
            }
        }
        if (changedTo.size() == 0) {
            return replacedNameSearchResult.notApplicable;
        }
        if (changedTo.size() > 1) {
            for (int j = 1; j < changedTo.size(); j++) {
                char thisResult = changedTo.get(j);
                char lastResult = changedTo.get(j - 1);
                if (thisResult != lastResult) {
                    return replacedNameSearchResult.inconsistent;
                }
            }
        }
        return new replacedNameSearchResult(true, changedTo.get(0));
    }

    @Override
    wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs) {
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(func);
        for (char name : namesOrd) {
            sb.append(name);
        }
        return sb.toString();
    }

    public int compareTo(wff other) {
        if (other instanceof sentence) {
            char otherFunc = ((sentence) other).func;
            int funcDiff = func - otherFunc;
            if (funcDiff != 0) {
                return funcDiff;
            }
            char[] otherNames = ((sentence) other).namesOrd;
            int namesLengthDiff = namesOrd.length - otherNames.length;
            if (namesLengthDiff != 0) {
                return namesLengthDiff;
            }
            for (int nameIndex = 0; nameIndex < namesOrd.length; nameIndex++) {
                char thisName = namesOrd[nameIndex];
                char otherName = otherNames[nameIndex];
                int thisNameDiff = thisName - otherName;
                if (thisNameDiff != 0) {
                    return thisNameDiff;
                }
            }
            return 0;
        } else {
            return super.compareTo(other);
        }
    }
}
