import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

                    String[] values = sequence.split(" ");
                    boolean ok = true;
                    for(String value: values) {
                        if (!grammar.getTerminals().contains(value)) {
                            ok = false;
                            System.out.println(value);
                            break;
                        }
                    }

                    if(ok) {
                        DescendentRecursiveParser descendentRecursiveParser = new DescendentRecursiveParser();
                        System.out.println();
                        descendentRecursiveParser.descendantRecursiveParserAlgorithm(values, grammar);
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