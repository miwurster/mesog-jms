package org.miczilla.lcm.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.miczilla.lcm.JsonHelper;

public class Order
{
  private String id = UUID.randomUUID().toString();
  private Map<String, Integer> orderEntries = new HashMap<>();
  private ShippingAddress shippingAddress;
  private PaymentDetails paymentDetails;

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public Map<String, Integer> getOrderEntries()
  {
    return orderEntries;
  }

  public void setOrderEntries(Map<String, Integer> orderEntries)
  {
    this.orderEntries = orderEntries;
  }

  public void addOrderEntry(final String productId, final int amount)
  {
    this.orderEntries.put(productId, amount);
  }

  public ShippingAddress getShippingAddress()
  {
    return shippingAddress;
  }

  public void setShippingAddress(ShippingAddress shippingAddress)
  {
    this.shippingAddress = shippingAddress;
  }

  public PaymentDetails getPaymentDetails()
  {
    return paymentDetails;
  }

  public void setPaymentDetails(PaymentDetails paymentDetails)
  {
    this.paymentDetails = paymentDetails;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Order order = (Order) o;
    return new EqualsBuilder()
      .append(id, order.id)
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
    return JsonHelper.marshal(this);
  }

  public static class ShippingAddress
  {
    private String name;
    private String street;
    private String zipCode;
    private String city;

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public String getStreet()
    {
      return street;
    }

    public void setStreet(String street)
    {
      this.street = street;
    }

    public String getZipCode()
    {
      return zipCode;
    }

    public void setZipCode(String zipCode)
    {
      this.zipCode = zipCode;
    }

    public String getCity()
    {
      return city;
    }

    public void setCity(String city)
    {
      this.city = city;
    }

    @Override
    public String toString()
    {
      return JsonHelper.marshal(this);
    }
  }

  public static class PaymentDetails
  {
    private String name;
    private String iban;
    private String bic;

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public String getIban()
    {
      return iban;
    }

    public void setIban(String iban)
    {
      this.iban = iban;
    }

    public String getBic()
    {
      return bic;
    }

    public void setBic(String bic)
    {
      this.bic = bic;
    }

    @Override
    public String toString()
    {
      return JsonHelper.marshal(this);
    }
  }
}
