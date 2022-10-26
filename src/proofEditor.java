import java.util.ArrayList;
import java.util.Scanner;

public class proofEditor {
    private ArrayList<proofItem> contents;
    private int lastLineNum;
    private boolean doneAddingPremises = false;

    public static final String[] allRPLCommands = {"DN", "DUP", "COMM", "ASSOC", "CONTRAP", "DEM", "BE", "CE", "DIST", "EXP", "ISYM", "CQN", "QN"};
    public static final String[] allINFCommands = {"MP", "MT", "HS", "SIMP", "CONJ", "DIL", "DS", "ADD", "IREF", "ISUB", "UI", "EI", "EG"};

    public proofEditor() {
        contents = new ArrayList<>();
        lastLineNum = 0;
    }

    public void enterCommandLoop() {
        System.out.println("Welcome to ProofHelper v2.0");
        Scanner in = new Scanner(System.in);
        String command = in.nextLine();
        while (!command.equals("quit")) {
            boolean called = false;
            if (command.equals("print")) {
                printState();
                called = true;
            }
            for (int i = 0; i < proofEditorCommand.ALL_COMMANDS.length && !called; i++) {
                proofEditorCommand thisCommand = proofEditorCommand.ALL_COMMANDS[i];
                if (thisCommand.isCalled(command)) {
                    thisCommand.call(command, this);
                    called = true;
                }
            }
            if (!called && !command.equals("quit") && command.length() != 0) {
                System.out.println("no such command. type \"help\" for a list of commands.");
            }
            if (!command.equals("quit")) {
                command = in.nextLine();
            }
        }
    }

    public static replacementRule getReplacementRuleFromCommand(String base) {
        for (int i = 0; i < allRPLCommands.length; i++) {
            if (base.equals(allRPLCommands[i].toLowerCase())) {
                return replacementRule.allRPL[i];
            }
        }
        return null;
    }

    public static inferenceRule getInferenceRuleFromCommand(String base) {
        for (int i = 0; i < allINFCommands.length; i++) {
            if (base.equals(allINFCommands[i].toLowerCase())) {
                return inferenceRule.allINF[i];
            }
        }
        return null;
    }

    public static class getRow_result {
        public boolean isInClosedSubproof;
        public proofRow row;

        public getRow_result(boolean isInClosedSubproof, proofRow row) {
            this.isInClosedSubproof = isInClosedSubproof;
            this.row = row;
        }
    }

    public getRow_result getRowWithNum(int num) {
        if (num > lastLineNum) {
            return null;
        }
        int n2 = num - 1;
        int index = 0;
        while (n2 > 0) {
            n2 -= contents.get(index).getNumRows();
            index++;
        }
        if (n2 < 0 || contents.get(index) instanceof subProof) {
            if (n2 != 0) {
                index--;
            }
            return ((subProof) contents.get(index)).getRowWithNum(num);
        } else {
            return new getRow_result(false, (proofRow) contents.get(index));
        }
    }

    public ArrayList<wff> checkWffs(int[] refs) {
        ArrayList<wff> refs_fs = new ArrayList<>();
        for (int ref : refs) {
            getRow_result thisRowResult = getRowWithNum(ref);
            if (thisRowResult == null) {
                System.out.println("Error: there is no row " + ref);
            } else {
                proofRow thisRow = thisRowResult.row;
                if (!thisRow.hasFormula()) {
                    System.out.println("Error: there is no formula on line " + ref);
                } else if (thisRowResult.isInClosedSubproof) {
                    System.out.println("Error: the formula on line " + ref + " is in a discharged subproof");
                } else {
                    refs_fs.add(thisRow.getFormula());
                }
            }
        }
        return refs_fs;
    }

    public void createConditionalSubproof(wff assp) {
        subProof_conditionalProof spcp = new subProof_conditionalProof(assp, lastLineNum + 1);
        addToEnd(spcp);
    }

    public void createIndirectSubproof(wff assp) {
        subProof_indirectProof spip = new subProof_indirectProof(assp, lastLineNum + 1);
        addToEnd(spip);
    }

    public void createUniversalGeneralizatonSubproof(char toFlag) {
        boolean alreadyFlagged = false;
        int rowWhereThisWasFlagged = -1;
        for (int i = 1; i <= lastLineNum; i++) {
            proofRow thisRow = getRowWithNum(i).row;
            if (thisRow.flagsName() && thisRow.getFlaggedName() == toFlag) {
                alreadyFlagged = true;
                rowWhereThisWasFlagged = i;
            }
        }
        if (!alreadyFlagged) {
            subProof_universalGeneralization spug = new subProof_universalGeneralization(toFlag, lastLineNum + 1);
            addToEnd(spug);
        } else {
            System.out.println("Cannot create universal generalization subproof: the name" + toFlag + " was already flagged on line " + rowWhereThisWasFlagged);
        }
    }

