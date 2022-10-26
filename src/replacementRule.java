import java.util.ArrayList;

public abstract class replacementRule {

    public static final replacementRule DN;
    public static final replacementRule DUP;
    public static final replacementRule COMM;
    public static final replacementRule ASSOC;
    public static final replacementRule CONTRAP;
    public static final replacementRule DEM;
    public static final replacementRule BE;
    public static final replacementRule CE;
    public static final replacementRule DIST;
    public static final replacementRule EXP;

    public static final replacementRule ISYM;

    public static final replacementRule QN;
    public static final replacementRule CQN;

    public static final replacementRule[] allRPL;

    static {
        wffVariable p = wffVariable.p;
        wffVariable q = wffVariable.q;
        wffVariable r = wffVariable.r;

        wff notNotP = new negation(new negation(p));
        DN = new singleReplacementRule("Double Negation", "D.N.", p, notNotP, p);

        wff pOrP = new disjunction(p, p);
        singleReplacementRule dup1 = new singleReplacementRule("Duplication 1", "Dup1", p, pOrP, p);
        wff pAndP = new conjunction(p, p);
        singleReplacementRule dup2 = new singleReplacementRule("Duplication 2", "Dup2", p, pAndP, p);
        DUP = new multipleReplacementRule("Duplication", "Dup.", dup1, dup2);

        wff pOrQ = new disjunction(p, q);
        wff qOrP = new disjunction(q, p);
        singleReplacementRule comm1 = new singleReplacementRule("Commutation 1", "Comm1", pOrQ, qOrP, p, q);
        wff pAndQ = new conjunction(p, q);
        wff qAndP = new conjunction(q, p);
        singleReplacementRule comm2 = new singleReplacementRule("Commutation 2", "Comm2", pAndQ, qAndP, p, q);
        COMM = new multipleReplacementRule("Commutation", "Comm.", comm1, comm2);

        wff pOrQ_orR = new disjunction(new disjunction(p, q), r);
        wff p_orQorR = new disjunction(p, new disjunction(q, r));
        singleReplacementRule assoc1 = new singleReplacementRule("Association 1", "Assoc1", pOrQ_orR, p_orQorR, p, q, r);
        wff pAndQ_andR = new conjunction(new conjunction(p, q), r);
        wff p_andQandR = new conjunction(p, new conjunction(q, r));
        singleReplacementRule assoc2 = new singleReplacementRule("Association 2", "Assoc2", pAndQ_andR, p_andQandR, p, q, r);
        ASSOC = new multipleReplacementRule("Association", "Assoc.", assoc1, assoc2);

        wff notP = new negation(p);
        wff notQ = new negation(q);
        wff pThereforeQ = new conditional(p, q);
        wff notQThereforeNotP = new conditional(notQ, notP);
        CONTRAP = new singleReplacementRule("Contraposition", "Contrap.", pThereforeQ, notQThereforeNotP, p, q);

        wff not_PorQ = new negation(pOrQ);
        wff notP_and_notQ = new conjunction(notP, notQ);
        singleReplacementRule dem1 = new singleReplacementRule("demorgans 1", "DeM1", not_PorQ, notP_and_notQ, p, q);
        wff not_pAndQ = new negation(pAndQ);
        wff notP_or_notQ = new disjunction(notP, notQ);
        singleReplacementRule dem2 = new singleReplacementRule("demorgans 2", "DeM2", not_pAndQ, notP_or_notQ, p, q);
        DEM = new multipleReplacementRule("DeMorgan's", "DeM.", dem1, dem2);

        wff p_iff_q = new biconditional(p, q);
        wff qThereforeP = new conditional(q, p);
        wff pThereforeQ_and_qThereforeP = new conjunction(pThereforeQ, qThereforeP);
        BE = new singleReplacementRule("Biconditional Exchange", "B.E.", p_iff_q, pThereforeQ_and_qThereforeP, p, q);

        wff notP_or_q = new disjunction(notP, q);
        CE = new singleReplacementRule("Conditional Exchange", "C.E.", pThereforeQ, notP_or_q, p, q);

        wff qOrR = new disjunction(q, r);
        wff pAnd_qOrR = new conjunction(p, qOrR);
        wff pAndR = new conjunction(p, r);
        wff pAndQ_or_pAndR = new disjunction(pAndQ, pAndR);
        singleReplacementRule dist1 = new singleReplacementRule("Distribution 1", "dist1", pAnd_qOrR, pAndQ_or_pAndR, p, q, r);
        wff qAndR = new conjunction(q, r);
        wff pOr_qAndR = new disjunction(p, qAndR);
        wff pOrR = new disjunction(p, r);
        wff pOrQ_and_pOrR = new conjunction(pOrQ, pOrR);
        singleReplacementRule dist2 = new singleReplacementRule("Distribution 2", "dist2", pOr_qAndR, pOrQ_and_pOrR, p, q, r);
        DIST = new multipleReplacementRule("Distribution", "Dist.", dist1, dist2);

        wff pAndQ_thereforeR = new conditional(pAndQ, r);
        wff qThereforeR = new conditional(q, r);
        wff pTherefore_qThereforeR = new conditional(p, qThereforeR);
        EXP = new singleReplacementRule("Exportation", "Exp.", pAndQ_thereforeR, pTherefore_qThereforeR, p, q, r);

        ISYM = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof identityFormula) {
                    char name1 = ((identityFormula) in).getName1();
                    char name2 = ((identityFormula) in).getName2();
                    wff newReplacement = new identityFormula(name2, name1);
                    r.add(newReplacement);
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Identity Symmetry";
            }

