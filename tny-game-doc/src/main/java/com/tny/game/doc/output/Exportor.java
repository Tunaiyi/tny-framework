package com.tny.game.doc.output;

import com.thoughtworks.xstream.XStream;
import com.tny.game.LogUtils;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.table.TableAttribute;
import com.tny.game.doc.table.TableAttributeCreator;
import com.tny.game.doc.table.XMLTable;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.filter.ClassFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 导出器
 * Created by Kun Yang on 16/1/31.
 */
public class Exportor {

//    private static final Logger LOGGER = LoggerFactory.getLogger("Exporter");

    private File baseDir;

    private String basePackage;

    private Pather mvlPath;

    private Pather mvlOutputPath;

    private Pather outputPath;

    private TypeFormatter formatter;

    private List<ClassFilter> filters = new ArrayList<>();

    private TableAttributeCreator creator;

    public Exportor setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public Exportor setBaseDir(File baseDir) {
        this.baseDir = baseDir;
        return this;
    }

    public Exportor setBaseDir(String baseDir) {
        this.baseDir = new File(baseDir);
        return this;
    }

    public Exportor addFilter(ClassFilter filter) {
        this.filters.add(filter);
        return this;
    }

    public Exportor addFilter(Collection<ClassFilter> filters) {
        this.filters.addAll(filters);
        return this;
    }

    public Exportor setFormatter(TypeFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public Exportor setOutputPath(String outputPath) {
        this.outputPath = cl -> outputPath;
        return this;
    }

    public Exportor setMvlPath(String mvlPath) {
        this.mvlPath = cl -> mvlPath;
        return this;
    }

    public Exportor setMvlOutputPath(String mvlOutputPath) {
        this.mvlOutputPath = cl -> mvlOutputPath;
        return this;
    }

    public Exportor setOutputPath(Pather outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    public Exportor setMvlPath(Pather mvlPath) {
        this.mvlPath = mvlPath;
        return this;
    }

    public Exportor setMvlOutputPath(Pather mvlOutputPath) {
        this.mvlOutputPath = mvlOutputPath;
        return this;
    }

    public Exportor setCreator(TableAttributeCreator creator) {
        this.creator = creator;
        return this;
    }

    public Exportor clean() throws IOException {
        if (this.baseDir.exists()) {
            System.out.println(LogUtils.format("正在清空 [{}] 文件夹......", this.baseDir));
            FileUtils.deleteDirectory(this.baseDir);
            System.out.println(LogUtils.format("清空 [{}] 文件夹完成", this.baseDir));
        }
        return this;
    }

    public void exportAll() {
        if (!baseDir.exists()) {
            try {
                FileUtils.forceMkdir(baseDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClassScanner scanner = new ClassScanner();
        scanner.addFilter(filters);
        Set<Class<?>> classSet = scanner.getClasses(basePackage);
        Set<Class<?>> tempSet = new TreeSet<>((o1, o2) -> o1.getCanonicalName().compareTo(o2.getCanonicalName()));
        tempSet.addAll(classSet);
        if (classSet.isEmpty()) {
            System.out.println(LogUtils.format("{}包下未找到符合的类", basePackage));
            return;
        }
        classSet = tempSet;
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasSystemAttribute(null, "class");
        int count = 0;
        for (Class<?> clazz : classSet) {
            try {
                XMLTable table = createTable(clazz);
                FileOutputStream output = null;
                try {
                    File outputFile = new File(baseDir, outputPath.getPath(clazz));
                    output = FileUtils.openOutputStream(outputFile);
                    System.out.println(LogUtils.format("正在导出 {} ......", outputFile.getAbsoluteFile()));
                    IOUtils.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", output);
                    xstream.toXML(table, output);
                    System.out.println(LogUtils.format("导出 {} 完成", outputFile.getAbsoluteFile()));
                    count++;
                } catch (Exception e) {
                    IOUtils.closeQuietly(output);
                }
            } catch (Throwable e) {
                e.printStackTrace();
//                LOGGER.error("{} 类导出异常", clazz, e);
                throw e;
            }
        }
        System.out.println(LogUtils.format("{} 包中,一共导出 {} 个文件到 {} 文件夹下", basePackage, count, baseDir.getAbsoluteFile()));
    }

    public void export2One() {
        if (!baseDir.exists()) {
            try {
                FileUtils.forceMkdir(baseDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClassScanner scanner = new ClassScanner();
        scanner.addFilter(filters);
        Set<Class<?>> classSet = scanner.getClasses(basePackage);
        Set<Class<?>> tempSet = new TreeSet<>((o1, o2) -> o1.getCanonicalName().compareTo(o2.getCanonicalName()));
        tempSet.addAll(classSet);
        classSet = tempSet;
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasSystemAttribute(null, "class");
        XMLTable table = createTable(classSet);
        File outputFile = new File(baseDir, outputPath.getPath(null));
        FileOutputStream output = null;
        try {
            output = FileUtils.openOutputStream(outputFile);
            IOUtils.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", output);
            xstream.toXML(table, output);
        } catch (Exception e) {
            IOUtils.closeQuietly(output);
        }
        System.out.println(LogUtils.format("{} 包中,一共导出 {} 个类到 {} 文件", basePackage, classSet.size(), outputFile.getAbsoluteFile()));
    }

    public XMLTable createTable(Class<?> clazz) {
        TableAttribute attribute = creator.create();
        attribute.putAttribute(clazz, formatter);
        XMLTable table = new XMLTable(mvlPath.getPath(clazz), mvlOutputPath.getPath(clazz));
        table.setAttributeMap(attribute);
        return table;
    }

    public XMLTable createTable(Collection<Class<?>> classes) {
        TableAttribute attribute = creator.create();
        for (Class<?> clazz : classes)
            attribute.putAttribute(clazz, formatter);
        XMLTable table = new XMLTable(mvlPath.getPath(null), mvlOutputPath.getPath(null));
        table.setAttributeMap(attribute);
        return table;
    }


}
