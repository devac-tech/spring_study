package raisetech.studentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 受講生を表すデータクラスです。
 */
@Schema(description = "受講生詳細")
@Data
public class Student {

  @Pattern(regexp = "^\\d+$", message = "数字のみ入力するようにしてください")
  private String id;

  @NotBlank(message = "名前を入力してください。")
  private String name;

  @NotBlank(message = "カナ名を入力してください。")
  private String kanaName;

  @NotBlank(message = "ニックネームを入力してください。")
  private String nickname;

  @NotBlank(message = "メールアドレスを入力してください。")
  @Email(message = "正しい形式のメールアドレスを入力してください。")
  private String email;

  /**
   * 居住地域（例：大阪府、奈良県など）
   */
  @NotBlank(message = "居住地域（例：大阪府、奈良県など）を入力してください。")
  private String address;

  private int age;

  /**
   * 性別（男性、女性、その他）
   */
  @NotBlank(message = "性別（男性、女性、その他）を入力してください。")
  private String gender;

  /**
   * 備考欄。自由記述フィールド。
   */
  private String remark;

  /**
   * 論理削除フラグ。true の場合は削除済みとして扱う。
   */
  private boolean isDeleted;
}
