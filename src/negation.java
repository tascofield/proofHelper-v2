import java.util.ArrayList;

public class negation extends wff {
    private wff inner;

    public negation(wff inner) {
        this.inner = inner;
    }

    public wff getInr() {
        return inner;
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        wff innerReplacement = inner.getNewCopyWithReplacement(from, to);
        return new negation(innerReplacement);
    }

    public ArrayList<Character> getAllUsedLetters() {
        return inner.getAllUsedLetters();
    }

    public ArrayList<Character> getAllLettersUsedAsVariables() {
        return inner.getAllLettersUsedAsVariables();
    }

    public ArrayList<Character> getAllLettersUsedAsConstants() {
        return inner.getAllLettersUsedAsConstants();
    }

    public boolean equals(wff other) {
        if (!(other instanceof negation)) {
            return false;
        }
        wff otherInner = ((negation) other).getInr();
        return inner.equals(otherInner);
    }

    ArrayList<wff> getSubformulasInOrder() {
        ArrayList<wff> r = new ArrayList<>();
        r.add(inner);
        return r;
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        return new negation(to);
    }

    wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement) {
        if (path.length == 0) {
            return replacement;
        }
        int nextDest = path[0];
        int[] truncatedPath = wff.truncatePath(path);
        if (nextDest == 0) {
            wff newInner = inner.makeNewCopyWithFormulaAtThisPathReplacedBy(truncatedPath, replacement);
            return new negation(newInner);
        }
        return this;
    }

    wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to) {
        if (this.equals(from)) {
            return to;
        }
        wff newInner = inner.makeNewCopyWithAllInstancesOfThisReplacedBy(from, to);
        return new negation(newInner);
    }

    @Override
    wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs) {
        return new negation(subs.get(0));
    }

    public String toString() {
        if (inner instanceof identityFormula && main.disPlayNegatedIdentitiesWithDedicatedSymbol) {
            return ((identityFormula) inner).getNegatedForm();
        }
        String innerString = main.encapsulateIfNecessary(inner);
        return main.negationSymbol + innerString;
    }
}
