import java.util.ArrayList;

public class multipleBasicInferenceRule extends basicInferenceRule {
    private basicInferenceRule[] contents;

    public multipleBasicInferenceRule(String fullname, String abbrev, basicInferenceRule... contents) {
        this.contents = contents;
        this.fullName = fullname;
        this.abbrev = abbrev;
    }

    public ArrayList<wff> getPossibleConclusionsWithThesePremises(ArrayList<wff> premises) {
        ArrayList<wff> r = new ArrayList<>();
        for (basicInferenceRule thisRule : contents) {
            r.addAll(thisRule.getPossibleConclusionsWithThesePremises(premises));
        }
        main.removeDuplicatesFromWffListAndSort(r);
        return r;
    }


    public String getComplaintOfTryingToGetConclusionFromThesePremises(ArrayList<wff> premises) {
        String[] complaintsOfContents = new String[contents.length];
        for (int i = 0; i < complaintsOfContents.length; i++) {
            complaintsOfContents[i] = contents[i].getComplaintOfTryingToGetConclusionFromThesePremises(premises);
        }
        StringBuilder r = new StringBuilder();
        r.append("Error applying ");
        r.append(fullName);
        r.append(": for each possible form,");
        for (String thisComplaint : complaintsOfContents) {
            r.append("\n\t-");
            r.append(thisComplaint);
        }
        return r.toString();
    }

    @Override
    String getInferenceFormString() {
        ArrayList<ArrayList<String>> linesOfInferenceFormsOfContents = new ArrayList<>();
        int[] maxLengthsOfForms = new int[contents.length];
        for (basicInferenceRule r : contents) {
            linesOfInferenceFormsOfContents.add(new ArrayList<>());
            String thisInferenceForm = r.getInferenceFormString();
            String[] linesOfThisInfereneForm = thisInferenceForm.split("\n");
            for (String line : linesOfThisInfereneForm) {
                linesOfInferenceFormsOfContents.get(linesOfInferenceFormsOfContents.size() - 1).add(line);
                int prevmax = maxLengthsOfForms[linesOfInferenceFormsOfContents.size() - 1];
                maxLengthsOfForms[linesOfInferenceFormsOfContents.size() - 1] = Math.max(prevmax, line.length());
            }
        }
        ArrayList<String> newLines = new ArrayList<>();
        int maxNumLines = -1;
        for (int i = 0; i < linesOfInferenceFormsOfContents.size(); i++) {
            maxNumLines = Math.max(maxNumLines, linesOfInferenceFormsOfContents.get(i).size());
        }
        for (int i = 0; i < maxNumLines; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < linesOfInferenceFormsOfContents.size(); j++) {
                if (linesOfInferenceFormsOfContents.get(j).size() > i) {
                    String thisLineOfThisForm = linesOfInferenceFormsOfContents.get(j).get(i);
                    sb.append(main.getNCharsInARow(' ', maxLengthsOfForms[j] - thisLineOfThisForm.length()));
                    sb.append(thisLineOfThisForm);
                } else {
                    sb.append(main.getNCharsInARow(' ', maxLengthsOfForms[j]));
                }
                sb.append("  ");
            }
            newLines.add(sb.toString());
        }
        StringBuilder fsb = new StringBuilder();
        fsb.append(newLines.get(0));
        for (int i = 1; i < newLines.size(); i++) {
            fsb.append('\n');
            fsb.append(newLines.get(i));
        }
        return fsb.toString();
    }
}
