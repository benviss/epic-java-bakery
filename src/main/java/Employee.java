import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class Employee {
  private int id;
  private String name;
  private String password;
  private static Employee loggedInEmployee;

  public Employee(String name, String password) {
    this.name = name;
    this.password = password;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String newName) {
    this.name = newName;
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("UPDATE employees SET name = :name WHERE id=:id")
        .addParameter("name", this.name)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public int getId() {
    return this.id;
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      this.id = (int) con.createQuery("INSERT INTO employees (name, password) VALUES (:name, :password)", true)
        .addParameter("name", this.name)
        .addParameter("password", this.password)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Employee> all() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM employees")
        .executeAndFetch(Employee.class);
    }
  }

  public static Employee findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM employees WHERE id=:id")
        .addParameter("id", id)
        .executeAndFetchFirst(Employee.class);
    }
  }

  public static boolean employeeValidated(String name, String password) {
    try(Connection con = DB.sql2o.open()) {
      Employee employee = con.createQuery("SELECT * FROM employees WHERE name = :name AND password = :password")
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
      Employee employee = con.createQuery("SELECT * FROM employees WHERE name = :name")
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

  public static Employee getLoggedInEmployee() {
    return loggedInEmployee;
  }

  public static void setLoggedInEmployee(Employee employee) {
    loggedInEmployee = employee;
  }

}
