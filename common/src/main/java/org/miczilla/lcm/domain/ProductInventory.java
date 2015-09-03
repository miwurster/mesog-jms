package org.miczilla.lcm.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.miczilla.lcm.JsonHelper;

public class ProductInventory
{
  private Product product;
  private int amount;

  public ProductInventory(final Product product, final int amount)
  {
    this.product = product;
    this.amount = amount;
  }

  public ProductInventory(final Product product)
  {
    this(product, 0);
  }

  public Product getProduct()
  {
    return product;
  }

  public void setProduct(final Product product)
  {
    this.product = product;
  }

  public int getAmount()
  {
    return amount;
  }

  public void setAmount(final int amount)
  {
    this.amount = amount;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final ProductInventory that = (ProductInventory) o;
    return new EqualsBuilder()
      .append(product, that.product)
      .isEquals();
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder(17, 37)
      .append(product)
      .toHashCode();
  }

  @Override
  public String toString()
  {
    return JsonHelper.marshal(this);
  }
}
