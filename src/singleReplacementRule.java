import java.util.ArrayList;

public class singleReplacementRule extends recursiveReplacementRule {
    private wffVariable[] formulaVariables;
    private wff exchangeableFormula1;
    private wff exchangeableFormula2;

    private String fullName;
    private String abbrev;

    public singleReplacementRule(String fullname, String abbreviation, wff f1, wff f2, wffVariable... vars) {
        this.fullName = fullname;
        this.abbrev = abbreviation;
        this.exchangeableFormula1 = f1;
        this.exchangeableFormula2 = f2;
        this.formulaVariables = vars;
    }

    public String getAbbreviation() {
        return abbrev;
    }

    public String getFullName() {
        return fullName;
    }

    public String getReplacementFormString() {
        String s1 = exchangeableFormula1.toString().replace("?", "");
        String s2 = exchangeableFormula2.toString().replace("?", "");
        return s1 + "::" + s2;
    }


    public ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
        ArrayList<wff> r = new ArrayList<>();
        wffVariable.link[] maybeLinkageFrom1To2 = wffVariable.maybeGetLinkageWithThisInputAndPattern(in, exchangeableFormula1, formulaVariables);
        if (maybeLinkageFrom1To2 != null) {
            wff thisShallowReplacement = wffVariable.applyLinkageToTemplate(maybeLinkageFrom1To2, exchangeableFormula2);
            r.add(thisShallowReplacement);
        }
        wffVariable.link[] maybeLinkageFrom2To1 = wffVariable.maybeGetLinkageWithThisInputAndPattern(in, exchangeableFormula2, formulaVariables);
        if (maybeLinkageFrom2To1 != null) {
            wff thisShallowReplacement = wffVariable.applyLinkageToTemplate(maybeLinkageFrom2To1, exchangeableFormula1);
            r.add(thisShallowReplacement);
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }


    /*
    @Deprecated
    public ArrayList<wff> getAllPossibleReplacementsWith(wff in)
    {
        ArrayList<wff> possibleReplacements = new ArrayList<>();
        ArrayList<wffVariable.replacementLinkage> linkagesFromFirstToSecond = wffVariable.getAllLinkagesForThisExchangeableFormula(in,exchangeableFormula1,formulaVariables,new int[0]);
        for (wffVariable.replacementLinkage linkageFrom1to2: linkagesFromFirstToSecond)
        {
            wff thisReplacement = wffVariable.applyLinkageToInput(linkageFrom1to2,in,exchangeableFormula2);
            possibleReplacements.add(thisReplacement);
        }
        ArrayList<wffVariable.replacementLinkage> linkagesFromSecondToFirst = wffVariable.getAllLinkagesForThisExchangeableFormula(in,exchangeableFormula2,formulaVariables,new int[0]);
        for (wffVariable.replacementLinkage linkageFrom2to1: linkagesFromSecondToFirst)
        {
            wff thisReplacement = wffVariable.applyLinkageToInput(linkageFrom2to1,in,exchangeableFormula1);
            possibleReplacements.add(thisReplacement);
        }
        main.removeDuplicatesFromWffListAndSort(possibleReplacements);
        return possibleReplacements;
    }
    */

}
