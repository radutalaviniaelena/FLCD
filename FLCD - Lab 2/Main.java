public class Main {
    public static void main(String[] args) {
        /*
        main () {
        _first, _second: Integer;
        read >> _first >> _second;
        _maxim: Integer = -1;

        if (_first > _second) {
            _maxim = _first;
        } else {
            _maxim = _second;
        }

        write << _maxim;
    }
         */

        SymbolTable symbolTable = new SymbolTable();

        symbolTable.add("_first");
        symbolTable.add("_second");
        symbolTable.add("_maxim");
        symbolTable.add("-1");

        System.out.println("The identifiers and constants are: " + symbolTable.getIdentifiersAndConstants() + "\n");

        Integer[] existentIdentifier = symbolTable.search("_second");
        System.out.println("The identifier \"_second\" exists has the index " + existentIdentifier[0] + "\n");

        Integer[] nonexistentIdentifier = symbolTable.search("_a");
        System.out.println("The identifier \"_a\" does not exist and " +
                "it can be added at position " + nonexistentIdentifier[1] + "\n");

        symbolTable.add("_a");
        System.out.println("The symbol table after adding the identifier \"_a\": "
                + symbolTable.getIdentifiersAndConstants() + "\n");

        System.out.println("The array has 5 elements; if we want to modify an item at position >= 5 " +
                "(symbolTable.update(6, \"_A\")) we receive: " + symbolTable.update(6, "_A") + "\n");

        symbolTable.update(1, "_A");
        System.out.println("We modifies the identifier from position 1 into \"_A\". " +
                "The array after modification is: " + symbolTable.getIdentifiersAndConstants() + "\n");

        String deletedItem = symbolTable.deleteItemFromIndex(1);
        System.out.println("The identifier from position 1 was deleted: " + deletedItem + "\n");
        System.out.println("The array after modification is: " + symbolTable.getIdentifiersAndConstants() + "\n");

        System.out.println("The identifier \"_first\" was deleted: " + symbolTable.deleteItem("_first") + "\n");
        System.out.println("The array after modification is: " + symbolTable.getIdentifiersAndConstants() + "\n");

        System.out.println("The identifier \"_fourth\" does not exist, so it cannot be deleted: " +
                symbolTable.deleteItem("_fourth") + "\n");
    }
}
