import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CSVParser parser = new CSVParser();

        try {
            parser.parse("src/example.csv");

            List<String[]> records = parser.getRecords();
            int recordCount = parser.getRecordCount();

            for (int i = 0; i < recordCount; i++) {
                String[] values = parser.getValuesForRecord(i);
                System.out.println("Запись #" + (i + 1) + ": " + String.join(", ", values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
