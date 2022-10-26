public class subProof_indirectProof extends subProof {
    public wff assp;

    public subProof_indirectProof(wff assp, int rowNumOfFirst) {
        this.assp = assp;
        this.rowNumOfFirst = rowNumOfFirst;
        contents.add(new proofRow() {

            @Override
            int[] getPremiseRowNumbers() {
                return new int[0];
            }

            @Override
            boolean flagsName() {
                return false;
            }

            @Override
            char getFlaggedName() {
                return 0;
            }

            @Override
            String getJustification() {
                return "Assp. (I.P.)";
            }

            @Override
            boolean hasFormula() {
                return true;
            }

            @Override
            wff getFormula() {
                return assp;
            }
        });
    }

    @Override
    String getClosingComplaintWith(wff in) {
        if (!(in instanceof negation)) {
            return "In an indirect subproof, the result must be a negation";
        }
        wff in_in = ((negation) in).getInr();
        if (!in_in.equals(assp)) {
            return "In an indirect subproof, the result must be the negation of the assumption";
        }
        if (lastInWrongForm()) {
            return "In an indirect subproof, the last formula it contains must be of the form P&~P";
        }
        return "";
    }

    @Override
    void closeWith(wff in) {
        isClosed = true;
        finalResultRow = new proofRow() {
            @Override
            int[] getPremiseRowNumbers() {
                return getContentsRange();
            }

            @Override
            boolean flagsName() {
                return false;
            }

            @Override
            char getFlaggedName() {
                return 0;
            }

            @Override
            String getJustification() {
                return "I.P.";
            }

            @Override
            boolean hasFormula() {
                return true;
            }

            @Override
            wff getFormula() {
                return in;
            }
        };
    }

    @Override
    wff getAutoCompleteIfDone() {
        if (lastInWrongForm()) {
            return null;
        }
        return new negation(assp);
    }

    private boolean lastInWrongForm() {
        wff lastFormula = ((proofRow) contents.get(contents.size() - 1)).getFormula();
        if (!(lastFormula instanceof conjunction)) {
            return true;
        }
        wff in1 = ((conjunction) lastFormula).getInner1();
        wff in2 = ((conjunction) lastFormula).getInner2();
        if (!(in2 instanceof negation)) {
            return true;
        }
        return !in1.equals(((negation) in2).getInr());
    }
}
