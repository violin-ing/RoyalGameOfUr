import java.util.Random;

public class Dice {
    private Random random = new Random();

    //Faithful recreation of the dice roll mechanic, rolls 4 dice (with 50/50 odds to land on a point or not, for each point landed on, 1 movement point is added)
    public int roll() {
        int totalPoints = 0;
        
        for (int i = 0; i < 4; i++) {
            if (random.nextDouble() < 0.5) {totalPoints++;}
        }
        
        return totalPoints;
    }
}