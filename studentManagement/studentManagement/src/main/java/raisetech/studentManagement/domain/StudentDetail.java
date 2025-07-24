package raisetech.studentManagement.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import raisetech.studentManagement.data.CourseStatus;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;

/**
 * 受講生詳細を表すドメインモデルクラス
 */
@Schema(description = "受講生詳細")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {

  @Valid
  private Student student;

  @Valid
  private List<StudentCourse> studentCourseList;

  @Valid
  private List<CourseStatus> courseStatusList;
}
