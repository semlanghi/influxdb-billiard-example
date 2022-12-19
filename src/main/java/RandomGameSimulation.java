import java.io.File;
import java.io.IOException;

public class RandomGameSimulation {

    public static final int NUMBER_OF_TABLES = 2;
    public static final int NUMBER_OF_BALLS = 15;

    public static void main(String[] args) throws IOException {

        Table[] tables = new Table[NUMBER_OF_TABLES];
        for (int i = 0; i < NUMBER_OF_TABLES; i++) {
            tables[i] = new Table(i*1000, i*1000, i);
            Game.initialize(tables[i], NUMBER_OF_BALLS*i,
                    new FileDataProducer(new File("/Users/samuelelanghi/Documents/projects/influxdb-billiard-example/src/main/resources/game-random.txt"))).randomSym();
        }


    }
}
