package pythonIntegration.exception;

public class WrongInputFormatException extends ParserException {
    public WrongInputFormatException() {
        super("Wrong input format, try updating your python");
    }
}
