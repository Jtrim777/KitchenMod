package jake.kitchenmod.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.World;

public class TimeHelper {
  public static long getTime(World world) {
    return world.getDayTime();
  }

  public static boolean isDaytime(long time) {
    return TimeSegment.DAY.contains(time);
  }

  public static List<TimeSegment> getMarkers(long time) {
    return Arrays.stream(TimeSegment.values()).filter(ts -> ts.contains(time))
        .collect(Collectors.toList());
  }

  public static boolean timeIs(long time, TimeSegment segment) {
    return segment.contains(time);
  }

  public enum TimeSegment {
    DAY(0, 12000),
    NIGHT(12000, 24000),
    DAWN(23000, 24000),
    DUSK(12000, 13000),
    MORNING(23000, 1000),
    EVENING(11000, 13000),
    NOON(5700, 6300),
    MIDNIGHT(17000, 18300);


    TimeSegment(int start, int end) {
      this.start = start;
      this.end = end;
    }

    private int start;
    private int end;

    boolean contains(long time) {
      if (end > start) {
        return time >= start && time <= end;
      } else {
        return (time >= start && time <= 24000)
            || (time >= 0 && time < end)
            || time == 0;
      }
    }
  }
}
