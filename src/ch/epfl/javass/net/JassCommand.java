package ch.epfl.javass.net;

/**
 * Public Enum representing the commands exchanged between a client and the
 * server
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public enum JassCommand {
    PLRS("PLRS"),
    TRMP("TRMP"),
    HAND("HAND"),
    TRCK("TRCK"),
    CARD("CARD"),
    SCOR("SCOR"),
    WINR("WINR");

    // Empty constructor
    JassCommand(String command) {
    }

    @Override
    public String toString() {
        return this.name();
    }

}
