package space.baiye.rpc.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Baiye on 2022/1/12.
 *
 * @author Baiye
 */
@Slf4j
public class ClassUtils {

    private String defaultClassPath = ClassUtils.class.getResource("/").getPath();

    private static Set<Class<?>> CLASS_SET = new HashSet<>();

    private static String CLASS_SUFFIX = ".class";

    public static ClassLoader getClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * loadClass
     * @param className
     * @param isInitialized 代表不初始化该类 默认不需要初始化
     * @return
     */
    public static Class<?> loadClass(String className,boolean isInitialized)
    {
        Class<?> cls = null;
        try {
            cls = Class.forName(className,isInitialized,getClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("load class failure",e);
            throw new RuntimeException(e);
        }

        return cls;
    }

    private static void doAddClass(String className) {
        Class<?> cls = loadClass(className,false);
        CLASS_SET.add(cls);
    }

    public static Set<Class<?>> getClassSet (String packageName) {
        if (!CLASS_SET.isEmpty()) {
            return CLASS_SET;
        }
        //避免多次扫描
        synchronized (ClassUtils.class) {
            try {
                Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    if (url == null) {
                        continue;
                    }
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        //替换空格
                        String packagePath = url.getPath().replaceAll("%20", "");
                        addClasses(packagePath,packageName);
                    } else if ("jar".equals(protocol)) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection == null) {
                            continue;
                        }
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if (jarFile == null) {
                            continue;
                        }
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry jarEntry = entries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            if (jarEntryName.endsWith(CLASS_SUFFIX)) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                doAddClass(className);
                            }
                        }

                    }
                }
            } catch (IOException e) {
                log.error("scan class error:{}",e);
            }

        }
        return CLASS_SET;
    }

    private static void addClasses(String packagePath,String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.isFile() && f.getName().endsWith(CLASS_SUFFIX)) || f.isDirectory();
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if (StringUtils.isNotEmpty(packageName)) {
                    className = packageName.concat(".").concat(className);
                }
                doAddClass(className);
            } else {
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath.concat("/").concat(subPackagePath);
                }

                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName.concat(".").concat(subPackageName);
                }
                addClasses(subPackagePath,subPackageName);
            }
        }

    }

    public static void main(String[] args) {

        System.out.println(ClassUtils.class.getName());
        String defaultClassPath = ClassUtils.class.getResource("/").getPath();

        String packageName = "space.baiye";
        String basePackPath = packageName.replace(".",File.separator);
        String searchPath = defaultClassPath + basePackPath;

        Set<Class<?>> classSet = getClassSet(packageName);
        for (Class<?> aClass : classSet) {
            System.out.println(aClass.getName());
        }
    }




}
