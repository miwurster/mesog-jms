package org.miczilla.lcm.command;

import java.util.Set;
import org.miczilla.lcm.ConsoleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCommand implements ConsoleCommand
{
  private static Logger logger = LoggerFactory.getLogger(DefaultCommand.class);

  @Override
  public void execute(final Set<String> args)
  {
    logger.warn("Not yet implemented...");
  }
}
