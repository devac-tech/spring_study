package raisetech.studentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.studentManagement.converter.StudentConverter;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.domain.StudentDetail;
import raisetech.studentManagement.exception.TestException;
import raisetech.studentManagement.service.StudentService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして実行されるControllerです。
 */
@Validated
@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  @GetMapping("/exception")
  public void exception() {
    throw new TestException("このAPIは現在利用できません。古いURLです。");
  }

  @ExceptionHandler(TestException.class)
  public ResponseEntity<String> handleTestException(TestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * 受講生詳細の一覧検索です。
   * 全件検索を行うもので、条件指定は行わないものになります。
   *
   * @return 受講生詳細一覧(全件)
   */
  @Operation(summary = "一覧検索", description = "受講生の一覧を検索します。")
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() throws TestException {
//    throw new TestException("エラーが発生しました。");
    return service.searchStudentList();
  }

  /**
   *　受講生詳細の検索です。
   *  IDに紐づく任意の受講生の情報を取得します。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  @Operation(summary = "受講生詳細の検索", description = "受講生詳細を検索します。")
  @GetMapping("/student/{id}")
  public StudentDetail getStudent(
      @PathVariable @NotBlank @Pattern(regexp = "^\\d+$") String id) {
    return service.searchStudent(id);
  }

  @GetMapping("/newStudent")
  @Operation(summary = "新規受講生登録画面", description = "新規受講生登録画面を表示します。")
  public String newStudent(Model model) {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudentCourseList(Arrays.asList(new StudentCourse()));
    model.addAttribute("studentDetail", studentDetail);
    return "registerStudent";
  }

  /**
   * 受講生詳細の登録を行います。
   *
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(summary = "受講生登録", description = "受講生を登録します。")
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody @Valid StudentDetail studentDetail, BindingResult result) {
    // 新規受講生情報を登録する処理を実装する
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の更新を行います。
   * キャンセルフラグの更新もここで行います(論理削除)
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @PutMapping("/updateStudent")
  @Operation(summary = "受講生更新", description = "受講生を更新します。")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功。");
  }

}
