package ch.epfl.javass;

/**
 * Useful methods which throw common exceptions encountered
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Throws an IllegalArgumentException if the argument is not valid
     * 
     * @param (b)
     *            the parameter to test
     * @throws IllegalArgumentException
     *             if b is false
     */
    public static void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();
    }

    /**
     * Throws an IndexOutOfBoundsException if the arguments are not valid
     * 
     * @param (index)
     *            the integer to test
     * @param (size)
     *            the maximum size of the index
     * @throws IndexOutOfBoundsException
     *             if index is negative or higher than size
     * @return the index if it is valid
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return index;
    }

    /**
     * Throws an IllegalStateException if the state is not valid
     * 
     * @param (b)
     *            the parameter to test
     * @throws IllegalStateException
     *             if b is false
     */
    public static void checkState(boolean b) {
        if (!b)
            throw new IllegalStateException();
    }
}