import org.sql2o.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;


public class Transaction {
  private int id;
  private int salePrice;
  private int customerId;
  private Timestamp dateAndTime;

  public Transaction(int customerId) {
    this.customerId = customerId;
  }

  public int getId() {
    return this.id;
  }

  public int getSalePrice() {
    return this.salePrice;
  }

  public int getCustomerId() {
    return this.customerId;
  }

  public Timestamp getTimestamp() {
    return this.dateAndTime;
  }

  public void save() {
    try (Connection con = DB.sql2o.open()) {
      this.id = (int) con.createQuery("INSERT INTO transactions (salePrice, customerId, dateAndTime) VALUES (:salePrice, :customerId, now())", true)
      .addParameter("salePrice", this.salePrice)
      .addParameter("customerId", this.customerId)
      .executeUpdate()
      .getKey();
    }
  }

  public static List<Transaction> all() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM transactions;")
      .executeAndFetch(Transaction.class);
    }
  }

  public static Transaction findById(int _id) {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT * FROM transactions WHERE id=:id")
        .addParameter("id", _id)
        .executeAndFetchFirst(Transaction.class);
    }
  }

  @Override
  public boolean equals(Object _obj) {
    if (!(_obj instanceof Transaction)) {
      return false;
    } else {
      Transaction transaction = (Transaction) _obj;
      return this.id == transaction.getId() && this.customerId == transaction.getCustomerId();
    }
  }

  public void linkTransactionGoods(int _goodsId) {
    this.salePrice += Good.findById(_goodsId).getSellPrice();
    try(Connection con = DB.sql2o.open()) {
      con.createQuery("INSERT INTO transactions_goods_link (transaction_id, good_id) VALUES (:transaction_id, :good_id)")
        .addParameter("transaction_id", this.id)
        .addParameter("good_id",_goodsId)
        .executeUpdate();
      con.createQuery("UPDATE transactions Set salePrice = :salePrice WHERE id=:id")
        .addParameter("salePrice", this.salePrice)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public List<Integer> getGoodsIds() {
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery("SELECT good_id FROM transactions_goods_link WHERE transaction_id=:transaction_id")
      .addParameter("transaction_id", this.id)
      .executeAndFetch(Integer.class);
    }
  }

}
