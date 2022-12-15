public class Table {
    public final int x0;
    public final int y0;
    public final int maxX;
    public final int maxY;
    public final int tableId;


    public Table(int x0, int y0, int tableId) {
        this.x0 = x0;
        this.y0 = y0;
        this.tableId = tableId;
        this.maxX = this.x0 + 142;
        this.maxY = this.y0 + 284;
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableId='" + tableId + '\'' +
                '}';
    }
}
