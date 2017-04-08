package com.tny.game.doc.output;

import com.tny.game.LogUtils;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.table.ConfigTable;
import com.tny.game.doc.table.TableAttribute;
import com.tny.game.doc.table.TableAttributeCreator;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.ClassFilter;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.apache.commons.io.FileUtils.*;

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

    private String headMessage;

    private List<ClassFilter> filters = new ArrayList<>();

    private TableAttributeCreator creator;

    private ClassLoader classLoader;

    public Exportor setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public Exportor setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
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

    public Exportor setHeadMessage(String headMessage) {
        this.headMessage = headMessage;
        return this;
    }

    public Exportor clean() throws IOException {
        if (this.baseDir.exists()) {
            System.out.println(LogUtils.format("正在清空 [{}] 文件夹......", this.baseDir));
            deleteDirectory(this.baseDir);
            System.out.println(LogUtils.format("清空 [{}] 文件夹完成", this.baseDir));
        }
        return this;
    }

    public void exportAll(OutputType type) throws IOException {
        if (!baseDir.exists()) {
            try {
                forceMkdir(baseDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClassSelector selector = ClassSelector.instance()
                .addFilter(this.filters);
        ClassScanner.instance(classLoader)
                .addSelector(selector)
                .scan(basePackage);
        Set<Class<?>> tempSet = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        tempSet.addAll(selector.getClasses());
        if (tempSet.isEmpty()) {
            System.out.println(LogUtils.format("{}包下未找到符合的类", basePackage));
            return;
        }
        // XStream xstream = new XStream();
        // xstream.autodetectAnnotations(true);
        // xstream.aliasSystemAttribute(null, "class");

        Exporter exporter = type.create();
        int count = 0;
        for (Class<?> clazz : tempSet) {
            try {
                ConfigTable table = createTable(clazz);
                File outputFile = new File(baseDir, outputPath.getPath(clazz));
                try (FileOutputStream output = openOutputStream(outputFile);
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"))) {
                    System.out.println(LogUtils.format("正在导出 {} ......", outputFile.getAbsoluteFile()));
                    IOUtils.write(exporter.getHead(), writer);
                    IOUtils.write(headMessage, writer);
                    IOUtils.write(exporter.output(table), writer);
                    System.out.println(LogUtils.format("导出 {} 完成", outputFile.getAbsoluteFile()));
                    count++;
                }
            } catch (Throwable e) {
                e.printStackTrace();
//                LOGGER.error("{} 类导出异常", clazz, e);
                throw e;
            }
        }
        System.out.println(LogUtils.format("{} 包中,一共导出 {} 个文件到 {} 文件夹下", basePackage, count, baseDir.getAbsoluteFile()));
    }

    public void export2One(OutputType type) throws IOException {
        if (!baseDir.exists()) {
            try {
                forceMkdir(baseDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClassSelector selector = ClassSelector.instance()
                .addFilter(this.filters);
        ClassScanner.instance(classLoader)
                .addSelector(selector)
                .scan(basePackage);
        Set<Class<?>> tempSet = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        tempSet.addAll(selector.getClasses());
        Exporter exporter = type.create();
        // XStream xstream = new XStream();
        // xstream.autodetectAnnotations(true);
        // xstream.aliasSystemAttribute(null, "class");
        ConfigTable table = createTable(tempSet);
        File outputFile = new File(baseDir, outputPath.getPath(null));
        System.out.println(LogUtils.format("正在导出 {} ......", outputFile.getAbsoluteFile()));
        try (FileOutputStream output = openOutputStream(outputFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"))) {
            IOUtils.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", writer);
            if (headMessage != null)
                IOUtils.write(headMessage, writer);
            IOUtils.write(exporter.output(table), writer);
//            xstream.toXML(table, writer);
        }
        System.out.println(LogUtils.format("{} 包中,一共导出 {} 个类到 {} 文件", basePackage, selector.getClasses().size(), outputFile.getAbsoluteFile()));
    }

    public ConfigTable createTable(Class<?> clazz) {
        TableAttribute attribute = creator.create();
        attribute.putAttribute(clazz, formatter);
        ConfigTable table = new ConfigTable(mvlPath.getPath(clazz), mvlOutputPath.getPath(clazz));
        table.setAttributeMap(attribute);
        return table;
    }

    public ConfigTable createTable(Collection<Class<?>> classes) {
        TableAttribute attribute = creator.create();
        for (Class<?> clazz : classes)
            attribute.putAttribute(clazz, formatter);
        ConfigTable table = new ConfigTable(mvlPath.getPath(null), mvlOutputPath.getPath(null));
        table.setAttributeMap(attribute);
        return table;
    }


}
