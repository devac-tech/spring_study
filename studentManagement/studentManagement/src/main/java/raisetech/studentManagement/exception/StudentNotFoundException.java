package raisetech.studentManagement.exception;

public class StudentNotFoundException extends RuntimeException {

  /**
   * 指定されたメッセージで {@code StudentNotFoundException} を構築します。
   *
   * @param message 例外の詳細メッセージ
   */
  public StudentNotFoundException(String message) {
    super(message);
  }
}
