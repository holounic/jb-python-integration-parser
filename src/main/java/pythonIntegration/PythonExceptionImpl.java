package pythonIntegration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class PythonExceptionImpl implements PythonException {
    @NotNull
    private final String exceptionType;
    @NotNull
    private final String exceptionMessage;
    @NotNull
    private final File exceptionLocation;
    @NotNull
    private final List<ExceptionStackTraceElement> exceptionStackTrace;

    public PythonExceptionImpl(@NotNull String exceptionType, @NotNull String exceptionMessage, @NotNull File exceptionLocation, List<ExceptionStackTraceElement> exceptionStackTrace) {
        this.exceptionType = exceptionType;
        this.exceptionMessage = exceptionMessage;
        this.exceptionLocation = exceptionLocation;
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public PythonExceptionImpl() {
        this("RandomType", "RandomMessage", new File("randomLocation.py"), Collections.emptyList());
    }

    @Override
    @NotNull
    public String getExceptionType() {
        return exceptionType;
    }

    @Override
    @NotNull
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    @NotNull
    public File getExceptionLocation() {
        return exceptionLocation;
    }

    @Override
    @NotNull
    public List<ExceptionStackTraceElement> getStackTrace() {
        return exceptionStackTrace;
    }
}
