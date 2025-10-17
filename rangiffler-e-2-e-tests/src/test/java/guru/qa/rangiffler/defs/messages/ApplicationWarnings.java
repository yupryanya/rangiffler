package guru.qa.rangiffler.defs.messages;

public class ApplicationWarnings {
  public class SignupWarnings {
    public static final String USER_EXISTS = "Username `%s` already exists";
    public static final String INVALID_USERNAME = "Allowed username length should be from 3 to 50 characters";
    public static final String INVALID_PASSWORD = "Allowed password length should be from 3 to 12 characters";
    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords should be equal";
    public static final String VALIDATION_MESSAGE = "Please fill out this field.";
  }

  public class LoginWarnings {
    public static final String BAD_CREDENTIALS = "Bad credentials";
  }
}