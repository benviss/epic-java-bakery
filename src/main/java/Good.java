import org.sql2o.*;
import java.util.ArrayList;
import java.util.List;

public class Good {

  protected String name;
  protected int id;
  protected int stockPrice;
  protected int sellPrice;
  protected String type;

  public Good(String name, int stockPrice, int sellPrice, String type) {
    this.name = name;
    this.stockPrice = stockPrice;
    this.sellPrice = sellPrice;
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public void setName(String newName) {
    this.name = newName;
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("UPDATE goods SET name = :name WHERE id=:id AND type=:type")
        .addParameter("name", this.name)
        .addParameter("type", this.type)
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
      con.createQuery("UPDATE goods SET stockPrice = :stockPrice WHERE id=:id AND type=:type")
        .addParameter("type", this.type)
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
      con.createQuery("UPDATE goods SET sellPrice = :sellPrice WHERE id=:id AND type=:type")
        .addParameter("type", this.type)
        .addParameter("sellPrice", this.sellPrice)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO goods (name, stockPrice, sellPrice, type) VALUES (:name, :stockPrice, :sellPrice, :type)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .addParameter("stockPrice", this.stockPrice)
        .addParameter("sellPrice", this.sellPrice)
        .addParameter("type", this.type)
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

  public static List<Good> allByType(String _type) {
    try(Connection con = DB.sql2o.open()) {
    return con.createQuery("SELECT * FROM goods WHERE type=:type")
        .addParameter("type", _type)
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

  public List<Integer> getTransactionIds() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT transaction_id FROM transactions_goods_link WHERE good_id=:good_id")
        .addParameter("good_id", this.id)
        .executeAndFetch(Integer.class);
    }
  }

  @Override
  public boolean equals(Object testObj) {
    if(!(testObj instanceof Good)) {
      return false;
    } else {
      Good good = (Good) testObj;
      return this.id == good.getId() && this.type.equals(good.getType());
    }
  }

  public int getSellVolume() {
    try(Connection con = DB.sql2o.open()) {
      List<Integer> allGoodIds = con.createQuery("SELECT good_id FROM transactions_goods_link WHERE good_id = :id")
        .addParameter("id", this.id)
        .executeAndFetch(Integer.class);
      return allGoodIds.size();
    }
  }

  public int getTotalSales() {
    return this.sellPrice * this.getSellVolume();
  }
}
