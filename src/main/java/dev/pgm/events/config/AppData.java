package dev.pgm.events.config;

import dev.pgm.events.Tournament;

public class AppData {

  public static boolean observersMustReady() {
    return Tournament.get().getConfig().getBoolean("observers-must-ready");
  }

  public static boolean fullTeamReadyCheck() {
    return Tournament.get().getConfig().getBoolean("ready-full-team-required");
  }

  public static boolean readyReminders() {
    return Tournament.get().getConfig().getBoolean("ready-reminders");
  }

  public static boolean autoUnready() {
    return Tournament.get().getConfig().getBoolean("auto-unready");
  }
}
