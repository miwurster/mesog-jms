package org.miczilla.lcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.apache.commons.lang.StringUtils;

public final class ConsoleHelper
{
  public static String readLine()
  {
    java.io.Console console = System.console();

    if (console != null)
    {
      return System.console().readLine();
    }
    else
    {
      try
      {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        return buffer.readLine();
      }
      catch (IOException e)
      {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  public static String readLine(String fmt, Object... args)
  {
    java.io.Console console = System.console();

    if (console != null)
    {
      return System.console().readLine(fmt, args);
    }
    else
    {
      print(fmt, args);
      try
      {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        return buffer.readLine();
      }
      catch (IOException e)
      {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  public static void print(String text, Object... args)
  {
    printf(System.out, text, args);
  }

  public static void printErr(String text, Object... args)
  {
    printf(System.err, text, args);
  }

  public static void printf(PrintStream stream, String text, Object... args)
  {
    stream.printf(text, args);
  }

  public static void println()
  {
    println(Consts.EMPTY_STRING);
  }

  public static void println(String text, Object... args)
  {
    print(text + Consts.NL, args);
  }

  public static void printlnErr(String text, Object... args)
  {
    printErr(text + Consts.NL, args);
  }

  public static void printHeader(String text, Object... args)
  {
    String fullText = String.format(text, args);
    println(StringUtils.center(Consts.EMPTY_STRING, 80, '='));
    println(String.format("==== %s ====", StringUtils.center(fullText, 70)));
    println(StringUtils.center(Consts.EMPTY_STRING, 80, '='));
  }

  public static void printHeadline(String text, Object... args)
  {
    String fullText = "   " + String.format(text, args) + "   ";
    println(StringUtils.center(fullText, 40, '+'));
  }

  public static void printSeparator()
  {
    println(StringUtils.center(Consts.EMPTY_STRING, 80, '='));
  }

  private ConsoleHelper()
  {
    throw new UnsupportedOperationException();
  }
}
