public class Chip {
    private String ownership;
    private int amount;

    public Chip() {
        this.ownership = "none";
        this.amount = 0;
    }

    public void setOwnership(String player) {
        switch (player) {
            case "P1": this.ownership = "P1"; break;
            case "P2": this.ownership = "P2"; break;
            case "none": this.ownership = "none"; break;
            default: System.err.println("Expected arguments: |P1| or |P2|"); break;
        }
        return;
    }

    public void increaseAmn() {this.amount += 1;}
    public void decreaseAmn() {this.amount -= 1;}
    public void setAmn(int amn) {this.amount = amn;}

    public String getOwnership() {return ownership;}
    public int getAmn() {return amount;}
}