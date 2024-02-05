import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
    private List<String[]> records;

    public void parse(String filePath) throws IOException {
        records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseLine(line);
                records.add(values);
            }
        }
    }

    public List<String[]> getRecords() {
        return records;
    }

    public int getRecordCount() {
        return records.size();
    }

    public String[] getValuesForRecord(int recordIndex) {
        if (recordIndex >= 0 && recordIndex < records.size()) {
            return records.get(recordIndex);
        }
        return null;
    }

    private String[] parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(c);
            }
        }

        values.add(currentValue.toString());

        return values.toArray(new String[0]);
    }


}
