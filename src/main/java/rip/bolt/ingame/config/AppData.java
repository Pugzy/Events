package rip.bolt.ingame.config;

import rip.bolt.ingame.Tournament;

public class AppData {

    public static class API {

        public static boolean isEnabled() {
            return Tournament.get().getConfig().getBoolean("api.enabled");
        }

        public static String getURL() {
            return Tournament.get().getConfig().getString("api.url");
        }

        public static String getKey() {
            return Tournament.get().getConfig().getString("api.key");
        }

        public static String getGetMatchPath() {
            return Tournament.get().getConfig().getString("api.getMatch").replace("{server}", getServerName());            
        }

        public static String getPlayerAbandonPath() {
            return Tournament.get().getConfig().getString("api.playerAbandon");
        }

        public static String getMatchResultsPath() {
            return Tournament.get().getConfig().getString("api.matchResults").replace("{server}", getServerName());
        }

        private static String getServerName() {
            return String.valueOf(System.getenv("SERVER_NAME"));
        }

    }

    public static boolean observersMustReady() {
        return Tournament.get().getConfig().getBoolean("observers-must-ready");
    }

    public static long absentSecondsLimit() {
        return Tournament.get().getConfig().getLong("absence-time-seconds", 120);
    }

}
