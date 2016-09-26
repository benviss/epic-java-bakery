import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class Customer {
  private int id;
  private String name;
  private String password;
  private static Customer loggedInCustomer;

  public Customer(String name, String password) {
    this.name = name;
    this.password = password;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String newName) {
    this.name = newName;
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("UPDATE customers SET name = :name WHERE id=:id")
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
      this.id = (int) con.createQuery("INSERT INTO customers (name, password) VALUES (:name, :password)", true)
        .addParameter("name", this.name)
        .addParameter("password", this.password)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Customer> all() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM customers")
        .executeAndFetch(Customer.class);
    }
  }

  public static Customer findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM customers WHERE id=:id")
        .addParameter("id", id)
        .executeAndFetchFirst(Customer.class);
    }
  }

  public static boolean customerValidated(String name, String password) {
    try(Connection con = DB.sql2o.open()) {
      Customer customer = con.createQuery("SELECT * FROM customers WHERE name = :name AND password = :password")
        .addParameter("name", name)
        .addParameter("password", password)
        .executeAndFetchFirst(Customer.class);
      if(customer != null)
        return true;
      else
        return false;
    }
  }

  public static boolean customerNameExists(String name) {
    try(Connection con = DB.sql2o.open()) {
      Customer customer = con.createQuery("SELECT * FROM customers WHERE name = :name")
        .addParameter("name", name)
        .executeAndFetchFirst(Customer.class);
      if(customer != null)
        return true;
      else
        return false;
    }
  }

  @Override
  public boolean equals(Object testObj) {
    if(!(testObj instanceof Customer))
      return false;
    else {
      Customer customer = (Customer) testObj;
      return this.id == customer.getId() && this.name.equals(customer.getName());
    }
  }

  public static Customer getLoggedInCustomer() {
    return loggedInCustomer;
  }

  public static void setLoggedInCustomer(Customer customer) {
    loggedInCustomer = customer;
  }

}
