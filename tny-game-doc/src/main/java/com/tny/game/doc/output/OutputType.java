package com.tny.game.doc.output;

import java.util.function.Supplier;

/**
 * Created by Kun Yang on 2017/4/8.
 */
public enum OutputType {

	XML(XMLExporter::new),

	JSON(JSONExporter::new),

	MVEL(MVELExporter::new),
	//
	;

	private Supplier<Exporter> creator;

	OutputType(Supplier<Exporter> creator) {
		this.creator = creator;
	}

	public Exporter create() {
		return creator.get();
	}
}
