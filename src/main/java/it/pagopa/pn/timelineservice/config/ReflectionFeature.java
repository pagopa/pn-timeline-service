package it.pagopa.pn.timelineservice.config;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionFeature implements Feature {
    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        List<String> packages = List.of(
                "it.pagopa.pn.timelineservice.middleware.dao.dynamo.entity",
                "it.pagopa.pn.timelineservice.generated.openapi.server.v1.dto",
                "it.pagopa.pn.timelineservice.generated.openapi.msclient.datavault.model",
                "it.pagopa.pn.timelineservice.dto"
        );

        for (String packageName : packages) {
            registerPackageClasses(packageName);
        }
    }

    private void registerPackageClasses(String packageName) {
        // Implementazione semplificata se conosci già le classi
        // o se puoi usare convenzioni di naming
        Set<Class<?>> classes = findClassesInPackage(packageName);

        for (Class<?> clazz : classes) {
            RuntimeReflection.register(clazz);
            RuntimeReflection.register(clazz.getDeclaredFields());
            RuntimeReflection.register(clazz.getDeclaredMethods());
            RuntimeReflection.register(clazz.getDeclaredConstructors());
        }
    }

    private Set<Class<?>> findClassesInPackage(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                classes.addAll(findClasses(resource, packageName));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error scanning package: " + packageName, e);
        }

        return classes;
    }

    private Set<Class<?>> findClasses(URL resource, String packageName) throws IOException {
        Set<Class<?>> classes = new HashSet<>();

        if (resource.getProtocol().equals("file")) {
            // Directory nel filesystem
            File directory = new File(resource.getFile());
            classes.addAll(findClassesInDirectory(directory, packageName));
        } else if (resource.getProtocol().equals("jar")) {
            // JAR file
            String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
            classes.addAll(findClassesInJar(jarPath, packageName));
        }

        return classes;
    }

    private Set<Class<?>> findClassesInDirectory(File directory, String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        // Ignora classi che non possono essere caricate
                    }
                }
            }
        }

        return classes;
    }

    private Set<Class<?>> findClassesInJar(String jarPath, String packageName) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');

        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.startsWith(packagePath) && name.endsWith(".class")) {
                    String className = name.replace('/', '.').substring(0, name.length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        // Ignora classi che non possono essere caricate
                    }
                }
            }
        }

        return classes;
    }
}
