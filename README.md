# Java Jar Plugin [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cmeza/java-jar-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cmeza/java-jar-plugin)

Dynamic loading of .Jar files

### Wiki ##
### Home
* [Home](https://github.com/cmeza20/java-jar-plugin/wiki)
* [Get Started](https://github.com/cmeza20/java-jar-plugin/wiki/Get-Started)

***

### Manager
* [JarPluginManager](https://github.com/cmeza20/java-jar-plugin/wiki/JarPluginManager)
* [JarPluginManager.Builder](https://github.com/cmeza20/java-jar-plugin/wiki/JarPluginManager.Builder)

***

### Logger
* [Slf4j Api](https://github.com/cmeza20/java-jar-plugin/wiki/Slf4j-Api)
* [Slf4j Simple Implementation](https://github.com/cmeza20/java-jar-plugin/wiki/Slf4j-Simple-Implementation)


### Example

###### InterfaceClass.java
```java
public interface InterfaceClass {
    void run();
}

```

###### Another Jar project (sample.jar)
```java
public class CustomPlugin implements InterfaceClass {
    @Override
    public void run() {
        System.out.println("plugin executed");
    }
}
```

###### Another Jar project (pom.xml) ######
###### _Set EntryName into MANIFEST_ ######
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <configuration>
                <archive>
                    <addMavenDescriptor>false</addMavenDescriptor>
                    <manifestEntries>
                        <Plugin-Entry>com.plugin.example.CustomPlugin</Plugin-Entry>
                    </manifestEntries>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>
```

###### JarPlugin initialized
```java
public class Main {
    public static void main(String[] args) {
        
        JarPlugin<InterfaceClass> jarPlugin = JarPluginManager.builder(InterfaceClass.class)
                .setEntryName("Plugin-Entry")
                .setDisabledPluginSuffix(".disabled.jar")
                .build();

        // Get InterfaceClass from sample.jar
        Optional<InterfaceClass> interfaceClassOptional = jarPlugin.loadJar(new File("/home/sample.jar"));
        
        // Get the InterfaceClass list from /home directory
        List<InterfaceClass> interfaceClassList = jarPlugin.loadJarDirectory(new File("/home"));
    }
}
```

License
----

MIT
