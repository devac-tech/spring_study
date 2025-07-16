package raisetech.studentManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.studentManagement.converter.StudentConverter;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.domain.StudentDetail;
import raisetech.studentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before(){
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

    sut.searchStudentList();
    // 検証
    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void 受講生詳細検索_リポジトリの処理が呼び出せれ受講生詳細が返却されていること() {
    // 準備
    Student student = new Student();
    student.setId("1");
    student.setName("山田太郎");

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse course = new StudentCourse();
    course.setCourseName("Javaコース");
    studentCourseList.add(course);

    when(repository.searchStudent(student.getId())).thenReturn(student);
    when(repository.searchStudentCourse(student.getId())).thenReturn(studentCourseList);

    StudentDetail result = sut.searchStudent(student.getId());

    // 検証
    assertEquals(student, result.getStudent());
    assertEquals(studentCourseList, result.getStudentCourseList());
    verify(repository, times(1)).searchStudent(student.getId());
    verify(repository, times(1)).searchStudentCourse(student.getId());
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

    StudentDetail result = sut.registerStudent(studentDetail);

    // 検証
    assertEquals(studentDetail, result);
    verify(repository, times(1)).registerStudent(studentDetail.getStudent());
    verify(repository, times(1)).registerStudentCourse(studentDetail.getStudentCourseList().getFirst());
  }

  @Test
  void 受講生詳細の登録_初期化処理が行われていること() {
    // 準備
    Student student = new Student();
    student.setId("999");

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setCourseName("Javaコース");

    sut.initStudentsCourse(studentCourse, student);

    // 検証
    assertEquals("999", studentCourse.getStudentId());
    assertEquals(LocalDateTime.now().getHour(),
        studentCourse.getCourseStartAt().getHour());
    assertEquals(LocalDateTime.now().plusYears(1).getYear(),
        studentCourse.getCourseEndAt().getYear());
  }

  @Test
  void 受講生詳細の更新_リポジトリ処理が適切に呼び出せていること() {
    // 準備
    Student student = new Student();
    student.setId("1");
    student.setName("山田太郎");

    StudentCourse course = new StudentCourse();
    course.setCourseName("Javaコース");

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(new ArrayList<>(List.of(course)));

    sut.updateStudent(studentDetail);

    // 検証
    verify(repository, times(1)).updateStudent(studentDetail.getStudent());
    verify(repository, times(1)).updateStudentCourse(studentDetail.getStudentCourseList().getFirst());
  }
}