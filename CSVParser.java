import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVParser {
    private final List<Record> records = new CopyOnWriteArrayList<>();
    private static final List<Character> POSSIBLE_DELIMITERS = List.of(',', ';', '\t', ' ');

    public void parse(String filePath) throws IOException, InterruptedException, ExecutionException {
        char delimiter = determineDelimiter(filePath);
        String[] headers = getHeaders(filePath, delimiter);
        
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            List<Callable<Record>> tasks = lines.skip(1)
                .map(line -> (Callable<Record>) () -> parseLine(line, headers, delimiter))
                .toList();

            int numCores = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            
            List<Future<Record>> futures = executor.invokeAll(tasks);
            for (Future<Record> future : futures) {
                records.add(future.get());
            }
            executor.shutdown();
        }
    }

    private char determineDelimiter(String filePath) throws IOException {
        Map<Character, Integer> delimiterCounts = new HashMap<>();
        for (char delimiter : POSSIBLE_DELIMITERS) {
            delimiterCounts.put(delimiter, 0);
        }

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            lines.limit(10).forEach(line -> {
                for (char delimiter : POSSIBLE_DELIMITERS) {
                    int count = line.chars().filter(ch -> ch == delimiter).toArray().length;
                    delimiterCounts.put(delimiter, delimiterCounts.get(delimiter) + count);
                }
            });
        }

        return Collections.max(delimiterCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private String[] getHeaders(String filePath, char delimiter) throws IOException {
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            return lines.findFirst().map(line -> line.split(Character.toString(delimiter))).orElse(new String[0]);
        }
    }

    private Record parseLine(String line, String[] headers, char delimiter) {
        String[] values = line.split(Character.toString(delimiter));
        Record record = new Record();
        for (int i = 0; i < headers.length; i++) {
            if (i < values.length) {
                record.addField(headers[i], values[i]);
            } else {
                record.addField(headers[i], null);
            }
        }
        return record;
    }

    public List<Record> getRecords() {
        return new ArrayList<>(records);
    }

    public int getRecordCount() {
        return records.size();
    }

    public Record getRecord(int recordIndex) {
        if (recordIndex >= 0 && recordIndex < records.size()) {
            return records.get(recordIndex);
        }
        return null;
    }

    public static void main(String[] args) {
        CSVParser parser = new CSVParser();
        try {
            parser.parse("data.csv");
            System.out.println("Total records: " + parser.getRecordCount());
            parser.getRecords().forEach(record -> System.out.println(record.getFullName()));
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

class Record {
    private final ConcurrentHashMap<String, Object> fields = new ConcurrentHashMap<>();

    public void addField(String fieldName, Object value) {
        fields.put(fieldName, value);
    }

    public Object getField(String fieldName) {
        return fields.get(fieldName);
    }

    public String getStringField(String fieldName) {
        return (String) fields.get(fieldName);
    }
