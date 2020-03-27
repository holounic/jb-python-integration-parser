package pythonIntegration.parser;


public class ParserSource {
    private final String source;
    private int index;

    public ParserSource(String source) {
        if (source == null) {
            throw new NullPointerException();
        }
        this.source = source;
    }

    public int getIndex() {
        return index;
    }

    public char getNext() {
        if (index >= source.length()) {
            return '\0';
        }
        return source.charAt(index++);
    }

    public boolean test(String toCompare) {
        int length = toCompare.length();
        if (index + length >= source.length()) {
            return false;
        }
        if (toCompare.equals(source.substring(index, index + length))) {
            index += length;
            return true;
        }
        return false;
    }

    public boolean test(char c) {
        return test(Character.toString(c));
    }

    public char peek() {
        if (index >= source.length()) {
            return '\0';
        }
        return source.charAt(index);
    }

    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > source.length()) {
            return "";
        }
        return source.substring(beginIndex, endIndex);
    }

    public boolean hasNext() {
        return index < source.length();
    }

    public void skip(Criterion criterion) {
        while (hasNext() && criterion.satisfies(peek())) {
            index++;
        }
    }
}
