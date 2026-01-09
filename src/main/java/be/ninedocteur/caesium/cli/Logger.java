package be.ninedocteur.caesium.cli;

public final class Logger {
    private static final String RESET = "\u001B[0m";
    private static final String FG_BLACK = "\u001B[30m";
    private static final String FG_WHITE = "\u001B[37m";
    private static final String BG_WHITE = "\u001B[47m";
    private static final String BG_GREEN = "\u001B[42m";
    private static final String BG_ORANGE = "\u001B[48;5;208m";
    private static final String BG_RED = "\u001B[41m";

    private static final boolean COLORS_ENABLED = isColorEnabled();

    private Logger() {
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void success(String message) {
        log(Level.SUCCESS, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    private static void log(Level level, String message) {
        String label = formatLabel(level);
        String output = label + " " + message;
        if (level == Level.ERROR || level == Level.WARN) {
            System.err.println(output);
        } else {
            System.out.println(output);
        }
    }

    private static String formatLabel(Level level) {
        if (!COLORS_ENABLED) {
            return "[" + level.label + "]";
        }
        return level.bg + level.fg + " " + level.label + " " + RESET;
    }

    private static boolean isColorEnabled() {
        if (System.getenv("NO_COLOR") != null) {
            return false;
        }
        return System.console() != null;
    }

    private enum Level {
        INFO("INFO", BG_WHITE, FG_BLACK),
        SUCCESS("SUCCESS", BG_GREEN, FG_BLACK),
        WARN("WARN", BG_ORANGE, FG_BLACK),
        ERROR("ERROR", BG_RED, FG_WHITE);

        private final String label;
        private final String bg;
        private final String fg;

        Level(String label, String bg, String fg) {
            this.label = label;
            this.bg = bg;
            this.fg = fg;
        }
    }
}
