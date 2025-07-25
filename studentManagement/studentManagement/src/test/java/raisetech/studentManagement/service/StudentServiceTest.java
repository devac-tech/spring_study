package raisetech.studentManagement.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.studentManagement.converter.StudentConverter;
import raisetech.studentManagement.data.CourseStatus;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.domain.StudentDetail;
import raisetech.studentManagement.dto.StudentSearchCondition;
import raisetech.studentManagement.exception.StudentNotFoundException;
import raisetech.studentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<CourseStatus> courseStatusList = new ArrayList<>();
    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(repository.searchCourseStatusList()).thenReturn(courseStatusList);

    sut.searchStudentList();

    // 検証
    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(repository, times(1)).searchCourseStatusList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList,
        courseStatusList);
  }

  @Test
  void 受講生詳細の検索_リポジトリの処理が呼び出せれ受講生詳細が返却されていること() {
    // 準備
    Student student = new Student();
    student.setId("1");
    student.setName("山田太郎");

    String courseId = "1";
    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(courseId);
    studentCourse.setStudentId(student.getId());
    studentCourse.setCourseName("Javaコース");
    studentCourseList.add(studentCourse);

    Set<String> studentCourseIds = Set.of(studentCourse.getId());
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setStudentCourseId(studentCourse.getId());
    List<CourseStatus> courseStatusList = List.of(courseStatus);

    when(repository.searchStudent(student.getId())).thenReturn(student);
    when(repository.searchStudentCourse(student.getId())).thenReturn(studentCourseList);
    when(repository.searchCourseStatus(studentCourseIds)).thenReturn(courseStatusList);

    StudentDetail expected = new StudentDetail(student, studentCourseList, courseStatusList);
    StudentDetail actual = sut.searchStudent(student.getId());

    // 検証
    verify(repository, times(1)).searchStudent(student.getId());
    verify(repository, times(1)).searchStudentCourse(student.getId());
    verify(repository, times(1)).searchCourseStatus(studentCourseIds);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void 受講生詳細の検索_受講生が存在しない場合に例外がスローされること() {
    // 準備
    String id = "999";
    when(repository.searchStudent(id)).thenReturn(null);

    StudentNotFoundException thrown = assertThrows(StudentNotFoundException.class, () -> {
      sut.searchStudent(id);
    });

    // 検証
    assertThat(thrown.getMessage()).contains("(ID：" + id + ")");
  }

  @Test
  void 受講生詳細の条件指定検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 準備
    StudentSearchCondition condition = new StudentSearchCondition();
    List<Student> students = List.of(new Student());
    List<StudentCourse> studentCourses = new ArrayList<>();
    List<CourseStatus> courseStatuses = new ArrayList<>();

    when(repository.searchByCondition(condition)).thenReturn(students);
    when(repository.searchStudentCourseList()).thenReturn(studentCourses);
    when(repository.searchCourseStatusList()).thenReturn(courseStatuses);

    sut.searchByCondition(condition);

    // 検証
    verify(repository, times(1)).searchByCondition(condition);
    verify(repository, times(1)).searchStudentCourseList();
    verify(repository, times(1)).searchCourseStatusList();
    verify(converter, times(1)).convertStudentDetails(students, studentCourses, courseStatuses);
  }

  @Test
  void 受講生詳細の条件指定検索_条件に合致する受講生がいない場合に例外がスローされること() {
    // 準備
    StudentSearchCondition condition = new StudentSearchCondition();
    when(repository.searchByCondition(condition)).thenReturn(new ArrayList<>());

    StudentNotFoundException thrown = assertThrows(StudentNotFoundException.class, () -> {
      sut.searchByCondition(condition);
    });

    // 検証
    assertThat(thrown.getMessage()).contains("(検索条件：" + condition + ")");
  }

  @Test
  void 受講生詳細の登録_リポジトリ処理が呼び出され受講生詳細が返却されていること() {
    // 準備
    Student student = new Student();
    student.setId("1");
    student.setName("山田太郎");

    StudentCourse course = new StudentCourse();
    course.setCourseName("Javaコース");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(new ArrayList<>(List.of(course)));
    studentDetail.setCourseStatusList(List.of());

    StudentDetail result = sut.registerStudent(studentDetail);

    // 検証
    assertEquals(studentDetail, result);
    verify(repository, times(1)).registerStudent(studentDetail.getStudent());
    verify(repository, times(1)).registerStudentCourse(
        studentDetail.getStudentCourseList().getFirst());
    verify(repository, times(1)).registerCourseStatus(
        studentDetail.getCourseStatusList().getFirst());
  }

  @Test
  void 受講生詳細の登録_受講生コース情報の初期化処理が行われていること() {
    // 準備
    Student student = new Student();
    student.setId("999");

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setCourseName("Javaコース");

    sut.initStudentsCourse(studentCourse, student.getId());

    // 検証
    assertEquals("999", studentCourse.getStudentId());
    assertEquals(LocalDateTime.now().getHour(),
        studentCourse.getCourseStartAt().getHour());
    assertEquals(LocalDateTime.now().plusYears(1).getYear(),
        studentCourse.getCourseEndAt().getYear());
  }

  @Test
  void 受講生詳細の登録_申込状況の初期化処理が行われていること() {
    // 準備
    String id = "999";
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(id);
    CourseStatus courseStatus = new CourseStatus();

    sut.initCourseStatus(courseStatus, studentCourse.getId());

    // 検証
    assertThat(courseStatus.getStudentCourseId()).isEqualTo(id);
    assertThat(courseStatus.getStatus()).isEqualTo("仮申込");
  }

  @Test
  void 受講生詳細の更新_リポジトリ処理が適切に呼び出せていること() {
    // 準備
    Student student = new Student();
    student.setId("1");
    student.setName("山田太郎");

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setCourseName("Javaコース");

    CourseStatus courseStatus = new CourseStatus();
    List<CourseStatus> courseStatusList = List.of(courseStatus);

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(new ArrayList<>(List.of(studentCourse)));
    studentDetail.setCourseStatusList(courseStatusList);

    sut.updateStudent(studentDetail);

    // 検証
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).updateStudentCourse(studentCourse);
    verify(repository, times(1)).updateCourseStatus(courseStatus);
  }
}