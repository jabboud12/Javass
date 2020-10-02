package ch.epfl.javass.bits;

import static ch.epfl.javass.Preconditions.checkArgument;

/**
 * Useful methods to manipulate 64 bits integers
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class Bits64 {

    private Bits64() {
    }

    /**
     * Constructs a bit-string consisting of one's from a certain index to
     * another, and sets the remaining bits to 0
     *
     * @param (start)
     *            index of first digit to consider
     * @param (size)
     *            size of the bit-string
     * @throws IllegalArgumentException
     *             if start and/or size indices are not valid
     * @return bit-string consisting of a 1-bit sequences going from index start
     *         (included) to start+size (excluded)
     */
    public static long mask(int start, int size) {
        checkArgument(start >= 0 && start <= Long.SIZE && size >= 0
                && size <= Long.SIZE && start + size <= Long.SIZE);

        if (size == Long.SIZE)
            return -1;

        return ((1L << size) - 1L) << start;
    }

    /**
     * Constructs a bit-string whose first (Least Significant) bits are similar
     * to a certain part of the bit-string given as a parameter
     *
     * @param (bits)
     *            initial bit string
     * @param (start)
     *            index of first bit to consider
     * @param (size)
     *            size of the bit-string
     * @throws IllegalArgumentException
     *             if start and/or size indices are not valid
     * @return bit-string whose Least Significant Bits correspond to the bits of
     *         the bit-string "bits" going from index start (included) to index
     *         start+size (excluded)
     */
    public static long extract(long bits, int start, int size) {
        checkArgument(start >= 0 && start <= Long.SIZE && size >= 0
                && size <= Long.SIZE && start + size <= Long.SIZE);

        return (bits >> start) & mask(0, size);
    }

    /**
     * Concatenates a single bit-string from two different bit-strings
     *
     * @param (v1)
     *            bit-string that will occupy the Least Significant Bits
     * @param (s1)
     *            size of the bit-string v1
     * @param (v2)
     *            bit-string that will be concatenated with v1 whilst occupying
     *            more important bits (v2 to the left of v1)
     * @param (s2)
     *            size of the bit-string v2
     * @throws IllegalArgumentException
     *             if one argument is not valid
     * @return a bit string consisting of v1 as Least significant bits and v2 as
     *         most significant bits
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        checkArgument(
                s1 + s2 <= Long.SIZE && packCheck(v1, s1) && packCheck(v2, s2));

        return (v2 << s1) | v1;
    }

    /*
     * Checks if we can add a long, depending on its given size (size must be
     * positive, smaller than 64, and bigger or equal to the long's bit sequence
     * size
     */
    private static boolean packCheck(long v, int s) {
        return (s > 0 && s < Long.SIZE && v >> s == 0);
    }
}
