public class subProof_universalGeneralization extends subProof {
    private char flaggedName;

    public subProof_universalGeneralization(char flaggedName, int rowNumOfFirst) {
        this.flaggedName = flaggedName;
        this.rowNumOfFirst = rowNumOfFirst;
        contents.add(new proofRow() {
            public boolean showFlagAtEnd() {
                return false;
            }

            @Override
            int[] getPremiseRowNumbers() {
                return new int[0];
            }

            @Override
            boolean flagsName() {
                return true;
            }

            @Override
            char getFlaggedName() {
                return flaggedName;
            }

            @Override
            String getJustification() {
                return "F.S. (U.G.)";
            }

            @Override
            boolean hasFormula() {
                return false;
            }

            @Override
            wff getFormula() {
                return null;
            }
        });
    }

    @Override
    String getClosingComplaintWith(wff in) {
        if (!(in instanceof universalQuantifier)) {
            return "The result of a universal generalization subproof must be a universal quantifier";
        }
        wff in_in = ((universalQuantifier) in).getInr();
        char in_var = ((universalQuantifier) in).getVar();
        wff last = getLastRowInContents().getFormula();
        wff.replacedNameSearchResult r = in_in.maybeFindTheNameThisWasReplacedTo(last, in_var);
        boolean resultIsValid = r.hasResult && r.nameThisWasReplacedWith == flaggedName;
        if (!resultIsValid) {
            return "The contents of the result of a universal generalization subproof must be the same as the last" +
                    " formula it contains, but with some instances of the flagged name (" + flaggedName + " in this" +
                    " case) replaced by the universal quantifier variable (" + in_var + " in this case)";
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
                return "U.G.";
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
        return null;
    }


}
