package org.miczilla.lcm.shop;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.miczilla.lcm.domain.CategorySubscription;
import org.miczilla.lcm.domain.Product;
import org.miczilla.lcm.domain.ProductInventory;
import org.springframework.stereotype.Component;

@Component
public class DataAccess
{
  private Set<ProductInventory> productInventory = new LinkedHashSet<>();
  private Set<CategorySubscription> categorySubscriptions = new LinkedHashSet<>();

  @PostConstruct
  public void bootstrapInventory()
  {
    productInventory.add(new ProductInventory(
      Product.create()
        .id("123")
        .name("Ultralight")
        .producerName("Lighthouse")
        .price(15)
        .weight(0.1)
        .category("Flashlights")
        .build(), 1000));
    productInventory.add(new ProductInventory(
      Product.create()
        .id("456")
        .name("NeoX 512GB")
        .producerName("Samsung")
        .price(150)
        .weight(0.05)
        .category("HDD")
        .build(), 1000));
    productInventory.add(new ProductInventory(
      Product.create()
        .id("789")
        .name("NeoFlash 16GB")
        .producerName("Samsung")
        .price(45)
        .weight(0.03)
        .category("USB drives")
        .category("HDD")
        .build(), 1000));
  }

  public Set<ProductInventory> getProductInventory()
  {
    return this.productInventory;
  }

  public Set<CategorySubscription> getCategorySubscriptions()
  {
    return this.categorySubscriptions;
  }

  public void createOrUpdate(final CategorySubscription subscription)
  {
    this.categorySubscriptions.add(subscription);
  }

  public Product lookupProductById(final String id)
  {
    for (ProductInventory inventory : this.productInventory)
    {
      Product product = inventory.getProduct();
      if (id.equalsIgnoreCase(inventory.getProduct().getId()))
      {
        return product;
      }
    }
    return null;
  }

  public Set<CategorySubscription> lookupCategorySubscriptions(final Set<String> categories)
  {
    Set<CategorySubscription> subscriptions = new HashSet<>();
    for (CategorySubscription subscription : this.categorySubscriptions)
    {
      if (CollectionUtils.containsAny(subscription.getCategoryList(), categories))
      {
        subscriptions.add(subscription);
      }
    }
    return subscriptions;
  }

//  public boolean checkProductAvailability(final Product product, final int amount)
//  {
//    ProductInventory inventory =
//      (ProductInventory) CollectionUtils.find(productInventory, new ProductPredicate(product));
//    return inventory != null && inventory.getAmount() >= amount;
//  }
//
//  public boolean checkAvailability(final Set<OrderEntry> order)
//  {
//    for (OrderEntry entry : order)
//    {
//      boolean productAvailable
//        = checkProductAvailability(entry.getProduct(), entry.getAmount());
//      if (!productAvailable)
//      {
//        return false;
//      }
//    }
//    return true;
//  }

//  private static class ProductPredicate implements Predicate
//  {
//    private Product product;
//
//    public ProductPredicate(final Product product)
//    {
//      this.product = product;
//    }
//
//    @Override
//    public boolean evaluate(final Object object)
//    {
//      ProductInventory o = (ProductInventory) object;
//      return product.equals(o.getProduct());
//    }
//  }
}