    public void addPremise(wff pr) {
        if (!doneAddingPremises) {
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
                    return "Pr.";
                }

                @Override
                boolean hasFormula() {
                    return true;
                }

                @Override
                wff getFormula() {
                    return pr;
                }
            });
            lastLineNum++;
            printState();
        } else {
            System.out.println("You can't add any more premises because there is already something after them");
        }

    }

    public void attemptToDischargeSubproof_auto(String subproofType) {
        proofItem lastItem = contents.get(contents.size() - 1);
        if (lastItem instanceof subProof) {
            if (((subProof) lastItem).attemptToDischargeWorkingSubproof_auto(subproofType)) {
                lastLineNum++;
                printState();
            }
        } else {
            System.out.println("Cannot automatically discharge subproof: the last item is not a subproof");
        }
    }

    public void attemptToDischargeSubproofWith(wff in, String subproofType) {
        proofItem lastItem = contents.get(contents.size() - 1);
        if (lastItem instanceof subProof) {
            if (((subProof) lastItem).attemptToDischargeWorkingSubproofWith(in, subproofType)) {
                lastLineNum++;
            }
            printState();
        } else {
            System.out.println("Cannot discharge subproof: the last item is not a subproof");
        }
    }

    public void attemptInferenceRule(inferenceRule r, int[] refs, wff target) {
        ArrayList<wff> refs_fs = checkWffs(refs);
        if (refs.length == refs_fs.size()) {
            inferenceRule.inferenceAttemptResult res = r.attemptInference(refs_fs, target);
            if (!res.isValid) {
                System.out.println(res.complaint);
            } else {
                if (r == inferenceRule.EI) {
                    char flag = res.nameToFlag;
                    boolean alreadyFlagged = false;
                    int rowWhereThisWasFlagged = -1;
                    for (int i = 1; i <= lastLineNum; i++) {
                        proofRow thisRow = getRowWithNum(i).row;
                        if (thisRow.flagsName() && thisRow.getFlaggedName() == flag) {
                            alreadyFlagged = true;
                            rowWhereThisWasFlagged = i;
                        }
                    }
                    if (!alreadyFlagged) {
                        addInferenceRuleRow_flag(r, refs, target, flag);
                    } else {
                        System.out.println("Cannot create existential instantiation: the name " + flag + " was already flagged on line " + rowWhereThisWasFlagged);
                    }
                } else {
                    addInferenceRuleRow_noFlag(r, refs, target);
                }
            }
        }
    }

    public void attemptInferenceRule_auto(inferenceRule r, int[] refs) {
        ArrayList<wff> refs_fs = checkWffs(refs);
        if (refs.length == refs_fs.size()) {
            if (!(r instanceof basicInferenceRule)) {
                System.out.println("This inference rule does not support automatic completion. Please specify what formula to add");
            } else {
                ArrayList<wff> possibleConclusions = ((basicInferenceRule) r).getPossibleConclusionsWithThesePremises(refs_fs);
                if (possibleConclusions.size() > 1) {
                    main.removeDuplicatesFromWffListAndSort(possibleConclusions);
                    System.out.println("Ambiguity detected. You need to specify which one of these you want to add:");
                    for (wff pos : possibleConclusions) {
                        System.out.println("\t- " + pos.toString());
                    }
                } else if (possibleConclusions.size() < 1) {
                    //System.out.println("No possible applications of " + r.getFullName() + " found:");
                    String complaint = ((basicInferenceRule) r).getComplaintOfTryingToGetConclusionFromThesePremises(refs_fs);
                    System.out.println(complaint);
                } else {
                    addInferenceRuleRow_noFlag(r, refs, possibleConclusions.get(0));
                }
            }
        }
    }

    public void attemptReplacementRule(replacementRule r, int[] refs, wff target) {
        ArrayList<wff> refs_fs = checkWffs(refs);
        if (refs_fs.size() == refs.length) {
            if (refs_fs.size() != 1) {
                System.out.println("Error: replacement rules require exactly one previous formula");
            } else {
                ArrayList<wff> possibilities = r.getAllPossibleReplacementsWith(refs_fs.get(0));
                boolean found = false;
                for (int i = 0; i < possibilities.size() && !found; i++) {
                    if (possibilities.get(i).equals(target)) {
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("That is an invalid application of " + r.getFullName());
                    System.out.println(r.toString());
                } else {
                    addReplacementRuleRow(r, refs, target);
                }
            }
        }
    }

    public void attemptReplacementRule_auto(replacementRule r, int[] refs) {
        ArrayList<wff> refs_fs = checkWffs(refs);
        if (refs.length == refs_fs.size()) {
            if (refs_fs.size() != 1) {
                System.out.println("Error: replacement rules require exactly one previous formula");
            } else {
                ArrayList<wff> possibilities = r.getAllPossibleReplacementsWith(refs_fs.get(0));
                if (possibilities.size() > 1) {
                    main.removeDuplicatesFromWffListAndSort(possibilities);
                    System.out.println("Ambiguity detected. You need to specify which one of these you want to add:");
                    for (wff pos : possibilities) {
                        System.out.println("\t- " + pos.toString());
                    }
                } else if (possibilities.size() < 1) {
                    System.out.println("No possible applications of " + r.getFullName() + " found");
                } else {
                    addReplacementRuleRow(r, refs, possibilities.get(0));
                }
            }
        }
    }

    public void addReplacementRuleRow(replacementRule r, int[] refs, wff target) {
        addToEnd(new proofRow() {
            @Override
            int[] getPremiseRowNumbers() {
                return refs;
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
                return r.getAbbreviation();
            }

            @Override
            boolean hasFormula() {
                return true;
            }

            @Override
            wff getFormula() {
                return target;
            }
        });
    }

    public void addInferenceRuleRow_noFlag(inferenceRule r, int[] refs, wff target) {
        addToEnd(new proofRow() {
            @Override
            int[] getPremiseRowNumbers() {
                return refs;
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
                return r.getAbbreviation();
            }

            @Override
            boolean hasFormula() {
                return true;
            }

            @Override
            wff getFormula() {
                return target;
            }
        });
    }

    public void addInferenceRuleRow_flag(inferenceRule r, int[] refs, wff target, char flag) {
        addToEnd(new proofRow() {
            @Override
            int[] getPremiseRowNumbers() {
                return refs;
            }

            @Override
            boolean flagsName() {
                return true;
            }

            @Override
            char getFlaggedName() {
                return flag;
            }

            @Override
            String getJustification() {
                return r.getAbbreviation();
            }

            @Override
            boolean hasFormula() {
                return true;
            }

            @Override
            wff getFormula() {
                return target;
            }
        });
    }

    public void addToEnd(proofItem r) {
        boolean addToSubproof = contents.size() != 0;
        proofItem lastItem = null;
        if (addToSubproof) {
            lastItem = contents.get(contents.size() - 1);
            addToSubproof = lastItem instanceof subProof;
            if (addToSubproof) {
                addToSubproof = !((subProof) lastItem).isClosed();
            }
        }
        if (addToSubproof) {
            ((subProof) lastItem).addToEnd(r);
        } else {
            contents.add(r);
        }
        lastLineNum++;
        doneAddingPremises = true;
        printState();
    }

    public void printState() {
        ArrayList<String> scopesAndFormulas = new ArrayList<>();
        ArrayList<String> justifications = new ArrayList<>();
        for (proofItem i : contents) {
            if (i instanceof subProof) {
                scopesAndFormulas.addAll(((subProof) i).toDisplay_justScopeAndFormulas());
                justifications.addAll(((subProof) i).toDisplay_justJustificationsAndReferences());
            } else if (i instanceof proofRow) {
                scopesAndFormulas.add(((proofRow) i).toDisplay_justFormulasIfApplicable());
                justifications.add(((proofRow) i).getJustification() + ((proofRow) i).getReferencesString());
            } else {
                scopesAndFormulas.add("?");
                justifications.add("?");
            }
        }
        int numNumberedLines = 0;
        for (String j : justifications) {
            if (j.length() != 0) {
                numNumberedLines++;
            }
        }
        int maxDigits = (int) Math.floor(Math.log10(numNumberedLines)) + 1;
        int maxCharsInScopesAndFormulas = -1;
        for (String s : scopesAndFormulas) {
            maxCharsInScopesAndFormulas = Math.max(maxCharsInScopesAndFormulas, s.length());
        }
        int lastNum = 1;
        for (int index = 0; index < scopesAndFormulas.size(); index++) {
            String thisScopeAndFormula = scopesAndFormulas.get(index);
            thisScopeAndFormula = thisScopeAndFormula + main.getNCharsInARow(' ', maxCharsInScopesAndFormulas - thisScopeAndFormula.length());
            String thisJustification = justifications.get(index);
            String thisNumbering;
            if (thisJustification.length() != 0) {
                String sn = Integer.toString(lastNum);
                thisNumbering = main.getNCharsInARow(' ', maxDigits - sn.length()) + sn + ". ";
                lastNum++;
            } else {
                thisNumbering = main.getNCharsInARow(' ', maxDigits + 2);
            }
            System.out.println(thisNumbering + thisScopeAndFormula + "  " + thisJustification);
        }
    }

}
