package org.miczilla.lcm.shop.command;

import java.util.Set;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.domain.ProductInventory;
import org.miczilla.lcm.shop.DataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListInventoryCommand implements ConsoleCommand
{
  @Autowired
  private DataAccess dataAccess;

  @Override
  public void execute(final Set<String> args)
  {
    for (ProductInventory inventory : dataAccess.getProductInventory())
    {
      ConsoleHelper.println(inventory.toString());
    }
  }
}
