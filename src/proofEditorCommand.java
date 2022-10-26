import java.util.ArrayList;
import java.util.Scanner;

public abstract class proofEditorCommand {

    public static final proofEditorCommand PR;
    public static final proofEditorCommand ANY_RPL;
    public static final proofEditorCommand ANY_INF;
    public static final proofEditorCommand ADD_CONDITIONAL_ASSUMPTION;
    public static final proofEditorCommand ADD_INDIRECT_ASSUMPTION;
    public static final proofEditorCommand ADD_UNIVERSAL_GENERALIZATION_SUBPROOF;
    public static final proofEditorCommand CLOSE_SUBPROOF;
    public static final proofEditorCommand HELP;

    public static final proofEditorCommand[] ALL_COMMANDS;

    static {
        PR = new proofEditorCommand() {
            @Override
            String getDescription() {
                return "Add a new premise to the editor, if there is nothing else yet.";
            }

            @Override
            void call(String input, proofEditor e) {
                if (!hasFormula(input)) {
                    System.out.println("Expected formula, got none.");
                    printUsage();
                } else {
                    formulaBuilder fb = getFormulaBuilder(input);
                    if (fb.isValid()) {
                        e.addPremise(fb.getResult());
                    } else {
                        System.out.println(fb.getComplaint());
                    }
                }
            }

            @Override
            String[] getAllAliases() {
                return new String[]{"pr", "premise", "addpremise"};
            }

            @Override
            String getUsage() {
                return "pr [formula]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                return new int[]{0};
            }

        };

        ANY_RPL = new proofEditorCommand() {

            @Override
            String getDescription() {
                StringBuilder sb = new StringBuilder();
                sb.append("Use a replacement rule.");
                sb.append('\n');
                sb.append("Codes for replacement rules:");
                for (int i = 0; i < proofEditor.allRPLCommands.length; i++) {
                    sb.append("\n\t-");
                    sb.append(proofEditor.allRPLCommands[i]);
                    sb.append(": ");
                    sb.append(replacementRule.allRPL[i].getFullName());
                }
                return sb.toString();
            }

            @Override
            void call(String input, proofEditor e) {
                replacementRule RPL = proofEditor.getReplacementRuleFromCommand(getBase(input));
                if (!hasFormula(input)) {
                    e.attemptReplacementRule_auto(RPL, getRefs(input));
                } else {
                    formulaBuilder fb = getFormulaBuilder(input);
                    if (fb.isValid()) {
                        e.attemptReplacementRule(RPL, getRefs(input), fb.getResult());
                    } else {
                        System.out.print(fb.getComplaint());
                    }
                }
            }

            @Override
            String[] getAllAliases() {
                String[] r = new String[proofEditor.allRPLCommands.length];
                for (int i = 0; i < r.length; i++) {
                    r[i] = proofEditor.allRPLCommands[i].toLowerCase();
                }
                return r;
            }

            @Override
            String getUsage() {
                return "[code] [line numbers...] [formula (sometimes optional)]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                int[] r = new int[proofEditor.allRPLCommands.length];
                for (int i = 0; i < r.length; i++) {
                    r[i] = i;
                }
                return r;
            }
        };

        ANY_INF = new proofEditorCommand() {
            @Override
            String getDescription() {
                StringBuilder sb = new StringBuilder();
                sb.append("Use an inference rule.");
                sb.append('\n');
                sb.append("Codes for inference rules:");
                for (int i = 0; i < proofEditor.allINFCommands.length; i++) {
                    sb.append("\n\t-");
                    sb.append(proofEditor.allINFCommands[i]);
                    sb.append(": ");
                    sb.append(inferenceRule.allINF[i].getFullName());
                }
                return sb.toString();
            }

            @Override
            void call(String input, proofEditor e) {
                inferenceRule INF = proofEditor.getInferenceRuleFromCommand(getBase(input));
                if (!hasFormula(input)) {
                    e.attemptInferenceRule_auto(INF, getRefs(input));
                } else {
                    formulaBuilder fb = getFormulaBuilder(input);
                    if (fb.isValid()) {
                        e.attemptInferenceRule(INF, getRefs(input), fb.getResult());
                    } else {
                        System.out.print(fb.getComplaint());
                    }
                }
            }

            @Override
            String[] getAllAliases() {
                String[] r = new String[proofEditor.allINFCommands.length];
                for (int i = 0; i < r.length; i++) {
                    r[i] = proofEditor.allINFCommands[i].toLowerCase();
                }
                return r;
            }

            @Override
            String getUsage() {
                return "[code] [line numbers...] [formula (sometimes optional)]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                int[] r = new int[proofEditor.allINFCommands.length];
                for (int i = 0; i < r.length; i++) {
                    r[i] = i;
                }
                return r;
            }
        };

        ADD_CONDITIONAL_ASSUMPTION = new proofEditorCommand() {
            @Override
            String getDescription() {
                return "Create a new conditional subproof. Can be closed with the cp command";
            }

            @Override
            void call(String input, proofEditor e) {
                if (!hasFormula(input)) {
                    System.out.println("Expected formula, got none");
                    printUsage();
                } else {
                    formulaBuilder fb = getFormulaBuilder(input);
                    if (fb.isValid()) {
                        e.createConditionalSubproof(fb.getResult());
                    } else {
                        System.out.println(fb.getComplaint());
                    }
                }
            }

            @Override
            String[] getAllAliases() {
                return new String[]{"aspcp", "asspcp", "assp(cp)", "asp(cp)"};
            }

            @Override
            String getUsage() {
                return "aspcp [formula]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                return new int[]{0};
            }
        };

        ADD_INDIRECT_ASSUMPTION = new proofEditorCommand() {
            @Override
            String getDescription() {
                return "Create a new indirect subproof. Can be closed with the ip command";
            }

            @Override
            void call(String input, proofEditor e) {
                if (!hasFormula(input)) {
                    System.out.println("Expected formula, got none");
                    printUsage();
                } else {
                    formulaBuilder fb = getFormulaBuilder(input);
                    if (fb.isValid()) {
                        e.createIndirectSubproof(fb.getResult());
                    } else {
                        System.out.println(fb.getComplaint());
                    }
                }

            }

            @Override
            String[] getAllAliases() {
                return new String[]{"aspip", "asspip", "assp(ip)", "asp(ip)"};
            }

            @Override
            String getUsage() {
                return "aspip [formula]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                return new int[]{0};
            }
        };

        ADD_UNIVERSAL_GENERALIZATION_SUBPROOF = new proofEditorCommand() {
            @Override
            String getDescription() {
                return "Create a new subproof with a flagged name. Can be closed with the ug command";
            }

            @Override
            void call(String input, proofEditor e) {
                Scanner commandScanner = new Scanner(input);
                commandScanner.next();
                if (!commandScanner.hasNext()) {
                    System.out.println("Error: expected 1 character");
                    printUsage();
                } else {
                    String name = commandScanner.next();
                    if (name.length() != 1) {
                        System.out.println("Error: name of flagged var must be 1 char long");
                        printUsage();
                    } else {
                        char cn = name.charAt(0);
                        if ('a' <= cn && cn <= 'z') {
                            e.createUniversalGeneralizatonSubproof(cn);
                        } else {
                            System.out.println("Error: name of flagged var is not a lowercase letter");
                            printUsage();
                        }
                    }
                }
            }

            @Override
            String[] getAllAliases() {
                return new String[]{"fsug", "flag", "fs(ug)"};
            }

            @Override
            String getUsage() {
                return "fsug [flagged name, must be 1 lowercase character]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                return new int[]{1};
            }
        };

        CLOSE_SUBPROOF = new proofEditorCommand() {
            private String[] aliases = {"cp", "closeconditionalsubproof", "ip", "closeindirectsubproof", "ug", "closeflaggedsubproof", "closeuniversalgeneralizationsubproof"};
            private int[] correspondingTypes = {0, 0, 1, 1, 2, 2, 2};
            private String[] typeRefs = {subProof.conditionalName, subProof.indirectName, subProof.universalname};

            @Override
            String getDescription() {
                return "Discharge a subproof. Different types require different commands:" +
                        "\n\t-" + subProof.conditionalName + ": cp" +
                        "\n\t-" + subProof.indirectName + ": ip" +
                        "\n\t-" + subProof.universalname + ": ug";
            }

            @Override
            void call(String input, proofEditor e) {
                String base = getBase(input);
                boolean found = false;
                int aliasIndex = -1;
                for (int i = 0; i < aliases.length && !found; i++) {
                    if (aliases[i].equals(base)) {
                        found = true;
                        aliasIndex = i;
                    }
                }
                if (!found) {
                    System.out.println("You shouldn't be seeing this, but there was an error finding the correct alias for this command");
                } else {
                    String subproofType = typeRefs[correspondingTypes[aliasIndex]];
                    if (hasFormula(input)) {
                        formulaBuilder fb = getFormulaBuilder(input);
                        if (fb.isValid()) {
                            e.attemptToDischargeSubproofWith(fb.getResult(), subproofType);
                        } else {
                            System.out.println(fb.getComplaint());
                        }
                    } else {
                        e.attemptToDischargeSubproof_auto(subproofType);
                    }
                }
            }

            @Override
            String[] getAllAliases() {
                return aliases;
            }

            @Override
            String getUsage() {
                return "[command] [formula (sometimes optional)]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                return new int[]{0, 2, 4};
            }
        };

        HELP = new proofEditorCommand() {
            @Override
            String getDescription() {
                return "Get help with something. You seem to be getting the hang of it";
            }

            @Override
            void call(String input, proofEditor e) {
                boolean hasSpecificCommand = hasFormula(input); //not the best name for that function in this case, but still does the correct thing
                if (hasSpecificCommand) {
                    Scanner commandScanner = new Scanner(input);
                    commandScanner.next();
                    String commandToHelpWith = commandScanner.next().toLowerCase();
                    boolean found = false;
                    int commandIndex = -1;
                    for (int i = 0; i < ALL_COMMANDS.length && !found; i++) {
                        proofEditorCommand thisCommand = ALL_COMMANDS[i];
                        String[] theseAliases = thisCommand.getAllAliases();
                        for (int j = 0; j < theseAliases.length && !found; j++) {
                            if (theseAliases[j].equals(commandToHelpWith)) {
                                found = true;
                                commandIndex = i;
                            }
                        }
                    }
                    if (!found) {
                        System.out.println("Could not find any command with the name " + commandToHelpWith);
                    } else {
                        proofEditorCommand command = ALL_COMMANDS[commandIndex];
                        if (command == ANY_RPL) {
                            replacementRule RPL = proofEditor.getReplacementRuleFromCommand(commandToHelpWith);
                            System.out.println(RPL.getReplacementFormString());
                        } else if (command == ANY_INF) {
                            inferenceRule INF = proofEditor.getInferenceRuleFromCommand(commandToHelpWith);
                            System.out.println(INF.getInferenceFormString());
                        } else {
                            System.out.println("Aliases:");
                            for (String al : command.getAllAliases()) {
                                System.out.println("\t-" + al);
                            }
                            System.out.println("Description: " + command.getDescription());
                            System.out.println("Usage: " + command.getUsage());
                        }
                    }
                } else {
                    System.out.println("List of commands:");
                    for (proofEditorCommand c : ALL_COMMANDS) {
                        for (int aliasIndex : c.getAliasIndicesForCommandList()) {
                            System.out.println("\t-" + c.getAllAliases()[aliasIndex]);
                        }
                    }
                    System.out.println("For more information about a specific command, type \"help [command]\"");
                }
            }

            @Override
            String[] getAllAliases() {
                return new String[]{"help", "helf"};
            }

            @Override
            String getUsage() {
                return "help [command name (leave blank for list)]";
            }

            @Override
            int[] getAliasIndicesForCommandList() {
                return new int[]{0};
            }
        };


        ALL_COMMANDS = new proofEditorCommand[]{
                PR,
                ANY_RPL,
                ANY_INF,
                ADD_CONDITIONAL_ASSUMPTION,
                ADD_INDIRECT_ASSUMPTION,
                ADD_UNIVERSAL_GENERALIZATION_SUBPROOF,
                CLOSE_SUBPROOF,
                HELP
        };
    }

