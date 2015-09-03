package org.miczilla.lcm.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.miczilla.lcm.JsonHelper;

public class OrderRequest
{
  public enum Type
  {
    REQUEST, ORDER, APPROVAL
  }

  public enum Status
  {
    APPROVED, REJECTED
  }

  private String id = UUID.randomUUID().toString();
  private Map<String, Integer> orderEntries = new HashMap<>();
  private Type type = Type.REQUEST;
  private Status status = Status.REJECTED;

  public String getId()
  {
    return id;
  }

  public void setId(final String id)
  {
    this.id = id;
  }

  public Map<String, Integer> getOrderEntries()
  {
    return orderEntries;
  }

  public void setOrderEntries(final Map<String, Integer> orderEntries)
  {
    this.orderEntries = orderEntries;
  }

  public void addOrderEntry(final String productId, final int amount)
  {
    this.orderEntries.put(productId, amount);
  }

  public Type getType()
  {
    return type;
  }

  public void setType(final Type type)
  {
    this.type = type;
  }

  public Status getStatus()
  {
    return status;
  }

  public void setStatus(final Status status)
  {
    this.status = status;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final OrderRequest that = (OrderRequest) o;
    return new EqualsBuilder()
      .append(id, that.id)
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
}
