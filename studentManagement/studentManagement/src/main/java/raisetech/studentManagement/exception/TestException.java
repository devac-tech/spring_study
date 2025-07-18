package raisetech.studentManagement.exception;

public class TestException extends RuntimeException {

  public TestException() {
    super();
  }

  public TestException(String message) {
    super(message);
  }

  public TestException(String message, Throwable cause) {
    super(message, cause);
  }

  public TestException(Throwable cause) {
    super(cause);
  }

  protected TestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

