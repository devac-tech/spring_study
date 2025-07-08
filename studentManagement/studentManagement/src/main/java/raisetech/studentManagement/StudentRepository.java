package raisetech.studentManagement;

import javax.management.MXBean;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM student WHERE name = #{name}")
  Student searchByNames(String name);

  @Insert("INSERT student values (#{name}, #{age})")
  void resisterStudent(String name, int age);

  @Update("UPDATE student SET age = #{age}")
  void updateStudent(int age);

  @Delete("DELETE FROM student WHERE name=#{name}")
  void deleteStudent(String name);

}
