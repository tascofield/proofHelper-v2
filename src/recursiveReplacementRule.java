import java.util.ArrayList;

public abstract class recursiveReplacementRule extends replacementRule {
    abstract ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in);

    public ArrayList<wff> getAllPossibleReplacementsWith(wff in) {
        return in.getAllPossibleRecursiveReplacementsWith(this);
    }
}
