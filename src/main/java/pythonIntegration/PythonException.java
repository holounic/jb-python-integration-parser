package pythonIntegration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * Please do not modify me
 */
public interface PythonException {
    @NotNull
    String getExceptionType();

    @NotNull
    String getExceptionMessage();

    @NotNull
    File getExceptionLocation();

    @NotNull
    List<ExceptionStackTraceElement> getStackTrace();
}
