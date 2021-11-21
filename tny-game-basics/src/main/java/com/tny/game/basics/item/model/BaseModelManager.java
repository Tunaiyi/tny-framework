package com.tny.game.basics.item.model;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.basics.item.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * xml映射事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public abstract class BaseModelManager<M extends Model> extends AbstractModelManager<M> implements SingleValueConverter {

	protected void initModel(Object context, M model) {
		if (model instanceof BaseModel) {
			BaseModel<Object> current = as(model);
			current.init(context);
		}
	}

}
