package org.miczilla.lcm.customer.command;

import java.util.Queue;
import java.util.Set;
import javax.annotation.Resource;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.domain.SpecialOffer;
import org.springframework.stereotype.Component;

@Component
public class ShowOfferCommand implements ConsoleCommand
{
  @Resource(name = "specialOffers")
  private Queue<SpecialOffer> specialOffers;

  @Override
  public void execute(final Set<String> args)
  {
    final SpecialOffer offer = specialOffers.peek();
    if (offer == null)
    {
      ConsoleHelper.println("No offer has been received yet");
      return;
    }
    ConsoleHelper.println("Current offer:\n%s", offer.toString());
  }
}
