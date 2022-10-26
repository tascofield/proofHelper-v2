import java.util.ArrayList;

public class existentialQuantifier extends quantifier {
    public existentialQuantifier(char var, wff inner) {
        this.var = var;
        this.inner = inner;
    }

    public String getQuantifierString() {
        return "(" + main.existenceSymbol + var + ")";
    }

    public wff getNewCopyWithReplacement(char from, char to) {
        char varOfnew;
        if (from == var) {
            //this line should never be called
            varOfnew = to;
        } else {
            varOfnew = var;
        }
        wff innerReplacement = inner.getNewCopyWithReplacement(from, to);
        return new existentialQuantifier(varOfnew, innerReplacement);
    }

    wff makeNewCopyWithSubformulaAtIndexSubstitutedWith(int index, wff to) {
        return new existentialQuantifier(var, to);
    }

    wff makeNewCopyWithFormulaAtThisPathReplacedBy(int[] path, wff replacement) {
        if (path.length == 0) {
            return replacement;
        }
        int nextDest = path[0];
        int[] truncatedPath = wff.truncatePath(path);
        if (nextDest == 0) {
            wff newInner = inner.makeNewCopyWithFormulaAtThisPathReplacedBy(truncatedPath, replacement);
            return new existentialQuantifier(var, newInner);
        }
        return this;
    }

    public ArrayList<wff> getAllPossibleNameReplacementChoicesWith(char from, char to) {
        ArrayList<wff> replacementsOfInner = inner.getAllPossibleNameReplacementChoicesWith(from, to);
        ArrayList<wff> r = new ArrayList<>();
        r.addAll(replacementsOfInner);
        if (var == from) {
            for (wff innerReplacement : replacementsOfInner) {
                existentialQuantifier thisReplacement = new existentialQuantifier(to, innerReplacement);
                r.add(thisReplacement);
            }
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }

    wff makeNewCopyWithAllInstancesOfThisReplacedBy(wff from, wff to) {
        if (this.equals(from)) {
            return to;
        }
        wff newInner = inner.makeNewCopyWithAllInstancesOfThisReplacedBy(from, to);
        return new existentialQuantifier(var, newInner);
    }

    @Override
    wff makeNewCopyWithTheseSubformulas(ArrayList<wff> subs) {
        return new existentialQuantifier(var, subs.get(0));
    }
}
