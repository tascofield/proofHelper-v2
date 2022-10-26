import java.util.ArrayList;

public abstract class subProof extends proofItem {
    protected ArrayList<proofItem> contents = new ArrayList<>();
    protected boolean isClosed = false;
    protected proofRow finalResultRow;
    protected int rowNumOfFirst;
    public static String typeName;

    abstract String getClosingComplaintWith(wff in);

    abstract void closeWith(wff in);

    abstract wff getAutoCompleteIfDone();

    public static final String conditionalName = "Conditional Subproof";
    public static final String indirectName = "Indirect Subproof";
    public static final String universalname = "Flagged Subproof";

    public String getTypeName() {
        if (this instanceof subProof_conditionalProof) {
            return conditionalName;
        } else if (this instanceof subProof_indirectProof) {
            return indirectName;
        } else if (this instanceof subProof_universalGeneralization) {
            return universalname;
        } else {
            return "?";
        }
    }

    public proofRow getFinalResultRow() {
        return finalResultRow;
    }

    public int getNumRows() {
        int n = 0;
        for (proofItem i : contents) {
            n += i.getNumRows();
        }
        if (isClosed) {
            n++;
        }
        return n;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public int[] getContentsRange() {
        int n = getNumRows();
        if (isClosed) {
            n--;
        }
        int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            r[i] = rowNumOfFirst + i;
        }
        return r;
    }

    public proofEditor.getRow_result getRowWithNum(int num) {
        int n2 = num - rowNumOfFirst;
        int index = 0;
        while (n2 > 0) {
            n2 -= contents.get(index).getNumRows();
            index++;
        }
        if (n2 < 0 || (index < contents.size() && contents.get(index) instanceof subProof)) {
            if (n2 != 0) {
                index--;
            }
            return ((subProof) contents.get(index)).getRowWithNum(num);
        } else {
            if (index == contents.size()) {
                return new proofEditor.getRow_result(false, finalResultRow);
            }
            return new proofEditor.getRow_result(isClosed, (proofRow) contents.get(index));
        }
    }

    public proofRow getLastRowInContents() {
        return getRowWithNum(rowNumOfFirst + getNumRows() - 1).row;
    }

    public ArrayList<String> toDisplay_justScopeAndFormulas() {
        ArrayList<String> r = new ArrayList<>();
        for (int index = 0; index < contents.size(); index++) {
            proofItem i = contents.get(index);
            if (index == 0) {
                r.add(main.lightDownRightBar + " " + ((proofRow) i).toDisplay_justFormulasIfApplicable());
            } else if (i instanceof subProof) {
                ArrayList<String> stuff = ((subProof) i).toDisplay_justScopeAndFormulas();
                for (String thing : stuff) {
                    r.add(main.lightVerticalSingleBar + thing);
                }
            } else if (i instanceof proofRow) {
                r.add(main.lightVerticalSingleBar + " " + ((proofRow) i).toDisplay_justFormulasIfApplicable());
            } else {
                r.add("?");
            }
        }
        if (isClosed) {
            r.add(main.lightUpRightBar + main.getNCharsInARow(main.horizontalSingleBar, 5));
            r.add(finalResultRow.toDisplay_justFormulasIfApplicable());
        }
        return r;
    }

    public ArrayList<String> toDisplay_justJustificationsAndReferences() {
        ArrayList<String> r = new ArrayList<>();
        for (int index = 0; index < contents.size(); index++) {
            proofItem i = contents.get(index);
            if (i instanceof subProof) {
                r.addAll(((subProof) i).toDisplay_justJustificationsAndReferences());
            } else if (i instanceof proofRow) {
                r.add(((proofRow) i).getJustification() + ((proofRow) i).getReferencesString());
            } else {
                r.add("?");
            }
        }
        if (isClosed) {
            r.add("");
            r.add(finalResultRow.getJustification() + finalResultRow.getReferencesString());
        }
        return r;
    }

    public void addToEnd(proofItem r) {
        proofItem lastItem = contents.get(contents.size() - 1);
        if (lastItem instanceof subProof && !((subProof) lastItem).isClosed()) {
            ((subProof) lastItem).addToEnd(r);
        } else {
            contents.add(r);
        }
    }

    public boolean attemptToDischargeWorkingSubproof_auto(String subproofType) {
        proofItem lastItem = contents.get(contents.size() - 1);
        if (lastItem instanceof subProof && !((subProof) lastItem).isClosed) {
            return ((subProof) lastItem).attemptToDischargeWorkingSubproof_auto(subproofType);
        } else {
            if (getTypeName().equals(subproofType)) {
                wff autocomplete = getAutoCompleteIfDone();
                if (autocomplete != null) {
                    closeWith(autocomplete);
                    return true;
                } else {
                    System.out.println("Could not discharge working subproof automatically");
                }
            } else {
                System.out.println("Could not discharge working subproof: expected " + getTypeName() + ", got " + subproofType);
            }
        }
        return false;
    }

    public boolean attemptToDischargeWorkingSubproofWith(wff in, String subproofType) {
        proofItem lastItem = contents.get(contents.size() - 1);
        if (lastItem instanceof subProof && !((subProof) lastItem).isClosed) {
            return ((subProof) lastItem).attemptToDischargeWorkingSubproofWith(in, subproofType);
        } else {
            if (getTypeName().equals(subproofType)) {
                String complaint = getClosingComplaintWith(in);
                if (complaint.length() != 0) {
                    System.out.println(complaint);
                } else {
                    closeWith(in);
                    return true;
                }
            } else {
                System.out.println("Could not discharge working subproof: expected " + getTypeName() + ", got " + subproofType);
            }
        }
        return false;
    }
}
