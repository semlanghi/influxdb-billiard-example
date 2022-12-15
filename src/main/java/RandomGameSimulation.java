public class RandomGameSimulation {

    public static final int NUMBER_OF_TABLES = 2;
    public static final int NUMBER_OF_BALLS = 15;

    public static void main(String[] args){

        Table[] tables = new Table[NUMBER_OF_TABLES];
        for (int i = 0; i < NUMBER_OF_TABLES; i++) {
            tables[i] = new Table(i*1000, i*1000, i);
            Game.initialize(tables[i], NUMBER_OF_BALLS*i, NUMBER_OF_BALLS*(i+1)).randomSym();
        }


    }
}
