package raisetech.studentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 申込状況を表すデータクラスです。
 */
@Schema(description = "受講生コース情報")
@Data
public class CourseStatus {

  @Pattern(regexp = "^\\d+$", message = "数字のみ入力するようにしてください。")
  private String id; // 主キー

  /**
   * 受講生コースID。
   */
  @Pattern(regexp = "^\\d+$", message = "数字のみ入力するようにしてください。")
  private String studentCourseId;

  /**
   * 申込ステータス（仮申込、本申込、受講中、受講終了）。
   */
  @NotBlank(message = "申し込み状況を入力してください。")
  private String status;

}