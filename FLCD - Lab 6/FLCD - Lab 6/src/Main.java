import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void showMenu() {
        System.out.println("1. Print set of nonTerminals.");
        System.out.println("2. Print set of terminals.");
        System.out.println("3. Print set of productions.");
        System.out.println("4. Print set of productions for a given nonTerminal.");
        System.out.println("5. Print start symbol.");
        System.out.println("6. Verify if CFG.");
        System.out.println("7. Parse sequence.");
        System.out.println("0. Exit.\n");
    }

    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        grammar.readGrammarFromFile("src/g2.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean condition = true;
        String choice = "";

        while (condition) {
            showMenu();

            System.out.print("choice = ");
            try {
                choice = reader.readLine();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            switch (choice) {
                case "1":
                    System.out.println(grammar.getNonTerminals().size() + "\n" + grammar.getNonTerminals() + "\n");
                    break;
                case "2":
                    System.out.println(grammar.getTerminals().size() + "\n" + grammar.getTerminals() + "\n");
                    break;
                case "3":
                    System.out.println(grammar.getProductions().size() + grammar.printProductions() + "\n");
                    break;
                case "4":
                    String nonTerminal = "";
                    System.out.print("nonTerminal = ");
                    try {
                        nonTerminal = reader.readLine();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    System.out.println(grammar.getProductionForNonTerminal(nonTerminal).size()
                            + grammar.printProductionsForNonTerminal(nonTerminal) + "\n");
                    break;
                case "5":
                    System.out.println(grammar.getStartSymbol() + "\n");
                    break;
                case "6":
                    boolean isCFG = grammar.isCFG();
                    if (isCFG) {
                        System.out.println("The grammar is CFG.\n");
                    } else {
                        System.out.println("The grammar is not CFG.\n");
                    }
                    break;
                case "7":
                    String sequence = "";
                    System.out.print("sequence = ");
                    try {
                        sequence = reader.readLine();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    String[] split = sequence.split(" ");
                    List<String> values = new ArrayList<>();
                    for(int i = 0; i < split.length; ++i) {
                        if(split[i].charAt(0) != '"') {
                            values.add(split[i]);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(split[i]).append(" ");

                            if(!split[i].endsWith("\"")) {
                                i++;
                                while(i < split.length) {
                                    if (split[i].endsWith("\"")) {
                                        stringBuilder.append(split[i]);
                                        break;
                                    } else {
                                        stringBuilder.append(split[i]).append(" ");
                                    }
                                    i++;
                                }
                            }

                            values.add(stringBuilder.toString());
                        }
                    }

                    DescendentRecursiveParser descendentRecursiveParser = new DescendentRecursiveParser();

                    boolean ok = true;
                    for(String value: values) {
                        if (!descendentRecursiveParser.isIdentifier(value) &&
                                !descendentRecursiveParser.isConstant(value) && !grammar.getTerminals().contains(value)) {
                            ok = false;
                            System.out.println(value);
                            break;
                        }
                    }

                    if(ok) {
                        System.out.println();
                        descendentRecursiveParser.descendantRecursiveParserAlgorithm(values.toArray(new String[0]), grammar);
                        System.out.println();
                    } else {
                        System.out.println("The sequence is not correct!\n");
                    }

                    break;
                case "0":
                    condition = false;
                    break;
                default:
                    System.out.println("This option is not available. Try again.\n");
                    break;
            }
        }
    }
}