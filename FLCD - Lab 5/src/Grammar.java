import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {
    private final Set<String> terminals;
    private final Set<String> nonTerminals;
    private final Map<String, Set<String>> productions;
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

    public Set<String> getTerminals() {
        return terminals;
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public Map<String, Set<String>> getProductions() {
        return productions;
    }

    private int getNumber(BufferedReader reader, int number) throws IOException {
        String value;
        value = reader.readLine();
        for (int i = 0; i < value.length(); ++i) {
            number = number * 10 + (value.charAt(i) - '0');
        }
        return number;
    }

    public void readGrammarFromFile(String filePath) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(filePath));

            int numberOfTerminals = 0, numberOfNonTerminals = 0, numberOfProductions = 0;

            numberOfNonTerminals = getNumber(reader, numberOfNonTerminals);
            for(int i = 0; i < numberOfNonTerminals; ++i) {
                this.nonTerminals.add(reader.readLine());
            }

            numberOfTerminals = getNumber(reader, numberOfTerminals);
            for(int i = 0; i < numberOfTerminals; ++i) {
                this.terminals.add(reader.readLine());
            }

            numberOfProductions = getNumber(reader, numberOfProductions);
            String[] values;
            for (int i = 0; i < numberOfProductions; ++i) {
                values = reader.readLine().split(" -> ");
                String key = values[0], value = values[1];

                if(!this.nonTerminals.contains(key)) {
                    this.isCFG = false;
                }

                if(! this.productions.containsKey(key)) {
                    this.productions.put(key, new HashSet<>());
                }
                this.productions.get(key).add(value);
            }

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
        stringBuilder.append("\n");

        for(Map.Entry<String, Set<String>> entry: this.productions.entrySet()) {
            stringBuilder.append(entry.getKey()).append(" -> ");

            int i = 0;
            for(String value: entry.getValue()) {
                stringBuilder.append(value);
                if(i < (entry.getValue().size() - 1)) {
                    stringBuilder.append(" | ");
                    i++;
                }
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public String printProductionsForNonTerminal(String nonTerminal) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        int i = 0;
        Set<String> values = this.getProductionForNonTerminal(nonTerminal);
        for(String value: values) {
            stringBuilder.append(value);
            if(i < (values.size() - 1)) {
                stringBuilder.append(" | ");
                i++;
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "terminals = " + terminals +
                ", nonTerminals = " + nonTerminals +
                ", productions = " + printProductions() +
                ", startSymbol = '" + startSymbol + '\'' +
                ", isCFG = " + isCFG +
                '}';
    }
}
