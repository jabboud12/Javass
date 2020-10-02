package ch.epfl.javass.bits;

import static ch.epfl.javass.Preconditions.checkArgument;

/**
 * Useful methods to manipulate 32 bits integers
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class Bits32 {

    private Bits32() {
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
     * @return (Integer) bit-string consisting of a 1-bit sequences going from
     *         index start (included) to start+size (excluded)
     */
    public static int mask(int start, int size) {
        checkArgument(start >= 0 && start <= Integer.SIZE && size >= 0
                && size <= Integer.SIZE && start + size <= Integer.SIZE);

        if (size == Integer.SIZE)
            return -1;

        return ((1 << size) - 1) << start;
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
    public static int extract(int bits, int start, int size) {
        checkArgument(start >= 0 && start <= Integer.SIZE && size >= 0
                && size <= Integer.SIZE && start + size <= Integer.SIZE);

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
    public static int pack(int v1, int s1, int v2, int s2) {
        checkArgument(s1 + s2 <= Integer.SIZE && packCheck(v1, s1)
                && packCheck(v2, s2));

        return (v2 << s1) | v1;
    }

    /**
     * Concatenates a single bit-string from three different bit-strings
     * 
     * @param (v1)
     *            bit-string that will occupy the Least Significant Bits
     * @param (s1)
     *            size of the bit-string v1
     * @param (v2)
     *            bit-string that will occupy the bits between v1 and v3
     * @param (s2)
     *            s2 size of the bit-string v2
     * @param (v3)
     *            v3 bit-string that will occupy the Most Significant Bits
     * @param (s3)
     *            s3 size of the bit-string v3
     * @throws IllegalArgumentException
     *             if one argument is not valid
     * @return a bit string consisting of v1 v2 v3 as bits (respectively Least
     *         to Most Significant bits)
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        checkArgument(s1 + s2 + s3 <= Integer.SIZE && packCheck(v1, s1)
                && packCheck(v2, s2) && packCheck(v3, s3));

        return (v3 << s2 + s1) | (v2 << s1) | v1;
    }

    /**
     * Concatenates a single bit-string from seven different bit-strings
     *
     * @param (v1)
     *            bit-string that will occupy the Least Significant Bits
     * @param (s1)
     *            size of the bit-string v1
     * @param (v2)
     *            bit-string that will be concatenated with v1
     * @param (s2)
     *            size of the bit-string v2
     * @param (v3)
     *            bit-string that will be concatenated with v2
     * @param (s3)
     *            size of the bit-string v3
     * @param (v4)
     *            bit-string that will be concatenated with v3
     * @param (s4)
     *            size of the bit-string v4
     * @param (v5)
     *            bit-string that will be concatenated with v4
     * @param (s5)
     *            size of the bit-string v5
     * @param (v6)
     *            bit-string that will be concatenated with v5
     * @param (s6)
     *            size of the bit-string v6
     * @param (v7)
     *            bit-string that will occupy the Most Significant Bits
     * @param (s7)
     *            size of the bit-string v7
     * @throws IllegalArgumentException
     *             if one argument is not valid
     * @return (Integer) bit string consisting of v1 v2 v3 v4 v5 v6 v7 as bits
     *         (respectively Least to Most Significant bits)
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        checkArgument(s1 + s2 + s3 + s4 + s5 + s6 + s7 <= Integer.SIZE
                && packCheck(v1, s1) && packCheck(v2, s2) && packCheck(v3, s3)
                && packCheck(v4, s4) && packCheck(v5, s5) && packCheck(v6, s6)
                && packCheck(v7, s7));

        return (v7 << s6 + s5 + s4 + s3 + s2 + s1)
                | (v6 << s5 + s4 + s3 + s2 + s1) | (v5 << s4 + s3 + s2 + s1)
                | (v4 << s3 + s2 + s1) | (v3 << s2 + s1) | (v2 << s1) | v1;
    }

    /*
     * Checks if we can add an integer depending on its given size (size must be
     * positive, smaller than 32, and bigger or equal to the integer's bit
     * sequence size
     */
    private static boolean packCheck(int v, int s) {
        return (s > 0 && s < Integer.SIZE && v >> s == 0);
    }

}
