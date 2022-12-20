import java.io.IOException;

public interface BilliardDataProducer {
    public void writePosition(Position position) throws IOException;
    public void writeTurn(Ball ball, Table table, long timestamp) throws IOException;
    public void close() throws IOException;
    public void setStartTime(long startTime);
}
