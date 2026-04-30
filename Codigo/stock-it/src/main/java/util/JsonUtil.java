// util/JsonUtil.java
package util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class JsonUtil {
  public static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (d,t,c) ->
          d == null ? null : new JsonPrimitive(d.format(DateTimeFormatter.ISO_LOCAL_DATE)))
      .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (j,t,c) ->
          j == null || j.isJsonNull() ? null : LocalDate.parse(j.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
      .create();
  private JsonUtil() {}
}
