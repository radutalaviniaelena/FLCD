import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static List<String> readLinesFromFile(String pathToFile) {
        List<String> lines = new ArrayList<>();
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(pathToFile));

            String line = reader.readLine();
            while(line != null) {
                lines.add(line);
                line = reader.readLine();
            }

            reader.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }

        return lines;
    }

    public static void writeToFiles(String pathToPIF, String pathToSymbolTable,
                                    List<String> symbolTable, List<Pair<String, Integer>> PIF) {
        BufferedWriter writerForSymbolTable;
        BufferedWriter writerForPIF;

        try {
            int position = 0;
            writerForSymbolTable = new BufferedWriter(new FileWriter(pathToSymbolTable));
            writerForSymbolTable.write(String.format("%20s %20s \r\n", "Identifier/Constant", "Position"));
            for(String identifierOrConstant: symbolTable) {
                writerForSymbolTable.write(String.format("%20s %20d \r\n", identifierOrConstant, position));
                position++;
            }
            writerForSymbolTable.close();

            writerForPIF = new BufferedWriter(new FileWriter(pathToPIF));
            writerForPIF.write(String.format("%20s %20s \r\n", "Token", "Position in ST"));
            for(Pair<String, Integer> token: PIF) {
                writerForPIF.write(String.format("%20s %20d \r\n", token.first, token.second));
            }
            writerForPIF.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public static List<String> sortTokens() {
        List<String> tokens = readLinesFromFile("token.in");
        tokens.sort(Comparator.comparing(String::length));
        Collections.reverse(tokens);
        return tokens;
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> tokens = sortTokens();
        String programName = "", filename;

        System.out.println("Which program do you want to scan? (p1/p1-error/p2/p3) ");
        try {
            programName = reader.readLine();
        } catch(IOException exception) {
            exception.printStackTrace();
        }

        switch (programName) {
            case "p1":
                filename = "p1.txt";
                break;
            case "p2":
                filename = "p2.txt";
                break;
            case "p3":
                filename = "p3.txt";
                break;
            case "p1-error":
                filename = "p1-error.txt";
                break;
            default:
                System.out.println("Sorry! This program does not exist! :(");
                return;
        }

        List<String> lines = readLinesFromFile(filename);
        Scanner scanner = new Scanner(tokens);
        try{
            scanner.scan(lines);
            writeToFiles("PIF.out", "ST.out",
                    scanner.getSymbolTable().getIdentifiersAndConstants(), scanner.getPIF());
            System.out.println("\nLexically correct! :)\n");
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
