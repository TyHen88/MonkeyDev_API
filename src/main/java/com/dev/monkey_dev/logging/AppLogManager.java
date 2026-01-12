package com.dev.monkey_dev.logging;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dev.monkey_dev.util.StringUtils;

/**
 * Centralized logging manager for the application.
 * Provides structured logging with automatic caller detection and proper
 * exception handling.
 */
public class AppLogManager {

    private static final String LOG_FORMAT = "{0}:{1} - {2}";

    /**
     * Gets the caller class name from the stack trace.
     *
     * @param level the stack trace level
     * @return the caller class name
     */
    private static String getCallerAsString(int level) {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        if (stElements.length <= level + 1) {
            return "Unknown";
        }
        String rawFQN = stElements[level + 1].toString().split("\\(")[0];
        return rawFQN.substring(0, StringUtils.lastIndexOf(rawFQN, "."));
    }

    /**
     * Parses caller information and formats the log message.
     *
     * @param str the caller class name
     * @param x   the message object
     * @return array containing [category, formatted message]
     */
    private static String[] parseCaller(String str, Object x) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int lineNumber = stackTrace.length > 4 ? stackTrace[4].getLineNumber() : 0;
        String fileName = StringUtils.substringAfterLast(str, ".") + ".java";
        String formattedMessage = MessageFormat.format(LOG_FORMAT, fileName, lineNumber, String.valueOf(x));
        return new String[] { str, formattedMessage };
    }

    /**
     * Parses caller information automatically.
     *
     * @param x the message object
     * @return array containing [category, formatted message]
     */
    private static String[] parseCaller(Object x) {
        return parseCaller(getCallerAsString(3), String.valueOf(x));
    }

    // ==================== DEBUG LEVEL ====================

    /**
     * Logs a DEBUG message with automatic caller detection.
     *
     * @param x the message to log
     */
    public static void debug(Object x) {
        String[] parsed = parseCaller(String.valueOf(x));
        debug(parsed[0], parsed[1]);
    }

    /**
     * Logs a DEBUG message for a specific category.
     *
     * @param category the logger category
     * @param x        the message to log
     */
    public static void debug(String category, Object x) {
        Logger logger = getLogger(category);
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(x));
        }
    }

    /**
     * Logs a DEBUG message for a specific class.
     *
     * @param caller the caller class
     * @param x      the message to log
     */
    public static void debug(Class<?> caller, Object x) {
        String[] parsed = parseCaller(String.valueOf(x));
        debug(caller.getName(), parsed[1]);
    }

    /**
     * Logs a DEBUG message with format and arguments.
     *
     * @param category the logger category
     * @param format   the message format
     * @param args     the format arguments
     */
    public static void debug(String category, String format, Object... args) {
        Logger logger = getLogger(category);
        if (logger.isDebugEnabled()) {
            logger.debug(format, args);
        }
    }

    // ==================== INFO LEVEL ====================

    /**
     * Logs an INFO message with automatic caller detection.
     *
     * @param x the message to log
     */
    public static void info(Object x) {
        String[] parsed = parseCaller(String.valueOf(x));
        info(parsed[0], parsed[1]);
    }

    /**
     * Logs an INFO message for a specific category.
     *
     * @param category the logger category
     * @param x        the message to log
     */
    public static void info(String category, Object x) {
        Logger logger = getLogger(category);
        logger.info(String.valueOf(x));
    }

    /**
     * Logs an INFO message for a specific class.
     *
     * @param caller the caller class
     * @param x      the message to log
     */
    public static void info(Class<?> caller, Object x) {
        String[] parsed = parseCaller(String.valueOf(x));
        info(caller.getName(), parsed[1]);
    }

    /**
     * Logs an INFO message with format and arguments.
     *
     * @param category the logger category
     * @param format   the message format
     * @param args     the format arguments
     */
    public static void info(String category, String format, Object... args) {
        Logger logger = getLogger(category);
        logger.info(format, args);
    }

    // ==================== WARN LEVEL ====================

    /**
     * Logs a WARN message with automatic caller detection.
     *
     * @param x the message to log
     */
    public static void warn(Object x) {
        String[] parsed = parseCaller(String.valueOf(x));
        warn(parsed[0], parsed[1]);
    }

    /**
     * Logs a WARN message for a specific category.
     *
     * @param category the logger category
     * @param x        the message to log
     */
    public static void warn(String category, Object x) {
        Logger logger = getLogger(category);
        logger.warn(String.valueOf(x));
    }

    /**
     * Logs a WARN message for a specific class.
     *
     * @param caller the caller class
     * @param x      the message to log
     */
    public static void warn(Class<?> caller, Object x) {
        String[] parsed = parseCaller(String.valueOf(x));
        warn(caller.getName(), parsed[1]);
    }

    /**
     * Logs a WARN message with format and arguments.
     *
     * @param category the logger category
     * @param format   the message format
     * @param args     the format arguments
     */
    public static void warn(String category, String format, Object... args) {
        Logger logger = getLogger(category);
        logger.warn(format, args);
    }

    /**
     * Logs a WARN message with an exception.
     *
     * @param category the logger category
     * @param message  the message to log
     * @param ex       the exception
     */
    public static void warn(String category, String message, Throwable ex) {
        Logger logger = getLogger(category);
        logger.warn(message, ex);
    }

    // ==================== ERROR LEVEL ====================

    /**
     * Logs an ERROR message with automatic caller detection.
     *
     * @param x the message to log
     */
    public static void error(Object x) {
        String[] parsed = parseCaller(String.valueOf(x));
        error(parsed[0], parsed[1]);
    }

    /**
     * Logs an ERROR message for a specific category.
     *
     * @param category the logger category
     * @param x        the message to log
     */
    public static void error(String category, Object x) {
        Logger logger = getLogger(category);
        logger.error(String.valueOf(x));
    }

    /**
     * Logs an ERROR message with an exception.
     *
     * @param ex the exception to log
     */
    public static void error(Throwable ex) {
        String[] parsed = parseCaller(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName());
        error(parsed[0], parsed[1], ex);
    }

    /**
     * Logs an ERROR message for a specific category with an exception.
     *
     * @param category the logger category
     * @param message  the message to log
     * @param ex       the exception (can be null)
     */
    public static void error(String category, Object message, Throwable ex) {
        Logger logger = getLogger(category);
        if (ex != null) {
            logger.error(String.valueOf(message), ex);
        } else {
            logger.error(String.valueOf(message));
        }
    }

    /**
     * Logs an ERROR message for a specific class with an exception.
     *
     * @param caller  the caller class
     * @param message the message to log
     * @param ex      the exception (can be null)
     */
    public static void error(Class<?> caller, String message, Throwable ex) {
        error(caller.getName(), message, ex);
    }

    /**
     * Logs an ERROR message with format and arguments.
     *
     * @param category the logger category
     * @param format   the message format
     * @param args     the format arguments
     */
    public static void error(String category, String format, Object... args) {
        Logger logger = getLogger(category);
        logger.error(format, args);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Gets a logger instance for the specified category.
     *
     * @param category the logger category
     * @return the logger instance
     */
    public static Logger getLogger(String category) {
        if (StringUtils.isBlank(category)) {
            return getRootLogger();
        }
        return LoggerFactory.getLogger(category);
    }

    /**
     * Gets a logger instance for the specified class.
     *
     * @param clazz the class
     * @return the logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Gets the root logger instance.
     *
     * @return the root logger
     */
    public static Logger getRootLogger() {
        return LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }
}