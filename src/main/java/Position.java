import java.util.Objects;

public class Position {
    private Ball ball;
    private Table table;
    private float ts;
    private Versor velVersor;
    private float velScalar;
    private float x;
    private float y;
    private boolean ballMoving;

    public Position(Ball ball, Table table, long ts, int x, int y) {
        this.ball = ball;
        this.table = table;
        this.ts = ts;
        this.x = x;
        this.y = y;
        this.velVersor = new Versor(0f, 0f);
        this.velScalar = 0f;
    }

    public void push(Versor velVersor, float velScalar){
        this.velVersor = velVersor;
        this.velScalar = velScalar;
        this.ballMoving = true;
    }

    public void impactAnotherBall(Position position){
        velVersor.impactAnotherBall(position.velVersor);
        position.velScalar = this.velScalar * 3/4;
        this.velScalar = this.velScalar * 1/4;
        position.ballMoving = true;
    }

    public boolean isBallMoving() {
        return ballMoving;
    }

    public Versor getVelVersor() {
        return velVersor;
    }

    public long getTimestamp() {
        return (long) (ts*1000f);
    }

    public void impactWall(String wall){
        velVersor.impactWall(wall);
    }

    public void progress(float deltaTime){
        if(isBallMoving())
            System.out.println("ball " + ball.ballId +" on table " + table.tableId + " moving from "+ this);

        this.x = (this.x + velScalar * velVersor.getX()*deltaTime);
        this.y = (this.y + velScalar * velVersor.getY()*deltaTime);
        velScalar = (float) (velScalar - (Game.MAX_STRENGTH/15)*deltaTime);
        if (velScalar<1){
            velScalar = 0.0f;
            velVersor.stop();
            this.ballMoving = false;
        }

        this.ts = ts + deltaTime;

        if(isBallMoving())
            System.out.println("to "+ this);
    }



    public Ball getBall() {
        return ball;
    }

    public Table getTable() {
        return table;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String isOutOfBoundsX(){
        if (this.x >= this.table.maxX && this.velVersor.getX() > 0)
            return "right";
        if(this.x <= this.table.x0  && this.velVersor.getX() < 0)
            return "left";
        else return "empty";
    }

    public String isOutOfBoundsY(){
        if(this.y >= this.table.maxY && this.velVersor.getY() > 0)
            return "up";
        if(this.y <= this.table.y0 && this.velVersor.getY() < 0)
            return "down";
        else return "empty";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "ball=" + ball +
                ", table=" + table +
                ", ts=" + ts +
                ", velVersor=" + velVersor +
                ", velScalar=" + velScalar +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public int distance(Position position) {
        return (int) Math.sqrt(Math.pow(this.x - position.x, 2f) + Math.pow(this.y - position.y, 2f));
    }
}
