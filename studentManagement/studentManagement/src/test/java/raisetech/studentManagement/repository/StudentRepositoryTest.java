package raisetech.studentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索が行えること(){
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 受講生の検索を行えること(){
    Student actual = sut.searchStudent("1");

    assertThat(actual.getId()).isEqualTo("1");
    assertThat(actual.getName()).isEqualTo("山田太郎");
    assertThat(actual.getEmail()).isEqualTo("taro@example.com");
  }

  @Test
  void 受講生のコース情報の全件検索が行えること(){
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(9);
  }

  @Test
  void 受講生IDに紐づく受講生コース情報の検索を行えること(){
    List<StudentCourse> actual = sut.searchStudentCourse("1");

    assertThat(actual.get(0).getStudentId()).isEqualTo("1");
    assertThat(actual.get(0).getCourseName()).isEqualTo("Javaコース");
    assertThat(actual.get(1).getStudentId()).isEqualTo("1");
    assertThat(actual.get(1).getCourseName()).isEqualTo("AWSコース");
  }

  @Test
  void 受講生の登録が行えること(){
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

    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コース情報の登録が行えること(){
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
  void 受講生の更新が行えること(){
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

    assertThat(actual.getAge()).isEqualTo(26);
  }

  @Test
  void 受講生コース情報の更新が行えること(){
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("1");
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("デザインコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    sut.updateStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();

    assertThat(actual.get(0).getStudentId()).isEqualTo("1");
    assertThat(actual.get(0).getCourseName()).isEqualTo("デザインコース");
    assertThat(actual.get(1).getCourseName()).isEqualTo("AWSコース");
  }


}