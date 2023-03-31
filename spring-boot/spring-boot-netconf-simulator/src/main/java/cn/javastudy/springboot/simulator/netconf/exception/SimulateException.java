package cn.javastudy.springboot.simulator.netconf.exception;

import java.text.MessageFormat;

public class SimulateException extends Exception {

    private static final long serialVersionUID = 1376436051277802732L;

    public SimulateException(final String formatPattern, Object... arguments) {
        super(formatMessage(formatPattern, arguments));
    }

    public SimulateException(final String formatPattern, final Exception cause, Object... arguments) {
        super(formatMessage(formatPattern, arguments), cause);
    }

    private static String formatMessage(String formatPattern, Object... arguments) {
        MessageFormat format = new MessageFormat(formatPattern);
        return format.format(arguments);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
