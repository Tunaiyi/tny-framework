package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.net.dispatcher.Response;

public class ResponseItem {

    private int id;

    private int number;

    private Response response;

    public ResponseItem(int id, Response response) {
        super();
        this.id = id;
        this.number = response.getNumber();
        this.response = response;
    }

    public int getID() {
        return this.id;
    }

    public Response getResponse() {
        return this.response;
    }

    public int getNumber() {
        return number;
    }

}
