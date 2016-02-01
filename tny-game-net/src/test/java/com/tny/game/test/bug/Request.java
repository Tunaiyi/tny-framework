package com.tny.game.test.bug;

import java.util.Arrays;
import java.util.List;

public class Request {

    private String name;

    private List<Object> param;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the param
     */
    public Object[] getParam() {
        return null;
    }

    /**
     * @param param the param to set
     */
    public void setParam(Object[] param) {
        this.param = Arrays.asList(param);
    }

    /**
     * @return the param
     */
    public List<Object> getParamList() {
        return param;
    }
}
