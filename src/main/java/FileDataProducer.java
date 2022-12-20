import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDataProducer implements BilliardDataProducer{

    private final FileWriter fileWriter;
    private long startTime;

    public FileDataProducer(File file) throws IOException {
        fileWriter = new FileWriter(file);
        this.startTime = System.nanoTime();
    }

    @Override
    public void writePosition(Position position) throws IOException {

        Point point = Point.measurement("position")
                .addTag("table", String.valueOf(position.getTable().tableId))
                .addTag("ball", position.getBall().color)
                .addField("x", position.getX())
                .addField("y", position.getY())
                .time(position.getTimestampNano()+startTime, WritePrecision.NS);

        fileWriter.write(point.toLineProtocol()+"\n");
        fileWriter.flush();
    }

    @Override
    public void writeTurn(Ball ball, Table table, long timestamp) throws IOException {
        Point point = Point.measurement("turn")
                .addTag("table", String.valueOf(table.tableId))
                .addField("ball", ball.color)
                .time(timestamp+startTime, WritePrecision.NS);

        fileWriter.write(point.toLineProtocol()+"\n");
        fileWriter.flush();
    }

    @Override
    public void close() throws IOException {
        fileWriter.close();
    }

    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
