package org.miczilla.lcm;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

public final class JsonHelper
{
  private static ObjectMapper mapper = new ObjectMapper();

  public static String marshal(final Object data)
  {
    try
    {
      return mapper.writeValueAsString(data);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static <T> T unmarshal(final String content, final Class<T> contentType)
  {
    try
    {
      return mapper.readValue(content, contentType);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static String prettyPrint(final Object data)
  {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    try
    {
      return mapper.writeValueAsString(data);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException(e.getMessage());
    }
  }

  private JsonHelper()
  {
    throw new UnsupportedOperationException();
  }
}
