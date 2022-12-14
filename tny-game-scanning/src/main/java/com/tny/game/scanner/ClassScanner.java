/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
            Set<Class<?>> classes = AutoLoadClasses.getClasses(ClassSelectorProvider.class);
            for (Class<?> clazz : classes) {
                List<ClassSelectorProviderInvoker> invokers = ClassSelectorProviderInvoker.instance(clazz);
                for (ClassSelectorProviderInvoker invoker : invokers) {
                    try {
                        this.selectors.add(invoker.selector());
                    } catch (Exception e) {
                        onFail(e, clazz + " ?????? selector ??????");
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
     * ??????package??????????????????Class
     *
     * @param packs ???????????????
     */
    public void scan(String... packs) throws IOException {
        this.scan(Arrays.asList(packs));
    }

    /**
     * ??????package??????????????????Class
     *
     * @param packs ???????????????
     */
    public void scan(Collection<String> packs) {
        // ??????????????????
        packs.forEach(pack -> {
            // ?????????????????? ???????????????
            String packageDirName = pack.replace('.', '/');
            // ??????????????????????????? ??????????????????????????????????????????things
            Enumeration<URL> dirs;
            try {
                dirs = this.classLoader.getResources(packageDirName);
                // ??????????????????
                while (dirs.hasMoreElements()) {
                    // ?????????????????????
                    URL url = dirs.nextElement();
                    // ?????????????????????
                    String protocol = url.getProtocol();
                    // ????????????????????????????????????????????????
                    if ("file".equals(protocol)) {
                        // ????????????????????????
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        // ????????????????????????????????????????????? ?????????????????????
                        this.findAndAddClassesByFile(pack, filePath, true);
                    } else if ("jar".equals(protocol)) {
                        this.findAndAddClassesInJar(url);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("scan {} ??????", pack, e);
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
     * ??????????????????????????????????????????Class
     *
     * @param packageName ??????
     * @param packagePath ??????
     * @param recursive   ????????????
     */
    private void findAndAddClassesByFile(String packageName, String packagePath, final boolean recursive) {
        // ????????????????????? ????????????File
        File dir = new File(packagePath);
        // ????????????????????? ??????????????????????????????
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("?????????????????? " + packageName + " ?????????????????????");
            return;
        }
        // ???????????? ?????????????????????????????? ????????????
        // ????????????????????? ??????????????????(???????????????) ????????????.class???????????????(????????????java?????????)
        File[] dirFiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        if (dirFiles == null || ArrayUtils.isEmpty(dirFiles)) {
            return;
        }
        // ??????????????????
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
                // ?????????java????????? ???????????????.class ???????????????
            } catch (IOException e) {
                onFail(e, "find class " + file + " ??????");
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
            onFail(e, "scan " + entryPath + " ??????");
        }
    }

    private void select(ClassLoader loader, MetadataReader reader) {
        Class<?> loadClass = null;
        for (ClassSelector selector : this.selectors) {
            Class<?> selClass = selector.select(reader, loader, loadClass);
            if (loadClass == null && selClass != null) {
                loadClass = selClass;
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

        private final Method method;

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
                throw new IllegalArgumentException(clazz + " ????????? " + ClassSelectorProvider.class + " ??????");
            }
            return invokers;
        }

    }

}
