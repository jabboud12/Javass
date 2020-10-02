package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;;

/**
 * Class containing useful methods to manipulate Strings and Numbers together
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class StringSerializer {
    
    private static int BASE = 16;
    
    // Private constructor
    private StringSerializer() {
    }

    /**
     * Returns the String representation of the unsigned version of the given
     * Integer
     * 
     * @param x
     *            The Integer to put into a String
     * @return The String associated with this Integer
     */
    public static String serializeInt(int x) {
        return Integer.toUnsignedString(x, BASE);
    }

    /**
     * Returns the Integer contained in the String representation given
     * 
     * @param s
     *            the String to transform back to an Integer
     * @return The Integer associated with this String
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, BASE);
    }

    /**
     * Returns the String representation of the unsigned version of the given
     * Long
     * 
     * @param x
     *            The Long to put into a String
     * @return The String associated with this Long
     */
    public static String serializeLong(long x) {
        return Long.toUnsignedString(x, BASE);
    }

    /**
     * Returns the Long contained in the String representation given
     * 
     * @param s
     *            The String to transform back to a Long
     * @return The Long associated with this String
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, BASE);
    }

    /**
     * Returns the String in UTF-8 contained in the String representation given
     * in base64
     * 
     * @param s
     *            The String in base64 to transform to UTF-8
     * @return The UTF-8 String associated with this String
     */
    public static String serializeString(String s) {
        return new String(Base64.getEncoder().encode(s.getBytes(UTF_8)));
    }

    /**
     * Returns the String in base64 contained in the String representation given
     * in UTF-8
     * 
     * @param s
     *            The String in UTF-8 to transform to base64
     * @return The base64 String associated with this String
     */
    public static String deserializeString(String s) {
        return new String(Base64.getDecoder().decode(s.getBytes(UTF_8)));
    }

    /**
     * Returns a single String containing all elements of a table, separated by
     * a given Character
     * 
     * @param d
     *            The Character that will be used as a separator
     * @param s
     *            The String table that will be merged into a single String
     * @return The String associated to the table and the Character given
     */
    public static String combine(Character d, String[] s) {
        return String.join(d.toString(), s);
    }

    /**
     * Returns a table of String containing elements separated from a single
     * String. We know when to stop extracting the substring when we reach a
     * given Character
     * 
     * @param d
     *            The Character that is used as a separator
     * @param s
     *            The String that will be divided into a table
     * @return The table associated to the String and Character given
     */
    public static String[] split(Character d, String s) {
        return s.split(d.toString());
    }

}
