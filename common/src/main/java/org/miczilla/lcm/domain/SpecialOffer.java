package org.miczilla.lcm.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.miczilla.lcm.JsonHelper;

public class SpecialOffer
{
  public static final String QUEUE_NAME = "org.miczilla.lcm.shop.SPECIAL_OFFER.";

  private String id = UUID.randomUUID().toString();
  private Set<Product> products = new LinkedHashSet<>();

  public String getId()
  {
    return id;
  }

  public void setId(final String id)
  {
    this.id = id;
  }

  public Set<Product> getProducts()
  {
    return products;
  }

  @JsonSetter
  public void setProducts(final Set<Product> products)
  {
    this.products = products;
  }

  public void setProducts(final Product... products)
  {
    Collections.addAll(this.products, products);
  }

  @Override
  public String toString()
  {
    return JsonHelper.marshal(this);
  }
}
