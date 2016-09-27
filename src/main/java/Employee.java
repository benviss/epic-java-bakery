import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class Employee extends User {
  public Employee(String name, String password) {
    super(name, password);
  }

  public static List<Employee> allEmployees() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM users WHERE usertype = :usertype")
        .addParameter("usertype", "employee")
        .executeAndFetch(Employee.class);
    }
  }

  public static Employee findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM users WHERE id=:id AND usertype = :usertype")
        .addParameter("id", id)
        .addParameter("usertype", "employee")
        .executeAndFetchFirst(Employee.class);
    }
  }

  public static boolean employeeValidated(String name, String password) {
    try(Connection con = DB.sql2o.open()) {
      Employee employee = con.createQuery("SELECT * FROM users WHERE name = :name AND password = :password")
        .addParameter("name", name)
        .addParameter("password", password)
        .executeAndFetchFirst(Employee.class);
      if(employee != null)
        return true;
      else
        return false;
    }
  }

  public static boolean employeeNameExists(String name) {
    try(Connection con = DB.sql2o.open()) {
      Employee employee = con.createQuery("SELECT * FROM users WHERE name = :name")
        .addParameter("name", name)
        .executeAndFetchFirst(Employee.class);
      if(employee != null)
        return true;
      else
        return false;
    }
  }

  @Override
  public boolean equals(Object testObj) {
    if(!(testObj instanceof Employee))
      return false;
    else {
      Employee employee = (Employee) testObj;
      return this.id == employee.getId() && this.name.equals(employee.getName());
    }
  }


}
