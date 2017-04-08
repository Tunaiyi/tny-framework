package com.tny.game.doc.imports;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.tny.game.doc.TypeFormatter;
import com.tny.game.doc.table.TableAttribute;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class ImportTableAttribute implements TableAttribute {

    private ImportList importList = new ImportList();

    @XStreamAlias("importList")
    private static class ImportList {

        @XStreamAsAttribute
        @XStreamAlias("class")
        private String type = "list";

        @XStreamImplicit(itemFieldName = "import")
        private SortedSet<ImportInfo> importList = new TreeSet<>();

    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
        this.importList.importList.add(new ImportInfo(clazz));
    }

    @Override
    public Object getContent() {
        return importList;
    }

    public Collection<ImportInfo> getImportList() {
        return Collections.unmodifiableSortedSet(importList.importList);
    }

}
