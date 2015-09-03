package org.miczilla.lcm.shop.command;

import java.util.Set;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.domain.CategorySubscription;
import org.miczilla.lcm.shop.DataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListSubscriptionsCommand implements ConsoleCommand
{
  @Autowired
  private DataAccess dataAccess;

  @Override
  public void execute(final Set<String> args)
  {
    final Set<CategorySubscription> subscriptions = dataAccess.getCategorySubscriptions();
    if (subscriptions.size() == 0)
    {
      ConsoleHelper.println("No subscriptions available");
    }
    for (CategorySubscription subscription : subscriptions)
    {
      ConsoleHelper.println(subscription.toString());
    }
  }
}
