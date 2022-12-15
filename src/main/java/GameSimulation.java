import java.util.Random;

public class GameSimulation {

    public static final int NUMBER_OF_TABLES = 1;
    public static final int NUMBER_OF_BALLS = 15*NUMBER_OF_TABLES;
    public static final int COLLISION_THRESHOLD = 3;
    public static final float MAX_STRENGTH = 30f;

    public static void main(String[] args){


        // INIT
        Table[] tables = new Table[NUMBER_OF_TABLES];
        Ball[] balls = new Ball[NUMBER_OF_BALLS];
        String[] colors = {"solid_yellow", "yellow_stripe", "solid_black", "solid_maroon", "solid_green", "solid_orange", "solid_purple", "solid_red", "solid_blue", "solid_yellow”, “blue_stripe", "red_stripe", "purple_stripe", "orange_stripe", "green_stripe", "maroon_stripe"};
        Position[] positions = new Position[NUMBER_OF_BALLS];

        Random randomPosX = new Random(112313214L);
        Random randomPosY = new Random(432539758345L);

        for (int i = 0; i < tables.length; i++) {
            tables[i] = new Table(i*1000, i*1000, i);

            for (int j = 15*i; j < 15*(i+1); j++) {
                balls[j] = new Ball(j, colors[j%15]);
                positions[j] = new Position(balls[j], tables[i], 0L, randomPosX.nextInt(tables[i].x0, tables[i].maxX), randomPosY.nextInt(tables[i].y0, tables[i].maxY));
                System.out.println(positions[j]);
            }
        }


        Random randomPush  = new Random(42L);

        for (int move = 0; move < 10; move++) {
            int ball = randomPush.nextInt(0,15);
            int target = randomPush.nextInt(0,15);
            while (target == ball)
                target = randomPush.nextInt(0,15);

            System.out.println("targeting ball "+target);


//            float xTmp = (float) Math.cos(Math.toRadians(randomPush.nextDouble(360.0)));
//            float yTmp = (float) Math.sin(Math.toRadians(randomPush.nextDouble(360.0)));

            float xTmp = positions[target].getX() - positions[ball].getX();
            float yTmp = positions[target].getY() - positions[ball].getY();
            float denom = (float) Math.sqrt(Math.pow(xTmp, 2f) + Math.pow(yTmp, 2f));

            float strength = randomPush.nextFloat(MAX_STRENGTH);
            positions[ball].push(new Versor(xTmp/denom, yTmp/denom), strength);

            System.out.println("pushing ball " + positions[ball].getBall().ballId +" on table " + positions[ball].getTable().tableId);

            int i = positions[ball].getTable().tableId;
            boolean allStopped = false;
            while(!allStopped) {
                allStopped = true;
                for (int j = 15 * i; j < 15 * (i + 1); j++) {
                    positions[j].progress(COLLISION_THRESHOLD/MAX_STRENGTH);
                    String wallX = positions[j].isOutOfBoundsX();
                    String wallY = positions[j].isOutOfBoundsY();

                    if(!wallX.equals("empty")){
                        positions[j].impactWall(wallX);
                        System.out.println("Impact " + wallX + " Wall!");
                    }
                    if(!wallY.equals("empty")){
                        positions[j].impactWall(wallY);
                        System.out.println("Impact " + wallY + " Wall!");
                    }

                    if(positions[j].isBallMoving())
                        for (int k = 15 * i; k < 15 * (i + 1); k++) {
                            if(positions[j].distance(positions[k])<COLLISION_THRESHOLD && j != k && (!positions[k].isBallMoving() || positions[k].getVelVersor().incident(positions[j].getVelVersor()))){
                                positions[j].impactAnotherBall(positions[k]);
                                System.out.println("Impact Ball " + positions[k].getBall().ballId + "!");
                            }
                        }
                }

                for (int k = 15 * i; k < 15 * (i + 1); k++) {

                    if(positions[k].isBallMoving())
                        allStopped = false;
                }
            }
        }

    }
}
