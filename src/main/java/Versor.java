public class Versor {

    private float x, y;

    public Versor(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void impactWall(String wall){
        switch (wall){
            case "left":
                this.x = -this.x;
                break;
            case "right":
                this.x = -this.x;
                break;
            case "up":
                this.y = -this.y;
                break;
            case "down":
                this.y = -this.y;
                break;
        }
    }

    public void impactAnotherBall(Versor other){
        other.x = this.x;
        other.y = this.y;
        this.x = -this.x;
        this.y = -this.y;
    }

    public void stop(){
        this.x = 0f;
        this.y = 0f;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Versor{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public boolean incident(Versor velVersor) {
        if ((x < 0 && velVersor.getX() > 0 || x > 0 && velVersor.getX() < 0) &&
                (y < 0 && velVersor.getY() > 0 || y > 0 && velVersor.getY() < 0))
            return false;
        else return true;
    }
}
