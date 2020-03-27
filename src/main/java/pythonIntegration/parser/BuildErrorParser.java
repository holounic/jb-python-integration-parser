package pythonIntegration.parser;

import org.jetbrains.annotations.NotNull;
import pythonIntegration.ExceptionStackTraceElement;
import pythonIntegration.PythonExceptionImpl;
import pythonIntegration.exception.ParserException;
import pythonIntegration.exception.UnexpectedTokenException;
import pythonIntegration.exception.WrongInputFormatException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * the {@code BuildErrorParser} is used to extract information
 * (message, type, location, stacktrase)
 * from a raw python exception text.
 */
public class BuildErrorParser {

    private ParserSource source;
    private static final char LINEBREAK = '\n';

    /**
     * Parses a block whilst the сurrent char satisfies the {@param criterion}
     * @param criterion defines which characters are valid for the current block. Parsing stops,
     *                  when invalid character found
     * @return a String of valid characters, which is a substring of source text
     * (excluding terminal whitespaces)
     */
    private String parseBlock(Criterion criterion) {
        int beginIndex = source.getIndex();
        int lastInformative = beginIndex;
        while (source.hasNext() && criterion.satisfies(source.peek())) {
            if (!Character.isWhitespace(source.peek())) {
                lastInformative = source.getIndex();
            }
            source.getNext();
        }
        return source.substring(beginIndex, lastInformative + 1);
    }

    /**
     * @return String, representing a file path
     */
    private String parseFilePath() {
        skipIncl(c -> c != '\"');
        return parseBlock(c -> c != '\"');
    }

    /**
     * @return String, representing a line number
     */
    private String parseLineNumber() {
        skipExcl(c -> !Character.isDigit(c));
        return parseBlock(c -> Character.isDigit(c));
    }

    /**
     * @return an instance of {@code ExceptionStackTraceElement}, representing a line and
     * a file name of the error.
     * @throws UnexpectedTokenException if the token "line" is not found
     */
    private ExceptionStackTraceElement parseStacktrase() throws UnexpectedTokenException {
        String filePath = parseFilePath();
        skipExcl(c -> !Character.isLetterOrDigit(c));

        int lineNumber;
        if (source.test("line")) {
            lineNumber = Integer.parseInt(parseLineNumber());
        } else {
            int exceptionIndex = source.getIndex();
            throw new UnexpectedTokenException("line", source.substring(exceptionIndex, exceptionIndex + 4), exceptionIndex);
        }
        nextLine();
        skipWhiteSpace();
        return new ExceptionStackTraceElement(new File(filePath), lineNumber);
    }

    /**
     * @return a String, representing a type of the source error
     */
    private String parseErrorType() {
        skipExcl(c -> !Character.isLetterOrDigit(c));
        return parseBlock(c -> c != ':');
    }

    /**
     * @return a String, representing a message of the source error
     */
    private String parseErrorMessage() {
         skipExcl(c-> !isInformative(c));
         return parseBlock(c -> c != LINEBREAK);
    }

    /**
     * @param message the text of the source error
     * @return instance of {@code PythonExceptionImpl}, representing message, type, location and stacktrase
     * of the given python exception
     * @throws ParserException if the given text does not follow the standart python exception syntax
     */
    @NotNull
     public PythonExceptionImpl parseExceptionMessage(@NotNull String message) throws ParserException {
         source = new ParserSource(message);

         List<ExceptionStackTraceElement> stackTrace = new ArrayList<>();
         while (true) {
             nextLine();
             skipExcl(c -> !isInformative(c));
             if (!source.test("File")) {
                 break;
             }
             stackTrace.add(parseStacktrase());
         }
         String exceptionType = parseErrorType();
         String exceptionMessage = parseErrorMessage();

         source = null;
         if (exceptionType == null || exceptionMessage == null || stackTrace.isEmpty()) {
            throw new WrongInputFormatException();
         }

         File exceptionAbsolutePath = stackTrace.get(stackTrace.size() - 1).getLocation();
         return new PythonExceptionImpl(exceptionType, exceptionMessage, exceptionAbsolutePath, stackTrace);
     }

    private boolean isInformative(char c) {
        return Character.isLetterOrDigit(c) || c == LINEBREAK;
    }

    private void skipWhiteSpace() {
        source.skip(c -> !isInformative(c));
    }

    private void skipExcl(Criterion criterion) {
        source.skip(criterion);
    }

    private void skipIncl(Criterion criterion) {
        skipExcl(criterion);
        source.getNext();
    }

    private void nextLine() {
        skipIncl(c -> c != LINEBREAK);
    }
}