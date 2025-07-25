package raisetech.studentManagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.studentManagement.converter.StudentConverter;
import raisetech.studentManagement.data.CourseStatus;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentCourse;
import raisetech.studentManagement.domain.StudentDetail;
import raisetech.studentManagement.dto.StudentSearchCondition;
import raisetech.studentManagement.exception.StudentNotFoundException;
import raisetech.studentManagement.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。
 * 受講生の検索や登録・更新処理を行います。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生詳細の一覧検索を行います。
   * 全件検索を行うので、条件指定を行いません。
   *
   * @return 受講生詳細一覧(全件)
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<CourseStatus> courseStatusList = repository.searchCourseStatusList();
    return converter.convertStudentDetails(studentList, studentCourseList, courseStatusList);
  }

  /**
   * 受講生詳細検索です。
   * IDに紐づく受講生情報を取得したあと、その受講生に紐づく受講生コース情報を取得して設定します。
   *
   * @param id 受講ID
   * @return 受講生詳細
   */
  public StudentDetail searchStudent(String id) {
    Student student = repository.searchStudent(id);

    if (Objects.isNull(student)) {
      throw new StudentNotFoundException("(ID：" + id + ")");
    }

    List<StudentCourse> studentCourseList = repository.searchStudentCourse(student.getId());

    Set<String> studentCourseIds = studentCourseList.stream()
        .map(StudentCourse::getId)
        .collect(Collectors.toSet());

    List<CourseStatus> courseStatus = repository.searchCourseStatus(studentCourseIds);

    return new StudentDetail(student, studentCourseList, courseStatus);
  }

  /**
   * 指定された検索条件に合致する受講生詳細を取得します。
   *
   * @param condition 検索条件
   * @return 検索条件に合致する受講生詳細のリスト
   */
  public List<StudentDetail> searchByCondition(StudentSearchCondition condition) {
    List<Student> studentList = repository.searchByCondition(condition);
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<CourseStatus> CourseStatusList = repository.searchCourseStatusList();

    if (studentList.isEmpty()) {
      throw new StudentNotFoundException("(検索条件：" + condition + ")");
    }

    return converter.convertStudentDetails(studentList, studentCourseList, CourseStatusList);
  }

  /**
   * 受講生詳細の登録を行います。 　
   * 受講生と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を紐づける値やコース開始日、コース終了日を設定します。
   *
   * @param studentDetail 受講生詳細
   * @return 登録情報を付与した受講生詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();

    // 受講生の登録を行う。
    repository.registerStudent(student);

    List<CourseStatus> courseStatusList = new ArrayList<>();

    studentDetail.getStudentCourseList().forEach(studentsCourse -> {

      initStudentsCourse(studentsCourse, student.getId());

      // 受講生コース情報の登録を行う。
      repository.registerStudentCourse(studentsCourse);

      CourseStatus courseStatus = new CourseStatus();
      initCourseStatus(courseStatus, studentsCourse.getId());

      // 申込状況の登録を行う。
      repository.registerCourseStatus(courseStatus);

      courseStatusList.add(courseStatus);
    });
    studentDetail.setCourseStatusList(courseStatusList);

    return studentDetail;
  }

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentCourse 受講生コース情報
   * @param id            受講生のID
   */
  void initStudentsCourse(StudentCourse studentCourse, String id) {
    LocalDateTime now = LocalDateTime.now();

    studentCourse.setStudentId(id);
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));
  }

  /**
   * 申込状況を登録する際の初期情報を設定します。
   *
   * @param courseStatus 申込状況
   * @param id           受講生コースID
   */
  void initCourseStatus(CourseStatus courseStatus, String id) {
    courseStatus.setStudentCourseId(id);
    courseStatus.setStatus("仮申込");
  }

  /**
   * 受講生詳細の更新を行います。
   * 受講生と受講生コース情報をそれぞれ更新します。
   *
   * @param studentDetail 受講生詳細
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    // 受講生の更新を行う。
    repository.updateStudent(studentDetail.getStudent());

    // 受講生コース情報の更新を行う。
    studentDetail.getStudentCourseList().forEach(studentCourse -> {
      repository.updateStudentCourse(studentCourse);
    });

    // 申込状況の更新を行う。
    studentDetail.getCourseStatusList().forEach(courseStatus -> {
      repository.updateCourseStatus(courseStatus);
    });
  }
}
