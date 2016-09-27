import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class User {
  public int id;
  public String name;
  public String password;
  public String userType;

  public static User loggedInUser;

  public User(String name, String password) {
    this.name = name;
    this.password = password;
    this.userType = "customer";
  }

  public String getName() {
    return this.name;
  }

  public void setName(String newName) {
    this.name = newName;
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("UPDATE users SET name = :name WHERE id=:id AND userType=:usertype")
        .addParameter("usertype", this.userType)
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
      this.id = (int) con.createQuery("INSERT INTO users (name, password, usertype) VALUES (:name, :password, :usertype)", true)
      .addParameter("usertype", this.userType)
        .addParameter("name", this.name)
        .addParameter("password", this.password)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<User> all() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM users WHERE usertype=:usertype")
        .addParameter("usertype", "customer")
        .executeAndFetch(User.class);
    }
  }

  public static User findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM users WHERE id=:id AND usertype=:usertype")
        .addParameter("usertype", "customer")
        .addParameter("id", id)
        .executeAndFetchFirst(User.class);
    }
  }

  public static boolean userValidated(String name, String password) {
    try(Connection con = DB.sql2o.open()) {
      User user = con.createQuery("SELECT * FROM users WHERE name = :name AND password = :password AND usertype=:usertype")
        .addParameter("usertype", "customer")
        .addParameter("name", name)
        .addParameter("password", password)
        .executeAndFetchFirst(User.class);
      if(user != null)
        return true;
      else
        return false;
    }
  }

  public static boolean userNameExists(String name) {
    try(Connection con = DB.sql2o.open()) {
      User user = con.createQuery("SELECT * FROM users WHERE name = :name AND usertype=:usertype")
        .addParameter("usertype", "customer")
        .addParameter("name", name)
        .executeAndFetchFirst(User.class);
      if(user != null)
        return true;
      else
        return false;
    }
  }

  @Override
  public boolean equals(Object testObj) {
    if(!(testObj instanceof User))
      return false;
    else {
      User user = (User) testObj;
      return this.id == user.getId() && this.name.equals(user.getName());
    }
  }

  public static User getLoggedInUser() {
    return loggedInUser;
  }

  public static void setLoggedInUser(User user) {
    loggedInUser = user;
  }

}
