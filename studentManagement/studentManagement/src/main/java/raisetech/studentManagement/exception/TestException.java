package raisetech.studentManagement.exception;

/**
 * アプリケーション内で使用されるカスタム例外クラスです。
 * <p>
 * 特定のエラーロジックに応じて例外をスローし、例外ハンドラによって処理されます。
 * </p>
 */
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

  protected TestException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

