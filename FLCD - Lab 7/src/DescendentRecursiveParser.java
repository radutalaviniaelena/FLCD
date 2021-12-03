import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DescendentRecursiveParser {

    public DescendentRecursiveParser() {
    }

    public Configuration expand(Configuration configuration, int count, Grammar grammar) {
        configuration.setMove(Move.EXPAND);

        String nonTerminal = configuration.getInputStack().pop();
        String production = grammar.getProductionForNonTerminal(nonTerminal).toArray()[count].toString();

        String[] values = production.split(" ");
        List<String> list = Arrays.asList(values);
        Collections.reverse(list);
        values = list.toArray(values);

        Stack<String> workingStack = configuration.getWorkingStack();
        workingStack.push(nonTerminal + "-" + (count + 1));
        configuration.setWorkingStack(workingStack);

        Stack<String> inputStack = configuration.getInputStack();
        for(String value: values) {
            inputStack.push(value);
        }
        configuration.setInputStack(inputStack);

        return configuration;
    }

    public Configuration advance(Configuration configuration) {
        configuration.setMove(Move.ADVANCE);

        String terminal = configuration.getInputStack().pop();

        if(!terminal.equals("epsilon")) {
            Stack<String> workingStack = configuration.getWorkingStack();
            workingStack.push(terminal);
            configuration.setWorkingStack(workingStack);

            configuration.setPositionCurrentSymbol(configuration.getPositionCurrentSymbol() + 1);
        }

        return configuration;
    }

    public Configuration momentaryInsuccess(Configuration configuration) {
        configuration.setMove(Move.MOMENTARY_INSUCCESS);
        configuration.setStateOfParsing(State.BACK_STATE);
        return configuration;
    }

    public Configuration back(Configuration configuration) {
        configuration.setMove(Move.BACK);

        configuration.setPositionCurrentSymbol(configuration.getPositionCurrentSymbol() - 1);

        String terminal = configuration.getWorkingStack().pop();
        Stack<String> inputStack = configuration.getInputStack();
        inputStack.push(terminal);
        configuration.setInputStack(inputStack);

        return configuration;
    }

    public Configuration anotherTry(Configuration configuration, Grammar grammar) {
        configuration.setMove(Move.ANOTHER_TRY);

        String topWorkingStack = configuration.getWorkingStack().pop();
        String[] values = topWorkingStack.split("-");
        String nonTerminal = values[0];
        int count = Integer.parseInt(values[1]) - 1;

        int maximumNumberOfProductions = grammar.getProductionForNonTerminal(nonTerminal).size();
        if((count + 1) < maximumNumberOfProductions) {
            configuration.setStateOfParsing(State.NORMAL_STATE);

            Stack<String> workingStack = configuration.getWorkingStack();
            workingStack.push(nonTerminal + "-" + (count + 2));
            configuration.setWorkingStack(workingStack);

            Stack<String> inputStack = configuration.getInputStack();
            anotherTry_helper(configuration, grammar, nonTerminal, count, inputStack);

            String production = grammar.getProductionForNonTerminal(nonTerminal).toArray()[count + 1].toString();

            values = production.split(" ");
            List<String> list = Arrays.asList(values);
            Collections.reverse(list);
            values = list.toArray(values);

            inputStack = configuration.getInputStack();
            for(String value: values) {
                inputStack.push(value);
            }
            configuration.setInputStack(inputStack);
        } else if(configuration.getPositionCurrentSymbol() == 1 && nonTerminal.equals(grammar.getStartSymbol())) {
            configuration.setStateOfParsing(State.ERROR_STATE);
        } else {
            Stack<String> inputStack = configuration.getInputStack();

            anotherTry_helper(configuration, grammar, nonTerminal, count, inputStack);

            inputStack.push(nonTerminal);
            configuration.setInputStack(inputStack);
        }

        return configuration;
    }

    private void anotherTry_helper(Configuration configuration, Grammar grammar, String nonTerminal, int count, Stack<String> inputStack) {
        if(!grammar.getProductionForNonTerminal(nonTerminal).toArray()[count].equals("epsilon")) {
            int sizeOfPreviousProduction = (int) Arrays.stream(grammar.getProductionForNonTerminal(nonTerminal)
                            .toArray()[count]
                            .toString()
                            .split(" "))
                    .count();
            while(sizeOfPreviousProduction > 0) {
                inputStack.pop();
                sizeOfPreviousProduction--;
            }
            configuration.setInputStack(inputStack);
        }
    }

    public Configuration success(Configuration configuration) {
        configuration.setMove(Move.SUCCESS);

        configuration.setStateOfParsing(State.FINAL_STATE);
        return configuration;
    }

    public void descendantRecursiveParserAlgorithm(String[] sequence, Grammar grammar) {
        List<Configuration> configurations = new ArrayList<>();
        try {
            Stack<String> inputStack = new Stack<>();
            inputStack.push(grammar.getStartSymbol());
            Configuration configuration = new Configuration(Move.EXPAND, State.NORMAL_STATE, 1, new Stack<>(), inputStack);

            constructWorkingAndInputStacks(configurations, configuration);

            while (!configuration.getStateOfParsing().equals(State.ERROR_STATE) &&
                    !configuration.getStateOfParsing().equals(State.FINAL_STATE)) {

                if (configuration.getStateOfParsing().equals(State.NORMAL_STATE)) {
                    if (configuration.getPositionCurrentSymbol() == (sequence.length + 1) && configuration.getInputStack().isEmpty()) {
                        configuration = this.success(configuration);
                    } else {
                        if (grammar.getNonTerminals().contains(configuration.getInputStack().peek())) {
                            configuration = this.expand(configuration, 0, grammar);
                        } else {
                            if (verifyAdvance(sequence, grammar, configuration)) {
                                configuration = this.advance(configuration);
                            } else {
                                configuration = this.momentaryInsuccess(configuration);
                            }
                        }
                    }

                    constructWorkingAndInputStacks(configurations, configuration);
                } else {
                    if (configuration.getStateOfParsing().equals(State.BACK_STATE)) {
                        if (grammar.getTerminals().contains(configuration.getWorkingStack().peek())) {
                            configuration = this.back(configuration);
                        } else {
                            configuration = this.anotherTry(configuration, grammar);
                        }

                        constructWorkingAndInputStacks(configurations, configuration);
                    }
                }
            }

            writeConfigurationsToFile("src/configurations.txt", configurations);

            if (configuration.getStateOfParsing().equals(State.ERROR_STATE)) {
                System.out.println("Error");
            } else {
                System.out.println("Sequence accepted");

                List<String> finalNonTerminals = selectFinalNonTerminals(configurations.get(configurations.size() - 1), grammar);
                writeTableToFile("src/table.txt", constructTree(finalNonTerminals, grammar));
            }
        } catch(Exception e) {
            writeConfigurationsToFile("src/configurations.txt", configurations);

            e.printStackTrace();
        }
    }

    public List<String> selectFinalNonTerminals(Configuration successConfiguration, Grammar grammar) {
        Stack<String> workingStack = successConfiguration.getWorkingStack();

        List<String> workingList = new ArrayList<>();
        while(!workingStack.isEmpty()) {
            String value = workingStack.pop();

            if(!value.equals("constant") && !value.equals("identifier") && !grammar.getTerminals().contains(value)) {
                workingList.add(value);
            }
        }

        Collections.reverse(workingList);

        return workingList;
    }

    public List<Node> constructTree(List<String> workingList, Grammar grammar) {
        int index = 0;
        List<Node> tree = new ArrayList<>();

        String[] values = workingList.get(index).split("-");
        String nonTerminal = values[index];

        Node startNode = new Node(0, nonTerminal, -1, -1);
        tree.add(startNode);

        Map<String, Queue<Integer>> parentIndexes = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(index);
        parentIndexes.put(nonTerminal, queue);

        for (String element : workingList) {
            values = element.split("-");
            nonTerminal = values[0];
            int count = Integer.parseInt(values[1]) - 1;

            String productionForNonTerminal = grammar.getProductionForNonTerminal(nonTerminal).toArray()[count].toString();
            String[] productionElements = productionForNonTerminal.split(" ");

            index++;
            Node node = new Node(index, productionElements[0], parentIndexes.get(nonTerminal).peek(), -1);
            tree.add(node);

            if (grammar.getNonTerminals().contains(productionElements[0])) {
                if (!parentIndexes.containsKey(productionElements[0])) {
                    queue = new LinkedList<>();
                } else {
                    queue = parentIndexes.get(productionElements[0]);
                }
                queue.add(index);
                parentIndexes.put(productionElements[0], queue);
            }

            for (int j = 1; j < productionElements.length; ++j) {
                index++;
                node = new Node(index, productionElements[j], parentIndexes.get(nonTerminal).peek(), index - 1);
                tree.add(node);

                if (grammar.getNonTerminals().contains(productionElements[j])) {
                    if (!parentIndexes.containsKey(productionElements[j])) {
                        queue = new LinkedList<>();
                    } else {
                        queue = parentIndexes.get(productionElements[j]);
                    }
                    queue.add(index);
                    parentIndexes.put(productionElements[j], queue);
                }
            }

            parentIndexes.get(nonTerminal).remove();
        }

        return tree;
    }

    public void writeTableToFile(String path, List<Node> tree) {
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(String.format("%40s %40s %40s %40s \r\n", "Index", "Info", "Parent", "Left Sibling"));
            for(Node node: tree) {
                writer.write(String.format("%40d %40s %40d %40d \r\n", node.getIndex(), node.getInfo(),
                        node.getParent(), node.getLeftSibling()));
            }
            writer.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public void writeConfigurationsToFile(String path, List<Configuration> configurations) {
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(String.format("%20s \r\n", "Configuration"));
            for(Configuration configuration: configurations) {
                writer.write(String.format("%20s \r\n", configuration));
            }
            writer.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    private boolean verifyAdvance(String[] sequence, Grammar grammar, Configuration configuration) {
        if(sequence.length > (configuration.getPositionCurrentSymbol() - 1)) {
            if((isIdentifier(sequence[configuration.getPositionCurrentSymbol() - 1]) && configuration.getInputStack().peek().equals("identifier")) ||
                    (isConstant(sequence[configuration.getPositionCurrentSymbol() - 1]) && configuration.getInputStack().peek().equals("constant")) ||
                    (grammar.getTerminals().contains(configuration.getInputStack().peek()) &&
                            sequence[configuration.getPositionCurrentSymbol() - 1].equals(configuration.getInputStack().peek()))) {

                return true;
            }
        }

        return configuration.getInputStack().peek().equals("epsilon");
    }

    public boolean isIdentifier(String identifier) {
        return identifier.charAt(0) == '_' && identifier.length() >= 2 && Character.isLetter(identifier.charAt(1));
    }

    public boolean isConstant(String constant) {
        if(constant.charAt(0) == '"' || constant.charAt(0) == '\'') {
            return true;
        } else {
            if(Character.isDigit(constant.charAt(0))) {
                return true;
            }

            if(constant.charAt(0) == '-' || constant.charAt(0) == '+') {
                return constant.length() >= 2 && Character.isDigit(constant.charAt(1));
            }
        }

        return false;
    }

    private void constructWorkingAndInputStacks(List<Configuration> configurations, Configuration configuration) {
        Stack<String> wS = new Stack<>();
        for(String e: configuration.getWorkingStack()) {
            wS.push(e);
        }

        Stack<String> iS = new Stack<>();
        for(String e: configuration.getInputStack()) {
            iS.push(e);
        }

        configurations.add(new Configuration(configuration.getMove(), configuration.getStateOfParsing(),
                configuration.getPositionCurrentSymbol(), wS, iS));
    }
}
