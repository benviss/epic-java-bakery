import org.sql2o.*;
import java.util.ArrayList;
import java.util.List;

public class Good {

  protected String name;
  private int id;
  // private int merchandiseId;
  protected int stockQuantity;
  protected int stockPrice;
  protected int sellPrice;
  private int categoryId;
  private int transactionId;

  public Good(String name, int stockPrice, int sellPrice, int transactionId) {
    this.name = name;
    this.stockPrice = stockPrice;
    this.sellPrice = sellPrice;
    this.categoryId = 1;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String newName) {
    this.name = newName;
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("UPDATE breads SET name = :name WHERE id=:id")
        .addParameter("name", this.name)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public int getId() {
    return this.id;
  }

  public int getStockPrice() {
    return this.stockPrice;
  }

  public void setStockPrice(int newStockPrice) {
    this.stockPrice = newStockPrice;
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("UPDATE breads SET stockPrice = :stockPrice WHERE id=:id")
        .addParameter("stockPrice", this.stockPrice)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public int getSellPrice() {
    return this.sellPrice;
  }

  public void setSellPrice(int newSellPrice) {
    this.sellPrice = newSellPrice;
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("UPDATE breads SET sellPrice = :sellPrice WHERE id=:id")
        .addParameter("sellPrice", this.sellPrice)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO goods (name, stockPrice, sellPrice, categoryId) VALUES (:name, :stockPrice, :sellPrice, :categoryId)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .addParameter("stockPrice", this.stockPrice)
        .addParameter("sellPrice", this.sellPrice)
        .addParameter("categoryId", this.categoryId)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Good> all() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM goods")
        .executeAndFetch(Good.class);
    }
  }

  public static Good findById(int id) {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM goods WHERE id=:id")
        .addParameter("id", id)
        .executeAndFetchFirst(Good.class);
    }
  }

  @Override
  public boolean equals(Object testObj) {
    if(!(testObj instanceof Good)) {
      return false;
    } else {
      Good good = (Good) testObj;
      return this.id == good.getId() && this.name.equals(good.getName());
    }
  }
}
