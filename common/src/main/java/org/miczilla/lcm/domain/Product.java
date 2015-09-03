package org.miczilla.lcm.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.miczilla.lcm.JsonHelper;

public class Product
{
  private String id;
  private String name;
  private Set<String> category;
  private String producerName;
  private double weight;
  private double price;

  public Product()
  {
  }

  private Product(final Builder builder)
  {
    this.id = builder.id;
    this.name = builder.name;
    this.category = builder.category;
    this.producerName = builder.producerName;
    this.weight = builder.weight;
    this.price = builder.price;
  }

  public String getId()
  {
    return id;
  }

  public void setId(final String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public Set<String> getCategory()
  {
    return category;
  }

  public void setCategory(final Set<String> category)
  {
    this.category = category;
  }

  public String getProducerName()
  {
    return producerName;
  }

  public void setProducerName(final String producerName)
  {
    this.producerName = producerName;
  }

  public double getWeight()
  {
    return weight;
  }

  public void setWeight(final int weight)
  {
    this.weight = weight;
  }

  public double getPrice()
  {
    return price;
  }

  public void setPrice(final double price)
  {
    this.price = price;
  }

  public static Builder create()
  {
    return new Builder();
  }

  public static class Builder
  {
    private String id = UUID.randomUUID().toString();
    private String name = "";
    private Set<String> category = new LinkedHashSet<>();
    private String producerName = "";
    private double weight = 0.0;
    private double price = 0.0;

    public Builder id()
    {
      this.id = UUID.randomUUID().toString();
      return this;
    }

    public Builder id(final String id)
    {
      this.id = id;
      return this;
    }

    public Builder name(final String name)
    {
      this.name = name;
      return this;
    }

    public Builder category(final String category)
    {
      this.category.add(category);
      return this;
    }

    public Builder category(final String... category)
    {
      Collections.addAll(this.category, category);
      return this;
    }

    public Builder producerName(final String producerName)
    {
      this.producerName = producerName;
      return this;
    }

    public Builder weight(final double weight)
    {
      this.weight = weight;
      return this;
    }

    public Builder price(final double price)
    {
      this.price = price;
      return this;
    }

    public Product build()
    {
      return new Product(this);
    }
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Product product = (Product) o;
    return new EqualsBuilder()
      .append(id, product.id)
      .isEquals();
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder(17, 37)
      .append(id)
      .toHashCode();
  }

  @Override
  public String toString()
  {
    return JsonHelper.prettyPrint(this);
  }
}
