package raisetech.studentManagement.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.studentManagement.data.CourseStatus;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.domain.StudentDetail;

/**
 * 受講生情報と受講生コース情報、もしくはその逆の変換を行うコンバーターです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づく受講生情報をマッピングする。
   * 受講生コース情報を受講生に対して複数存在するのでループを回して受講生詳細を情報を組み立てる。
   *
   * @param studentList        受講生の一覧
   * @param studentsCourseList 受講生コース情報のリスト
   * @param courseStatusList   申込状況の一覧
   * @return 受講生詳細情報のリスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> studentList,
      List<StudentCourse> studentsCourseList, List<CourseStatus> courseStatusList) {
    List<StudentDetail> studentDetailList = new ArrayList<>();
    for (Student student : studentList) {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      List<StudentCourse> convertStudentCourseList = studentsCourseList.stream()
          .filter(studentsCourse -> student.getId().equals(studentsCourse.getStudentId()))
          .collect(Collectors.toList());

      studentDetail.setStudentCourseList(convertStudentCourseList);

      Set<String> studentCourseIdList = convertStudentCourseList.stream()
          .map(StudentCourse::getId)
          .collect(Collectors.toSet());

      List<CourseStatus> convertCourseStatusList = courseStatusList.stream()
          .filter(
              courseStatus -> studentCourseIdList.contains(
                  courseStatus.getStudentCourseId()))
          .collect(Collectors.toList());
      studentDetail.setCourseStatusList(convertCourseStatusList);

      studentDetailList.add(studentDetail);
    }
    return studentDetailList;
  }

}
