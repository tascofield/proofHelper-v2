import java.util.ArrayList;

public abstract class inferenceRule {
    abstract inferenceAttemptResult attemptInference(ArrayList<wff> refs, wff concl);

    abstract String getAbbreviation();

    abstract String getFullName();

    abstract String getInferenceFormString();

    public static final basicInferenceRule MP;
    public static final basicInferenceRule MT;
    public static final basicInferenceRule HS;
    public static final basicInferenceRule SIMP;
    public static final basicInferenceRule CONJ;
    public static final basicInferenceRule DIL;
    public static final basicInferenceRule DS;
    public static final inferenceRule ADD;

    public static final inferenceRule IREF;
    public static final basicInferenceRule ISUB;

    public static final inferenceRule UI;
    public static final inferenceRule EI;
    public static final inferenceRule EG;

    public static final inferenceRule[] allINF;

    static {
        wffVariable p = wffVariable.p;
        wffVariable q = wffVariable.q;
        wffVariable r = wffVariable.r;
        wffVariable s = wffVariable.s;

        wff pThereforeQ = new conditional(p, q);
        wff[] MP_prs = new wff[]{pThereforeQ, p};
        MP = new singleBasicInferenceRule("Modus Ponens", "M.P.", MP_prs, q, p, q);

        wff notQ = new negation(q);
        wff notP = new negation(p);
        wff[] MT_prs = new wff[]{pThereforeQ, notQ};
        MT = new singleBasicInferenceRule("Modus Tollens", "M.T.", MT_prs, notP, p, q);

        wff qThereforeR = new conditional(q, r);
        wff pThereforeR = new conditional(p, r);
        wff[] HS_prs = new wff[]{pThereforeQ, qThereforeR};
        HS = new singleBasicInferenceRule("Hypothetical Syllogism", "H.S.", HS_prs, pThereforeR, p, q, r);

        wff pAndQ = new conjunction(p, q);
        wff[] SIMP_prs = new wff[]{pAndQ};
        singleBasicInferenceRule simp1 = new singleBasicInferenceRule("Simplification (1)", "Simp. 1", SIMP_prs, p, p, q);
        singleBasicInferenceRule simp2 = new singleBasicInferenceRule("Simplification (2)", "Simp. 2", SIMP_prs, q, p, q);
        SIMP = new multipleBasicInferenceRule("Simplification", "Simp.", simp1, simp2);

        wff[] CONJ_prs = new wff[]{p, q};
        CONJ = new singleBasicInferenceRule("Conjunction", "Conj.", CONJ_prs, pAndQ, p, q);

        wff rThereforeS = new conditional(r, s);
        wff pOrR = new disjunction(p, r);
        wff qOrS = new disjunction(q, s);
        wff[] DIL_prs = new wff[]{pThereforeQ, rThereforeS, pOrR};
        DIL = new singleBasicInferenceRule("Dilemma", "Dil.", DIL_prs, qOrS, p, q, r, s);

        wff pOrQ = new disjunction(p, q);
        wff[] DS_prs1 = new wff[]{pOrQ, notP};
        singleBasicInferenceRule ds1 = new singleBasicInferenceRule("Disjunctive Syllogism (1)", "D.S. 1", DS_prs1, q, p, q);
        wff[] DS_prs2 = new wff[]{pOrQ, notQ};
        singleBasicInferenceRule ds2 = new singleBasicInferenceRule("Disjunctive Syllogism (1)", "D.S. 2", DS_prs2, p, p, q);
        DS = new multipleBasicInferenceRule("Disjunctive Syllogism", "D.S.", ds1, ds2);

        ADD = new inferenceRule() {
            @Override
            inferenceAttemptResult attemptInference(ArrayList<wff> refs, wff concl) {
                if (refs.size() != 1) {
                    String compl = "Addition takes 1 premise";
                    return new inferenceAttemptResult(false, compl);
                }
                wff concli1;
                wff concli2;
                if (concl instanceof disjunction) {
                    concli1 = ((disjunction) concl).getInner1();
                    concli2 = ((disjunction) concl).getInner2();
                } else if (concl instanceof conjunction) {
                    concli1 = ((conjunction) concl).getInner1();
                    concli2 = ((conjunction) concl).getInner2();
                } else {
                    String compl = "When using addition, the conclusion must be a disjunction or a conjunction";
                    return new inferenceAttemptResult(false, compl);
                }
                wff ref = refs.get(0);
                boolean v = false;
                if (concli1.equals(ref)) {
                    v = true;
                }
                if (concli2.equals(ref)) {
                    v = true;
                }
                String compl;
                if (v) {
                    compl = "";
                } else {
                    compl = "When using addition, one of the two subformulas of the conclusion must equal the premise";
                }
                return new inferenceAttemptResult(v, compl);
            }

            @Override
            String getAbbreviation() {
                return "Add.";
            }

            @Override
            String getFullName() {
                return "Addition";
            }

            @Override
            String getInferenceFormString() {
                return "    p      q" +
                        "\n" + main.getNCharsInARow(main.horizontalSingleBar, 5) + "  " + main.getNCharsInARow(main.horizontalSingleBar, 5) +
                        "\n" + main.thereforeSymbol + "pvq  " + main.thereforeSymbol + "pvq";
            }
        };

        IREF = new inferenceRule() {
            @Override
            inferenceAttemptResult attemptInference(ArrayList<wff> refs, wff concl) {
                if (refs.size() != 1) {
                    String compl = "To use " + getFullName() + ", you need to reference 1 other row, for some reason";
                    return new inferenceAttemptResult(false, compl);
                }
                if (!(concl instanceof identityFormula)) {
                    String compl = getFullName() + " can only prove identity formulas";
                    return new inferenceAttemptResult(false, compl);
                }
                char name1 = ((identityFormula) concl).getName1();
                char name2 = ((identityFormula) concl).getName2();
                if (name1 != name2) {
                    String compl = "When using " + getFullName() + ", the conclusion must be equating names which are the same letter";
                    return new inferenceAttemptResult(false, compl);
                }
                return new inferenceAttemptResult(true, "");
            }

            @Override
            String getAbbreviation() {
                return "I. Ref.";
            }

            @Override
            String getFullName() {
                return "Identity Reflexivity";
            }

            @Override
            String getInferenceFormString() {
                return "    p" +
                        "\n" + main.getNCharsInARow(main.horizontalSingleBar, 5) +
                        "\n" + main.thereforeSymbol + "a=a";
            }
        };

        ISUB = new basicInferenceRule() {
            @Override
            public String getFullName() {
                this.fullName = "Identity Substitution";
                return super.getFullName();
            }

            @Override
            String getInferenceFormString() {
                return " a=b" +
                        "\n  " + main.phi + "a " +
                        "\n" + main.getNCharsInARow(main.horizontalSingleBar, 4) +
                        "\n" + main.thereforeSymbol + main.phi + "b";
            }

            public String getAbbreviation() {
                this.abbrev = "I. Sub.";
                return super.getAbbreviation();
            }

            @Override
            ArrayList<wff> getPossibleConclusionsWithThesePremises(ArrayList<wff> premises) {
                ArrayList<wff> r = new ArrayList<>();
                if (premises.size() != 2) {
                    return r;
                }
                ArrayList<Integer> indicesOfPremisesThatAreIdentities = new ArrayList<>(2);
                for (int prIndex = 0; prIndex < premises.size(); prIndex++) {
                    wff thisPremise = premises.get(prIndex);
                    if (thisPremise instanceof identityFormula) {
                        indicesOfPremisesThatAreIdentities.add(prIndex);
                    }
                }
                if (indicesOfPremisesThatAreIdentities.size() == 0) {
                    return r;
                }
                for (int thisIdentityIndex : indicesOfPremisesThatAreIdentities) {
                    identityFormula thisIdentity = (identityFormula) premises.get(thisIdentityIndex);
                    char thisName1 = thisIdentity.getName1();
                    char thisName2 = thisIdentity.getName2();
                    ArrayList<wff> everythingElse = new ArrayList<>(premises);
                    everythingElse.remove(thisIdentityIndex);
                    wff otherThingElse = everythingElse.get(0);
                    ArrayList<wff> replacementsOfOther = otherThingElse.getAllPossibleNameReplacementChoicesWith(thisName1, thisName2);
                    for (int index = 0; index < replacementsOfOther.size(); index++) {
                        if (replacementsOfOther.get(index).equals(otherThingElse)) {
                            replacementsOfOther.remove(index);
                            index--;
                        }
                    }
                    r.addAll(replacementsOfOther);
                }
                main.removeDuplicatesFromWffListAndSort(r);
                return r;
            }

            @Override
            String getComplaintOfTryingToGetConclusionFromThesePremises(ArrayList<wff> premises) {
                if (premises.size() != 2) {
                    return "To use " + getFullName() + ", you need to refer to 2 rows";
                }
                wff pr1 = premises.get(0);
                wff pr2 = premises.get(1);
                if (!(pr1 instanceof identityFormula || pr2 instanceof identityFormula)) {
                    return "To use " + getFullName() + ", one of the referred formulas must be an identity";
                }
                return "Error using " + getFullName() + ": that isn't one of the possible replacements";
            }
        };

        UI = new inferenceRule() {
            @Override
            inferenceAttemptResult attemptInference(ArrayList<wff> refs, wff concl) {
                if (refs.size() != 1) {
                    String compl = getFullName() + " takes 1 premise";
                    return new inferenceAttemptResult(false, compl);
                }
                wff ref = refs.get(0);
                if (!(ref instanceof universalQuantifier)) {
                    String compl = "When using " + getFullName() + ", the referenced row must be a universally quantified form";
                    return new inferenceAttemptResult(false, compl);
                }
                wff universalInner = ((universalQuantifier) ref).getInr();
                char universalVar = ((universalQuantifier) ref).getVar();
                wff.replacedNameSearchResult searchResult = universalInner.maybeFindTheNameThisWasReplacedTo(concl, universalVar);
                if (searchResult.hasResult) {
                    return new inferenceAttemptResult(true, "");
                }
                String compl = "When using " + getFullName() + ", the conclusion must be the result of substituting some name for the quantified variable";
                return new inferenceAttemptResult(false, compl);
            }

            @Override
            String getAbbreviation() {
                return "U.I.";
            }

            @Override
            String getFullName() {
                return "Universal Instantiation";
            }

            @Override
            String getInferenceFormString() {
                return "(x)" + main.phi + "x" +
                        "\n" + main.getNCharsInARow(main.horizontalSingleBar, 5) +
                        "\n " + main.thereforeSymbol + main.phi + "a";
            }
        };

        EI = new inferenceRule() {
            @Override
            inferenceAttemptResult attemptInference(ArrayList<wff> refs, wff concl) {
                if (refs.size() != 1) {
                    String compl = getFullName() + " takes 1 premise";
                    return new inferenceAttemptResult(false, compl);
                }
                wff ref = refs.get(0);
                if (!(ref instanceof existentialQuantifier)) {
                    String compl = "When using " + getFullName() + ", the referenced row must be an existentially quantified form";
                    return new inferenceAttemptResult(false, compl);
                }
                wff existentialInner = ((existentialQuantifier) ref).getInr();
                char existentialVar = ((existentialQuantifier) ref).getVar();
                if (concl.getAllUsedLetters().contains(existentialVar)) {
                    String compl = "When using " + getFullName() + ", the conclusion can't contain the variable used to quantify (" + existentialVar + " in this case)";
                    return new inferenceAttemptResult(false, compl);
                }
                wff.replacedNameSearchResult searchResult = existentialInner.maybeFindTheNameThisWasReplacedTo(concl, existentialVar);
                if (searchResult.hasResult) {
                    return new inferenceAttemptResult(true, "", searchResult.nameThisWasReplacedWith);
                }
                String compl = "When using " + getFullName() + ", the conclusion must be the result of substituting some name for the quantified variable";
                return new inferenceAttemptResult(false, compl);
            }

            @Override
            String getAbbreviation() {
                return "E.I.";
            }

            @Override
            String getFullName() {
                return "Existential Instantiation";
            }

            @Override
            String getInferenceFormString() {
                return "(" + main.existenceSymbol + "x)" + main.phi + "x" +
                        "\n" + main.getNCharsInARow(main.horizontalSingleBar, 6) +
                        "\n  " + main.thereforeSymbol + main.phi + "a" +
                        "(provided we flag a)";
            }
        };

        EG = new inferenceRule() {
            @Override
            inferenceAttemptResult attemptInference(ArrayList<wff> refs, wff concl) {
                if (refs.size() != 1) {
                    String compl = getFullName() + " takes 1 premise";
                    return new inferenceAttemptResult(false, compl);
                }
                wff ref = refs.get(0);
                if (!(concl instanceof existentialQuantifier)) {
                    String compl = "When using " + getFullName() + ", the conclusion must be an existentially quantified form";
                    return new inferenceAttemptResult(false, compl);
                }
                wff conclInner = ((existentialQuantifier) concl).getInr();
                char conclVar = ((existentialQuantifier) concl).getVar();
                wff.replacedNameSearchResult searchResult = conclInner.maybeFindTheNameThisWasReplacedTo(ref, conclVar);
                if (!searchResult.hasResult || searchResult.nameThisWasReplacedWith == '?') {
                    String compl = "When using " + getFullName() + ", the conclusion must be the result of replacing some instances of a name with the quantified variable in the conclusion (which seems to be" + conclVar + ")";
                    return new inferenceAttemptResult(false, compl);
                }
                return new inferenceAttemptResult(true, "");
            }

            @Override
            String getAbbreviation() {
                return "E.G.";
            }

            @Override
            String getFullName() {
                return "Existential Generalization";
            }

            @Override
            String getInferenceFormString() {
                return "      " + main.phi + "a" +
                        "\n" + main.getNCharsInARow(main.horizontalSingleBar, 8) +
                        "\n" + main.thereforeSymbol + "(" + main.existenceSymbol + "x)" + main.phi + "x";
            }
        };

        allINF = new inferenceRule[]{MP, MT, HS, SIMP, CONJ, DIL, DS, ADD, IREF, ISUB, UI, EI, EG};
    }


    public class inferenceAttemptResult {
        public String complaint;
        public boolean isValid;
        public char nameToFlag = 0;

        public inferenceAttemptResult(boolean isValid, String complaint) {
            this.complaint = complaint;
            this.isValid = isValid;
        }

        public inferenceAttemptResult(boolean isValid, String complaint, char nameToFlag) {
            this.complaint = complaint;
            this.isValid = isValid;
            this.nameToFlag = nameToFlag;
        }
    }
}


