package com.tny.game.base.item.behavior;

import com.google.common.collect.ImmutableMap;
import com.tny.game.base.log.LogName;
import com.tny.game.common.formula.FormulaHolder;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Kun Yang on 2018/5/31.
 */
public class DemandParamsObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

    /**
     * 参数
     */
    protected Map<DemandParam, FormulaHolder> paramMap;

    protected void initParamMap() {
        if (paramMap == null)
            paramMap = ImmutableMap.of();
        else
            paramMap = ImmutableMap.copyOf(paramMap);
    }

    public Map<DemandParam, Object> countAndSetDemandParams(String paramsKey, Map<String, Object> attributeMap) {
        if (this.paramMap == null || this.paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<DemandParam, Object> paramMap = new HashMap<>();
        for (Entry<DemandParam, FormulaHolder> entry : this.paramMap.entrySet()) {
            try {
                Object value = entry.getValue().createFormula().putAll(attributeMap).execute(Object.class);
                paramMap.put(entry.getKey(), value);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        attributeMap.put(paramsKey, paramMap);
        return paramMap;
    }

}
