/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.output;

import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.table.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.commons.io.FileUtils.*;

/**
 * 导出器
 * Created by Kun Yang on 16/1/31.
 */
public class ExportTask {

    private static final Logger LOGGER = LoggerFactory.getLogger("Exporter");

    private String desc;

    private PathResolver templateFileResolver;

    private PathResolver outputFileResolver;

    private TypeFormatter formatter;

    private String outputFileHead;

    private String outputFileTail;

    private TableAttributeFactory attributeFactory;

    public ExportTask setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public ExportTask setTemplateFileResolver(PathResolver templateFileResolver) {
        this.templateFileResolver = templateFileResolver;
        return this;
    }

    public ExportTask setTemplateFile(PathResolver templateFileResolver) {
        this.templateFileResolver = templateFileResolver;
        return this;
    }

    public ExportTask setOutputFileResolver(PathResolver outputFileResolver) {
        this.outputFileResolver = outputFileResolver;
        return this;
    }

    public ExportTask setOutputFileHead(String outputFileHead) {
        this.outputFileHead = outputFileHead;
        return this;
    }

    public ExportTask setOutputFileTail(String outputFileTail) {
        this.outputFileTail = outputFileTail;
        return this;
    }

    public ExportTask setFormatter(TypeFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public ExportTask setAttributeFactory(TableAttributeFactory attributeFactory) {
        this.attributeFactory = attributeFactory;
        return this;
    }

    private void mkdir(File dir) {
        try {
            forceMkdir(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void export(Collection<Class<?>> classes, OutputType type) throws IOException {
        Set<Class<?>> tempSet = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        Map<String, OutputScheme> outputSchemeMap = new ConcurrentHashMap<>();
        Attributes attributes = ContextAttributes.create();
        tempSet.addAll(classes);
        if (tempSet.isEmpty()) {
            LOGGER.info("ExportTask {} : 包下未找到符合的类", desc);
            return;
        }
        for (Class<?> clazz : tempSet) {
            addToOutputScheme(clazz, outputSchemeMap, attributes);
        }
        Exporter exporter = type.create();
        int count = 0;
        int success = 0;
        int failed = 0;
        for (OutputScheme scheme : outputSchemeMap.values()) {
            if (write(exporter, scheme)) {
                success++;
            } else {
                failed++;
            }
            count++;
        }
        if (failed > 0) {
            LOGGER.error("ExportTask {} : 一共导出 {} 个文件, {} 个成功, {} 个失败", desc, count, success, failed);
        } else {
            LOGGER.info("ExportTask {} : 一共导出 {} 个文件, {} 个成功, {} 个失败", desc, count, success, failed);
        }
    }

    private boolean write(Exporter exporter, OutputScheme scheme) {
        File outputFile = scheme.getOutput();
        mkdir(outputFile.getAbsoluteFile().getParentFile());
        if (outputFile.exists()) {
            LOGGER.warn("ExportTask {} : 正在删除 {}", desc, outputFile.getAbsoluteFile());
            outputFile.deleteOnExit();
        }
        LOGGER.info("ExportTask {} : 正在导出 {} ......", desc, outputFile.getAbsoluteFile());
        try (FileOutputStream output = openOutputStream(outputFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))) {
            IOUtils.write(exporter.getHead(), writer);
            IOUtils.write(outputFileHead, writer);
            IOUtils.write(exporter.output(scheme), writer);
            IOUtils.write(outputFileTail, writer);
            IOUtils.write(exporter.getTail(), writer);
        } catch (Throwable e) {
            LOGGER.error("ExportTask {} : 通过 {} 导出到文件 {} exception", desc, scheme.getTemplate(), scheme.getOutput(), e);
            return false;
        }

        List<Class<?>> classes = scheme.getClasses();
        String classesList = classes.stream().map(Class::getCanonicalName).collect(Collectors.joining("\n"));
        LOGGER.info("ExportTask {} \n[导出清单]:\n{}\n[统计]:总共 {} 个类通过 {} 模板导出到 {} 文件成功", desc, classesList,
                classes.size(), scheme.getTemplate().getAbsolutePath(), outputFile.getAbsoluteFile());
        return true;
    }

    private void addToOutputScheme(Class<?> clazz, Map<String, OutputScheme> outputSchemeMap, Attributes attributes) {
        File template = templateFileResolver.resolve(clazz);
        File output = outputFileResolver.resolve(clazz);
        String tasKey = schemeKey(template, output);
        OutputScheme scheme = outputSchemeMap.computeIfAbsent(tasKey, k -> new OutputScheme(template, output, attributeFactory.create()));
        scheme.putAttribute(clazz, formatter, attributes);
    }

    private String schemeKey(File template, File output) {
        return template.getAbsolutePath() + "_to_" + output.getAbsolutePath();
    }

}
