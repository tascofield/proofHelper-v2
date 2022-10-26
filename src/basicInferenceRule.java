import java.util.ArrayList;

public abstract class basicInferenceRule extends inferenceRule {
    abstract ArrayList<wff> getPossibleConclusionsWithThesePremises(ArrayList<wff> premises);

    abstract String getComplaintOfTryingToGetConclusionFromThesePremises(ArrayList<wff> premises);

    public String fullName;
    public String abbrev;

    public String getAbbreviation() {
        return abbrev;
    }

    public String getFullName() {
        return fullName;
    }

    public inferenceAttemptResult attemptInference(ArrayList<wff> refs, wff concl) {
        ArrayList<wff> possibleConclusions = getPossibleConclusionsWithThesePremises(refs);
        for (wff possibleConcl : possibleConclusions) {
            if (concl.equals(possibleConcl)) {
                return new inferenceAttemptResult(true, "");
            }
        }
        String complaint = getComplaintOfTryingToGetConclusionFromThesePremises(refs);
        return new inferenceAttemptResult(false, complaint);
    }
}
