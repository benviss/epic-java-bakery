import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;

public class TransactionTest{

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void getMethods_returnInternalValues_varies() {
    Transaction newTransaction = new Transaction(1);
    newTransaction.save();
    assertTrue("all test", Transaction.all().get(0).equals(newTransaction));
    assertEquals("getcustomerId test", 1, newTransaction.getCustomerId());
    assertEquals("getId test", newTransaction.getId(), Transaction.all().get(0).getId());
    assertTrue("findById test", Transaction.findById(newTransaction.getId()).equals(newTransaction));
  }

  // @Test
  // public void getGoods_returnsAllGoodsInTransaction_List() {
  //   Transaction newTransaction = new Transaction(1);
  //   newTransaction.save();
  //   Good testGood1 = new Good("French bread", 5, 10);
  //   Good testGood2 = new Good("French bread", 5, 10);
  //   testGood2.save();
  //   testGood1.save();
  //   assertEquals("")
  // }


}
