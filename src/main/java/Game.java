import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Game {



    public static final int COLLISION_THRESHOLD = 3;
    public static final float MAX_STRENGTH = 30f;
    private Position[] positions;
    private Table table;
    private BilliardDataProducer wrapper;
    private FileWriter groundTruthFile;

    private Game(Position[] positions, Table table, BilliardDataProducer dataProducer) {
        this.table = table;
        this.positions = positions;
        this.wrapper = dataProducer;
        try {
            this.groundTruthFile = new FileWriter("/Users/samuelelanghi/Documents/projects/influxdb-billiard-example/src/main/resources/ground-truth-table-1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Game initialize(Table table, int minIdBall, BilliardDataProducer producer){


        Ball[] balls = new Ball[15];
        String[] colors = {"solid_yellow", "yellow_stripe", "solid_black", "solid_maroon", "solid_green", "solid_orange", "solid_purple", "solid_red", "solid_blue", "solid_yellow", "blue_stripe", "red_stripe", "purple_stripe", "orange_stripe", "green_stripe", "maroon_stripe"};
        Position[] positions = new Position[15];

        Random randomPosX = new Random(112313214L);
        Random randomPosY = new Random(432539758345L);

        for (int j = 0, z = minIdBall; j < RandomGameSimulation.NUMBER_OF_BALLS; j++) {
            balls[j] = new Ball(z++, colors[j%15]);
            positions[j] = new Position(balls[j], table, 0L, randomPosX.nextInt(table.x0, table.maxX), randomPosY.nextInt(table.y0, table.maxY));
            System.out.println(positions[j]);
        }

        return new Game(positions, table, producer);
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

        try {
            wrapper.writeTurn(positions[ballNorm].getBall(), positions[ballNorm].getTable(), positions[ballNorm].getTimestamp());
        } catch (IOException e) {
            e.printStackTrace();
        }


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
                try {
                    wrapper.writePosition(positions[k]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(positions[k].isBallMoving())
                    allStopped = false;
            }
        }
    }

    public void randomSym() {
        Random randomPush  = new Random(42L);

        for (int move = 0; move < 100; move++) {
            int ball = randomPush.nextInt(0,15);
            int target = randomPush.nextInt(0,15);
            while (target == ball)
                target = randomPush.nextInt(0,15);

//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            float xTmp = positions[target].getX() - positions[ball].getX();
            float yTmp = positions[target].getY() - positions[ball].getY();
            float denom = (float) Math.sqrt(Math.pow(xTmp, 2f) + Math.pow(yTmp, 2f));

            float strength = randomPush.nextFloat(MAX_STRENGTH);
            positions[ball].push(new Versor(xTmp/denom, yTmp/denom), strength);

            try {
                wrapper.writeTurn(positions[ball].getBall(), positions[ball].getTable(), positions[ball].getTimestamp());
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                        Point point = Point.measurement("collision")
                                .addField("object1", positions[j].getBall().color)
                                .addField("object2", wallX)
                                .time(positions[j].getTimestamp(), WritePrecision.MS);
                        try {
                            groundTruthFile.write(point.toLineProtocol()+"\n");
                            groundTruthFile.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!wallY.equals("empty")){
                        positions[j].impactWall(wallY);
                        System.out.println("Impact " + wallY + " Wall!");
                        Point point = Point.measurement("collision")
                                .addField("object1", positions[j].getBall().color)
                                .addField("object2", wallY)
                                .time(positions[j].getTimestamp(), WritePrecision.MS);
                        try {
                            groundTruthFile.write(point.toLineProtocol()+"\n");
                            groundTruthFile.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(positions[j].isBallMoving())
                        for (int k = 0; k < positions.length; k++) {
                            if(positions[j].distance(positions[k])<COLLISION_THRESHOLD && j != k && (!positions[k].isBallMoving() || positions[k].getVelVersor().incident(positions[j].getVelVersor()))){
                                positions[j].impactAnotherBall(positions[k]);
                                System.out.println("Impact Ball " + positions[k].getBall().ballId + "!");
                                Point point = Point.measurement("collision")
                                        .addField("object1", positions[j].getBall().color)
                                        .addField("object2", positions[k].getBall().color)
                                        .time(positions[j].getTimestamp(), WritePrecision.MS);
                                try {
                                    groundTruthFile.write(point.toLineProtocol()+"\n");
                                    groundTruthFile.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                }



                for (int k = 0; k < positions.length; k++) {
                    try {
                        wrapper.writePosition(positions[k]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(positions[k].isBallMoving())
                        allStopped = false;
                }
            }
        }
    }


}
