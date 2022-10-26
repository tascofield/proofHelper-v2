public class subProof_conditionalProof extends subProof {
    public wff assp;

    public subProof_conditionalProof(wff assp, int rowNumOfFirst) {
        this.assp = assp;
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
                return "Assp. (C.P.)";
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
        this.rowNumOfFirst = rowNumOfFirst;
    }

    @Override
    String getClosingComplaintWith(wff in) {
        if (!(in instanceof conditional)) {
            return "A conditional subproof can only prove a conditional";
        }
        conditional inc = (conditional) in;
        wff ant = inc.getAntecedent();
        wff con = inc.getConsequent();
        if (!ant.equals(assp)) {
            return "The antecedent of the result of a conditional subproof must be the same as the initial assumption";
        }
        wff lastFormula = getLastRowInContents().getFormula();
        if (!con.equals(lastFormula)) {
            return "The consequent of the result of a conditional subproof must be the same as the last formula it contains";
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
                return "C.P.";
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
        proofRow lastRow = getRowWithNum(getNumRows() + rowNumOfFirst - 1).row;
        if (lastRow.hasFormula()) {
            return new conditional(assp, lastRow.getFormula());
        }
        return null;
    }
}
