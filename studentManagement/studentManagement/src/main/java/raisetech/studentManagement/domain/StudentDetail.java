package raisetech.studentManagement.domain;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentsCourses;

@Getter
@Setter
public class StudentDetail {
  private Student student;
  private String courseName;
  private LocalDate courseStartAt;
  private LocalDate courseEndAt;
  // 複数の場合
  private List<StudentsCourses> studentsCourses;
}
