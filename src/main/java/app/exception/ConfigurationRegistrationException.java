package app.exception;

public class ConfigurationRegistrationException extends RuntimeException {
    public ConfigurationRegistrationException(String message, Class<?> clazz) {
        super("Error registering instances from configuration class: " + clazz.getName() + ". Reason: " + message);
    }
}