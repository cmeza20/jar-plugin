package com.cmeza.java.jar.plugin;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface JarPlugin<T> {
    Optional<T> loadJar(File pluginFile);

    List<T> loadJarDirectory(File pluginDirectory);

}
