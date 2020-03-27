package pythonIntegration;

import org.junit.Assert;
import org.junit.Test;
import pythonIntegration.exception.ParserException;
import pythonIntegration.parser.BuildErrorParser;

import java.io.File;
import java.util.Random;


public class BuildErrorParserTest {
    private static final BuildErrorParser parser = new BuildErrorParser();

    private final int STANDART_TESTS_NUMBER = ERROR_TYPES.length;
    private final int RANDOM_TESTS_NUMBER = 100;
    private final int WRONG_INPUT_TESTS_NUMBER = 10;


    private static final String[] ERROR_TYPES = {
            "ZeroDivisionError",
            "TypeError",
            "KeyError",
            "UserGeneratedError1",
            "AssertionError"
    };

    private static final String[] ABSOLUTE_PATHS = {
            "/Home/file",
            "/User/work-space/library",
            "/Desktop/linear-algebra/third-lab",
            "/home/seva/Desktop/JBR/siam-efficientdet/model/chest/bifpn",
            "/another-python-file"
    };

    private static final String[] ERROR_MESSAGES = {
            "division by zero",
            "incompetible type",
            "some key",
            ":::bad day",
            "oopsy"
    };

    private static final String[] ANNOTATIONS = {
            "1 / 0",
            "unicorn 2",
            "keyyyg",
            ":(((((((",
            "something went wrong"
    };

    @Test
    public void testGiven() {
        String text = "Traceback (most recent call last):\n" +
                "  File \"file.py\", line 1, in <module>\n" +
                "    import library\n" +
                "  File \"/home/admin/library.py\", line 2\n" +
                "    return 1 1 1 1\n" +
                "             ^\n" +
                "SyntaxError: invalid syntax";
        try {
            test("/home/admin/library", "SyntaxError", "invalid syntax", 2,2,false, text);
        } catch (AssertionError e) {
            errorMessage(0, "GIVEN", text);
            throw e;
        }
        passedMessage(1, "GIVEN");
    }

    @Test
    public void testStandart() {
        for (int i = 0; i < STANDART_TESTS_NUMBER; i++) {
            int line = i + 1;
            String text = generateExceptionText(ABSOLUTE_PATHS[i], line, ANNOTATIONS[i], ERROR_TYPES[i], ERROR_MESSAGES[i]);
            try {
                test(ABSOLUTE_PATHS[i], ERROR_TYPES[i], ERROR_MESSAGES[i], 1, line,false, text);
            } catch (AssertionError e) {
                errorMessage(i, "STANDART", text);
                throw e;
            }
        }
        passedMessage(STANDART_TESTS_NUMBER, "STANDART");
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < RANDOM_TESTS_NUMBER; i++) {
            String path = generateFilePath(generateNumber());
            int line = generateNumber();
            String annotation = generateAnnotation(generateNumber());
            String type = generateExceptionName(generateNumber());
            String message = generateAnnotation(generateNumber());
            String text = generateExceptionText(path, line, annotation, type, message);
            try {
                test(path, type, message, 1, line, false, text);
            } catch (AssertionError e) {
                errorMessage(i, "RANDOM", text);
                throw e;
            }
        }
        passedMessage(RANDOM_TESTS_NUMBER, "RANDOM");
    }

    @Test
    public void testWrongInput() {
        for (int i = 0; i < WRONG_INPUT_TESTS_NUMBER; i++) {
            String text = generateRandomArgument(i, c -> true);
            try {
                test(null, null, null, 0, 0,true, text);
            } catch (AssertionError e) {
                errorMessage(i, "WRONG INPUT FORMAT", text);
                throw e;
            }
        }
        passedMessage(WRONG_INPUT_TESTS_NUMBER, "WRONG INPUT FORMAT");
    }

    private void test(final String path, final String type, final String errorMessage,
                      final int stackTraseLength, final int line, final boolean expectedException, final String text) {
        PythonException exception = new PythonExceptionImpl();
        try {
            exception = parser.parseExceptionMessage(text);
        } catch (ParserException e) {
            Assert.assertTrue(expectedException);
            return;
        }
        checkOutput(exception, path, extractInformative(type), extractInformative(errorMessage), stackTraseLength, line);
    }

    private void checkOutput(PythonException result, String absolutePath, String errorType, String errorMessage, int stackTraseLength, int line) {
        compare(absolutePath + ".py", result.getExceptionLocation().getAbsolutePath());
        compare(errorType, result.getExceptionType());
        compare(errorMessage, result.getExceptionMessage());
        int realSTSize = result.getStackTrace().size();
        compare(stackTraseLength, realSTSize);
        compare(line, result.getStackTrace().get(realSTSize - 1).getLine());
        compare(new File(absolutePath + ".py"), result.getStackTrace().get(realSTSize - 1).getLocation());
    }

    private static String generateRandomArgument(int targetLength, CharFilter criterion) {
        Random random = new Random();
        return random.ints(Character.MIN_VALUE, Character.MAX_VALUE + 1)
                .filter(x -> criterion.satisfies(x) && x != (int) '\n')
                .limit(targetLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private int generateNumber() {
        return new Random().nextInt(100) + 1;
    }

    private String generateAnnotation(int targetLength) {
        return generateRandomArgument(targetLength, x -> true);
    }

    private String generateExceptionName(int targetLength) {
        return generateRandomArgument(targetLength, x -> Character.isLetterOrDigit(x) || x == '_' || x == '-');
    }

    private static String generateFilePath(int targetLength) {
        return '/' + generateRandomArgument(targetLength, x -> x == (int) '/' || Character.isLetterOrDigit(x));
    }

    private String generateExceptionText(String absolutePath, int line, String annotation, String errorType, String errorMessage) {
        return String.format("Traceback (most recent call last):\n" +
                "  File \"%s.py\", line %d\n" +
                "  %s\n" +
                "             ^\n" +
                "%s: %s", absolutePath, line, annotation, errorType, errorMessage);
    }

    private void compare(Object expected, Object found) {
        Assert.assertEquals(expected, found);
    }

    private static String extractInformative(String argument) {
        int beginIndex = -1, endIndex = -1;
        for (int i = 0; i < argument.length(); i++) {
            if (!Character.isWhitespace(argument.charAt(i))) {
                if (beginIndex == -1) {
                    beginIndex = i;
                }
                endIndex = i;
            }
        }
        return argument.substring(beginIndex, endIndex + 1);
    }

    private void passedMessage(int testsNumber, String testName) {
        log(String.format("PASSED %d %s TESTS", testsNumber, testName));
    }

    private void errorMessage(int testIndex, String testName, String text) {
        passedMessage(testIndex, testName);
        log("WRONG OUTPUT AT TEST: " + text);
    }

    private void log(Object s) {
        System.out.println(s);
    }
}
