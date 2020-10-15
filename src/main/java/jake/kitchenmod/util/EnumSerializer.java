package jake.kitchenmod.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;

public class EnumSerializer<E extends Enum<E>> implements IDataSerializer<E> {

  public static <X extends Enum<X>> EnumSerializer<X> create(Class<X> type) {
    EnumSerializer<X> result = new EnumSerializer<>(type);
    DataSerializers.registerSerializer(result);

    return result;
  }

  private Class<E> enumType;

  private EnumSerializer(Class<E> enumType) {
    this.enumType = enumType;
  }

  @Override
  public void write(PacketBuffer buf, E value) {
    buf.writeByte(value.ordinal());
  }

  @Override
  public E read(PacketBuffer buf) {
    int ordinal = buf.readByte();

    return enumType.getEnumConstants()[ordinal];
  }

  @Override
  public E copyValue(E value) {
    return value;
  }
}
