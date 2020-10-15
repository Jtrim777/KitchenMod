package jake.kitchenmod.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ModHacker {

  public static void updateField(Object obj, String fieldName, Object newValue)
      throws NoSuchFieldException, IllegalAccessException {

    Field f = obj.getClass().getDeclaredField(fieldName);

    f.setAccessible(true);

    f.set(obj, newValue);
  }

  public static void updateClassConstant(Class clazz, String fieldName, Object newValue)
    throws NoSuchFieldException, IllegalAccessException {
    Field f = clazz.getDeclaredField(fieldName);

    f.setAccessible(true);

    Field mods = Field.class.getDeclaredField("modifiers");
    mods.setAccessible(true);

    int omods = f.getModifiers();

    mods.setInt(f, f.getModifiers() & ~Modifier.FINAL);

    f.set(null, newValue);

    mods.setInt(f, omods);
  }
}
