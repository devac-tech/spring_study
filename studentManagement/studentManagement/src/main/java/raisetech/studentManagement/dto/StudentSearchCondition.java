package raisetech.studentManagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 受講生条件付き検索の際に使用する条件を保持するクラスです。 各フィールドは検索フォームやクエリパラメータからの入力値として利用され、受講生の絞り込みに使用されます。
 */
@Schema(description = "検索条件")
@Data
public class StudentSearchCondition {

  private String name;
  private String kananame;
  private String nickname;
  private String email;
  private String address;
  private Integer minAge;
  private Integer maxAge;
  private String gender;
  private String remark;
}
