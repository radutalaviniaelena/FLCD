import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Pair<T1> {
    public T1 firstState;
    public T1 secondState;
    public T1 value;

    public Pair(T1 firstState, T1 secondState, T1 value) {
        this.firstState = firstState;
        this.secondState = secondState;
        this.value = value;
    }

    @Override
    public String toString() {
        return firstState + " -> " + secondState + " (" + value + ")";
    }
}

public class Main {
    private static Set<String> states;
    private static Set<String> alphabet;
    private static Set<String> finalStates;
    private static List<Pair<String>> transitions;
    private static String initialState;

    private static int getNumber(BufferedReader reader, int number) throws IOException {
        String value;
        value = reader.readLine();
        for (int i = 0; i < value.length(); ++i) {
            number = number * 10 + (value.charAt(i) - '0');
        }
        return number;
    }

    private static void extractFromFile(BufferedReader reader, int number, Set<String> set) throws IOException {
        number = getNumber(reader, number);
        set.addAll(Arrays.asList(reader.readLine().split(" ")).subList(0, number));
    }

    public static void readFromFile(String filePath) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(filePath));

            int numberOfStates = 0, numberOfFinalStates = 0, numberOfTransitions = 0;

            extractFromFile(reader, numberOfStates, states);
            initialState = reader.readLine();
            extractFromFile(reader, numberOfFinalStates, finalStates);

            numberOfTransitions = getNumber(reader, numberOfTransitions);
            String[] values;
            for(int i = 0; i < numberOfTransitions; ++i) {
                values = reader.readLine().split(" ");
                Pair<String> pair = new Pair<>(values[0], values[1], values[2]);
                transitions.add(pair);
                alphabet.add(values[2]);
            }
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static String getSecondStateBy(String firstState, String value) {
        String secondState = "";

        for (Pair<String> transition : transitions) {
            if (transition.firstState.equals(firstState) && transition.value.equals(value)) {
                secondState = transition.secondState;
                break;
            }
        }

        return secondState;
    }

    public static boolean verifyIfDeterministicOrNot() {
        for(String state: states) {
           List<String> values = new ArrayList<>();

            for(Pair<String> transition: transitions) {
                if(transition.firstState.equals(state)) {
                    values.add(transition.value);
                }
            }

            for(int i = 0; i < (values.size() - 1); ++i) {
                for(int j = (i + 1); j < values.size(); ++j) {
                    if(values.get(i).equals(values.get(j))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean verifySequence(String[] sequence) {
        String currentState = initialState;

        for (String s : sequence) {
            String state = getSecondStateBy(currentState, s);
            if (state.equals("")) {
                return false;
            } else {
                currentState = state;
            }
        }

        boolean verify = false;
        for(String finalState: finalStates) {
            if (finalState.equals(currentState)) {
                verify = true;
                break;
            }
        }

        return verify;
    }

    public static String showMenu() {
        return "1. Show the set of states.\n" +
                "2. Show the alphabet.\n" +
                "3. Show the transitions.\n" +
                "4. Show the initial state.\n" +
                "5. Show the set of final states.\n" +
                "6. Verify if the FA is deterministic or not.\n" +
                "7. Given if a given sequence is accepted by the FA.\n" +
                "0. Exit.\n";
    }

    public static void main(String[] args) {
        states = new HashSet<>();
        alphabet = new HashSet<>();
        finalStates = new HashSet<>();
        transitions = new ArrayList<>();
        readFromFile("src/FA.in");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        boolean condition = true;
        String choice = "";
        while(condition) {
            System.out.println(showMenu());

            System.out.print("choice = ");
            try {
                choice = reader.readLine();
            } catch(IOException ioException) {
                ioException.printStackTrace();
            }

            switch (choice) {
                case "1":
                    System.out.println(states + "\n");
                    break;
                case "2":
                    System.out.println(alphabet + "\n");
                    break;
                case "3":
                    System.out.println(transitions + "\n");
                    break;
                case "4":
                    System.out.println(initialState + "\n");
                    break;
                case "5":
                    System.out.println(finalStates + "\n");
                    break;
                case "6":
                    boolean isDeterministic = verifyIfDeterministicOrNot();
                    if(isDeterministic) {
                        System.out.println("The FA is deterministic.\n");
                    } else {
                        System.out.println("The FA is not deterministic.\n");
                    }
                    break;
                case "7":
                    String sequence = "";
                    System.out.print("sequence = ");
                    try {
                        sequence = reader.readLine();
                    } catch(IOException ioException) {
                        ioException.printStackTrace();
                    }

                    isDeterministic = verifyIfDeterministicOrNot();
                    if(isDeterministic) {
                        boolean verify = verifySequence(sequence.split(" "));
                        if(verify) {
                            System.out.println("The given sequence is accepted by the FA.\n");
                        } else {
                            System.out.println("The given sequence is not accepted by the FA.\n");
                        }
                    } else {
                        System.out.println("The FA is not deterministic.\n");
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
