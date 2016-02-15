package com.tny.game.actor.local;

import com.tny.game.LogUtils;
import com.tny.game.actor.ActorRef;
import com.tny.game.actor.local.reason.SuspendReason;
import com.tny.game.actor.local.reason.SuspendReasonType;
import com.tny.game.actor.local.reason.TerminationReason;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.reflect.ReflectUtils;
import com.tny.game.common.utils.collection.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

public class DefaultChildrenContainer extends ChildrenContainer {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultChildrenContainer.class);

    private final static long CHILDREN_MAP_OFF_SET = Unsafer.unsafe().objectFieldOffset(ReflectUtils.getDeepField(DefaultChildrenContainer.class, "_childrenMap"));
    private final static long DEATH_SET_OFF_SET = Unsafer.unsafe().objectFieldOffset(ReflectUtils.getDeepField(DefaultChildrenContainer.class, "_deathSet"));
    private final static long STATE_OFF_SET = Unsafer.unsafe().objectFieldOffset(ReflectUtils.getDeepField(DefaultChildrenContainer.class, "_state"));

    private volatile Optional<ConcurrentMap<String, SimpleChildNode>> _childrenMap = Optional.empty();

    private volatile Optional<Set<ActorRef>> _deathSet = Optional.empty();

    private volatile ChildrenContainerState _state = ChildrenContainerState.normal();

    private boolean swapCreateChildren(Optional<ConcurrentMap<String, SimpleChildNode>> expect, Optional<ConcurrentMap<?, ?>> update) {
        return Unsafer.unsafe().compareAndSwapObject(this, CHILDREN_MAP_OFF_SET, expect, update);
    }

    private boolean swapDeathSet(Optional<Set<ActorRef>> expect, Optional<Set<?>> update) {
        return Unsafer.unsafe().compareAndSwapObject(this, DEATH_SET_OFF_SET, expect, update);
    }

    private boolean swapState(ChildrenContainerState expect, ChildrenContainerState update) {
        return Unsafer.unsafe().compareAndSwapObject(this, STATE_OFF_SET, expect, update);
    }

    private void putState(ChildrenContainerState update) {
        Unsafer.unsafe().putObjectVolatile(this, STATE_OFF_SET, update);
    }

    private Optional<ConcurrentMap<String, SimpleChildNode>> childrenMap() {
        return _childrenMap;
    }

    private Optional<Set<ActorRef>> deathSet() {
        return _deathSet;
    }

    private ChildrenContainerState state() {
        return _state;
    }

    private Optional<ConcurrentMap<String, SimpleChildNode>> getAndCreateChildrenMap() {
        Optional<ConcurrentMap<String, SimpleChildNode>> map;
        do {
            map = childrenMap();
            if (map.isPresent())
                return map;
        } while (!isTerminating() && !swapCreateChildren(map, Optional.of(new ConcurrentHashMap<>())));
        return childrenMap();
    }

    private Optional<Set<ActorRef>> getAndCreateDeathSet() {
        Optional<Set<ActorRef>> set;
        do {
            set = deathSet();
            if (set.isPresent())
                return set;
        } while (!isTerminating() && !swapDeathSet(set, Optional.of(new CopyOnWriteArraySet<>())));
        return deathSet();
    }

    @Override
    protected Optional<ChildNode> initChildNode(ActorRef actorRef) {
        if (isTerminating())
            Optional.empty();
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            SimpleChildNode node = map.get().get(actorRef.getPath().getName());
            if (node == null)
                return Optional.empty();
            if (node.isNameReserved())
                node.reserve(actorRef);
            return Optional.of(node);
        }
        return Optional.empty();
    }

    @Override
    protected Optional<SuspendReason> remove(ActorRef child) {
        Optional<Set<ActorRef>> set = deathSet();
        ChildrenContainerState state = state();
        if (set.isPresent()) {
            Set<ActorRef> deathSet = set.get();
            deathSet.remove(child);
            while (deathSet.isEmpty() && state.isTerminating()) {
                ChildrenContainerState newState = checkReasonType(state, SuspendReasonType.TERMINATION) ?
                        ChildrenContainerState.terminated() : ChildrenContainerState.normal();
                if (swapState(state, newState))
                    break;
                state = state();
            }
        }
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            map.get().remove(child.getPath().getName());
        }
        return Optional.of(state.getReason());
    }

    @Override
    public void shallDie(ActorRef actor) {
        Optional<Set<ActorRef>> set = getAndCreateDeathSet();
        if (set.isPresent()) {
            set.get().add(actor);
        }
    }

    @Override
    public void reserve(String name) {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = getAndCreateChildrenMap();
        if (map.isPresent()) {
            SimpleChildNode oldOne = map.get().putIfAbsent(name, new SimpleChildNode(name));
            ExceptionUtils.checkArgument(oldOne == null,
                    LogUtils.format("无法保存 ChildNode.name {} : 已经存在", name));
        } else {
            throw new IllegalStateException(LogUtils.format("无法保存 ChildNode.name {} : terminating", name));
        }
    }

    @Override
    public void unreserve(String name) {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            SimpleChildNode node = map.get().get(name);
            if (node != null && node.isNameReserved())
                map.get().remove(name, node);
        }
    }

    @Override
    public Optional<ChildNode> getNodeByName(String name) {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            SimpleChildNode node = map.get().get(name);
            return node == null ? Optional.empty() : Optional.<ChildNode>of(node);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ActorRef> getRefByRef(ActorRef actorRef) {
        Optional<ChildNode> node = getRefNodeByRef(actorRef);
        if (node.isPresent())
            return Optional.empty();
        return Optional.of(node.get().getActorRef());
    }

    @Override
    public Optional<ChildNode> getRefNodeByRef(ActorRef actorRef) {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            SimpleChildNode node = map.get().get(actorRef.getPath().getName());
            return node == null || node.isNameReserved() || !node.contains(actorRef) ?
                    Optional.empty() : Optional.of(node);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ChildNode> getRefNodeByName(String name) {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            SimpleChildNode node = map.get().get(name);
            return node == null || node.isNameReserved() ? Optional.empty() : Optional.of(node);
        }
        return Optional.empty();
    }

    @Override
    protected Stream<ActorRef> getAllChildren() {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            map.get().values()
                    .stream()
                    .filter(SimpleChildNode::isReserved)
                    .map(SimpleChildNode::getActorRef);
        }
        return Stream.empty();
    }

    @Override
    protected Stream<ChildNode> getAllChildrenRefNode() {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            map.get().values()
                    .stream()
                    .filter(SimpleChildNode::isReserved);
        }
        return Stream.empty();
    }


    @Override
    public Iterable<? extends ChildNode> nodes() {
        Optional<ConcurrentMap<String, SimpleChildNode>> map = childrenMap();
        if (map.isPresent()) {
            return map.get().values();
        }
        return IterableUtils.empty();
    }

    @Override
    public boolean isNormal() {
        return state().isNormal();
    }

    @Override
    public boolean isTerminating() {
        return state().isTerminating();
    }

    private boolean checkReasonType(ChildrenContainerState state, SuspendReasonType reasonType) {
        if (state.isTerminating())
            return false;
        SuspendReason reason = state.getReason();
        return reason != null && reason.getReasonType() == reasonType;
    }

    @Override
    protected void setTerminated() {
        Optional<ConcurrentMap<String, SimpleChildNode>> currentChildrenMap = childrenMap();
        if (currentChildrenMap.isPresent()) {
            while (!swapCreateChildren(currentChildrenMap, Optional.empty())) {
                currentChildrenMap = childrenMap();
            }
        }
        Optional<Set<ActorRef>> currentDeathSet = deathSet();
        if (currentDeathSet.isPresent()) {
            while (!swapDeathSet(currentDeathSet, Optional.empty())) {
                currentDeathSet = deathSet();
            }
        }
        putState(ChildrenContainerState.terminated());
    }

    @Override
    protected boolean setTerminating() {
        return setSuspendReason(TerminationReason.instance(), false);
    }

    @Override
    protected Optional<SuspendReason> getSuspendReason() {
        SuspendReason reason = state().getReason();
        return reason == null ? Optional.empty() : Optional.of(reason);
    }

    @Override
    protected boolean setSuspendReason(SuspendReason reason) {
        return swapState(ChildrenContainerState.state(reason), true).isPresent();
    }

    private boolean setSuspendReason(SuspendReason reason, boolean terminating) {
        return swapState(ChildrenContainerState.state(reason), terminating).isPresent();
    }

    protected Optional<ChildrenContainerState> swapState(ChildrenContainerState newState, boolean terminating) {
        if (checkReasonType(newState, SuspendReasonType.TERMINATED))
            Optional.empty();
        ChildrenContainerState state = state();
        while ((!terminating || state.isTerminating()) && !checkReasonType(state, SuspendReasonType.TERMINATED)) {
            if (this.swapState(state, newState))
                return Optional.of(state);
            state = state();
        }
        return Optional.empty();
    }

    @Override
    public boolean isEmpty() {
        Optional<ConcurrentMap<String, SimpleChildNode>> currentChildrenMap = childrenMap();
        return !currentChildrenMap.isPresent() || currentChildrenMap.get().isEmpty();
    }

    private static class SimpleChildNode implements ChildNode {

        private String name;
        private volatile ActorRef actorRef;

        protected SimpleChildNode(String name) {
            this.name = name;
        }

        @Override
        public boolean contains(ActorRef actorRef) {
            ActorRef ref = this.actorRef;
            return ref != null && ref.equals(actorRef);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ActorRef getActorRef() {
            return actorRef;
        }

        @Override
        public boolean isReserved() {
            return actorRef == null;
        }

        @Override
        public boolean isNameReserved() {
            return actorRef == null;
        }

        private boolean reserve(ActorRef actorRef) {
            if (this.actorRef == null && actorRef.getPath().getName().equals(name)) {
                this.actorRef = actorRef;
                return true;
            }
            return false;
        }

        @Override
        public Integer getAID() {
            ActorRef ref = this.actorRef;
            return ref == null ? null : ref.getPath().getAID();
        }

    }

}
