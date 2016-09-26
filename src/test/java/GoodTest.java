import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;

public class GoodTest{

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void getMethods_returnInternalValues_varies() {
    Good newGood = new Good("French Good",1,2);
    newGood.save();
    assertTrue("all test", Good.all().get(0).equals(newGood));
    assertEquals("getName test", "French Good", newGood.getName());
    assertEquals("getStockPrice test", 1, newGood.getStockPrice());
    assertEquals("getSellPrice test", 2, newGood.getSellPrice());
    assertEquals("getId test", newGood.getId(), Good.all().get(0).getId());
    assertTrue("findById test", Good.findById(newGood.getId()).equals(newGood));
  }

}
