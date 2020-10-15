package jake.kitchenmod.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModTaskManager {
  private Map<TaskKey, Executor> tasks;

  public ModTaskManager() {
    tasks = new HashMap<>();
  }

  public ModTaskManager addTask(TaskKey key, Executor task) {
    if (tasks.containsKey(key) && tasks.get(key) != Executor.DO_NOTHING) {
      Executor current = tasks.get(key);

      tasks.put(key, current.andThen(task));
    } else {
      tasks.put(key, task);
    }

    return this;
  }

  public void fireTask(TaskKey key) {
    this.tasks.getOrDefault(key, () -> {}).execute();
  }

  public static <E extends Event> void deferTask(Consumer<E> taskWrapper, IEventBus bus) {
    bus.addListener(taskWrapper);
  }

  public static class TaskKey {
    private String name;

    public TaskKey(String name) {
      this.name = name;
    }
  }
}
