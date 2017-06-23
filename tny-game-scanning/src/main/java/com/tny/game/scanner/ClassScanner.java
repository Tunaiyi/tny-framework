package com.tny.game.scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.*;

public class ClassScanner {

    public static final int DEFAULT_CACHE_LIMIT = 256;

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private static MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    protected static final Logger LOG = getLogger(ClassScanner.class);

    private List<ClassSelector> selectors = new ArrayList<>();

    public static MetadataReaderFactory getReaderFactory() {
        return readerFactory;
    }

    public ClassLoader classLoader;

    public static ClassScanner instance(ClassLoader classLoader) {
        return new ClassScanner(classLoader);
    }

    public static ClassScanner instance() {
        return new ClassScanner();
    }

    private ClassScanner() {
    }

    private ClassScanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassScanner addSelector(ClassSelector... selectors) {
        for (ClassSelector selector : selectors)
            this.selectors.add(selector);
        return this;
    }

    public ClassScanner addSelector(Collection<ClassSelector> selectors) {
        this.selectors.addAll(selectors);
        return this;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packs
     * @return
     */
    public void scan(String... packs) {

        // 是否循环迭代
        boolean recursive = true;
        Stream.of(packs).parallel().forEach(pack -> {
            // 获取包的名字 并进行替换
            String packageName = pack;
            String packageDirName = packageName.replace('.', '/');
            // 定义一个枚举的集合 并进行循环来处理这个目录下的things
            Enumeration<URL> dirs;
            try {
                if (classLoader == null)
                    classLoader = Thread.currentThread().getContextClassLoader();
                dirs = classLoader.getResources(packageDirName);
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
                        this.findAndAddClassesInDirByFile(packageName, filePath, recursive);
                    } else if ("jar".equals(protocol)) {
                        this.findAndAddClassesInJar(url);
                    }
                }
            } catch (IOException e) {
                LOG.error("scan {} 异常", pack, e);
            }
        });
        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (ClassSelector selector : selectors) {
            tasks.add(ForkJoinPool.commonPool()
                    .submit(selector::selected));
        }
        tasks.forEach(ForkJoinTask::join);
    }

    private void findAndAddClassesInJar(URL url) throws IOException {
        UrlResource jarRes = null;
        if (StringUtils.endsWith(url.getPath(), "/"))
            jarRes = new UrlResource(url);
        else
            jarRes = new UrlResource(url + "/");
        URLConnection con = jarRes.getURL().openConnection();
        JarURLConnection jarCon = (JarURLConnection) con;
        JarFile jarFile = jarCon.getJarFile();
        JarEntry jarEntry = jarCon.getJarEntry();
        String rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
            rootEntryPath = rootEntryPath + "/";
        }
        String rootPath = rootEntryPath;
        UrlResource jar = jarRes;
        jarFile.stream().parallel().forEach(entry -> {
            String entryPath = entry.getName();
            if (!entryPath.startsWith(rootPath))
                return;
            try {
                String relativePath = entryPath.substring(rootPath.length());
                if (relativePath.endsWith(".class")) {
                    Resource resource;
                    resource = jar.createRelative(relativePath);
                    if (!resource.isReadable())
                        return;
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    Class<?> clazz = null;
                    for (ClassSelector selector : selectors) {
                        Class<?> selClass = selector.selector(reader, clazz);
                        if (clazz == null && selClass != null)
                            clazz = selClass;
                    }
                }
            } catch (Throwable e) {
                LOG.error("scan {} 异常", entryPath, e);
            }
        });
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @throws IOException
     */
    private void findAndAddClassesInDirByFile(String packageName, String packagePath, final boolean recursive) throws IOException {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        if (dirfiles == null)
            return;
        // 循环所有文件
        Stream.of(dirfiles).parallel()
                .forEach(file -> {
                    if (file.isDirectory()) {
                        try {
                            String nextPackage = packageName + "." + file.getName();
                            this.findAndAddClassesInDirByFile(nextPackage, file.getAbsolutePath(), recursive);
                        } catch (IOException e) {
                            LOG.error("find dir {} 异常", packageName);
                        }
                    } else {
                        FileSystemResource resource = new FileSystemResource(file);
                        MetadataReader reader = null;
                        try {
                            reader = readerFactory.getMetadataReader(resource);
                            // 如果是java类文件 去掉后面的.class 只留下类名
                            Class<?> clazz = null;
                            for (ClassSelector selector : selectors)
                                clazz = selector.selector(reader, clazz);
                        } catch (IOException e) {
                            LOG.error("find class {} 异常", file);
                        }
                    }
                });
    }

}
