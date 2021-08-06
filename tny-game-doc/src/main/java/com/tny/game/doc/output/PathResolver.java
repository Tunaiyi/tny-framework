package com.tny.game.doc.output;

import java.io.File;

/**
 * Created by Kun Yang on 16/1/31.
 */
public interface PathResolver {

	File resolve(Class<?> clazz);

}
