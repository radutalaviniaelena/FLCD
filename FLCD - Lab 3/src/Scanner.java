import java.util.ArrayList;
import java.util.List;

class Pair<T1, T2> {
    public T1 first;
    public T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}

public class Scanner {
    private final List<String> tokens;
    private final SymbolTable symbolTable;
    private int currentLine;
    private final List<Pair<String, Integer>> PIF;

    public Scanner(List<String> tokens) {
        this.tokens = tokens;
        this.symbolTable = new SymbolTable();
        this.currentLine = 0;
        this.PIF = new ArrayList<>();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<Pair<String, Integer>> getPIF() {
        return PIF;
    }

    public void scan(List<String> programLines) throws Exception {
        for(String line: programLines) {
            this.currentLine++;
            getTokensFromLine(line);
        }
    }

    public void getTokensFromLine(String line) throws Exception {
        int index = 0;

        // while the next characters are spaces or tab, we should ignore them
        while(index < line.length() && (line.charAt(index) == ' ' || line.charAt(index) == '\t')) {
            index++;
        }

        while(index < line.length()) {
            // we check if the next token is an identifier or a constant
            boolean validIdentifierOrConstant = false;

            if(line.charAt(index) == '_' && (index + 1) < line.length() && Character.isLetter(line.charAt(index + 1))) {
                index = treatIdentifier(line, index);
                validIdentifierOrConstant = true;
            }

            if(Character.isDigit(line.charAt(index)) ||
                    (line.charAt(index) == '-' &&
                            (index + 1) < line.length() && Character.isDigit(line.charAt(index + 1)))) {
                index = treatIntegerConstant(line, index);
                validIdentifierOrConstant = true;
            }

            if(line.charAt(index) == '"') {
                index = treatStringConstant(line, index);
                validIdentifierOrConstant = true;
            }

            if(line.charAt(index) == '\'') {
                index = treatCharacterConstant(line, index);
                validIdentifierOrConstant = true;
            }

            if(!validIdentifierOrConstant) {
                // if it is not an identifier or a constant, we should check if the next token is from token.in
                // (if it is a reserved word, an operator or a separator)
                boolean validToken = false;

                for(String token: this.tokens) {
                    if(line.startsWith(token, index)) {
                        System.out.println("Valid Token - index: " + index);
                        System.out.println("Valid Token: " + token + "\n");
                        // in case we find a valid token from token.in, we put it in pif with position -1
                        // and jump over the entire token
                        validToken = true;
                        this.PIF.add(new Pair<>(token, -1));
                        index += token.length();
                        break;
                    }
                }

                if(!validToken) {
                    throw new Exception("Lexical error: not a valid token. Line: " + this.currentLine);
                }
            }

            // while the next characters are spaces or tab, we should ignore them
            while(index < line.length() && (line.charAt(index) == ' ' || line.charAt(index) == '\t')) {
                index++;
            }
        }
    }

    private int treatCharacterConstant(String line, int index) throws Exception {
        System.out.println("Character Constant - index: " + index);
        index++;

        if(index < line.length() && !Character.isLetterOrDigit(line.charAt(index))) {
            throw new Exception("Lexical error: characters must be digits or letters. Line: " + this.currentLine);
        }

        char character = 0;
        if(index < line.length() && line.charAt(index) != '\'') {
            character = line.charAt(index);
        }

        if(index == line.length()) {
            throw new Exception("Lexical error: unclosed quotes for characters. Line: " + this.currentLine);
        }

        index++;
        System.out.println("Character Constant: " + character + "\n");
        addToPIFAndST(String.valueOf(character), "const");
        return index;
    }

    private int treatStringConstant(String line, int index) throws Exception {
        System.out.println("String Constant - index: " + index);
        index++;

        boolean ok = true;
        StringBuilder string = new StringBuilder();
        while(index < line.length() && line.charAt(index) != '"') {
            string.append(line.charAt(index));
            if(!Character.isLetterOrDigit(line.charAt(index))) {
                ok = false;
            }
            index++;
        }

        if(!ok) {
            throw new Exception("Lexical error: strings must contain digits and/or letters. Line: " + this.currentLine);
        }

        if(index == line.length()) {
            throw new Exception("Lexical error: unclosed quotes for strings. Line: " + this.currentLine);
        }

        index++;
        System.out.println("String Constant: " + string + "\n");
        addToPIFAndST(string.toString(), "const");
        return index;
    }

    private int treatIntegerConstant(String line, int index) {
        System.out.println("Integer Constant - index: " + index);
        int sign = 1;
        if(line.charAt(index) == '-') {
            sign = -1;
            index++;
        }

        int number = 0;
        while(index < line.length() && Character.isDigit(line.charAt(index))) {
            number = number * 10 + (line.charAt(index) - '0');
            index++;
        }

        number = number * sign;
        System.out.println("Integer Constant: " + number + "\n");
        addToPIFAndST(String.valueOf(number), "const");
        return index;
    }

    private int treatIdentifier(String line, int index) {
        System.out.println("Identifier - index: " + index);
        StringBuilder identifier = new StringBuilder();
        identifier.append(line.charAt(index));

        index++;
        while(index < line.length() && Character.isLetterOrDigit(line.charAt(index))) {
            identifier.append(line.charAt(index));
            index++;
        }

        System.out.println("Identifier: " + identifier + "\n");
        addToPIFAndST(identifier.toString(), "ident");
        return index;
    }

    private void addToPIFAndST(String token, String type) {
        Integer[] positionInST = this.symbolTable.search(token);
        if(positionInST[0] != -1) {
            this.PIF.add(new Pair<>(type, positionInST[0]));
        } else {
            for(Pair<String, Integer> entry: this.PIF) {
                if(entry.second >= positionInST[1])
                    entry.second += 1;
            }
            this.symbolTable.add(token, positionInST[1]);
            this.PIF.add(new Pair<>(type, positionInST[1]));
        }
    }
}
