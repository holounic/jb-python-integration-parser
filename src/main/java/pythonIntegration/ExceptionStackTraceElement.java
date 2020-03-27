package pythonIntegration;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ExceptionStackTraceElement {
    @NotNull
    private final File location;
    private final int line;

    public ExceptionStackTraceElement(@NotNull File location, int line) {
        this.location = location;
        this.line = line;
    }

    @NotNull
    public File getLocation() {
        return location;
    }

    public int getLine() {
        return line;
    }
}
