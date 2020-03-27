package pythonIntegration.parser;


public class ParserSource {
    private final String source;
    private int pointer;

    public ParserSource(String source) {
        if (source == null) {
            throw new NullPointerException();
        }
        this.source = source;
    }

    public int getIndex() {
        return pointer;
    }

    public char getNext() {
        if (pointer >= source.length()) {
            return '\0';
        }
        return source.charAt(pointer++);
    }

    public boolean test(String toCompare) {
        int length = toCompare.length();
        if (pointer + length >= source.length()) {
            return false;
        }
        if (toCompare.equals(source.substring(pointer, pointer + length))) {
            pointer += length;
            return true;
        }
        return false;
    }

    public boolean test(char c) {
        return test(Character.toString(c));
    }

    public char peek() {
        if (pointer >= source.length()) {
            return '\0';
        }
        return source.charAt(pointer);
    }

    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > source.length()) {
            return "";
        }
        return source.substring(beginIndex, endIndex);
    }

    public boolean hasNext() {
        return pointer < source.length();
    }

    public void skip(Criterion criterion) {
        while (hasNext() && criterion.satisfies(peek())) {
            pointer++;
        }
    }
}
