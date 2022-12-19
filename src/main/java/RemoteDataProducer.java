import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class RemoteDataProducer implements BilliardDataProducer{

    private final InfluxDBClient influxDBClient;
    private char[] token = "EHKGukNcTj63y_bY8waI_YdWzR9-d4D0KGGH5OF4laRhfR17rAbI-YW_3uo0Qqt_YgfYoErHITwxXqXQu89vwA==".toCharArray();
    private String org = "my-org";
    private String bucket = "my-bucket";
    private WriteApiBlocking writeApi;

    public RemoteDataProducer() {
        influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);
        writeApi = influxDBClient.getWriteApiBlocking();
    }

    @Override
    public void writePosition(Position position) {

        Point point = Point.measurement("position")
                .addTag("table", String.valueOf(position.getTable().tableId))
                .addField("x", position.getX())
                .addField("y", position.getY())
                .time(position.getTimestamp(), WritePrecision.MS);

        writeApi.writePoint(point);
    }

    @Override
    public void writeTurn(Ball ball, Table table, long timestamp) {
        Point point = Point.measurement("turn")
                .addTag("table", String.valueOf(table.tableId))
                .addTag("ball", ball.color)
                .time(timestamp, WritePrecision.MS);

        writeApi.writePoint(point);
    }

    @Override
    public void close() {
        influxDBClient.close();
    }
}
