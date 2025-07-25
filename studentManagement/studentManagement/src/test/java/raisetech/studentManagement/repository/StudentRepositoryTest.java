package raisetech.studentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.studentManagement.data.CourseStatus;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.dto.StudentSearchCondition;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索が行えること() {
    // 準備
    List<Student> actual = sut.search();

    // 検証
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 受講生の検索を行えること() {
    // 準備
    Student actual = sut.searchStudent("1");

    // 検証
    assertThat(actual.getId()).isEqualTo("1");
    assertThat(actual.getName()).isEqualTo("山田太郎");
    assertThat(actual.getEmail()).isEqualTo("taro@example.com");
  }

  @Test
  void 受講生コース情報の全件検索が行えること() {
    // 準備
    List<StudentCourse> actual = sut.searchStudentCourseList();

    // 検証
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 受講生IDに紐づく受講生コース情報の検索を行えること() {
    // 準備
    List<StudentCourse> actual = sut.searchStudentCourse("1");

    // 検証
    assertThat(actual.get(0).getStudentId()).isEqualTo("1");
    assertThat(actual.get(0).getCourseName()).isEqualTo("Javaコース");
    assertThat(actual.get(1).getStudentId()).isEqualTo("1");
    assertThat(actual.get(1).getCourseName()).isEqualTo("AWSコース");
  }

  @Test
  void 申込状況の全件検索が行えること() {
    // 準備
    List<CourseStatus> actual = sut.searchCourseStatusList();

    // 検証
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 受講生コース情報IDに紐づく申込状況の検索が行えること() {
    // 準備
    Set<String> studentCourseIds = Set.of("1", "2");
    List<CourseStatus> actual = sut.searchCourseStatus(studentCourseIds);

    // 検証
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  void 条件に合致する受講生の検索が行えること() {
    // 準備
    StudentSearchCondition condition = new StudentSearchCondition();
    condition.setName("山田");
    List<Student> actual = sut.searchByCondition(condition);

    // 検証
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  void 受講生の登録が行えること() {
    // 準備
    Student student = new Student();
    student.setName("江並公史");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setAddress("奈良県");
    student.setGender("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    // 検証
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コース情報の登録が行えること() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 申込状況の登録が行えること() {
    // 準備
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setStudentCourseId("1");
    courseStatus.setStatus("仮申込");

    sut.registerCourseStatus(courseStatus);
    List<CourseStatus> actual = sut.searchCourseStatusList();

    // 検証
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生の更新が行えること() {
    // 準備
    Student student = new Student();
    student.setId("1");
    student.setName("山田太郎");
    student.setKanaName("ヤマダタロウ");
    student.setNickname("タロウ");
    student.setEmail("taro@example.com");
    student.setAddress("東京");
    student.setAge(26);
    student.setGender("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.updateStudent(student);
    Student actual = sut.searchStudent("1");

    // 検証
    assertThat(actual.getAge()).isEqualTo(26);
  }

  @Test
  void 受講生コース情報の更新が行えること() {
    // 準備
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("デザインコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    sut.updateStudentCourse(studentCourse);
    List<StudentCourse> actual = sut.searchStudentCourseList();

    // 検証
    assertThat(actual.get(0).getStudentId()).isEqualTo("1");
    assertThat(actual.get(0).getCourseName()).isEqualTo("デザインコース");
    assertThat(actual.get(1).getCourseName()).isEqualTo("AWSコース");
  }

  @Test
  void 申込状況の更新が行えること() {
    // 準備
    List<StudentCourse> studentCourseList = sut.searchStudentCourse("1");
    StudentCourse studentCourse = studentCourseList.get(0);
    Set<String> studentCourseIds = Set.of(studentCourse.getId());
    List<CourseStatus> applicationStatusList = sut.searchCourseStatus(studentCourseIds);
    CourseStatus applicationStatus = applicationStatusList.get(0);

    // 更新前の申込状況の確認
    assertThat(applicationStatus.getStatus()).isEqualTo("仮申込");

    applicationStatus.setStatus("本申込");
    sut.updateCourseStatus(applicationStatus);

    List<CourseStatus> actual = sut.searchCourseStatus(studentCourseIds);

    // 検証
    assertThat(actual.get(0).getStatus()).isEqualTo("本申込");
  }
}