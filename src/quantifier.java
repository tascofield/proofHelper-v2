import java.util.ArrayList;

public abstract class quantifier extends wff {
    protected char var;
    protected wff inner;

    public char getVar() {
        return var;
    }

    public wff getInr() {
        return inner;
    }

    abstract protected String getQuantifierString();

    public ArrayList<Character> getAllUsedLetters() {
        ArrayList<Character> r = new ArrayList<>();
        r.add(var);
        r.addAll(inner.getAllUsedLetters());
        main.removeDuplicatesFromCharListAndSort(r);
        return r;
    }

    public ArrayList<Character> getAllLettersUsedAsConstants() {
        ArrayList<Character> r = inner.getAllLettersUsedAsConstants();
        int searchIndex = 0;
        while (searchIndex < r.size()) {
            char charAtIndex = r.get(searchIndex);
            if (charAtIndex == var) {
                r.remove(searchIndex);
            } else {
                searchIndex++;
            }
        }
        return r;
    }

    public ArrayList<Character> getAllLettersUsedAsVariables() {
        ArrayList<Character> r = inner.getAllLettersUsedAsVariables();
        r.add(var);
        return r;
    }

    public String toString() {
        String quantifierString = getQuantifierString();
        String contents = main.encapsulateIfNecessary(inner);
        return quantifierString + contents;
    }

    public boolean equals(wff other) {
        if (!(other instanceof quantifier)) {
            return false;
        }
        Class otherClass = other.getClass();
        Class thisClass = this.getClass();
        if (thisClass != otherClass) {
            return false;
        }
        char otherVar = ((quantifier) other).getVar();
        if (otherVar != var) {
            return false;
        }
        wff otherInner = ((quantifier) other).getInr();
        return inner.equals(otherInner);
    }

    public ArrayList<wff> getSubformulasInOrder() {
        ArrayList<wff> r = new ArrayList<>();
        r.add(inner);
        return r;
    }
}
