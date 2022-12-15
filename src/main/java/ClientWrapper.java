import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.time.Instant;
import java.util.List;

public class ClientWrapper {

    private final InfluxDBClient influxDBClient;
    private char[] token = "EHKGukNcTj63y_bY8waI_YdWzR9-d4D0KGGH5OF4laRhfR17rAbI-YW_3uo0Qqt_YgfYoErHITwxXqXQu89vwA==".toCharArray();
    private String org = "my-org";
    private String bucket = "my-bucket";
    private WriteApiBlocking writeApi;

    public ClientWrapper() {
        influxDBClient = InfluxDBClientFactory.create("http://localhost:8086", token, org, bucket);
    }

    public void writePosition(Position position) {

        writeApi = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement("position")
                .addTag("table", String.valueOf(position.getTable().tableId))
                .addField("x", position.getX())
                .addField("y", position.getY())
                .time(position.getTimestamp(), WritePrecision.S);

        writeApi.writePoint(point);

//        //
//        // Query data
//        //
//        String flux = "from(bucket:\"my-bucket\") |> range(start: 0)";
//
//        QueryApi queryApi = influxDBClient.getQueryApi();
//
//        List<FluxTable> tables = queryApi.query(flux);
//        for (FluxTable fluxTable : tables) {
//            List<FluxRecord> records = fluxTable.getRecords();
//            for (FluxRecord fluxRecord : records) {
//                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
//            }
//        }

        influxDBClient.close();
    }
}
