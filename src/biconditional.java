import java.util.ArrayList;

public class biconditional extends wff {
    private wff inner1;
    private wff inner2;

    public biconditional(wff inner1, wff inner2) {
        this.inner1 = inner1;
        this.inner2 = inner2;
    }

    public wff getInner1() {
        return inner1;
    }

    public wff getInner2() {
        return inner2;
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        wff i1Repl = inner1.getNewCopyWithReplacement(from, to);
        wff i2Repl = inner2.getNewCopyWithReplacement(from, to);
        return new conditional(i1Repl, i2Repl);
    }

    public ArrayList<Character> getAllUsedLetters() {
        ArrayList<Character> r1 = inner1.getAllUsedLetters();
        r1.addAll(inner2.getAllUsedLetters());
        main.removeDuplicatesFromCharListAndSort(r1);
        return r1;
    }

    public ArrayList<Character> getAllLettersUsedAsVariables() {
        ArrayList<Character> r1 = inner1.getAllLettersUsedAsVariables();
        r1.addAll(inner2.getAllLettersUsedAsVariables());
        main.removeDuplicatesFromCharListAndSort(r1);
        return r1;
    }

    public ArrayList<Character> getAllLettersUsedAsConstants() {
        ArrayList<Character> r1 = inner1.getAllLettersUsedAsConstants();
        r1.addAll(inner2.getAllLettersUsedAsConstants());
        main.removeDuplicatesFromCharListAndSort(r1);
        return r1;
    }

    public boolean equals(wff other) {
        if (!(other instanceof biconditional)) {
            return false;
        }
        wff otherI1 = ((biconditional) other).getInner1();
        if (!otherI1.equals(inner1)) {
            return false;
        }
        wff otherI2 = ((biconditional) other).getInner2();
        return (otherI2.equals(inner2));
    }

    ArrayList<wff> getSubformulasInOrder() {
        ArrayList<wff> r = new ArrayList<>();
        r.add(inner1);
        r.add(inner2);
        return r;
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        if (index == 0) {
            return new biconditional(to, inner2);
        }
        return new biconditional(inner1, to);
    }

    wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement) {
        if (path.length == 0) {
            return replacement;
        }
        int nextDest = path[0];
        int[] truncatedPath = wff.truncatePath(path);
        if (nextDest == 0) {
            wff newInner1 = inner1.makeNewCopyWithFormulaAtThisPathReplacedBy(truncatedPath, replacement);
            return new biconditional(newInner1, inner2);
        }
        if (nextDest == 1) {
            wff newInner2 = inner2.makeNewCopyWithFormulaAtThisPathReplacedBy(truncatedPath, replacement);
            return new biconditional(inner1, newInner2);
        }
        return this;
    }

    wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to) {
        if (this.equals(from)) {
            return to;
        }
        wff newInner1 = inner1.makeNewCopyWithAllInstancesOfThisReplacedBy(from, to);
        wff newInner2 = inner2.makeNewCopyWithAllInstancesOfThisReplacedBy(from, to);
        return new biconditional(newInner1, newInner2);
    }

    wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs) {
        return new biconditional(subs.get(0), subs.get(1));
    }

    public String toString() {
        String i1S = main.encapsulateIfNecessary(inner1);
        String i2S = main.encapsulateIfNecessary(inner2);
        return i1S + main.biconditionalSymbol + i2S;
    }
}
