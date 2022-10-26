import java.util.ArrayList;

public class multipleReplacementRule extends recursiveReplacementRule {
    private String abbreviation;
    private String fullName;

    private recursiveReplacementRule[] forms;

    public multipleReplacementRule(String fullname, String abbreviation, recursiveReplacementRule... forms) {
        this.forms = forms;
        this.abbreviation = abbreviation;
        this.fullName = fullname;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getReplacementFormString() {
        String[] eachLine = new String[forms.length];
        for (int i = 0; i < forms.length; i++) {
            eachLine[i] = forms[i].getReplacementFormString();
        }
        StringBuilder r = new StringBuilder();
        if (eachLine.length != 0) {
            r.append(eachLine[0]);
            for (int j = 1; j < eachLine.length; j++) {
                r.append('\n');
                r.append(eachLine[j]);
            }
        }
        return r.toString();
    }


    ArrayList<wff> getAllShallowReplacementsForThisFormula(wff in) {
        ArrayList<wff> r = new ArrayList<>();
        for (recursiveReplacementRule thisForm : forms) {
            ArrayList<wff> shallowReplacementsOfThisForm = thisForm.getAllShallowReplacementsForThisFormula(in);
            r.addAll(shallowReplacementsOfThisForm);
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }

    /*
    @Deprecated
    public ArrayList<wff> getAllPossibleReplacementsWith(wff in) {
        ArrayList<wff> r = new ArrayList<>();
        for (replacementRule rule: forms)
        {
            r.addAll(rule.getAllPossibleReplacementsWith(in));
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }
    */
}
