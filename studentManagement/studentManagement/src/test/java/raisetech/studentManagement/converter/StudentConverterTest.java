package raisetech.studentManagement.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.studentManagement.data.CourseStatus;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.domain.StudentDetail;

class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void before() {
    sut = new StudentConverter();
  }

  @Test
  void 受講生のリストと受講生コース情報のリストと申込状況のリストを渡して受講生詳細のリストが作成できること() {

    // 準備
    Student student = createStudent();

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setId("1");
    courseStatus.setStudentCourseId(studentCourse.getId());
    courseStatus.setStatus("仮申込");

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    List<CourseStatus> courseStatusList = List.of(courseStatus);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        courseStatusList);
    StudentDetail actualStudentDetail = actual.get(0);

    // 検証
    assertThat(actualStudentDetail.getStudent().getName()).isEqualTo("江並公史");
    assertThat(actualStudentDetail.getStudentCourseList().get(0).getCourseName()).isEqualTo(
        "Javaコース");
    assertThat(actualStudentDetail.getCourseStatusList().get(0).getStatus()).isEqualTo("仮申込");

  }

  @Test
  void 受講生のリストと受講生コース情報のリストと申込状況のリストを渡した時に紐づかない受講生コース情報は除外されること() {
    Student student = createStudent();

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("2");
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setId("1");
    courseStatus.setStudentCourseId("2");
    courseStatus.setStatus("仮申込");

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    List<CourseStatus> courseStatusList = List.of(courseStatus);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        courseStatusList);
    StudentDetail actualStudentDetail = actual.get(0);

    assertThat(actualStudentDetail.getStudent().getName()).isEqualTo("江並公史");
    assertThat(actualStudentDetail.getStudentCourseList()).isEmpty();
    assertThat(actualStudentDetail.getCourseStatusList()).isEmpty();

  }

  @Test
  void 受講生が複数のコースを受講している場合_すべての受講生コース情報および申込状況が紐づけられること() {

    // 準備
    Student student = createStudent();

    StudentCourse studentCourse1 = new StudentCourse();
    studentCourse1.setId("1");
    studentCourse1.setStudentId("1");
    studentCourse1.setCourseName("Javaコース");
    studentCourse1.setCourseStartAt(LocalDateTime.now());
    studentCourse1.setCourseEndAt(LocalDateTime.now().plusYears(1));

    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse2.setId("2");
    studentCourse2.setStudentId("1");
    studentCourse2.setCourseName("AWSコース");
    studentCourse2.setCourseStartAt(LocalDateTime.now());
    studentCourse2.setCourseEndAt(LocalDateTime.now().plusYears(1));

    CourseStatus courseStatus1 = new CourseStatus();
    courseStatus1.setId("1");
    courseStatus1.setStudentCourseId(studentCourse1.getId());
    courseStatus1.setStatus("仮申込");

    CourseStatus courseStatus2 = new CourseStatus();
    courseStatus2.setId("2");
    courseStatus2.setStudentCourseId(studentCourse2.getId());
    courseStatus2.setStatus("本申込");

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse1, studentCourse2);
    List<CourseStatus> courseStatusList = List.of(courseStatus1, courseStatus2);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        courseStatusList);
    StudentDetail actualStudentDetail = actual.get(0);

    // 検証
    assertThat(actualStudentDetail.getStudent().getName()).isEqualTo("江並公史");
    assertThat(actualStudentDetail.getStudentCourseList().get(0).getCourseName()).isEqualTo(
        "Javaコース");
    assertThat(actualStudentDetail.getStudentCourseList().get(1).getCourseName()).isEqualTo(
        "AWSコース");
    assertThat(actualStudentDetail.getCourseStatusList().get(0).getStatus()).isEqualTo("仮申込");
    assertThat(actualStudentDetail.getCourseStatusList().get(1).getStatus()).isEqualTo("本申込");

  }

  @Test
  void 受講生リストが空の場合_空のリストが返されること() {

    // 準備
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("Javaコース");

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setId("1");
    courseStatus.setStudentCourseId(studentCourse.getId());
    courseStatus.setStatus("仮申込");

    List<Student> studentList = List.of();
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    List<CourseStatus> courseStatusList = List.of(courseStatus);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        courseStatusList);

    // 検証
    assertThat(actual).isEmpty();

  }

  @Test
  void 受講生コース情報リストが空の場合_受講生コース情報および申込状況が空のリストが返されること() {

    // 準備
    Student student = createStudent();

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setId("1");
    courseStatus.setStudentCourseId("1");
    courseStatus.setStatus("仮申込");

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<CourseStatus> courseStatusList = List.of(courseStatus);

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        courseStatusList);
    StudentDetail actualStudentDetail = actual.get(0);

    // 検証
    assertThat(actualStudentDetail.getStudent().getName()).isEqualTo("江並公史");
    assertThat(actualStudentDetail.getStudentCourseList()).isEmpty();
    assertThat(actualStudentDetail.getCourseStatusList()).isEmpty();

  }

  @Test
  void 申込状況リストが空の場合は受講生コース情報の紐付けのみ行われ_申込状況が空のリストが返されること() {

    // 準備
    Student student = createStudent();

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("Javaコース");

    List<Student> studentList = List.of(student);
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    List<CourseStatus> courseStatusList = new ArrayList<>();

    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentCourseList,
        courseStatusList);
    StudentDetail actualStudentDetail = actual.get(0);

    // 検証
    assertThat(actualStudentDetail.getStudent().getName()).isEqualTo("江並公史");
    assertThat(actualStudentDetail.getStudentCourseList().get(0).getCourseName()).isEqualTo(
        "Javaコース");
    assertThat(actualStudentDetail.getCourseStatusList()).isEmpty();
  }

  /**
   * テスト用の {@link Student} オブジェクトを生成します。
   * <p>
   * 各フィールドにあらかじめ定義された値が設定されており、
   * テストケース内での共通データとして利用することを想定しています。
   *
   * @return テストデータとしての {@link Student} インスタンス
   */
  private Student createStudent() {
    Student student = new Student();
    student.setId("1");
    student.setName("江並公史");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setAddress("奈良県");
    student.setGender("男性");
    student.setRemark("");
    student.setDeleted(false);
    return student;
  }
}