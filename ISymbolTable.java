public interface ISymbolTable {
    /**
     * This function adds a given item to be list of identifiers and constants.
     * @param item : an identifier/a constant of type String
     * @return : -1 if the given item already exists in the list
     *           or the position in which it was added if it does not exist
     */
    int add(String item);

    /**
     * This function adds a given item to be list of identifiers and constants on a given position.
     * @param item : an identifier/a constant of type String
     * @param index : an integer value, representing the position in which the given item will be added
     */
    void add(String item, int index);

    /**
     * This function searches for a given item in the list of identifiers and constants. This is a binary search because
     * the list is alphabetically ordered.
     * @param item : an identifier/a constant of type String
     * @return : an array of two integers:
     *                  1) first integer - the position of the item if it exists/-1 if it does not exist;
     *                  2) second integer - -1 if the item exists/left variable if it does not exist (because left
     *                                                            represents the position where it will be added)
     */
    Integer[] search(String item);

    /**
     * This functions deletes the item from the given position from the list of identifiers and constants.
     * @param index : an integer value, representing the index of the item to be deleted
     * @return : "" if index is out of range or the item which was deleted
     */
    String deleteItemFromIndex(int index);

    /**
     * This function deletes a given item from the list of identifiers and constants.
     * @param item : an identifier/a constant of type String
     * @return : the position of the item in the list; -1, if the item does not exist in the list
     */
    int deleteItem(String item);

    /**
     * This function updates the item from a given position.
     * @param index : an integer value, representing the index of the item to be updated
     * @param newItem : the new identifier/constant of type String
     * @return : false if index is out of range; true if it successfully updates the item
     */
    boolean update(int index, String newItem);
}
