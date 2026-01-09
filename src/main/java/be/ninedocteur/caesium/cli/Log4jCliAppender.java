package be.ninedocteur.caesium.cli;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "CliLogger", category = "Core", elementType = Appender.ELEMENT_TYPE, printObject = true)
public final class Log4jCliAppender extends AbstractAppender {

    private Log4jCliAppender(String name, Filter filter) {
        super(name, filter, null, true);
    }

    @PluginFactory
    public static Log4jCliAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter) {
        return new Log4jCliAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();
        Throwable throwable = event.getThrown();
        Level level = event.getLevel();

        if (level == Level.ERROR || level == Level.FATAL) {
            Logger.error(message, throwable);
        } else if (level == Level.WARN) {
            Logger.warn(message);
        } else if (level == Level.INFO) {
            Logger.info(message);
        } else {
            Logger.info(message);
        }
    }
}
