# CSV Files

**A CSV (Comma-Separated Values) file parser is a data format in which values are separated by commas. Each line of the file represents a record, and the values within the line are separated by commas. The parser should be able to read CSV files, parse their contents, and provide access to the data.**

---

**CSVParser has the following methods:**

- **parse(String filePath):** A method that accepts the path to a CSV file and reads its content. After reading the file, the data should be split into records and values within the records.
- **getRecords():** A method that returns a list of records from the CSV file. Each record can be represented, for example, as an array of strings.
- **getRecordCount():** A method that returns the number of records in the file.
- **getValuesForRecord(int recordIndex):** A method that returns the values for a specified record by index. The values can be represented, for example, as an array of strings.
