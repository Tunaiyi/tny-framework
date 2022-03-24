package com.tny.game.gradle.doc.plugin

import com.tny.game.doc.LangTypeFormatter
import com.tny.game.doc.TypeFormatter
import com.tny.game.doc.controller.ModuleTableAttribute
import com.tny.game.doc.enums.EnumTableAttribute
import com.tny.game.doc.output.ExportTask
import com.tny.game.doc.output.OutputType
import com.tny.game.doc.table.TableAttributeFactory
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

import javax.inject.Inject

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 4:06 上午
 */
class FileExportScheme {

    private String name = "FileExport"

    /**
     * 添加到的任务名
     */
    private List<String> taskNames = new ArrayList<>()

    private OutputType outputType = OutputType.MVEL

    private TypeFormatter typeFormatter = LangTypeFormatter.RAW

    private FileResolver templateFileResolver

    private FileResolver outputFileResolver

    private String outputFileHead

    private String outputFileTail

    private TableAttributeFactory attributeFactory

    private ObjectFactory objectFactory;

    @Inject
    FileExportScheme(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Input
    String getName() {
        name
    }

    void setName(String name) {
        this.name = name
    }

    void name(String name) {
        this.setName(name)
    }

    @Input
    List<String> getTaskNames() {
        taskNames
    }

    void setTaskNames(List<String> taskNames) {
        this.taskNames = taskNames
    }

    void taskNames(String... taskNames) {
        this.taskNames = taskNames.toList()
    }

    void taskNames(List<String> taskNames) {
        this.taskNames = taskNames
    }

    @Nested
    FileResolver getTemplateFileResolver() {
        templateFileResolver
    }

    void setTemplateFileResolver(FileResolver templateFileResolver) {
        this.templateFileResolver = templateFileResolver
    }

    void templateFileResolver(FileResolver templateFileResolver) {
        this.templateFileResolver = templateFileResolver
    }

    void templateFile(GString file) {
        this.templateFileResolver = { new File(file.toString()) }
    }

    void templateFile(String file) {
        this.templateFileResolver = { new File(file) }
    }

    void templateFile(File file) {
        this.templateFileResolver = { file }
    }

    void templateFilePath(FilePathResolver resolver) {
        this.templateFileResolver = new Path2FileResolver(resolver)
    }

    @Nested
    FileResolver getOutputFileResolver() {
        outputFileResolver
    }

    void setOutputFileResolver(FileResolver outputFileResolver) {
        this.outputFileResolver = outputFileResolver
    }

    void outputFileResolver(FileResolver outputFileResolver) {
        this.outputFileResolver = outputFileResolver
    }

    void outputFile(String file) {
        this.outputFileResolver = { new File(file) }
    }

    void outputFile(GString file) {
        this.outputFileResolver = { new File(file.toString()) }
    }

    void outputFile(File file) {
        this.outputFileResolver = { file }
    }

    void outputFilePath(FilePathResolver resolver) {
        this.outputFileResolver = new Path2FileResolver(resolver)
    }

    @Input
    TypeFormatter getTypeFormatter() {
        this.typeFormatter
    }

    void setTypeFormatter(TypeFormatter typeFormatter) {
        this.typeFormatter = typeFormatter
    }

    void typeFormatter(TypeFormatter typeFormatter) {
        this.typeFormatter = typeFormatter
    }

    @Input
    OutputType getOutputType() {
        return outputType
    }

    void setOutputType(OutputType outputType) {
        this.outputType = outputType
    }

    @Input
    String getOutputFileHead() {
        this.outputFileHead
    }

    void setOutputFileHead(String outputFileHead) {
        this.outputFileHead = outputFileHead
    }

    void outputFileHead(String outputFileHead) {
        this.outputFileHead = outputFileHead
    }

    @Input
    String getOutputFileTail() {
        this.outputFileTail
    }

    void setOutputFileTail(String outputFileTail) {
        this.outputFileTail = outputFileTail
    }

    void outputFileTail(String outputFileTail) {
        this.outputFileTail = outputFileTail
    }

    @Input
    TableAttributeFactory getAttributeFactory() {
        return attributeFactory
    }

    void dtoAttributeFactory(Action<DTOTableAttributeSpec> action) {
        def spec = objectFactory.newInstance(DTOTableAttributeSpec.class)
        action.execute(spec)
        this.attributeFactory = spec.createFactory()
    }

    void serviceAttributeFactory() {
        this.attributeFactory = { new ModuleTableAttribute() }
    }

    void enumAttributeFactory() {
        this.attributeFactory = { new EnumTableAttribute() }
    }

    void setAttributeFactory(TableAttributeFactory attributeFactory) {
        this.attributeFactory = attributeFactory
    }

    void attributeFactory(TableAttributeFactory attributeFactory) {
        this.attributeFactory = attributeFactory
    }

    ExportTask exportTask(String generateName) {
        return new ExportTask()
                .setDesc("[${generateName}]-${this.name}")
                .setTemplateFile(this.templateFileResolver)
                .setOutputFileResolver(this.outputFileResolver)
                .setFormatter(this.typeFormatter)
                .setOutputFileHead(this.outputFileHead)
                .setOutputFileTail(this.outputFileTail)
                .setAttributeFactory(this.attributeFactory)
    }

}
