package com.tny.game.doc.imports;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.net.annotation.*;
import org.slf4j.*;

@XStreamAlias("dtoImport")
public class ImportDto implements Comparable<ImportDto> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImportDto.class);

	@XStreamAsAttribute
	private String packageName;

	@XStreamAsAttribute
	private String className;

	@XStreamAsAttribute
	private int id = -1;

	public ImportDto(Class<?> clazz) {
		super();
		Controller controller = clazz.getAnnotation(Controller.class);
		if (controller != null) {
            if (controller.value() < 0) {
                LOGGER.warn("{} controller value {} < 0", clazz, controller.value());
            }
			this.id = controller.value();
		} else {
			//            ProtoEx proto = clazz.getAnnotation(ProtoEx.class);
			//            if (proto != null) {
			//                this.id = proto.value();
			//            } else {
			//                LOGGER.warn("{} is not controller and proto", clazz);
			//            }
		}
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
