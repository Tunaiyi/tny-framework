package com.tny.game.doc.imports;

import com.thoughtworks.xstream.annotations.*;
import org.slf4j.*;

@XStreamAlias("dtoImport")
public class ImportDto implements Comparable<ImportDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDto.class);

    @XStreamAsAttribute
    private String packageName;

    @XStreamAsAttribute
    private String className;

    public ImportDto(Class<?> clazz) {
        super();
        this.packageName = clazz.getPackage().getName();
        this.className = clazz.getSimpleName();
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getClassName() {
        return this.className;
    }

    @Override
    public int compareTo(ImportDto other) {
        int value = this.packageName.compareTo(other.packageName);
        if (value == 0) {
            return this.className.compareTo(other.className);
        }
        return value;
    }

}
