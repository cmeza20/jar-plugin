package com.cmeza.java.jar.plugin.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginExecute<T> {
    private static final Logger log = LoggerFactory.getLogger(PluginExecute.class);
    private final File pluginFile;
    private final JarPluginManager.Builder<T> builder;

    public PluginExecute(File pluginFile, JarPluginManager.Builder<T> builder) {
        this.pluginFile = pluginFile;
        this.builder = builder;
    }

    @SuppressWarnings("unchecked")
    public Optional<T> execute() {
        try {
            log.info("Initializing jar file: {}", pluginFile);

            if (pluginFile.getName().endsWith(builder.getDisabledPluginSuffix())) {
                log.info("Disabled plugin: " + pluginFile + ", ignored.");
                return Optional.empty();
            }

            JarFile jarFile = new JarFile(pluginFile);
            Manifest manifest = jarFile.getManifest();
            String entryClass = manifest.getMainAttributes().getValue(builder.getEntryName());

            if (Objects.isNull(entryClass) || entryClass.isEmpty()) {
                log.warn(pluginFile.getAbsolutePath() + " does not contain the '" + builder.getEntryName() + "' attribute in its Manifest");
                return Optional.empty();
            }

            log.debug(entryClass + " found in jar file");

            PluginClassLoader classLoader = new PluginClassLoader(jarFile);
            Class<?> klass = Class.forName(entryClass, false, classLoader);
            if (!Arrays.asList(klass.getInterfaces()).contains(builder.getClassType())) {
                log.error(klass.getName() + " does not implement the " + builder.getClassType().getName() + " interface");
                return Optional.empty();
            }

            log.debug(klass + " implements interface " + builder.getClassType().getName());

            T result = (T) klass.newInstance();
            log.info("Plugin loaded: " + pluginFile.getAbsolutePath());

            return Optional.of(result);
        } catch (Throwable e) {
            log.error("Parse plugin info failed", e);
            return Optional.empty();
        }
    }
}
