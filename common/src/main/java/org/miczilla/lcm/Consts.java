package org.miczilla.lcm;

/**
 * Collected constants of general utility
 */
public final class Consts
{
  public static final String SPECIAL_OFFER_QUEUE_NAME = "org.miczilla.lcm.shop.SPECIAL_OFFER.";
  public static final String ORDER_APPROVAL_QUEUE_NAME = "org.miczilla.lcm.shop.ORDER_APPROVAL.";

  public static final boolean PASSES = true;
  public static final boolean FAILS = false;

  public static final boolean SUCCESS = true;
  public static final boolean FAILURE = false;

  public static final boolean TRUE = true;
  public static final boolean FALSE = false;

  public static final String EMPTY_STRING = "";
  public static final String SPACE = " ";
  public static final String TAB = "\t";
  public static final String SINGLE_QUOTE = "'";
  public static final String PERIOD = ".";
  public static final String DOUBLE_QUOTE = "\"";

  /**
   * Useful for {@link String} operations, which return an index of
   * <tt>-1</tt> when an item is not found.
   */
  public static final int NOT_FOUND = -1;

  /**
   * System property - <tt>line.separator</tt>
   */
  public static final String NL = System.getProperty("line.separator");

  /**
   * System property - <tt>file.separator</tt>
   */
  public static final String FS = System.getProperty("file.separator");

  /**
   * System property - <tt>path.separator</tt>
   */
  public static final String PS = System.getProperty("path.separator");

  /**
   * System property - <tt>java.io.tmpdir</tt>
   */
  public static final String TMPDIR = System.getProperty("java.io.tmpdir");

  private Consts()
  {
    throw new UnsupportedOperationException();
  }
}
