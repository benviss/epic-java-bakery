import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class User {
  public int id;
  public String name;
  public String password;
  public String userType;

  public static int loggedInUserId = -1;

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

  public static List<User> allUsers() {
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

  public static Integer userValidated(String name, String password) {
    try(Connection con = DB.sql2o.open()) {
      Integer userId = con.createQuery("SELECT id FROM users WHERE name = :name AND password = :password AND usertype=:usertype")
        .addParameter("usertype", "customer")
        .addParameter("name", name)
        .addParameter("password", password)
        .executeAndFetchFirst(Integer.class);
      if(userId != null)
        return userId;
      else
        return -1;
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

  public static int getLoggedInUserId() {
    return loggedInUserId;
  }

  public static void setLoggedInUserId(int userId) {
    loggedInUserId = userId;
  }

  public List<Transaction> getTransactions() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM transactions WHERE customerId=:id")
        .addParameter("id", this.id)
        .executeAndFetch(Transaction.class);
    }
  }

  // public List<Transaction> getTransactions() {
  //   List<Integer> ids = this.getTransactionIds();
  //   List<Transaction> transactions = new ArrayList<>();
  //   for(Integer id : ids) {
  //     Transaction newTransaction = Transaction.findById(id);
  //     transactions.add(newTransaction);
  //   }
  //   return transactions;
  // }

  public int totalSpent() {
    int sum = 0;
    for (Transaction transaction :this.getTransactions()) {
      sum += transaction.getSalePrice();
    }
    return sum;
  }

}
