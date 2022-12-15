public class Ball {
    public final int ballId;
    public final String color;


    public Ball(int ballId, String color) {
        this.ballId = ballId;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Ball{" +
                "ballId='" + ballId + '\'' +
                '}';
    }
}
