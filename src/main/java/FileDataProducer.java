import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDataProducer implements BilliardDataProducer{

    private final FileWriter fileWriter;

    public FileDataProducer(File file) throws IOException {
        fileWriter = new FileWriter(file);
    }

    @Override
    public void writePosition(Position position) throws IOException {

        Point point = Point.measurement("position")
                .addTag("table", String.valueOf(position.getTable().tableId))
                .addTag("ball", position.getBall().color)
                .addField("x", position.getX())
                .addField("y", position.getY())
                .time(position.getTimestamp(), WritePrecision.MS);

        fileWriter.write(point.toLineProtocol()+"\n");
        fileWriter.flush();
    }

    @Override
    public void writeTurn(Ball ball, Table table, long timestamp) throws IOException {
        Point point = Point.measurement("turn")
                .addTag("table", String.valueOf(table.tableId))
                .addField("ball", ball.color)
                .time(timestamp, WritePrecision.MS);

        fileWriter.write(point.toLineProtocol()+"\n");
        fileWriter.flush();
    }

    @Override
    public void close() throws IOException {
        fileWriter.close();
    }
}
