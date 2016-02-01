import com.tny.game.doc.TypeFormatter
import com.tny.game.doc.output.Exportor
import com.tny.game.doc.output.Pather
import com.tny.game.doc.table.TableAttributeCreator
import com.tny.game.scanner.filter.ClassFilter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by Kun Yang on 16/2/1.
 */
class CodeExportAll extends DefaultTask {

    @Optional
    File baseDir;

    @Optional
    boolean clean;

    @Optional
    String basePackage;

    @Optional
    Pather mvlPath;

    @Optional
    Pather mvlOutputPath;

    @Optional
    Pather outputPath;

    @Optional
    TypeFormatter formatter;

    @Optional
    List<ClassFilter> filters;

    @Optional
    TableAttributeCreator creator;


    @TaskAction
    def export() {
        Exportor exportor = new Exportor()
                .setBaseDir(baseDir)
                .setBasePackage(basePackage)
                .setMvlPath(mvlPath)
                .setMvlOutputPath(mvlOutputPath)
                .setOutputPath(outputPath)
                .setFormatter(formatter)
                .setCreator(creator)
                .addFilter(filters);
        if (clean)
            exportor.clean()
        exportor.exportAll();
    }

}

class CodeExport2One extends DefaultTask {

    @Optional
    File baseDir;

    @Optional
    boolean clean;

    @Optional
    String basePackage;

    @Optional
    Pather mvlPath;

    @Optional
    Pather mvlOutputPath;

    @Optional
    String outputFile;

    @Optional
    TypeFormatter formatter;

    @Optional
    List<ClassFilter> filters;

    @Optional
    TableAttributeCreator creator;


    @TaskAction
    def export() {
        Exportor exportor = new Exportor()
                .setBaseDir(baseDir)
                .setBasePackage(basePackage)
                .setMvlPath(mvlPath)
                .setMvlOutputPath(mvlOutputPath)
                .setOutputPath { clazz -> outputFile }
                .setFormatter(formatter)
                .setCreator(creator)
                .addFilter(filters)
        if (clean)
            exportor.clean()
        exportor.export2One();
    }

}

