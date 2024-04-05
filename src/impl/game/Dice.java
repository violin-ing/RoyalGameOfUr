import java.util.Random;
/**
 * Dice class handles random dice rolls
 */
public class Dice {
    private Random random = new Random();

    /**
     * generates a random number for a roll between 0-4
     * @return integer number for roll amount (0-4)
     */
    public int roll() {
        int totalPoints = 0;
        
        for (int i = 0; i < 4; i++) {
            if (random.nextDouble() < 0.5) {totalPoints++;}
        }
        
        return totalPoints;
    }
}