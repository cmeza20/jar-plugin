package com.cmeza.java.jar.plugin.manager;

import com.cmeza.java.jar.plugin.JarPlugin;
import com.cmeza.java.jar.plugin.exceptions.JarPluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public final class JarPluginManager<T> implements JarPlugin<T> {
    private static final Logger log = LoggerFactory.getLogger(JarPluginManager.class);
    private final Builder<T> builder;

    private JarPluginManager(Builder<T> builder) {
        this.builder = builder;
    }

    public static <T> JarPluginManager.Builder<T> builder(Class<T> clazz) {
        return new JarPluginManager.Builder<>(clazz);
    }

    @Override
    public Optional<T> loadJar(File pluginFile) {
        if (Objects.isNull(pluginFile) || !pluginFile.exists()) {
            throw new JarPluginException("Jar File not found.");
        }

        if (!pluginFile.isFile()) {
            throw new JarPluginException(pluginFile.getAbsolutePath() + " it's not a jar");
        }

        PluginExecute<T> pluginExecute = new PluginExecute<>(pluginFile, builder);

        return pluginExecute.execute();
    }

    @Override
    public List<T> loadJarDirectory(File pluginDirectory) {
        if (Objects.isNull(pluginDirectory) || !pluginDirectory.exists() || !pluginDirectory.isDirectory()) {
            throw new JarPluginException("Jar Directory not found.");
        }

        log.debug("Search for .jar file in the folder: {}", pluginDirectory);

        File[] pluginFiles = pluginDirectory.listFiles((d, n) -> n.endsWith(".jar"));
        if (Objects.isNull(pluginFiles) || pluginFiles.length == 0) {
            log.warn("jars not found in: {}", pluginDirectory);
            return Collections.emptyList();
        }

        List<T> result = new LinkedList<>();
        for (File pluginFile : pluginFiles) {
            Optional<T> res = loadJar(pluginFile);
            res.ifPresent(result::add);
        }
        return result;
    }

    public static class Builder<T> {
        private final Class<T> classType;
        private String entryName = "Plugin-Entry";
        private String disabledPluginSuffix = ".disabled.jar";

        public Builder(Class<T> classType) {
            this.classType = classType;
        }

        public String getEntryName() {
            return entryName;
        }

        public Builder<T> setEntryName(String entryName) {
            this.entryName = entryName;
            return this;
        }

        public String getDisabledPluginSuffix() {
            return disabledPluginSuffix;
        }

        public Builder<T> setDisabledPluginSuffix(String disabledPluginSuffix) {
            this.disabledPluginSuffix = disabledPluginSuffix;
            return this;
        }

        public Class<T> getClassType() {
            return classType;
        }

        public JarPlugin<T> build() {
            if (Objects.isNull(classType)) {
                throw new JarPluginException("ClassType is required");
            }
            if (!classType.isInterface()) {
                throw new JarPluginException("ClassType has to be an interface");
            }
            if (Objects.isNull(entryName) || entryName.isEmpty()) {
                throw new JarPluginException("Entry name is required");
            }
            if (Objects.isNull(disabledPluginSuffix) || disabledPluginSuffix.isEmpty()) {
                throw new JarPluginException("DisabledPluginSuffix is required");
            }
            return new JarPluginManager<>(this);
        }
    }
}
