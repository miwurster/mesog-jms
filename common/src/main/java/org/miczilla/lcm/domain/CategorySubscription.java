package org.miczilla.lcm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.miczilla.lcm.JsonHelper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategorySubscription
{
  private String customer;

  @JsonProperty("categories")
  private Set<String> categoryList = new LinkedHashSet<>();

  public CategorySubscription()
  {
  }

  public CategorySubscription(final String customer, final String categories)
  {
    this.customer = customer;
    Collections.addAll(this.categoryList, categories.split(","));
  }

  public CategorySubscription(final String customer, final String[] categories)
  {
    this.customer = customer;
    Collections.addAll(this.categoryList, categories);
  }

  public String getCustomer()
  {
    return customer;
  }

  public void setCustomer(final String customer)
  {
    this.customer = customer;
  }

  public Set<String> getCategoryList()
  {
    return categoryList;
  }

  public void setCategoryList(final Set<String> categoryList)
  {
    this.categoryList = categoryList;
  }

  public String getReplyToQueueName()
  {
    return SpecialOffer.QUEUE_NAME + this.customer;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final CategorySubscription that = (CategorySubscription) o;
    return new EqualsBuilder()
      .append(customer, that.customer)
      .isEquals();
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder(17, 37)
      .append(customer)
      .toHashCode();
  }

  @Override
  public String toString()
  {
    return JsonHelper.marshal(this);
  }
}
