import ch.epfl.javass.jass.Trick;

/*
 * Author :   Joseph E. Abboud.
 * Date   :   8 Mar 2019
 */

public class MainClass {
    public static void main(String[] args) {
        // long i = 0;
        // long j =
        // 0b100000000_0000000_000010000_0000000_110010010_0000000_001000011L;
        int k = 0b11_11_1000_111111_111111_110001_110011;
        System.out.println(Trick.ofPacked(k).player(0));
    }

}
