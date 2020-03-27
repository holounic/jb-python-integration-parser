package pythonIntegration.parser;

@FunctionalInterface
public interface Criterion {
    boolean satisfies(char c);
}
