import java.util.ArrayList;

public class universalQuantifier extends quantifier {
    public universalQuantifier(char var, wff inner) {
        this.var = var;
        this.inner = inner;
    }

    public String getQuantifierString() {
        return "(" + var + ")";
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        char varOfNew;
        if (from == var) {
            //this line should never be called
            varOfNew = to;
        } else {
            varOfNew = var;
        }
        wff innerReplacement = inner.getNewCopyWithReplacement(from, to);
        return new universalQuantifier(varOfNew, innerReplacement);
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        return new universalQuantifier(var, to);
    }

    wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement) {
        if (path.length == 0) {
            return replacement;
        }
        int nextDest = path[0];
        int[] truncatedPath = wff.truncatePath(path);
        if (nextDest == 0) {
            wff newInner = inner.makeNewCopyWithFormulaAtThisPathReplacedBy(truncatedPath, replacement);
            return new universalQuantifier(var, newInner);
        }
        return this;
    }

    wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to) {
        if (this.equals(from)) {
            return to;
        }
        wff newInner = inner.makeNewCopyWithAllInstancesOfThisReplacedBy(from, to);
        return new universalQuantifier(var, newInner);
    }

    public ArrayList<wff> getAllPossibleNameReplacementChoicesWith(char from, char to) {
        ArrayList<wff> replacementsOfInner = inner.getAllPossibleNameReplacementChoicesWith(from, to);
        ArrayList<wff> r = new ArrayList<>();
        r.addAll(replacementsOfInner);
        if (var == from) {
            for (wff innerReplacement : replacementsOfInner) {
                universalQuantifier thisReplacement = new universalQuantifier(to, innerReplacement);
                r.add(thisReplacement);
            }
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }

    @Override
    wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs) {
        return new universalQuantifier(var, subs.get(0));
    }
}
