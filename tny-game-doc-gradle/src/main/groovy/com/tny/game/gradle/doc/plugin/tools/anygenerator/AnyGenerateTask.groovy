/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.gradle.doc.plugin.tools.anygenerator

import com.tny.game.doc.annotation.ClassDoc
import com.tny.game.scanner.ClassMetadataReaderFactory
import com.tny.game.scanner.ClassScanner
import com.tny.game.scanner.ClassSelector
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.internal.tasks.compile.DefaultJavaCompileSpec
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject
import java.lang.reflect.Method

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/3 3:30 上午
 */
class AnyGenerateTask extends DefaultTask {


    /** The Constant LIBS_PATH. */
    protected static final String LIBS_PATH = "libs"

    /** The Constant CLASSES_PATH. */
    protected static final String CLASSES_PATH = "classes/java/main"

    private List<String> classPackages = new ArrayList<>()

    private List<ClassSelector> classSelectors = new ArrayList<>()

    private String outputParentDirectory

    private List<File> classFiles = new ArrayList<>()

    @Inject
    AnyGenerateTask() {
        this.group = "any-generator"
    }

//    @Input
//    List<String> getClassPackages() {
//        return classPackages
//    }

////    @Classpath
////    @InputFiles
//    List<File> getClassFiles() {
//        return classFiles
//    }

//    @InputDirectory
//    String getOutputParentDirectory() {
//        return outputParentDirectory
//    }

    protected void setClassPackages(List<String> classPackages) {
        this.classPackages = classPackages
    }

    protected void addClassSelector(ClassSelector selector) {
        this.classSelectors.add(selector)
    }

    protected void setOutputParentDirectory(String outputParentDirectory) {
        this.outputParentDirectory = outputParentDirectory
    }

    @TaskAction
    void doAction() {
        this.classFiles.clear()
        project.logger.info("begin to execute AnyGenerateTask action : ${classPackages}")
        Task javaCompileTask = project.getTasks().getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
        Method createSpecMethod = ReflectionUtils.findMethod(javaCompileTask.getClass(), "createSpec")
        final List<URL> classesUrlList = new ArrayList<URL>()
        if (createSpecMethod != null) {
            createSpecMethod.setAccessible(true)
            Object ret = ReflectionUtils.invokeMethod(createSpecMethod, javaCompileTask)
            DefaultJavaCompileSpec spec = (DefaultJavaCompileSpec) ret
            List<File> compileClasspath = spec.getCompileClasspath()
            classFiles.addAll(compileClasspath)
            compileClasspath.forEach({
                try {
                    classesUrlList.add(it.toURI().toURL())
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage(), e)
                }
            })
        }

        File classPath = new File(outputParentDirectory + File.separator + CLASSES_PATH)
        logger.info("导入 classPath : ${classPath}")
        classPath.mkdir()
        File libsPath = new File(outputParentDirectory + File.separator + LIBS_PATH)
        logger.info("导入 libsPath : ${libsPath}")
        libsPath.mkdir()
        try {
            classesUrlList.add(classPath.toURI().toURL())
        } catch (MalformedURLException e1) {
            e1.printStackTrace()
        }
        addFileUrls(classPath, "class", new ArrayList<URL>())
        addFileUrls(libsPath, "jar", classesUrlList)
        classFiles.add(classPath)
        classFiles.add(libsPath)
        classFiles.forEach({ logger.info("导入文件 classFile -> ${it}") })

        URL[] classesUrls = classesUrlList.toArray(new URL[classesUrlList.size()])
        URLClassLoader classLoader = new URLClassLoader(classesUrls, ClassDoc.class.getClassLoader())
        Thread.currentThread().setContextClassLoader(classLoader)
        try {
            ClassMetadataReaderFactory.init(classLoader)
            ClassScanner scanner = ClassScanner.instance(classLoader, false, false)
            scanner.addSelector(this.classSelectors)
                    .scan(classPackages)
            logger.info("execute ClassesScanTask action finished.")
        } catch (Exception e) {
            logger.error(e.getMessage(), e)
        } finally {
            try {
                classLoader.close()
            } catch (Exception e) {
                logger.error(e.getMessage(), e)
            }
        }
    }


    /**
     * List URL files.
     *
     * @param classesPath the classes path
     * @param ext the ext
     * @param list the list
     */
    private void addFileUrls(File path, String ext, final List<URL> list) {
        String[] filter = [ext]
        Collection<File> files = FileUtils.listFiles(path, filter, true)
        if (files != null) {
            for (File file : files) {
                try {
                    this.classFiles.add(file)
                    list.add(file.toURI().toURL())
                } catch (Exception e) {
                    project.logger.error(e.getMessage(), e)
                }
            }
        }
    }


}
