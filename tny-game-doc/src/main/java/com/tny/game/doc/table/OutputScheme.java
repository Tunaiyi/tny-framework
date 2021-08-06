package com.tny.game.doc.table;

import com.tny.game.doc.*;

import java.io.File;
import java.util.*;

public class OutputScheme {

	private File template;

	private File output;

	private TableAttribute attribute;

	private List<Class<?>> classes = new LinkedList<>();

	public OutputScheme() {
	}

	public OutputScheme(File template, File output, TableAttribute attribute) {
		super();
		this.template = template;
		this.output = output;
		this.attribute = attribute;
	}

	public File getTemplate() {
		return template;
	}

	public File getOutput() {
		return output;
	}

	public TableAttribute getAttribute() {
		return attribute;
	}

	public List<Class<?>> getClasses() {
		return classes;
	}

	public void putAttribute(Class<?> clazz, TypeFormatter formatter) {
		this.classes.add(clazz);
		this.attribute.putAttribute(clazz, formatter);
	}

}
