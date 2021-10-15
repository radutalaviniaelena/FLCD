import java.util.ArrayList;
import java.util.List;

public class SymbolTable implements ISymbolTable {
    private List<String> identifiersAndConstants;

    public SymbolTable() {
        this.identifiersAndConstants = new ArrayList<>();
    }

    public SymbolTable(List<String> identifiersAndConstants) {
        this.identifiersAndConstants = identifiersAndConstants;
    }

    public List<String> getIdentifiersAndConstants() {
        return identifiersAndConstants;
    }

    public void setIdentifiersAndConstants(List<String> identifiersAndConstants) {
        this.identifiersAndConstants = identifiersAndConstants;
    }

    @Override
    public void add(String item, int index) {
        this.identifiersAndConstants.add(index, item);
    }

    @Override
    public int add(String item) {
        Integer[] verifyIfExist = search(item);

        if(verifyIfExist[0] == -1) {
            this.identifiersAndConstants.add(verifyIfExist[1], item);
            return verifyIfExist[1];
        } else {
            return -1;
        }
    }

    @Override
    public Integer[] search(String item) {
        int left = 0, right = this.identifiersAndConstants.size() - 1;
        Integer[] returned = new Integer[2];

        while(left <= right) {
            int average = (left + right) / 2;
            String element = this.identifiersAndConstants.get(average);

            if(element.equals(item)) {
                returned[0] = average;
                returned[1] = -1;

                return returned;
            } else {
                if(item.compareTo(element) < 0) {
                    right = average - 1;
                } else {
                    left = average + 1;
                }
            }
        }

        returned[0] = -1;
        returned[1] = left;
        return returned;
    }

    @Override
    public String deleteItemFromIndex(int index) {
        if(index < 0 || index >= this.identifiersAndConstants.size()) {
            return "";
        }

        return this.identifiersAndConstants.remove(index);
    }

    @Override
    public int deleteItem(String item) {
        int index = this.identifiersAndConstants.indexOf(item);
        this.identifiersAndConstants.remove(item);

        return index;
    }

    @Override
    public boolean update(int index, String newItem) {
        if(index < 0 || index >= this.identifiersAndConstants.size()) {
            return false;
        } else {
            this.identifiersAndConstants.set(index, newItem);
            return true;
        }
    }
}
