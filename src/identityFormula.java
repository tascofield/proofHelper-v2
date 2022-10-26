import java.util.ArrayList;

public class identityFormula extends wff {

    private char name1;
    private char name2;

    public identityFormula(char name1, char name2) {
        this.name1 = name1;
        this.name2 = name2;
    }

    public char getName1() {
        return name1;
    }

    public char getName2() {
        return name2;
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        char newName1;
        if (name1 == from) {
            newName1 = to;
        } else {
            newName1 = name1;
        }
        char newName2;
        if (name2 == from) {
            newName2 = to;
        } else {
            newName2 = name2;
        }
        return new identityFormula(newName1, newName2);
    }

    public ArrayList<Character> getAllUsedLetters() {
        return getAllLettersUsedAsConstants();
    }

    public ArrayList<Character> getAllLettersUsedAsConstants() {
        ArrayList<Character> r = new ArrayList<>();
        r.add(name1);
        r.add(name2);
        return r;
    }

    public ArrayList<Character> getAllLettersUsedAsVariables() {
        return new ArrayList<>();
    }

    public boolean equals(wff other) {
        if (!(other instanceof identityFormula)) {
            return false;
        }
        char otherName1 = ((identityFormula) other).getName1();
        if (name1 != otherName1) {
            return false;
        }
        char otherName2 = ((identityFormula) other).getName2();
        return name2 == otherName2;
    }

    ArrayList<wff> getSubformulasInOrder() {
        return new ArrayList<>();
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        return this;
    }

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
        ArrayList<wff> r = new ArrayList<>();
        if (name1 == from) {
            r.add(new identityFormula(to, name2));
            if (name2 == from) {
                r.add(new identityFormula(to, to));
            }
        }
        if (name2 == from) {
            r.add(new identityFormula(name1, to));
        }
        r.add(this);
        return r;
    }

    public replacedNameSearchResult maybeFindTheNameThisWasReplacedTo(wff template, char from) {
        if (!(template instanceof identityFormula)) {
            return replacedNameSearchResult.inconsistent;
        }
        char tempName1 = ((identityFormula) template).getName1();
        char tempName2 = ((identityFormula) template).getName2();
        boolean changeName1 = name1 == from;
        boolean changeName2 = name2 == from;
        if (changeName1 && changeName2) {
            boolean bothResultsMatch = tempName1 == tempName2;
            if (bothResultsMatch) {
                return new replacedNameSearchResult(true, tempName1);
            } else {
                return replacedNameSearchResult.inconsistent;
            }
        }
        if (changeName1) {
            return new replacedNameSearchResult(true, tempName1);
        }
        if (changeName2) {
            return new replacedNameSearchResult(true, tempName2);
        }
        return replacedNameSearchResult.notApplicable;
    }

    @Override
    wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs) {
        return this;
    }

    public String toString() {
        return "" + name1 + main.equivalenceSymbol + name2;
    }

    public String getNegatedForm() {
        return "" + name1 + main.nonEquivalenceSymbol + name2;
    }
}
