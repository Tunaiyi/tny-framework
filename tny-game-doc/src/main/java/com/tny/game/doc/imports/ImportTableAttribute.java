package com.tny.game.doc.imports;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.common.collection.map.*;
import com.tny.game.doc.*;
import com.tny.game.doc.table.*;

import java.util.*;

public class ImportTableAttribute implements TableAttribute {

	private ImportList importList = new ImportList();

	@XStreamAlias("importList")
	private static class ImportList {

		@XStreamAsAttribute
		@XStreamAlias("class")
		private String type = "list";

		@XStreamImplicit(itemFieldName = "import")
		private SortedSet<ImportDto> importList = new TreeSet<>();

	}

	@Override
	public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
		this.importList.importList.add(new ImportDto(clazz));
	}

	@Override
	public Map<String, Object> getContext() {
		return MapBuilder.<String, Object>newBuilder()
				.put("importList", importList)
				.build();
	}

	public Collection<ImportDto> getImportList() {
		return Collections.unmodifiableSortedSet(importList.importList);
	}

}
