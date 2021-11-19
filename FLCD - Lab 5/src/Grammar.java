import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {
    private Set<String> terminals;
    private Set<String> nonTerminals;
    private Map<String, Set<String>> productions;
    private String startSymbol;
    private boolean isCFG;

    public Grammar() {
        this.terminals = new HashSet<>();
        this.nonTerminals = new HashSet<>();
        this.productions = new HashMap<>();
        this.startSymbol = "";
        this.isCFG = true;
    }

    public boolean isCFG() {
        return isCFG;
    }

    private int getNumber(BufferedReader reader, int number) throws IOException {
        String value;
        value = reader.readLine();
        for (int i = 0; i < value.length(); ++i) {
            number = number * 10 + (value.charAt(i) - '0');
        }
        return number;
    }

    private void extractFromFile(BufferedReader reader, int number, Set<String> set) throws IOException {
        number = getNumber(reader, number);
        /*set.addAll(Arrays.asList(reader.readLine().split(" ")).subList(0, number));*/
        String[] values = reader.readLine().split(" ");
        for(String value: values) {
            set.add(value);
        }
    }

    public void readGrammarFromFile(String filePath) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(filePath));

            int numberOfTerminals = 0, numberOfNonTerminals = 0, numberOfProductions = 0;

            extractFromFile(reader, numberOfNonTerminals, this.nonTerminals);
            extractFromFile(reader, numberOfTerminals, this.terminals);

            numberOfProductions = getNumber(reader, numberOfProductions);
            String[] values;
            for (int i = 0; i < numberOfProductions; ++i) {
                values = reader.readLine().split(" -> ");
                String key = values[0], value = values[1];

                if(key.length() != 1 || !this.nonTerminals.contains(key)) {
                    this.isCFG = false;
                }

                if(! this.productions.containsKey(key)) {
                    this.productions.put(key, new HashSet<>());
                }
                this.productions.get(key).add(value);
            }

            System.out.println(this.productions);

            this.startSymbol = reader.readLine();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public Set<String> getProductionForNonTerminal(String nonTerminal) {
        return this.productions.get(nonTerminal);
    }

    public String printProductions() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Map.Entry<String, Set<String>> entry: this.productions.entrySet()) {
            stringBuilder.append(entry.getKey()).append(" -> ");

            int i = 0;
            for(String value: entry.getValue()) {
                stringBuilder.append(value);
                if(i < (value.length() - 1)) {
                    stringBuilder.append(" | ");
                    i++;
                }
            }

            stringBuilder.append(", ");
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "terminals=" + terminals +
                ", nonTerminals=" + nonTerminals +
                ", productions=" + printProductions() +
                ", startSymbol='" + startSymbol + '\'' +
                ", isCFG=" + isCFG +
                '}';
    }
}
