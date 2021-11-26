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

/*            int sizeOfPreviousProduction = (int) Arrays.stream(grammar.getProductionForNonTerminal(nonTerminal)
                                                                      .toArray()[count]
                                                                      .toString()
                                                                      .split(" "))
                                                       .count();
            Stack<String> inputStack = configuration.getInputStack();
            while(sizeOfPreviousProduction > 0) {
                inputStack.pop();
                sizeOfPreviousProduction--;
            }
            configuration.setInputStack(inputStack);*/
            Stack<String> inputStack = configuration.getInputStack();
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

            inputStack.push(nonTerminal);
            configuration.setInputStack(inputStack);
        }

        return configuration;
    }

    public Configuration success(Configuration configuration) {
        configuration.setMove(Move.SUCCESS);

        configuration.setStateOfParsing(State.FINAL_STATE);
        return configuration;
    }

    public void descendantRecursiveParserAlgorithm(String[] sequence, Grammar grammar) {
        List<Configuration> configurations = new ArrayList<>();
        try {
            /*List<Configuration> configurations = new ArrayList<>();*/

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
                            if ((grammar.getTerminals().contains(configuration.getInputStack().peek()) &&
                                    sequence.length > (configuration.getPositionCurrentSymbol() - 1) &&
                                    sequence[configuration.getPositionCurrentSymbol() - 1].equals(configuration.getInputStack().peek()))
                                    || configuration.getInputStack().peek().equals("epsilon")) {
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

            for (Configuration c : configurations) {
                System.out.println(c);
            }

            if (configuration.getStateOfParsing().equals(State.ERROR_STATE)) {
                System.out.println("Error");
            } else {
                System.out.println("Sequence accepted");
            }
        } catch(Exception e) {
            for (Configuration c : configurations) {
                System.out.println(c);
            }

            e.printStackTrace();
        }
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
