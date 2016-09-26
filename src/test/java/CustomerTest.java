import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;

public class CustomerTest{

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void getMethods_returnInternalValues_varies() {
    Customer newCustomer = new Customer("French Customer");
    newCustomer.save();
    assertTrue("all test", Customer.all().get(0).equals(newCustomer));
    assertEquals("getName test", "French Customer", newCustomer.getName());
    assertEquals("getId test", newCustomer.getId(), Customer.all().get(0).getId());
    assertTrue("findById test", Customer.findById(newCustomer.getId()).equals(newCustomer));
  }

}
