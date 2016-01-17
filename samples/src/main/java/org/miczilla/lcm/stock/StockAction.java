package org.miczilla.lcm.stock;

public class StockAction
{
  private String id;
  private String company;
  private int amount;
  private int price;

  public StockAction()
  {
  }

  public StockAction(final String id, final String company, final int amount, final int price)
  {
    this.id = id;
    this.company = company;
    this.amount = amount;
    this.price = price;
  }

  public String getId()
  {
    return id;
  }

  public void setId(final String id)
  {
    this.id = id;
  }

  public String getCompany()
  {
    return company;
  }

  public void setCompany(final String company)
  {
    this.company = company;
  }

  public int getAmount()
  {
    return amount;
  }

  public void setAmount(final int amount)
  {
    this.amount = amount;
  }

  public int getPrice()
  {
    return price;
  }

  public void setPrice(final int price)
  {
    this.price = price;
  }
}
