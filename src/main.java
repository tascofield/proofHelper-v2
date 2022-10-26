import java.util.ArrayList;
import java.util.Collections;

public class main {

    public static final char existenceSymbol = '∃';

    public static final char negationSymbol = '~';
    public static final char horseshoeSymbol = '⊃';
    public static final char conjunctionSymbol = '·';
    public static final char disjunctionSymbol = '∨';
    public static final char biconditionalSymbol = '≡';

    public static final char equivalenceSymbol = '=';
    public static final char nonEquivalenceSymbol = '≠';

    public static final String thereforeSymbol = "/∴";
    public static final char phi = 'ϕ';
    public static final char psi = 'ψ';

    public static final char horizontalSingleBar = '─';
    public static final char lightVerticalSingleBar = '│';
    public static final char lightDownRightBar = '┌';
    public static final char lightUpRightBar = '└';

    public static final boolean disPlayNegatedIdentitiesWithDedicatedSymbol = true;

    public static void main(String[] args) {
        new proofEditor().enterCommandLoop();
        //test();
    }

    public static String encapsulateIfNecessary(wff f) {
        String fString = f.toString();
        boolean shouldEncapsulate = true;
        if (f instanceof negation) {
            shouldEncapsulate = false;
        }
        if (f instanceof sentence) {
            shouldEncapsulate = false;
        }
        if (f instanceof quantifier) {
            shouldEncapsulate = false;
        }
        if (f instanceof wffVariable) {
            shouldEncapsulate = false;
        }
        if (shouldEncapsulate) {
            fString = "(" + fString + ")";
        }
        return fString;
    }

    public static void removeDuplicatesFromCharListAndSort(ArrayList<Character> l) {
        Collections.sort(l);
        int index = 1;
        while (index < l.size()) {
            if (l.get(index) == l.get(index - 1)) {
                l.remove(index);
            } else {
                index++;
            }
        }
    }

    public static void removeDuplicatesFromWffListAndSort(ArrayList<wff> l) {
        Collections.sort(l);
        int index = 1;
        while (index < l.size()) {
            if (l.get(index).equals(l.get(index - 1))) {
                l.remove(index);
            } else {
                index++;
            }
        }
    }

    public static String getNCharsInARow(char c, int n) {
        if (n == 0) {
            return "";
        }
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < n; i++) {
            r.append(c);
        }
        return r.toString();
    }

    private static void test() {
        //formulaBuilder fb = new formulaBuilder("(Ex)(Fx&x==a)");
        //formulaBuilder fb = new formulaBuilder("(x)((Ey)Fya>(Ez)Fxaz)");
        //formulaBuilder fb = new formulaBuilder("~((A&B&C)v(B&C&A))");
        //formulaBuilder fb = new formulaBuilder("(x)(y)(Fxy>x==y)");
        //formulaBuilder fb = new formulaBuilder("(Ex)(Mx&Sx&(y)((My&Sy)>x==y)&Dx)");
        //formulaBuilder fb = new formulaBuilder("(x)(Fx>(GxvHx))");
        //formulaBuilder fb = new formulaBuilder("(x)~(y)~(Ez)((Uxz&Tzx)>(IbzvJyx))");
        //formulaBuilder fb = new formulaBuilder("Uabcdefghjik&(z)(y)(x)(w)(u)(t)(s)(r)(q)(p)((Upqrstuwxyz>Uqrstuwxyzp)&(Upqrstuwxyz>p!=q))");
        wff f = new formulaBuilder("Uabcdefghjik&(z)(y)(x)(w)(u)(t)(s)(r)(q)(p)((Upqrstuwxyz>Uqrstuwxyzp)&(Upqrstuwxyz>p!=q))").getResult();
        ArrayList<wff> replacements1 = replacementRule.CONTRAP.getAllPossibleReplacementsWith(new formulaBuilder("A>(Bx>(Cds&F))").getResult());
        ArrayList<wff> replacements2 = replacementRule.BE.getAllPossibleReplacementsWith(new formulaBuilder("((A=B)>C)&(C>(A=B))").getResult());
        ArrayList<wff> replacements3 = replacementRule.COMM.getAllPossibleReplacementsWith(new formulaBuilder("(A=B)v(A&(CvD))").getResult());
        ArrayList<wff> replacements4 = replacementRule.DEM.getAllPossibleReplacementsWith(new formulaBuilder("~(B=(~(b==c&c==d)v~(b==f&g!=s)v(~Fv~Q)v(~u==u&~(Bf=H))))").getResult());
        ArrayList<wff> replacements5 = replacementRule.CE.getAllPossibleReplacementsWith(new formulaBuilder("A>(Bx>(Cds&(~FvFdg)))").getResult());
        ArrayList<wff> replacements6 = replacementRule.DIST.getAllPossibleReplacementsWith(new formulaBuilder("(S&T)v(S&U)").getResult());
        ArrayList<wff> replacements7 = replacementRule.EXP.getAllPossibleReplacementsWith(new formulaBuilder("(Z>(B>C))=((Z>B)>C)=((A&((D&F)>C))>R)").getResult());
        ArrayList<wff> replacements8 = replacementRule.ISYM.getAllPossibleReplacementsWith(new formulaBuilder("a==b&(C=j==s)").getResult());
    }
}
