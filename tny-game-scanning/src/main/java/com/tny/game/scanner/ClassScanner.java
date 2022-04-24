package com.tny.game.scanner;

import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.exception.*;
import org.apache.commons.lang3.*;
import org.slf4j.Logger;
import org.springframework.core.io.*;
import org.springframework.core.type.classreading.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.*;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.*;

public class ClassScanner {

    private static final Logger LOGGER = getLogger(ClassScanner.class);

    private final Collection<ClassSelector> selectors = new ConcurrentLinkedQueue<>();

    private ClassLoader classLoader;

    private boolean throwOnScanFail = true;

    public static ClassScanner instance(ClassLoader classLoader, boolean throwOnScanFail) {
        return new ClassScanner(classLoader, throwOnScanFail);
    }

    public static ClassScanner instance(ClassLoader classLoader, boolean autoLoadSelector, boolean throwOnScanFail) {
        return new ClassScanner(classLoader, autoLoadSelector, throwOnScanFail);
    }

    public static ClassScanner instance() {
        return new ClassScanner(ClassScanner.class.getClassLoader(), true);
    }

    private ClassScanner(boolean autoLoadSelector) {
        if (autoLoadSelector) {
            Set<Class<?>> classes = AutoClassScanConfigure.getClasses(ClassSelectorProvider.class);
            for (Class<?> clazz : classes) {
                List<ClassSelectorProviderInvoker> invokers = ClassSelectorProviderInvoker.instance(clazz);
                for (ClassSelectorProviderInvoker invoker : invokers) {
                    try {
                        this.selectors.add(invoker.selector());
                    } catch (Exception e) {
                        onFail(e, clazz + " 获取 selector 异常");
                    }
                }
            }
        }
    }

    private ClassScanner(ClassLoader classLoader, boolean throwOnScanFail) {
        this(classLoader, true, throwOnScanFail);
    }

    private ClassScanner(ClassLoader classLoader, boolean autoLoadSelector, boolean throwOnScanFail) {
        this(autoLoadSelector);
        this.classLoader = classLoader;
        this.throwOnScanFail = throwOnScanFail;
    }

    public ClassScanner addSelector(ClassSelector... selectors) {
        Collections.addAll(this.selectors, selectors);
        return this;
    }

    public ClassScanner addSelector(Collection<ClassSelector> selectors) {
        this.selectors.addAll(selectors);
        return this;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packs 扫描包列表
     */
    public void scan(String... packs) throws IOException {
        this.scan(Arrays.asList(packs));
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packs 扫描包列表
     */
    public void scan(Collection<String> packs) {
        // 是否循环迭代
        packs.forEach(pack -> {
            // 获取包的名字 并进行替换
            String packageDirName = pack.replace('.', '/');
            // 定义一个枚举的集合 并进行循环来处理这个目录下的things
            Enumeration<URL> dirs;
            try {
                dirs = this.classLoader.getResources(packageDirName);
                // 循环迭代下去
                while (dirs.hasMoreElements()) {
                    // 获取下一个元素
                    URL url = dirs.nextElement();
                    // 得到协议的名称
                    String protocol = url.getProtocol();
                    // 如果是以文件的形式保存在服务器上
                    if ("file".equals(protocol)) {
                        // 获取包的物理路径
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        // 以文件的方式扫描整个包下的文件 并添加到集合中
                        this.findAndAddClassesByFile(pack, filePath, true);
                    } else if ("jar".equals(protocol)) {
                        this.findAndAddClassesInJar(url);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("scan {} 异常", pack, e);
            }
        });
        for (ClassSelector selector : this.selectors) {
            selector.selected();
        }
    }

    private void findAndAddClassesInJar(URL url) throws IOException {
        UrlResource jarRes;
        if (StringUtils.endsWith(url.getPath(), "/")) {
            jarRes = new UrlResource(url);
        } else {
            jarRes = new UrlResource(url + "/");
        }
        URLConnection con = jarRes.getURL().openConnection();
        JarURLConnection jarCon = (JarURLConnection)con;
        JarFile jarFile = jarCon.getJarFile();
        JarEntry jarEntry = jarCon.getJarEntry();
        String rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
            rootEntryPath = rootEntryPath + "/";
        }
        String rootPath = rootEntryPath;
        jarFile.stream().parallel().forEach(entry -> loadMetadataReader(rootPath, jarRes, entry));
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName 包名
     * @param packagePath 路径
     * @param recursive   是否迭代
     */
    private void findAndAddClassesByFile(String packageName, String packagePath, final boolean recursive) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirFiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        if (dirFiles == null || ArrayUtils.isEmpty(dirFiles)) {
            return;
        }
        // 循环所有文件
        Stream.of(dirFiles)
                .forEach(file -> loadMetadataReader(packageName, file, recursive));
    }

    private void loadMetadataReader(String packageName, File file, boolean recursive) {
        if (file.isDirectory()) {
            String nextPackage = packageName + "." + file.getName();
            this.findAndAddClassesByFile(nextPackage, file.getAbsolutePath(), recursive);
        } else {
            try {
                FileSystemResource resource = new FileSystemResource(file);
                MetadataReaderFactory factory = ClassMetadataReaderFactory.getFactory();
                LOGGER.info("scan class file : {}", file);
                select(this.classLoader, factory.getMetadataReader(resource));
                // 如果是java类文件 去掉后面的.class 只留下类名
            } catch (IOException e) {
                onFail(e, "find class " + file + " 异常");
            }
        }
    }

    private void loadMetadataReader(String rootPath, UrlResource jar, JarEntry entry) {
        String entryPath = entry.getName();
        if (!entryPath.startsWith(rootPath)) {
            return;
        }
        try {
            String relativePath = entryPath.substring(rootPath.length());
            if (relativePath.endsWith(".class")) {
                Resource resource;
                resource = jar.createRelative(relativePath);
                if (!resource.isReadable()) {
                    return;
                }
                MetadataReaderFactory factory = ClassMetadataReaderFactory.getFactory();
                LOGGER.info("scan class file : {}", relativePath);
                select(this.classLoader, factory.getMetadataReader(resource));
            }
        } catch (Throwable e) {
            onFail(e, "scan " + entryPath + " 异常");
        }
    }

    private void select(ClassLoader loader, MetadataReader reader) {
        Class<?> clazz = null;
        for (ClassSelector selector : this.selectors) {
            Class<?> selClass = selector.selector(reader, loader, null);
            if (clazz == null && selClass != null) {
                clazz = selClass;
            }
        }
    }

    private void onFail(Throwable e, String message) {
        if (this.throwOnScanFail) {
            throw new ClassScanException(message, e);
        } else {
            LOGGER.error(message, e);
        }
    }

    private static class ClassSelectorProviderInvoker {

        private Method method;

        private ClassSelectorProviderInvoker(Method method) {
            this.method = method;
        }

        public ClassSelector selector() throws Exception {
            return (ClassSelector)this.method.invoke(null);
        }

        public static List<ClassSelectorProviderInvoker> instance(Class<?> clazz) {
            List<ClassSelectorProviderInvoker> invokers = new ArrayList<>();
            for (Method method : clazz.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (Modifier.isStatic(modifiers) && method.getAnnotation(ClassSelectorProvider.class) != null) {
                    method.setAccessible(true);
                    invokers.add(new ClassSelectorProviderInvoker(method));
                }
            }
            if (invokers.isEmpty()) {
                throw new IllegalArgumentException(clazz + " 不存在 " + ClassSelectorProvider.class + " 方法");
            }
            return invokers;
        }

    }

}
