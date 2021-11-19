import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void showMenu() {
        System.out.println("1. Print set of nonTerminals.\n");
        System.out.println("2. Print set of terminals.\n");
        System.out.println("3. Print set of productions.\n");
        System.out.println("4. Print set of productions for a given nonTerminal.\n");
        System.out.println("5. Print start symbol.\n");
        System.out.println("6. Verify if CFG.\n");
        System.out.println("0. Exit.\n");
    }

    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        grammar.readGrammarFromFile("src/g1.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean condition = true;
        String choice = "";

        System.out.println(grammar.toString());

      /*  while(condition) {
            showMenu();

            System.out.print("choice = ");
            try {
                choice = reader.readLine();
            } catch(IOException ioException) {
                ioException.printStackTrace();
            }

            switch (choice) {
                case "1":

            }

        }*/
    }
}
