package jake.kitchenmod.util;

public interface Executor {
  void execute();

  default Executor andThen(Executor second) {
    return () -> {
      this.execute();
      second.execute();
    };
  }

  Executor DO_NOTHING = () -> {};
}
