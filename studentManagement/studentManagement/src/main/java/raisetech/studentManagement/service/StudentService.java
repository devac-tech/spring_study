package raisetech.studentManagement.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.studentManagement.data.Student;
import raisetech.studentManagement.data.StudentsCourses;
import raisetech.studentManagement.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    // 絞り込みをする。年齢が30代の人のみを抽出する。
    List<Student> studentList = repository.search()
        .stream()
        .filter(student -> student.getAge() >= 30 && student.getAge() < 40)
        .collect(Collectors.toList());
      return studentList;
//    return repository.search();
  }

  public List<StudentsCourses> searchStudentsCourseList() {
    // 絞り込み検索で「Javaコース」のコース情報のみを抽出する。
    List<StudentsCourses> studentsCourseList = repository.searchStudentsCourses()
        .stream()
        .filter(studentsCourses -> studentsCourses.getCourseName().equals("Javaコース"))
        .collect(Collectors.toList());
    return studentsCourseList;
//    return repository.searchStudentsCourses();
  }

}
