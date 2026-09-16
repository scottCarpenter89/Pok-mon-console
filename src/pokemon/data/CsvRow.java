package pokemon.data;

import java.util.ArrayList;
import java.util.List;

/**
 * One line of a CSV file, split into fields, with friendly accessors.
 *
 * <p>TEACHING NOTE (CS II/III - writing a parser):
 * {@link #split(String)} is a tiny <b>state machine</b>: it walks the line one
 * character at a time and remembers whether it is currently inside quotation marks.
 * That one boolean is what lets a field contain a comma. Students who have only
 * ever used {@code String.split(",")} find this genuinely eye-opening, and it is a
 * great first "write your own parser" exercise.
 */
public class CsvRow {

    private final String fileName;
    private final int lineNumber;
    private final List<String> fields;
    private final List<String> headers;

    public CsvRow(String fileName, int lineNumber, List<String> headers, List<String> fields) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.headers = headers;
        this.fields = fields;
    }

    /** Splits a CSV line, honouring "quoted, fields". */
    public static List<String> split(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                // Two quotes in a row inside a quoted field means a literal quote.
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields;
    }

    private int indexOf(String column) {
        int index = headers.indexOf(column);
        if (index < 0) {
            throw new DataException(where() + ": there is no column named '" + column + "'. "
                    + "Columns are " + headers);
        }
        return index;
    }

    public String where() {
        return fileName + " line " + lineNumber;
    }

    public String getString(String column) {
        int index = indexOf(column);
        if (index >= fields.size()) {
            return "";
        }
        return fields.get(index);
    }

    public String getString(String column, String fallback) {
        String value = getString(column);
        return value.isEmpty() ? fallback : value;
    }

    public int getInt(String column) {
        String raw = getString(column);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new DataException(where() + ": column '" + column
                    + "' should be a whole number but was \"" + raw + "\"", e);
        }
    }

    public int getInt(String column, int fallback) {
        String raw = getString(column);
        if (raw.isEmpty()) {
            return fallback;
        }
        return getInt(column);
    }

    public boolean getBoolean(String column) {
        String raw = getString(column).toLowerCase();
        return raw.equals("true") || raw.equals("yes") || raw.equals("1");
    }

    /** Splits a field such as {@code "route1|route2"} into a list. */
    public List<String> getList(String column, String separator) {
        List<String> values = new ArrayList<>();
        String raw = getString(column);
        if (raw.isEmpty()) {
            return values;
        }
        for (String part : raw.split(java.util.regex.Pattern.quote(separator))) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
        return values;
    }

    public int size() {
        return fields.size();
    }
}
