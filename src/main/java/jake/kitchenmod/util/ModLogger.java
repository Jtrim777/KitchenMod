package jake.kitchenmod.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModLogger {

  private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss");
  public static ModLogger DEFAULT;

  private Appendable outStream = null;
  private int levelAdvance = 0;
  private boolean logDebug = true;
  private boolean doubleSpace = true;

  private Map<String, Integer> tasks;

  public ModLogger(Appendable outStream) {
    this.outStream = outStream;
    this.tasks = new HashMap<>();

    DEFAULT = this;
  }

  public void direct(String filepath) throws IOException {
    outStream = new FileWriter(new File(filepath));
  }

  private String getTimestamp() {
    return FORMATTER.format(new Date());
  }

  private String assembleMessage(int level, String locale, String message, LogLevel severity) {
    String prefix = "";
    String footer = "";
    switch (severity) {
      case DEBUG:
        prefix = "\u001b[36m>>>";
        footer = "\u001b[0m";
        break;
      case WARNING:
        prefix = "\u001b[33m!";
        footer = "\u001b[0m";
        break;
      case ERROR:
        prefix = "\u001b[31m!!!";
        footer = "\u001b[0m";
        break;
    }

    String localeBracket = locale == null ? "" : " [" + locale + "]";

    if (level + levelAdvance > 0) {
      return String
          .format("%s%s[%s]%s %s%s", indent(level + levelAdvance), prefix, getTimestamp(),
              localeBracket, message, footer);
    } else {
      return String
          .format("%s[%s]%s :: %s%s", prefix, getTimestamp(), localeBracket, message, footer);
    }
  }

  private void write(String out) {
    try {
      outStream.append(out).append(doubleSpace ? "\n\n" : "\n");
      if (outStream instanceof FileWriter) {
        ((FileWriter) outStream).flush();
      }
    } catch (IOException e) {
      System.err.println("Failed to write to log: " + e.getMessage());
    }
  }

  private String indent(int level) {
    StringBuilder indent = new StringBuilder();
    for (int i = 0; i < level + levelAdvance; i++) {
      indent.append("    ");
    }

    return indent.toString();
  }

  public void log(String message, int level, String locale, LogLevel severity) {
    if (severity == LogLevel.DEBUG && !logDebug) {
      return;
    }

    String out = assembleMessage(level, locale, message, severity);

    write(out);
  }

  public void log(String message) {
    log(message, 0, null, LogLevel.INFO);
  }

  public void log(String message, int level) {
    log(message, level, null, LogLevel.INFO);
  }

  public void log(String message, String locale) {
    log(message, 0, locale, LogLevel.INFO);
  }

  public void log(String message, int level, String locale) {
    log(message, level, locale, LogLevel.INFO);
  }

  public void log(String message, LogLevel severity) {
    log(message, 0, null, severity);
  }

  public void log(String message, int level, LogLevel severity) {
    log(message, level, null, severity);
  }

  public void log(String message, String locale, LogLevel severity) {
    log(message, 0, locale, severity);
  }

  public void separate() {
    write("\n");
  }

  public void advanceLogLevel(int by) {
    this.levelAdvance += by;
  }
  public void reduceLogLevel(int by) {
    this.levelAdvance -= by;
  }


  public void returnLogLevel(int to) {
    this.levelAdvance = to;
  }

  public void startTask(String taskName) {
    if (tasks.containsKey(taskName)) {
      throw new IllegalArgumentException("The task " + taskName + " has already been started");
    }

    this.tasks.put(taskName, levelAdvance);

    doubleSpace = true;

    if (tasks.size() == 1) {
      write("[Task: " + taskName + "]");
    } else {
      write(indent(levelAdvance) + "[Subtask: " + taskName + "]");
    }

    advanceLogLevel(1);
  }

  public void startTask(String taskName, int depth) {
    if (tasks.containsKey(taskName)) {
      throw new IllegalArgumentException("The task " + taskName + " has already been started");
    }

    this.tasks.put(taskName, levelAdvance);
    advanceLogLevel(depth);
  }

  public void endTask(String taskName) {
    if (!tasks.containsKey(taskName)) {
      throw new IllegalArgumentException("No such task " + taskName + " exists");
    }

    int revert = this.tasks.remove(taskName);
    this.returnLogLevel(revert);

    if (levelAdvance == 0) {
      separate();
    }
  }

  public void resetLogLevel() {
    this.levelAdvance = 0;
  }

  public int getLogLevel() {
    return levelAdvance;
  }

  public void supressDebug() {
    this.logDebug = false;
  }

//  public static String format(String inp) {
//
//  }

  public enum LogLevel {
    INFO,
    DEBUG,
    WARNING,
    ERROR
  }
}

