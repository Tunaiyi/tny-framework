package com.tny.game.common.formula.groovy;

import java.util.*;

public class ScriptDescribe {

    public static final String DEFAULT_VALUE = "";

    private String name;
    private Collection<String> fields = new ArrayList<>();
    private Collection<Class<?>> importClasses = new ArrayList<>();
    private Collection<Class<?>> importStars = new ArrayList<>();
    private Set<ImportStatic> importStatics = new HashSet<>();

    private static class ImportStatic {

        private Class<?> clazz;

        private String field;

        private ImportStatic(Class<?> clazz, String field) {
            this.clazz = clazz;
            this.field = field;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.clazz == null) ? 0 : this.clazz.hashCode());
            result = prime * result + ((this.field == null) ? 0 : this.field.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (this.getClass() != obj.getClass())
                return false;
            ImportStatic other = (ImportStatic) obj;
            if (this.clazz == null) {
                if (other.clazz != null)
                    return false;
            } else if (!this.clazz.equals(other.clazz))
                return false;
            if (this.field == null) {
                if (other.field != null)
                    return false;
            } else if (!this.field.equals(other.field))
                return false;
            return true;
        }

    }

    protected ScriptDescribe() {
    }

    public static ScriptDescribe newInstance() {
        return new ScriptDescribe();
    }

    public String getName() {
        return this.name;
    }

    public Collection<String> getImportsInfo() {
        List<String> importsList = new ArrayList<>();
        for (Class<?> clazz : this.importClasses)
            importsList.add(clazz.getName());
        return importsList;
    }

    public Collection<String> getImportStatics() {
        List<String> importsList = new ArrayList<>();
        for (Class<?> clazz : this.importStars)
            importsList.add(clazz.getName() + ".*");
        for (ImportStatic info : this.importStatics)
            importsList.add(info.clazz.getName() + "." + info.field);
        return importsList;
    }

    public Collection<String> getFields() {
        return Collections.unmodifiableCollection(this.fields);
    }

    //	public Collection<Class<?>> getImportClasses() {
    //		return Collections.unmodifiableCollection(this.importClasses);
    //	}
    //
    //	public Collection<Class<?>> getImportStars() {
    //		return Collections.unmodifiableCollection(this.importStars);
    //	}
    //
    //	public Collection<ImportStatic> getImportStatics() {
    //		return Collections.unmodifiableCollection(this.importStatics);
    //	}

    public ScriptDescribe setName(String name) {
        this.name = name;
        return this;
    }

    public ScriptDescribe addFields(Collection<String> fields) {
        this.fields.addAll(fields);
        return this;
    }

    public ScriptDescribe addField(String field) {
        this.fields.add(field);
        return this;
    }

    public ScriptDescribe addImportClasses(Class<?> importClass) {
        this.importClasses.add(importClass);
        return this;
    }

    public ScriptDescribe addImportStar(Class<?> importClass) {
        this.importStars.add(importClass);
        return this;
    }

    public ScriptDescribe addImportStar(Collection<Class<?>> importClasses) {
        this.importStars.addAll(importClasses);
        return this;
    }

    public ScriptDescribe addImportStatic(Class<?> clazz, String field) {
        ImportStatic imports = new ImportStatic(clazz, field);
        this.importStatics.add(imports);
        return this;
    }

}
