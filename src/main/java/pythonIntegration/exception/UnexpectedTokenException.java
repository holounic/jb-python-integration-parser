package pythonIntegration.exception;

public class UnexpectedTokenException extends ParserException {
    public UnexpectedTokenException(String expected, String found, int index) {
        super(String.format("Expected \"%s\", found \"%s\" at index %d", expected, found, index));
    }

}