            @Override
            public String getAbbreviation() {
                return "I. Sym.";
            }

            @Override
            public String getReplacementFormString() {
                return "a=b::b=a";
            }
        };

        recursiveReplacementRule qn1 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff in_inner = ((negation) in).getInr();
                    if (in_inner instanceof universalQuantifier) {
                        wff quantified = ((universalQuantifier) in_inner).getInr();
                        char var = ((universalQuantifier) in_inner).getVar();
                        wff negatedQuantified = new negation(quantified);
                        wff existNegatedQuantified = new existentialQuantifier(var, negatedQuantified);
                        r.add(existNegatedQuantified);
                    }
                } else if (in instanceof existentialQuantifier) {
                    wff exists_inner = ((existentialQuantifier) in).getInr();
                    char var = ((existentialQuantifier) in).getVar();
                    if (exists_inner instanceof negation) {
                        wff negated = ((negation) exists_inner).getInr();
                        wff universalNegated = new universalQuantifier(var, negated);
                        wff negatedUniversalNegated = new negation(universalNegated);
                        r.add(negatedUniversalNegated);
                    }
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Quantifier Negation 1";
            }

            @Override
            public String getAbbreviation() {
                return "Q.N. 1";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(x)" + main.phi + "x::(" + main.existenceSymbol + "x)" + main.negationSymbol + main.phi + "x";
            }
        };

        recursiveReplacementRule qn2 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff negationInner = ((negation) in).getInr();
                    if (negationInner instanceof existentialQuantifier) {
                        char var = ((existentialQuantifier) negationInner).getVar();
                        wff negationExistInner = ((existentialQuantifier) negationInner).getInr();
                        wff negationOfContent = new negation(negationExistInner);
                        wff universalNegation = new universalQuantifier(var, negationOfContent);
                        r.add(universalNegation);
                    }
                } else if (in instanceof universalQuantifier) {
                    char var = ((universalQuantifier) in).getVar();
                    wff universalInner = ((universalQuantifier) in).getInr();
                    if (universalInner instanceof negation) {
                        wff universalNegatedInner = ((negation) universalInner).getInr();
                        wff existsContents = new existentialQuantifier(var, universalNegatedInner);
                        wff negatedExistsContents = new negation(existsContents);
                        r.add(negatedExistsContents);
                    }
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Quantifier Negation 2";
            }

            @Override
            public String getAbbreviation() {
                return "Q.N. 2";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(" + main.existenceSymbol + "x)" + main.phi + "x::(x)" + main.negationSymbol + main.phi + "x";
            }
        };

        recursiveReplacementRule qn3 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff negatedInner = ((negation) in).getInr();
                    if (negatedInner instanceof universalQuantifier) {
                        wff negatedUniversalInner = ((universalQuantifier) negatedInner).getInr();
                        char var = ((universalQuantifier) negatedInner).getVar();
                        if (negatedUniversalInner instanceof negation) {
                            wff negatedUniversalNegatedInner = ((negation) negatedUniversalInner).getInr();
                            wff existsContents = new existentialQuantifier(var, negatedUniversalNegatedInner);
                            r.add(existsContents);
                        }
                    }
                } else if (in instanceof existentialQuantifier) {
                    wff existsInner = ((existentialQuantifier) in).getInr();
                    char var = ((existentialQuantifier) in).getVar();
                    wff negatedContents = new negation(existsInner);
                    wff universalNegatedContents = new universalQuantifier(var, negatedContents);
                    wff negatedUniversalNegatedContents = new negation(universalNegatedContents);
                    r.add(negatedUniversalNegatedContents);
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Quantifier Negation 3";
            }

            @Override
            public String getAbbreviation() {
                return "Q.N. 3";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(x)" + main.negationSymbol + main.phi + "x::(x)" + main.phi + "x";
            }
        };

        recursiveReplacementRule qn4 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff negatedInner = ((negation) in).getInr();
                    if (negatedInner instanceof existentialQuantifier) {
                        wff negatedExistsInner = ((existentialQuantifier) negatedInner).getInr();
                        char var = ((existentialQuantifier) negatedInner).getVar();
                        if (negatedExistsInner instanceof negation) {
                            wff negatedExistsNegatedInner = ((negation) negatedExistsInner).getInr();
                            wff universalContents = new universalQuantifier(var, negatedExistsNegatedInner);
                            r.add(universalContents);
                        }
                    }
                } else if (in instanceof universalQuantifier) {
                    wff universalInner = ((universalQuantifier) in).getInr();
                    char var = ((universalQuantifier) in).getVar();
                    wff negatedContents = new negation(universalInner);
                    wff existsNegatedContents = new existentialQuantifier(var, negatedContents);
                    wff negatedExistsNegatedContents = new negation(existsNegatedContents);
                    r.add(negatedExistsNegatedContents);
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Quantifier Negation 4";
            }

            @Override
            public String getAbbreviation() {
                return "Q.N. 4";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(" + main.existenceSymbol + "x)" + main.negationSymbol + main.phi + "x::(x)" + main.phi + "x";
            }
        };

        QN = new multipleReplacementRule("Quantifier Negation", "Q.N.", qn1, qn2, qn3, qn4);

        recursiveReplacementRule cqn1 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff negatedInner = ((negation) in).getInr();
                    if (negatedInner instanceof universalQuantifier) {
                        wff negatedUniversalInner = ((universalQuantifier) negatedInner).getInr();
                        char var = ((universalQuantifier) negatedInner).getVar();
                        if (negatedUniversalInner instanceof conditional) {
                            wff negatedUniversalConditionalPhi = ((conditional) negatedUniversalInner).getAntecedent();
                            wff negatedUniversalConditionalPsi = ((conditional) negatedUniversalInner).getConsequent();
                            wff notPsi = new negation(negatedUniversalConditionalPsi);
                            wff phiAndNotPsi = new conjunction(negatedUniversalConditionalPhi, notPsi);
                            wff existsPhiAndNotPsi = new existentialQuantifier(var, phiAndNotPsi);
                            r.add(existsPhiAndNotPsi);
                        }
                    }
                } else if (in instanceof existentialQuantifier) {
                    wff existsInner = ((existentialQuantifier) in).getInr();
                    char var = ((existentialQuantifier) in).getVar();
                    if (existsInner instanceof conjunction) {
                        wff existsConjunctionInner1 = ((conjunction) existsInner).getInner1();
                        wff existsConjunctionInner2 = ((conjunction) existsInner).getInner2();
                        if (existsConjunctionInner2 instanceof negation) {
                            wff existsConjunctionInner2NegatedInner = ((negation) existsConjunctionInner2).getInr();
                            wff phiImpliesPsi = new conditional(existsConjunctionInner1, existsConjunctionInner2NegatedInner);
                            wff universalPhiImpliesPsi = new universalQuantifier(var, phiImpliesPsi);
                            wff negatedUniversalPhiImpliesPsi = new negation(universalPhiImpliesPsi);
                            r.add(negatedUniversalPhiImpliesPsi);
                        }
                    }
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Categorical Quantifier Negation 1";
            }

            @Override
            public String getAbbreviation() {
                return "C.Q.N. 1";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(x)(" + main.phi + "x" + main.horseshoeSymbol + main.psi + "x)::(" + main.existenceSymbol + "x)(" + main.phi + "x" + main.conjunctionSymbol + main.negationSymbol + main.psi + "x)";
            }
        };

        recursiveReplacementRule cqn2 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff negatedInner = ((negation) in).getInr();
                    if (negatedInner instanceof existentialQuantifier) {
                        wff negatedExistsInner = ((existentialQuantifier) negatedInner).getInr();
                        char var = ((existentialQuantifier) negatedInner).getVar();
                        if (negatedExistsInner instanceof conjunction) {
                            wff negatedExistsConjunctionPhi = ((conjunction) negatedExistsInner).getInner1();
                            wff negatedExistsConjunctionPsi = ((conjunction) negatedExistsInner).getInner2();
                            wff notPsi = new negation(negatedExistsConjunctionPsi);
                            wff phiThereforeNotPsi = new conditional(negatedExistsConjunctionPhi, notPsi);
                            wff universalPhiThereforeNotPsi = new universalQuantifier(var, phiThereforeNotPsi);
                            r.add(universalPhiThereforeNotPsi);
                        }
                    }
                } else if (in instanceof universalQuantifier) {
                    wff universalInner = ((universalQuantifier) in).getInr();
                    char var = ((universalQuantifier) in).getVar();
                    if (universalInner instanceof conjunction) {
                        wff phi = ((conjunction) universalInner).getInner1();
                        wff maybeNotPsi = ((conjunction) universalInner).getInner2();
                        if (maybeNotPsi instanceof negation) {
                            wff psi = ((negation) maybeNotPsi).getInr();
                            wff phiAndPsi = new conjunction(phi, psi);
                            wff existsPhiAndPsi = new existentialQuantifier(var, phiAndPsi);
                            wff negatedExistsPhiAndPsi = new negation(existsPhiAndPsi);
                            r.add(negatedExistsPhiAndPsi);
                        }
                    }
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Categorical Quantifier Negation 2";
            }

            @Override
            public String getAbbreviation() {
                return "C.Q.N. 2";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(" + main.existenceSymbol + "x)(" + main.phi + "x" + main.conjunctionSymbol + main.psi + ")::(x)(" + main.phi + main.horseshoeSymbol + main.negationSymbol + main.psi + "x)";
            }
        };

        recursiveReplacementRule cqn3 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff negatedInner = ((negation) in).getInr();
                    if (negatedInner instanceof universalQuantifier) {
                        wff negatedUniversalInner = ((universalQuantifier) negatedInner).getInr();
                        char var = ((universalQuantifier) negatedInner).getVar();
                        if (negatedUniversalInner instanceof conditional) {
                            wff phi = ((conditional) negatedUniversalInner).getAntecedent();
                            wff maybeNotPsi = ((conditional) negatedUniversalInner).getConsequent();
                            if (maybeNotPsi instanceof negation) {
                                wff psi = ((negation) maybeNotPsi).getInr();
                                wff phiAndPsi = new conjunction(phi, psi);
                                wff existsPhiAndPsi = new existentialQuantifier(var, phiAndPsi);
                                r.add(existsPhiAndPsi);
                            }
                        }
                    }
                } else if (in instanceof existentialQuantifier) {
                    wff existsInner = ((existentialQuantifier) in).getInr();
                    char var = ((existentialQuantifier) in).getVar();
                    if (existsInner instanceof conjunction) {
                        wff phi = ((conjunction) existsInner).getInner1();
                        wff psi = ((conjunction) existsInner).getInner2();
                        wff notPsi = new negation(psi);
                        wff phiThereforeNotPsi = new conditional(phi, notPsi);
                        wff universalPhiThereforeNotPsi = new universalQuantifier(var, phiThereforeNotPsi);
                        wff negatedUniversalPhiThereforeNotPsi = new negation(universalPhiThereforeNotPsi);
                        r.add(negatedUniversalPhiThereforeNotPsi);
                    }
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Categorical Quantifier Negation 3";
            }

            @Override
            public String getAbbreviation() {
                return "C.Q.N. 3";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(x)(" + main.phi + "x" + main.horseshoeSymbol + main.negationSymbol + main.psi + "x)::(" + main.existenceSymbol + "x)(" + main.phi + "x" + main.conjunctionSymbol + main.psi + "x)";
            }
        };

        recursiveReplacementRule cqn4 = new recursiveReplacementRule() {
            @Override
            ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
                ArrayList<wff> r = new ArrayList<>();
                if (in instanceof negation) {
                    wff negatedInner = ((negation) in).getInr();
                    if (negatedInner instanceof existentialQuantifier) {
                        wff negatedExistsInner = ((existentialQuantifier) negatedInner).getInr();
                        char var = ((existentialQuantifier) negatedInner).getVar();
                        if (negatedExistsInner instanceof conjunction) {
                            wff phi = ((conjunction) negatedExistsInner).getInner1();
                            wff maybeNotPsi = ((conjunction) negatedExistsInner).getInner2();
                            if (maybeNotPsi instanceof negation) {
                                wff psi = ((negation) maybeNotPsi).getInr();
                                wff phiThereforePsi = new conditional(phi, psi);
                                wff universalPhiThereforePsi = new universalQuantifier(var, phiThereforePsi);
                                r.add(universalPhiThereforePsi);
                            }
                        }
                    }
                } else if (in instanceof universalQuantifier) {
                    wff universalInner = ((universalQuantifier) in).getInr();
                    char var = ((universalQuantifier) in).getVar();
                    if (universalInner instanceof conditional) {
                        wff phi = ((conditional) universalInner).getAntecedent();
                        wff psi = ((conditional) universalInner).getConsequent();
                        wff notPsi = new negation(psi);
                        wff phiAndNotPsi = new conjunction(phi, notPsi);
                        wff existsPhiAndNotPsi = new existentialQuantifier(var, phiAndNotPsi);
                        wff negatedExistsPhiAndNotPsi = new negation(existsPhiAndNotPsi);
                        r.add(negatedExistsPhiAndNotPsi);
                    }
                }
                return r;
            }

            @Override
            public String getFullName() {
                return "Categorical Quantifier Negation 4";
            }

            @Override
            public String getAbbreviation() {
                return "C.Q.N. 4";
            }

            @Override
            public String getReplacementFormString() {
                return main.negationSymbol + "(" + main.existenceSymbol + "x)(" + main.phi + "x" + main.conjunctionSymbol + main.negationSymbol + main.psi + "x)::(x)(" + main.phi + "x" + main.horseshoeSymbol + main.psi + "x)";
            }
        };

        CQN = new multipleReplacementRule("Categorical Quantifier Negation", "C.Q.N.", cqn1, cqn2, cqn3, cqn4);

        allRPL = new replacementRule[]{DN, DUP, COMM, ASSOC, CONTRAP, DEM, BE, CE, DIST, EXP, ISYM, CQN, QN};
    }

    public abstract String getFullName();

    public abstract String getAbbreviation();

    public abstract String getReplacementFormString();

    public abstract ArrayList<wff> getAllPossibleReplacementsWith(wff in);

    public String toString() {
        return getFullName() + " (" + getAbbreviation() + ")\n" + getReplacementFormString();
    }

}
