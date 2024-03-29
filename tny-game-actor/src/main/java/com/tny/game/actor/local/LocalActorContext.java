/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.actor.local;

import com.tny.game.actor.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kun Yang on 16/3/2.
 */
public class LocalActorContext<ID, M> implements ActorContext<ID, DefaultLocalActor<ID, M>> {

    private ActorURL rootPath;

    private ActorTheatre defaultTheatre = ActorTheatres.getDefault();

    private ConcurrentHashMap<ID, DefaultLocalActor<ID, M>> actorMap = new ConcurrentHashMap<>();

    private ActorProps defaultProps = ActorProps.of();

    public LocalActorContext(ActorURL rootPath) {
        this(rootPath, null, null);
    }

    public LocalActorContext(ActorURL rootPath, ActorTheatre defaultTheatre) {
        this(rootPath, null, defaultTheatre);
    }

    public LocalActorContext(ActorURL rootPath, ActorProps defaultProps) {
        this(rootPath, defaultProps, null);
    }

    public LocalActorContext(ActorURL rootPath, ActorProps defaultProps, ActorTheatre defaultTheatre) {
        this.rootPath = rootPath;
        if (defaultTheatre != null) {
            this.defaultTheatre = defaultTheatre;
        }
        if (defaultProps != null) {
            this.defaultProps = defaultProps;
        }
        if (this.defaultTheatre == null) {
            this.defaultTheatre = ActorTheatres.getDefault();
        }
        if (this.defaultProps == null) {
            this.defaultProps = ActorProps.of();
        }
    }

    public DefaultLocalActor<ID, M> actorOf(ID id, ActorURL path, ActorProps props) {
        DefaultLocalActor<ID, M> actor = actorMap.get(id);
        if (actor != null) {
            return actor;
        }
        ActorCell cell = new ActorCell(id, path, props == null ? defaultProps : props);
        actor = add(cell.getActor());
        if (!actor.isTakenOver()) {
            ActorTheatre theatre = props.getActorTheatre();
            if (theatre == null) {
                theatre = defaultTheatre;
            }
            theatre.takeOver(actor);
        }
        return add(actor);
    }

    public DefaultLocalActor<ID, M> actorOf(ID id, ActorURL path, ActorTheatre theatre) {
        return actorOf(id, path, ActorProps.of(defaultProps).setActorTheatre(theatre));
    }

    @Override
    public DefaultLocalActor<ID, M> actorOf(ID id, ActorURL path) {
        return actorOf(id, path, (ActorProps) null);
    }

    @Override
    public DefaultLocalActor<ID, M> actorOf(ID id) {
        return actorOf(id, null, this.defaultProps);
    }

    protected boolean isExist(Actor<?, ?> actor) {
        return actorMap.get(actor.getActorId()) == actor;
    }

    protected boolean remove(Actor<?, ?> actor) {
        return actorMap.remove(actor.getActorId(), actor);
    }

    protected DefaultLocalActor<ID, M> add(DefaultLocalActor<ID, M> actor) {
        DefaultLocalActor<ID, M> old = this.actorMap.putIfAbsent(actor.getActorId(), actor);
        return old != null ? old : actor;
    }

    @Override
    public boolean stop(Actor<?, ?> actor) {
        if (actor instanceof DefaultLocalActor && this.isExist(actor)) {
            if (this.remove(actor)) {
                if (!actor.isTerminated()) {
                    ((DefaultLocalActor) actor).terminate();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void stopAll() {
        Map<ID, DefaultLocalActor<ID, M>> removes = this.actorMap;
        this.actorMap = new ConcurrentHashMap<>();
        removes.values().forEach(DefaultLocalActor::terminate);
    }

}
