import java.util.ArrayList;

public class conditional extends wff {
    private wff ant;
    private wff cons;

    public conditional(wff antecedent, wff consequent) {
        this.ant = antecedent;
        this.cons = consequent;
    }

    public wff getAntecedent() {
        return ant;
    }

    public wff getConsequent() {
        return cons;
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        wff antRepl = ant.getNewCopyWithReplacement(from, to);
        wff consRepl = cons.getNewCopyWithReplacement(from, to);
        return new conditional(antRepl, consRepl);
    }

    public ArrayList<Character> getAllUsedLetters() {
        ArrayList<Character> r1 = ant.getAllUsedLetters();
        r1.addAll(cons.getAllUsedLetters());
        main.removeDuplicatesFromCharListAndSort(r1);
        return r1;
    }

    public ArrayList<Character> getAllLettersUsedAsVariables() {
        ArrayList<Character> r1 = ant.getAllLettersUsedAsVariables();
        r1.addAll(cons.getAllLettersUsedAsVariables());
        main.removeDuplicatesFromCharListAndSort(r1);
        return r1;
    }

    public ArrayList<Character> getAllLettersUsedAsConstants() {
        ArrayList<Character> r1 = ant.getAllLettersUsedAsConstants();
        r1.addAll(cons.getAllLettersUsedAsConstants());
        main.removeDuplicatesFromCharListAndSort(r1);
        return r1;
    }

    public boolean equals(wff other) {
        if (!(other instanceof conditional)) {
            return false;
        }
        wff otherAnt = ((conditional) other).getAntecedent();
        if (!otherAnt.equals(ant)) {
            return false;
        }
        wff otherCons = ((conditional) other).getConsequent();
        return (otherCons.equals(cons));
    }

    @Override
    ArrayList<wff> getSubformulasInOrder() {
        ArrayList<wff> r = new ArrayList<>();
        r.add(ant);
        r.add(cons);
        return r;
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        if (index == 0) {
            return new conditional(to, cons);
        }
        return new conditional(ant, to);
    }

    wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement) {
        if (path.length == 0) {
            return replacement;
        }
        int nextDest = path[0];
        int[] truncatedPath = wff.truncatePath(path);
        if (nextDest == 0) {
            wff newAnt = ant.makeNewCopyWithFormulaAtThisPathReplacedBy(truncatedPath, replacement);
            return new conditional(newAnt, cons);
        }
        if (nextDest == 1) {
            wff newCons = cons.makeNewCopyWithFormulaAtThisPathReplacedBy(truncatedPath, replacement);
            return new conditional(ant, newCons);
        }
        return this;
    }

    wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to) {
        if (this.equals(from)) {
            return to;
        }
        wff newAnt = ant.makeNewCopyWithAllInstancesOfThisReplacedBy(from, to);
        wff newCons = cons.makeNewCopyWithAllInstancesOfThisReplacedBy(from, to);
        return new conditional(newAnt, newCons);
    }

    @Override
    wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs) {
        return new conditional(subs.get(0), subs.get(1));
    }

    public String toString() {
        String antS = main.encapsulateIfNecessary(ant);
        String consS = main.encapsulateIfNecessary(cons);
        return antS + main.horseshoeSymbol + consS;
    }
}
