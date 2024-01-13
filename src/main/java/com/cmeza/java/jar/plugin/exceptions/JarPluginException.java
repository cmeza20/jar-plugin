package com.cmeza.java.jar.plugin.exceptions;

public class JarPluginException extends RuntimeException {
    public JarPluginException(String message) {
        super(message);
    }

    public JarPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