    abstract String getDescription();

    abstract void call(String input, proofEditor e);

    abstract String[] getAllAliases();

    abstract String getUsage();

    abstract int[] getAliasIndicesForCommandList();

    public boolean isCalled(String input) {
        if (input.length() != 0) {
            String base = getBase(input);
            for (String al : getAllAliases()) {
                if (base.equals(al)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getBase(String command) {
        Scanner commandScanner = new Scanner(command);
        return commandScanner.next().toLowerCase();
    }

    public int[] getRefs(String command) {
        Scanner commandScanner = new Scanner(command);
        commandScanner.next();
        ArrayList<Integer> refs_al = new ArrayList<>();
        while (commandScanner.hasNextInt()) {
            refs_al.add(commandScanner.nextInt());
        }
        int[] refs = new int[refs_al.size()];
        for (int i = 0; i < refs_al.size(); i++) {
            refs[i] = refs_al.get(i);
        }
        return refs;
    }

    public boolean hasFormula(String command) {
        Scanner commandScanner = new Scanner(command);
        commandScanner.next();
        while (commandScanner.hasNextInt()) {
            commandScanner.nextInt();
        }
        return commandScanner.hasNext();
    }

    public formulaBuilder getFormulaBuilder(String command) {
        Scanner commandScanner = new Scanner(command);
        commandScanner.next();
        while (commandScanner.hasNextInt()) {
            commandScanner.nextInt();
        }
        String fs = commandScanner.next();
        formulaBuilder fb = new formulaBuilder(fs);
        return fb;
    }

    public void printUsage() {
        System.out.println("Usage: " + getUsage());
    }
}
