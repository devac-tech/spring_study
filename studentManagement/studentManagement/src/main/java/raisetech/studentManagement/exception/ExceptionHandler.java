package raisetech.studentManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * アプリケーション全体の例外処理を行うクラスです。
 * <p>
 * コントローラーでスローされた例外に対して、共通のエラーレスポンスを返します。
 * </p>
 */
@ControllerAdvice
public class ExceptionHandler {

  /**
   * {@link TestException} をハンドリングし、HTTP 400 (Bad Request) を返します。
   *
   * @param ex 処理中に発生した {@code TestException}
   * @return エラーメッセージを含む HTTP 400 レスポンス
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(TestException.class)
  public ResponseEntity<String> handleTestException(TestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
}