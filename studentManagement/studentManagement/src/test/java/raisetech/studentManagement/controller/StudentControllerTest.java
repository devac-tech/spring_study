package raisetech.studentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.studentManagement.data.CourseStatus;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.domain.StudentDetail;
import raisetech.studentManagement.service.StudentService;

@WebMvcTest(StudentController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
    // 準備
    when(service.searchStudentList()).thenReturn(List.of(new StudentDetail()));
    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"student\":null,\"studentCourseList\":null}]"));

    // 検証
    verify(service, times(1)).searchStudentList();
  }

  @Test
  void 受講生の検索が実行できて空で返ってくること() throws Exception {
    // 準備
    String id = "999";

    mockMvc.perform(get("/student/{id}", id))
        .andExpect(status().isOk());

    // 検証
    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 条件に合致する受講生の検索が実行できて空で返ってくること() throws Exception {
    // 準備
    mockMvc.perform(
            post("/students/search").contentType(MediaType.APPLICATION_JSON).content(
                """
                    {
                      "name": "山田太郎",
                      "kananame": "ヤマダタロウ",
                      "nickname": "タロ",
                      "email": "taro@example.com",
                      "address": "東京",
                      "minAge": 25,
                      "maxAge": 27,
                      "gender": "男性",
                      "remark": ""
                    }
                    """
            ))
        .andExpect(status().isOk());

    // 検証
    verify(service, times(1)).searchByCondition(any());
  }

  @Test
  void 受講生詳細の登録が実行できて空で返ってくること() throws Exception {
    // 準備
    mockMvc.perform(post("/registerStudent").contentType(MediaType.APPLICATION_JSON).content(
            """
                      {
                        "student": {
                            "name": "江並公史",
                            "kanaName": "エナミコウシ",
                            "nickname": "コウシ",
                            "email": "test@example.com",
                            "address": "奈良",
                            "age": 40,
                            "gender": "男性",
                            "remark": ""
                        },
                    "studentCourseList": [
                        {
                            "courseName": "Javaコース"
                        }
                      ]
                    }
                """
        ))
        .andExpect(status().isOk());

    // 検証
    verify(service, times(1)).registerStudent(any());
  }

  @Test
  void 受講生詳細の更新が実行できて空で返ってくること() throws Exception {
    // 準備
    mockMvc.perform(put("/updateStudent").contentType(MediaType.APPLICATION_JSON).content(
            """
                          {
                         "student": {
                                "id": "1",
                                "name": "山田太郎",
                                "kanaName": "ヤマダタロウ",
                                "nickname": "タロウ",
                                "email": "taro@example.com",
                                "address": "東京",
                                "age": 25,
                                "gender": "男性",
                                "remark": ""
                            },
                        "studentCourseList": [
                            {
                                "id": "1",
                                "studentId": "1",
                                "courseName": "Javaコース",
                                "courseStartAt": "2025-07-23T13:03:00",
                                "courseEndAt": "2026-07-23T13:03:00"
                            }
                          ],
                         "courseStatusList": [
                             {
                                 "id": "1",
                                 "studentCourseId": "1",
                                 "status": "本申込"
                             }
                         ]
                        }
                """
        ))
        .andExpect(status().isOk());

    // 検証
    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で返ってくること() throws Exception {
    mockMvc.perform(get("/exception"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLです。"));
  }

  @Test
  void 受講生詳細の受講生で適切な値を入力した時に入力チェックに異常が発生しないこと() {
    // 準備
    Student student = new Student();
    student.setId("1");
    student.setName("江並公史");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setAddress("奈良県");
    student.setGender("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    // 検証
    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックに掛かること() {
    // 準備
    Student student = new Student();
    student.setId("テストです。");
    student.setName("江並公史");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setAddress("奈良県");
    student.setGender("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    // 検証
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").containsOnly("数字のみ入力するようにしてください");
  }

  @Test
  void 受講生詳細の受講生で必須項目が空の場合に入力チェックに掛かること() {
    // 準備
    Student student = new Student();
    student.setName("");
    student.setKanaName("");
    student.setNickname("");
    student.setEmail("");
    student.setAddress("");
    student.setGender("");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    // 検証
    assertThat(violations.size()).isEqualTo(6);
    assertThat(violations).extracting(v -> v.getPropertyPath().toString(),
        ConstraintViolation::getMessage).containsExactlyInAnyOrder(
        tuple("name", "名前を入力してください。"),
        tuple("kanaName", "カナ名を入力してください。"),
        tuple("nickname", "ニックネームを入力してください。"),
        tuple("email", "メールアドレスを入力してください。"),
        tuple("address", "居住地域（例：大阪府、奈良県など）を入力してください。"),
        tuple("gender", "性別（男性、女性、その他）を入力してください。")
    );
  }

  @Test
  void 受講生詳細の受講生コース情報で適切な値を入力した時に入力チェックに異常が発生しないこと() {
    // 準備
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setCourseName("Javaコース");

    Set<ConstraintViolation<StudentCourse>> violations = validator.validate(studentCourse);

    // 検証
    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生コース情報で不正な形式や必須項目に空がある場合に入力チェックに掛かること() {
    // 準備
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("test");
    studentCourse.setStudentId("test");
    studentCourse.setCourseName("");

    Set<ConstraintViolation<StudentCourse>> violations = validator.validate(studentCourse);

    // 検証
    assertThat(violations.size()).isEqualTo(3);
    assertThat(violations).extracting(
            v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage)
        .containsExactlyInAnyOrder(
            tuple("id", "数字のみ入力するようにしてください。"),
            tuple("studentId", "数字のみ入力するようにしてください。"),
            tuple("courseName", "コース名を入力してください。")
        );
  }

  @Test
  void 受講生詳細の申込状況で適切な値を入力した時に入力チェックに異常が発生しないこと() {
    // 準備
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setStatus("本申込");

    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);

    // 検証
    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の申込状況で不正な形式や必須項目に空がある場合に入力チェックに掛かること() {
    // 準備
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setId("test");
    courseStatus.setStudentCourseId("test");
    courseStatus.setStatus("");

    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);

    // 検証
    assertThat(violations.size()).isEqualTo(3);
    assertThat(violations).extracting(
            v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage)
        .containsExactlyInAnyOrder(
            tuple("id", "数字のみ入力するようにしてください。"),
            tuple("studentCourseId", "数字のみ入力するようにしてください。"),
            tuple("status", "申し込み状況を入力してください。")
        );
  }
}