package org.miczilla.lcm.shop;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.miczilla.lcm.domain.OrderRequest;
import org.miczilla.lcm.domain.Product;

import static org.junit.Assert.*;

public class DataAccessTest {

  private DataAccess dataAccess;

  @Before
  public void setUp() throws Exception {
    dataAccess = new DataAccess();
    dataAccess.bootstrapInventory();
  }

  @Test
  public void checkProductAvailability_positive_test() throws Exception {
    final Product product = dataAccess.lookupProductById("456");
    Assert.assertTrue(dataAccess.checkProductAvailability(product, 1000));
  }

  @Test
  public void checkProductAvailability_negative_test() throws Exception {
    final Product product = dataAccess.lookupProductById("456");
    Assert.assertFalse(dataAccess.checkProductAvailability(product, 1001));
  }

  @Test
  public void checkProductAvailability_different_product_test() throws Exception {
    final Product product = Product.create().id("123").build();
    Assert.assertTrue(dataAccess.checkProductAvailability(product, 10));
  }

  @Test
  public void checkAvailability_positive_test() throws Exception {
    final OrderRequest orderRequest = new OrderRequest();
    orderRequest.addOrderEntry("123", 1000);
    orderRequest.addOrderEntry("456", 1000);
    Assert.assertTrue(dataAccess.checkAvailability(orderRequest));
  }

  @Test
  public void checkAvailability_negative_test() throws Exception {
    final OrderRequest orderRequest = new OrderRequest();
    orderRequest.addOrderEntry("123", 1000);
    orderRequest.addOrderEntry("456", 1001);
    Assert.assertFalse(dataAccess.checkAvailability(orderRequest));
  }
}
