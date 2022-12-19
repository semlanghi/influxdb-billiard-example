import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class InteractiveGameSimulation {

    public static void main(String[] args) throws IOException {

        Game[] games = new Game[RandomGameSimulation.NUMBER_OF_TABLES];
        for (int i = 0; i < RandomGameSimulation.NUMBER_OF_TABLES; i++) {
            Table tmp = new Table(i*1000, i*1000, i);
            games[i] = Game.initialize(tmp, RandomGameSimulation.NUMBER_OF_BALLS*i,
                    new FileDataProducer(new File("/Users/samuelelanghi/Documents/projects/influxdb-billiard-example/src/main/resources/game-interactive.txt")));
        }

        Scanner scanner = new Scanner(System.in);
        String continuation;
        do {
            for (int i = 0; i < games.length; i++) {
                System.out.println("Which ball you want to hit ("+ i*15 + "-" + (i+1)*15 + ")?");
                int ball = scanner.nextInt();
                System.out.println("Which ball you want to target ("+ i*15 + "-" + (i+1)*15 + ")?");
                int target = scanner.nextInt();
                System.out.println("With which ball speed?");
                float speed = scanner.nextFloat();
                games[i].move(ball, target, speed);
            }
            System.out.println("Do you want to continue?");
            continuation = scanner.next();
        } while (continuation.equalsIgnoreCase("YES"));

        scanner.close();
    }
}
