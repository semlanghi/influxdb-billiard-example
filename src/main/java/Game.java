import java.util.Random;

public class Game {



    public static final int COLLISION_THRESHOLD = 3;
    public static final float MAX_STRENGTH = 30f;
    private Position[] positions;
    private Table table;
    private ClientWrapper wrapper;

    private Game(Position[] positions, Table table) {
        this.table = table;
        this.positions = positions;
        this.wrapper = new ClientWrapper();
    }

    public static Game initialize(Table table, int minIdBall, int maxIdBall){


        Ball[] balls = new Ball[15];
        String[] colors = {"solid_yellow", "yellow_stripe", "solid_black", "solid_maroon", "solid_green", "solid_orange", "solid_purple", "solid_red", "solid_blue", "solid_yellow”, “blue_stripe", "red_stripe", "purple_stripe", "orange_stripe", "green_stripe", "maroon_stripe"};
        Position[] positions = new Position[15];

        Random randomPosX = new Random(112313214L);
        Random randomPosY = new Random(432539758345L);

        for (int j = 0, z = minIdBall; j < RandomGameSimulation.NUMBER_OF_BALLS; j++) {
            balls[j] = new Ball(z++, colors[j%15]);
            positions[j] = new Position(balls[j], table, 0L, randomPosX.nextInt(table.x0, table.maxX), randomPosY.nextInt(table.y0, table.maxY));
            System.out.println(positions[j]);
        }

        return new Game(positions, table);
    }

    public void move(int ball, int target, float strength){

        System.out.println("targeting ball "+target);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int ballNorm = ball% RandomGameSimulation.NUMBER_OF_BALLS;
        int targetNorm = target%RandomGameSimulation.NUMBER_OF_BALLS;

//            float xTmp = (float) Math.cos(Math.toRadians(randomPush.nextDouble(360.0)));
//            float yTmp = (float) Math.sin(Math.toRadians(randomPush.nextDouble(360.0)));

        float xTmp = positions[targetNorm].getX() - positions[ballNorm].getX();
        float yTmp = positions[targetNorm].getY() - positions[ballNorm].getY();
        float denom = (float) Math.sqrt(Math.pow(xTmp, 2f) + Math.pow(yTmp, 2f));

        positions[ballNorm].push(new Versor(xTmp/denom, yTmp/denom), strength);

        System.out.println("pushing ball " + positions[ballNorm].getBall().ballId +" on table " + positions[ballNorm].getTable().tableId);

        boolean allStopped = false;
        while(!allStopped) {
            allStopped = true;
            for (int j = 0; j < positions.length; j++) {
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
                    for (int k = 0; k < positions.length; k++) {
                        if(positions[j].distance(positions[k])<COLLISION_THRESHOLD && j != k && (!positions[k].isBallMoving() || positions[k].getVelVersor().incident(positions[j].getVelVersor()))){
                            positions[j].impactAnotherBall(positions[k]);
                            System.out.println("Impact Ball " + positions[k].getBall().ballId + "!");
                        }
                    }
            }



            for (int k = 0; k < positions.length; k++) {
                wrapper.writePosition(positions[k]);
                if(positions[k].isBallMoving())
                    allStopped = false;
            }
        }
    }

    public void displaySituation(){
        for (int i = 0; i < positions.length; i++) {
            System.out.println(positions[i]);
        }
    }

    public void randomSym() {
        Random randomPush  = new Random(42L);

        for (int move = 0; move < 10; move++) {
            int ball = randomPush.nextInt(0,15);
            int target = randomPush.nextInt(0,15);
            while (target == ball)
                target = randomPush.nextInt(0,15);

            System.out.println("targeting ball "+target);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            float xTmp = (float) Math.cos(Math.toRadians(randomPush.nextDouble(360.0)));
//            float yTmp = (float) Math.sin(Math.toRadians(randomPush.nextDouble(360.0)));

            float xTmp = positions[target].getX() - positions[ball].getX();
            float yTmp = positions[target].getY() - positions[ball].getY();
            float denom = (float) Math.sqrt(Math.pow(xTmp, 2f) + Math.pow(yTmp, 2f));

            float strength = randomPush.nextFloat(MAX_STRENGTH);
            positions[ball].push(new Versor(xTmp/denom, yTmp/denom), strength);

            System.out.println("pushing ball " + positions[ball].getBall().ballId +" on table " + positions[ball].getTable().tableId);

            boolean allStopped = false;
            while(!allStopped) {
                allStopped = true;
                for (int j = 0; j < positions.length; j++) {
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
                        for (int k = 0; k < positions.length; k++) {
                            if(positions[j].distance(positions[k])<COLLISION_THRESHOLD && j != k && (!positions[k].isBallMoving() || positions[k].getVelVersor().incident(positions[j].getVelVersor()))){
                                positions[j].impactAnotherBall(positions[k]);
                                System.out.println("Impact Ball " + positions[k].getBall().ballId + "!");
                            }
                        }
                }



                for (int k = 0; k < positions.length; k++) {
                    wrapper.writePosition(positions[k]);
                    if(positions[k].isBallMoving())
                        allStopped = false;
                }
            }
        }
    }


}
