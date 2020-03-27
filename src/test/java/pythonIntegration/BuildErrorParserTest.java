package pythonIntegration;

import org.junit.Assert;
import org.junit.Test;
import pythonIntegration.exception.ParserException;
import pythonIntegration.parser.BuildErrorParser;

import javax.swing.text.html.parser.Parser;
import java.util.List;
import java.util.Random;

public class BuildErrorParserTest {
    private final BuildErrorParser parser = new BuildErrorParser();
    private final int TEST_NUMBER = 100;

    private String generateMessage(String file, int line, String errorType, String errorMessage) {
        return String.format("Traceback (most recent call last):\n" +
                "  File \"%s.py\", line %d\n" +
                "  return 1 \n" +
                "%s: %s", file, line, errorType, errorMessage);
    }

    private void check(String file, int line, String errorType, String errorMessage) {
        PythonException exception = new PythonExceptionImpl();
        try {
            exception = parser.parseExceptionMessage(generateMessage(file, line, errorType, errorMessage));
        } catch (ParserException e) {
            log(e.getMessage());
        }
        Assert.assertEquals(errorType, exception.getExceptionType());
        Assert.assertEquals(errorMessage, exception.getExceptionMessage());
        Assert.assertEquals(file + ".py", exception.getExceptionLocation().getAbsolutePath());

        List<ExceptionStackTraceElement> stackTrace = exception.getStackTrace();
        Assert.assertEquals(1, stackTrace.size());

        ExceptionStackTraceElement exceptionSource = stackTrace.get(0);
        Assert.assertEquals(file + ".py", exceptionSource.getLocation().getAbsolutePath());
        Assert.assertEquals(line, exceptionSource.getLine());

    }

    @Test
    public void test() {
        check("/ddd-ddddd/random", 201, "Exception-Exception", "trolling       occured+/^,");
    }

    @Test
    public void testShouldParseRuntimeException() {
        String message = "Traceback (most recent call last):\n" +
                "  File \"file.py\", line 1, in <module>\n" +
                "    import library\n" +
                "  File \"/home/admin/library.py\", line 2\n" +
                "    return 1 1 1 1\n" +
                "             ^\n" +
                "SyntaxError: invalid syntax";


        PythonExceptionImpl exception = new PythonExceptionImpl();
        try {
            exception = parser.parseExceptionMessage(message);
        } catch (ParserException e) {
            log(e.getMessage());
        }

        Assert.assertEquals("SyntaxError", exception.getExceptionType());
        Assert.assertEquals("invalid syntax", exception.getExceptionMessage());
        Assert.assertEquals("/home/admin/library.py", exception.getExceptionLocation().getAbsolutePath());

        List<ExceptionStackTraceElement> stackTrace = exception.getStackTrace();
        Assert.assertEquals(2, stackTrace.size());

        ExceptionStackTraceElement exceptionSource = stackTrace.get(1);
        Assert.assertEquals("/home/admin/library.py", exceptionSource.getLocation().getAbsolutePath());
        Assert.assertEquals(2, exceptionSource.getLine());

    }

    public void log(Object message) {
        System.out.println(message);
    }
}
