package com.tny.game.basics.item.loader;

import java.util.List;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 02:08
 **/
public interface ModelLoadHandler<M> {

	void onLoad(List<M> models, String path, boolean reload) throws Exception;

}
