import java.util.Arrays;

public abstract class proofRow extends proofItem {
    abstract int[] getPremiseRowNumbers();

    abstract boolean flagsName();

    abstract char getFlaggedName();

    abstract String getJustification();

    abstract boolean hasFormula();

    abstract wff getFormula();


    public int getNumRows() {
        return 1;
    }

    public String toString() {
        return getFormula() + getJustification() + Arrays.toString(getPremiseRowNumbers());
    }

    public boolean showFlagAtEnd() {
        return true;
    }

    public String toDisplay_justFormulasIfApplicable() {
        StringBuilder sb = new StringBuilder();
        if (!showFlagAtEnd() || !hasFormula()) {
            sb.append("flag ");
            sb.append(getFlaggedName());
            return sb.toString();
        } else {
            return getFormula().toString();
        }
    }

    public String getReferencesString() {
        int[] refs = getPremiseRowNumbers();
        if (refs.length == 0) {
            return "";
        }
        if (refs.length > 2) {
            boolean purelyAscending = true;
            for (int i = 1; i < refs.length && purelyAscending; i++) {
                if (refs[i] - refs[i - 1] != 1) {
                    purelyAscending = false;
                }
            }
            if (purelyAscending) {
                return refs[0] + "-" + refs[refs.length - 1];
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(refs[0]);
        for (int i = 1; i < refs.length; i++) {
            sb.append(',');
            sb.append(refs[i]);
        }
        if (flagsName() && showFlagAtEnd()) {
            sb.append(" (flag ");
            sb.append(getFlaggedName());
            sb.append(")");
        }
        return sb.toString();
    }
}
