package com.tny.game.actor.local;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tny.game.LogUtils;
import com.tny.game.actor.*;
import com.tny.game.actor.event.Debug;
import com.tny.game.actor.event.Error;
import com.tny.game.actor.event.LogEvent;
import com.tny.game.actor.event.Warning;
import com.tny.game.actor.exception.ActorInitializationException;
import com.tny.game.actor.exception.ActorInterruptedException;
import com.tny.game.actor.exception.InvalidActorNameException;
import com.tny.game.actor.exception.NonFatal;
import com.tny.game.actor.exception.PostRestartException;
import com.tny.game.actor.exception.PreRestartException;
import com.tny.game.actor.local.message.TerminatedAutoMsg;
import com.tny.game.actor.local.reason.CreationReason;
import com.tny.game.actor.local.reason.RecreateReason;
import com.tny.game.actor.local.reason.SuspendReason;
import com.tny.game.actor.system.DeathWatchedSysMsg;
import com.tny.game.actor.system.FailedSysMsg;
import com.tny.game.actor.system.UnwatchSysMsg;
import com.tny.game.actor.system.WatchSysMsg;
import com.tny.game.actor.util.Durations;
import com.tny.game.common.reflect.ReflectUtils;
import com.tny.game.common.utils.Asserts;
import com.tny.game.common.utils.collection.ImmutableEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Actor单元,管理一个Actor的
 * Actor对象, ActorRef 对象, ActorMailBox对象, Dispatcher对象, ActorContext对象
 */
public class ActorCell implements Postor, Children, ActorContext, Cell {

    private final static long POST_BOX_OFF_SET = Unsafer.unsafe().objectFieldOffset(ReflectUtils.getDeepField(ActorCell.class, "_postbox"));

    public static final int UNDEFINED_AID = 0;

    private static final int DEFAULT_STATE = 0;
    private static final int SUSPENDED_STATE = 1;
    private static final int SUSPENDED_WAIT_FOR_CHILDREN_STATE = 2;

    private volatile Postbox _postbox;

    private BaseActorSystem system;

    private InternalActorRef self;

    private InternalActorRef parent;

    private ChildrenContainer childrenRefs = new DefaultChildrenContainer();

    private MessagePostman postman;

    private Props props;

    private ActorRefProvider provider;

    /*
     * 通过系统事件,收到CreateEvent创建
     */
    private Actor<Object> actor;

    private ImmutableList<Consumer<Object>> behaviorStack = ImmutableList.of();

    private Set<ActorRef> watching = new HashSet<>();

    private Set<ActorRef> watchedBy = new HashSet<>();

    private Set<ActorRef> terminatedQueued = new HashSet<>();

    private ActorRef failed = null;

    private ActorRef perpetrator = null;

    private Envelope currentMessage = null;

    private AtomicInteger nameCreator = new AtomicInteger(0);

    private static AtomicInteger aidCreator = new AtomicInteger(0);

    private static boolean match(String value) {
        return ActorPath.ELEMENT_REGEX.matcher(value).matches();
    }

    @Override
    public Postbox getPostbox() {
        return _postbox;
    }

    protected ActorRefProvider getProvider() {
        return provider;
    }

    @Override
    public Props getProps() {
        return props;
    }

    @Override
    public InternalActorRef getParent() {
        return parent;
    }

    @Override
    public MessagePostman getPostman() {
        return postman;
    }

    @Override
    public ActorCell actorCell() {
        return this;
    }

    @Override
    public Postbox swapPostbox(Postbox newPostbox) {
        Postbox oldMailbox = this.getPostbox();
        while (!Unsafer.unsafe().compareAndSwapObject(this, POST_BOX_OFF_SET, oldMailbox, newPostbox)) {
            oldMailbox = this.getPostbox();
        }
        return oldMailbox;
    }

    private String checkName(String name) {
        if (StringUtils.isBlank(name))
            throw new InvalidActorNameException("name 不可为null 或 字符串");
        if (!match(name))
            throw new InvalidActorNameException(LogUtils.format("{} name 与正则表达式 {} 不匹配", name, ActorPath.ELEMENT_REGEX_STR));
        return name;
    }

    static Entry<String, Optional<Integer>> splitNameAndAid(String name) {
        String[] values = StringUtils.split(name, "#");
        if (values.length < 2) {
            return ImmutableEntry.entry(values[0], Optional.empty());
        } else {
            Integer aid = NumberUtils.toInt(values[1]);
            if (ActorUtils.isUndefinedAID(aid)) {
                return ImmutableEntry.entry(values[0], Optional.empty());
            } else {
                return ImmutableEntry.entry(values[0], Optional.of(aid));
            }
        }
    }

    public Actor<?> getActor() {
        return actor;
    }

    @Override
    public ActorRef self() {
        return self;
    }

    public int getAID() {
        return self().getPath().getAID();
    }

    @Override
    public ActorRef getSender() {
        //TODO getSender
        return null;
    }

    @Override
    public ActorSystem getSystem() {
        return system;
    }

    @Override
    public boolean isLocal() {
        return self.isLocal();
    }

    @SuppressWarnings("unchecked")
    public void create(Optional<ActorInitializationException> failure) {
        failure.ifPresent((e) -> {
            throw e;
        });
        try {
            Actor<?> created = this.newActor();
            this.actor = (Actor<Object>) created;

            checkReceiveTimeout();
            publish(Debug.debug(self.getPath(), asClass(created), "启动actor ({}})", actor));
        } catch (Throwable e) {
            if (e instanceof InterruptedException) {
                clearActorCellFields();
                Thread.currentThread().interrupt();
                throw new ActorInitializationException(self, "在创建Actor的时候线程中断异常", e);
            } else if (NonFatal.isNonFatal(e)) {
                clearOutActorIfNonNull();
                throw new ActorInitializationException(self, "在创建Actor的时候发生异常", e);
            }
        }
    }

    private void clearOutActorIfNonNull() {
        if (this.actor != null) {
            clearActorFields(actor);
            actor = null;
        }
    }

    @SuppressWarnings("unchecked")
    protected Actor<Object> newActor() {
        return (Actor<Object>) props.newActor(this);
    }

    //region ActorContext 实现的方法
    @Override
    public ActorRef actorOf(Props props) {
        return makeChild(this, props, randomName(), false);
    }

    @Override
    public ActorRef actorOf(Props props, String name) {
        return makeChild(this, props, checkName(name), false);
    }


//    private String checkName(String name) {
//        if (StringUtils.isBlank(name))
//            throw new InvalidActorNameException("actor 名字不可为 null 或 空字符串");
//        if (nameRegex(name)) {
//            return name;
//        } else {
//
//        }
//    }

    private boolean nameRegex(String name) {
        return true;
    }

    private String randomName() {
        return "Actor-" + this.nameCreator.incrementAndGet();
    }

    @Override
    public void stop(ActorRef actor) {
        Optional<ChildNode> node = childrenRefs.getRefNodeByRef(actor);
        if (node.isPresent())
            childrenRefs.shallDie(actor);
        if (actor instanceof InternalActorRef)
            ((InternalActorRef) actor).stop();
    }


    @Override
    public void setReceiveTimeout(Duration duration) {
    }

    //endregion

    //region Children 实现的方法
    protected ChildrenContainer childrenRefs() {
        return childrenRefs;
    }

    @Override
    public void reserveChild(String name) {
        childrenRefs.reserve(name);
    }

    @Override
    public void unreserveChild(String name) {
        childrenRefs.unreserve(name);
    }

    @Override
    public Optional<ChildNode> initChildNode(ActorRef ref) {
        return childrenRefs.initChildNode(ref);
    }

    /**
     * 设置Container暂停原因
     *
     * @param reason 暂停原因
     * @return 返回是否设置成功
     */
    protected boolean setContainerSuspendReason(SuspendReason reason) {
        return childrenRefs.setSuspendReason(reason);
    }

    /**
     * 设置Container暂停原因为Terminated
     */
    protected boolean setTerminating() {
        return childrenRefs.setTerminating();
    }

    /**
     * 设置Container暂停原因为Terminated
     */
    protected void setTerminated() {
        childrenRefs.setTerminated();
    }

    /**
     * 是否正常
     *
     * @return 返回是否正常
     */
    protected boolean isNormal() {
        return childrenRefs.isNormal();
    }

    /**
     * @return 判断是否在终止中
     */
    protected boolean isTerminating() {
        return childrenRefs.isTerminating();
    }

    /**
     * @return 判断需要等待Children处理
     */
    protected boolean isWaitingForChildren() {
        Optional<SuspendReason> reason = childrenRefs.getSuspendReason();
        return reason.isPresent() && reason.get().getReasonType().isWaitingForChildren();
    }

    /**
     * 挂起所有Child
     */
    protected void suspendChildren() {
        this.suspendChildren(ImmutableSet.<ActorRef>of());
    }

    /**
     * 挂起除exceptFor外的所有Child
     *
     * @param exceptFor 不挂起列表
     */
    protected void suspendChildren(Set<ActorRef> exceptFor) {
        if (exceptFor == null)
            exceptFor = ImmutableSet.of();
        for (ChildNode node : childrenRefs.nodes()) {
            ActorRef actorRef = node.getActorRef();
            if (!node.isNameReserved() && exceptFor.contains(actorRef)) {
                if (actorRef instanceof InternalActorRef)
                    ((InternalActorRef) actorRef).suspend();
            }
        }
    }

    /**
     * 恢复指定child
     *
     * @param causedByFailure 挂起原因
     * @param child           恢复的child
     */
    protected void resumeChildren(Throwable causedByFailure, ActorRef child) {
        for (ChildNode node : childrenRefs.nodes()) {
            ActorRef actorRef = node.getActorRef();
            if (!node.isNameReserved()) {
                if (actorRef instanceof InternalActorRef)
                    ((InternalActorRef) actorRef).resume(
                            actorRef.equals(child) ? causedByFailure : null);
            }
        }
    }

    /**
     * 获取actor对应的ChildNode节点
     *
     * @param actor 指定的actor
     * @return 返回对应的节点
     */
    protected Optional<ChildNode> getChildNodeByRef(ActorRef actor) {
        return childrenRefs.getRefNodeByRef(actor);
    }

    /**
     * @return 获取所有的节点
     */
    private Iterable<? extends ChildNode> getAllChildNode() {
        return childrenRefs.nodes();
    }

    /**
     * 移除child,如果移除前和移除后State发生改变,返回移除前的状态
     *
     * @param child 指定child
     * @return 如果移除前和移除后State发生改变, 返回移除前的状态
     */
    private Optional<SuspendReason> removeChildAndGetStateChange(ActorRef child) {
        return childrenRefs.remove(child);
    }

    /**
     * 创建子Actor引用对象
     *
     * @param cell
     * @param props
     * @param name
     * @param systemService
     * @return
     */
    private ActorRef makeChild(ActorCell cell, Props props, String name, boolean systemService) {
        if (this.childrenRefs.isTerminating())
            throw new IllegalStateException("正在关闭,无法重建actor");
        reserveChild(name);
//        try {
//            ActorPath childPath = cell.self.getPath().child(name).withUid(aidCreator.incrementAndGet());
//            cell.provider.actorOf(cell.system, props, cell.self, childPath, systemService);
//        }
        return parent;
    }
    //endregion

    //region DeathWatch 实现的方法

    /**
     * 判断actor是否非本地
     *
     * @param actorRef 指定的actor
     * @return 是否非本地
     */
    private static boolean isNonLocal(ActorRef actorRef) {
        return actorRef == null || actorRef instanceof InternalActorRef && !((InternalActorRef) actorRef).isLocal();
    }

    @Override
    public ActorRef watch(ActorRef subject) {
        if (subject instanceof InternalActorRef) {
            InternalActorRef actor = (InternalActorRef) subject;
            if (subject != self() && !watchingContains(subject)) {
                maintainAddressTerminatedSubscription(actor, (watchee) -> {
                    watchee.sendSystemMessage(WatchSysMsg.message(watchee, this.self));
                    watching.add(watchee);
                    publish(Debug.debug(self.getPath(), asClass(actor), "{} 开始 {} 监视(Watch)", self, watchee));
                });
            }
        }
        return null;
    }

    @Override
    public ActorRef unwatch(ActorRef subject) {
        if (subject instanceof InternalActorRef) {
            InternalActorRef actor = (InternalActorRef) subject;
            if (subject != self() && !watchingContains(subject)) {
                actor.sendSystemMessage(UnwatchSysMsg.message(actor, this.self));
                maintainAddressTerminatedSubscription(subject, watchee -> {
                    this.removeFromSet(watchee, watching);
                    publish(Debug.debug(self.getPath(), asClass(actor), "{} 不再监视(Watch) {}", self, watchee));
                });
                this.removeFromSet(subject, terminatedQueued);

                return actor;
            }
        }
        return null;
    }

    /**
     * 接受处理到终止事件
     *
     * @param message 终止事件
     */
    protected void receivedTerminated(TerminatedAutoMsg message) {
        if (terminatedQueued.contains(message.getActorRef())) {
            terminatedQueued.remove(message.getActorRef());
            receiveMessage(message);
        }
    }

    /**
     * 处理收到Watchee或Child的 DeathWatchedSysMsg, 并转发向自己转发TerminatedMessage
     *
     * @param actor              发出DeathWatchedSysMsg的actor
     * @param existenceConfirmed 是否确认终止
     * @param addressTerminated  是否是远程终止
     */
    protected void watchedActorTerminated(ActorRef actor, boolean existenceConfirmed, boolean addressTerminated) {
        if (watchingContains(actor)) {
            maintainAddressTerminatedSubscription(actor, ref -> this.removeFromSet(actor, watching));
            if (!isTerminating()) {
                self.tell(TerminatedAutoMsg.message(actor, existenceConfirmed, addressTerminated), actor);
                terminatedQueuedFor(actor);
            }
        }
        Optional<ActorRef> ref;
        if ((ref = childrenRefs.getRefByRef(actor)).isPresent())
            handleChildTerminated(ref.get());
    }


    /**
     * @return 是否有非本地actor
     */
    private boolean isHasNonLocalAddress() {
        return ((watching.stream().anyMatch(ActorCell::isNonLocal)) || (watchedBy.stream().anyMatch(ActorCell::isNonLocal)));
    }

    /**
     * @param change
     * @param block
     * @param <A>
     */
    private <A extends ActorRef> void maintainAddressTerminatedSubscription(A change, Consumer<A> block) {
        if (isNonLocal(change)) {
            boolean had = isHasNonLocalAddress();
            block.accept(change);
            boolean has = isHasNonLocalAddress();
            if (had && !has)
                unsubscribeAddressTerminated();
            else if (!had && has)
                subscribeAddressTerminated();
        } else {
            block.accept(change);
        }
    }

    private void terminatedQueuedFor(ActorRef actor) {
        terminatedQueued.add(actor);
    }

    private boolean watchingContains(ActorRef subject) {
        return watching.contains(subject) || (!ActorUtils.isUndefinedAID(subject.getPath().getAID()) &&
                watching.contains(new UndefinedAIDActorRef(subject)));
    }

    private void removeFromSet(ActorRef subject, Set<ActorRef> set) {
        if (subject.getPath().getAID() != ActorCell.UNDEFINED_AID) {
            set.remove(subject);
            set.remove(new UndefinedAIDActorRef(subject));
        } else {
            while (set.contains(subject)) {
                set.remove(subject);
            }
        }
    }

    private void sendTerminated2LocalWatcher(ActorRef watcher) {
        sendTerminatedWatcher(false, watcher);
    }

    private void sendTerminated2RemoteWatcher(ActorRef watcher) {
        sendTerminatedWatcher(true, watcher);
    }

    private void sendTerminatedWatcher(boolean isLocal, ActorRef watcher) {
        if (watcher instanceof ActorRefScope && ((ActorRefScope) watcher).isLocal() == isLocal && watcher != parent) {
            watcher.tell(DeathWatchedSysMsg.message(self, true, false));
        }
    }

    protected void tellWatchersWeDied() {
        if (!watchedBy.isEmpty()) {
            try {
                watchedBy.stream().forEach(this::sendTerminated2RemoteWatcher);
                watchedBy.stream().forEach(this::sendTerminated2LocalWatcher);
            } finally {
                watchedBy = new HashSet<>();
            }
        }
    }

    private void tellUnwatchSelf(ActorRef actor) {
        try {
            watching.stream().forEach((watchee) -> watchee.tell(UnwatchSysMsg.message(watchee, self())));
        } finally {
            watching = new HashSet<>();
            terminatedQueued = new HashSet<>();
        }
    }

    protected void unwatchWatchedActors() {
        if (!watching.isEmpty()) {
            maintainAddressTerminatedSubscription(self(), this::tellUnwatchSelf);
        }
    }


    protected void addWatcher(ActorRef watchee, ActorRef watcher) {
        boolean watcheeSelf = self().equals(watchee);
        boolean watcherSelf = self().equals(watcher);

        if (watcheeSelf && !watcherSelf) {
            if (!watchedBy.contains(watcher))
                maintainAddressTerminatedSubscription(watcher, (watcherActor) -> {
                    watchedBy.add(watcherActor);
                    publish(Debug.debug(self.getPath(), asClass(actor), "{} 开始被 {} 监视(Watch)", self, watcher));
                });
        } else if (!watcheeSelf && watcherSelf) {
            this.watch(watchee);
        } else {
            publish(Warning.warning(self.getPath(), asClass(actor), "BUG:(addWatch) 监视者 {} 与被监视者 {} 都不是自己 {}", watcher, watchee, self));
        }
    }

    protected void removeWatcher(ActorRef watchee, ActorRef watcher) {
        boolean watcheeSelf = self().equals(watchee);
        boolean watcherSelf = self().equals(watcher);

        if (watcheeSelf && !watcherSelf) {
            if (watchedBy.contains(watcher))
                maintainAddressTerminatedSubscription(watcher, (watcherActor) -> {
                    watchedBy.remove(watcher);
                    publish(Debug.debug(self.getPath(), asClass(actor), "{} 不再被 {} 监视(Watch)", watchee, watcher));
                });
        } else if (!watcheeSelf && watcherSelf) {
            this.unwatch(watchee);
        } else {
            publish(Warning.warning(self.getPath(), asClass(actor), "BUG:(removeWatch) 监视者 {} 与被监视者 {} 都不是自己 {}", watcher, watchee, self));
        }

    }

    protected void addressTerminated(ActorAddress address) {
        maintainAddressTerminatedSubscription(self(), (ref) -> {
            ActorRef target = null;
            for (ActorRef watcher : watchedBy) {
                if (address.equals(watcher.getPath().getAddress())) {
                    target = watcher;
                    break;
                }
            }
            if (target != null)
                this.watchedBy.remove(target);
        });
        for (ActorRef watchee : watchedBy) {
            if (address.equals(watchee.getPath().getAddress()))
                self.sendSystemMessage(DeathWatchedSysMsg.message(watchee, childrenRefs.getRefNodeByRef(watchee).isPresent(), true));
        }
    }

    protected SuspendReason waitingForChildrenOrNull() {
        Optional<SuspendReason> reason = childrenRefs.getSuspendReason();
        if (reason.isPresent() && reason.get().getReasonType().isWaitingForChildren()) {
            return reason.get();
        }
        return null;
    }
    //endregion

    //region FaultHanding 实现方法

    private void clearFailed() {
        failed = null;
    }

    public void setFailed(ActorRef failed) {
        this.failed = failed;
    }

    public boolean isFailed() {
        return this.failed != null;
    }

    private void suspendNonRecursive() {
        postman.suspend(this);
    }

    private void resumeNonRecursive() {
        postman.resume(this);
    }


    /**
     * 处理自己重启系统事件
     *
     * @param cause
     */
    private void faultRecreate(Throwable cause) {
        if (actor == null) {
            publish(Error.error(cause, self.getPath(), asClass(actor), "由{}导致的Restart操作变成Create操作", cause));
            faultCreate();
        } else if (isNormal()) {
            Actor<Object> failedActor = this.actor;
            if (failedActor != null) {
                Optional<Object> optionalMessage = currentMessage != null ? Optional.of(currentMessage.getMessage()) : Optional.empty();
                try {
                    if (failedActor.getContext() != null)
                        failedActor.aroundPreRestart(cause, optionalMessage);
                } catch (Throwable e) {
                    handleNonFatalOrInterruptedException(e, (ex) -> {
                        PreRestartException preEx = new PreRestartException(self, ex, cause, optionalMessage);
                        publish(Error.error(preEx, self.getPath(), asClass(actor), e.getMessage()));
                    });
                } finally {
                    clearActorFields(failedActor);
                }
            }
            Asserts.assertAt(getPostbox().isSuspended(), "getPostbox 在重启中必须是挂起的, status=" + getPostbox().getStatus());
            if (!setContainerSuspendReason(RecreateReason.instance(cause)))
                finishRecreate(cause, failedActor);
        } else {
            faultResume(null);
        }
    }

    /**
     * 处理自己挂起系统事件
     */
    private void faultSuspend() {
        suspendNonRecursive();
        suspendChildren();
    }

    /**
     * 处理自己恢复系统事件
     *
     * @param cause 挂起原因
     */
    private void faultResume(Throwable cause) {
        if (actor == null) {
            system.eventStream().publish(Error.error(self.getPath(), asClass(actor),
                    "由{}导致的Resume操作变成Create操作" + cause));
            faultCreate();
        } else if (actor.getContext() == null && cause != null) {
            system.eventStream().publish(Error.error(self.getPath(), asClass(actor),
                    "由{}导致的Resume操作变成Create操作" + cause));
            faultRecreate(cause);
        } else {
            ActorRef perpetratorActor = this.perpetrator;
            try {
                resumeNonRecursive();
            } finally {
                if (cause != null)
                    clearFailed();
                resumeChildren(cause, perpetratorActor);
            }
        }
    }

    private void faultCreate() {
        Asserts.assertAt(getPostbox().isSuspended(), "getPostbox 在创建中必须是挂起的, status=" + getPostbox().getStatus());
        Asserts.assertAt(perpetrator == self);

        setReceiveTimeout(Durations.UNDEFINED);
        checkReceiveTimeout();
        this.childrenRefs().getAllChildren().forEach(this::stop);
        if (!setContainerSuspendReason(CreationReason.instance()))
            finishCreate();
    }


    private void finishCreate() {
        try {
            resumeNonRecursive();
        } finally {
            clearFailed();
        }
        try {
            create(Optional.empty());
        } catch (Throwable e) {
            handleNonFatalOrInterruptedException(e, (ex) -> handleInvokeFailure(Collections.emptyList(), e));
        }
    }


    protected void terminate() {
        setReceiveTimeout(Durations.UNDEFINED);
        cancelReceiveTimeout();

        boolean wasTerminating = isTerminating();
        if (setTerminating()) {
            unwatchWatchedActors();
            boolean empty = childrenRefs().isEmpty();
            childrenRefs().getAllChildren().forEach(this::stop);

            // 强制关闭的时候发送
            if (system.isAborting()) {
                childrenRefs().getAllChildren().forEach((child) -> {
                    if (child instanceof ActorRefScope && ((ActorRefScope) child).isLocal())
                        self.sendSystemMessage(DeathWatchedSysMsg.message(child, true, false));
                });
            }

            if (empty) {
                if (!wasTerminating) {
                    // do not process normal messages while waiting for all children to terminate
                    suspendNonRecursive();
                    // do not propagate failures during shutdown to the supervisor
                    setFailed(self);
                    publish(Debug.debug(self.getPath(), asClass(actor), "正在终止中......"));
                }
            } else {
                setTerminated();
                finishTerminate();
            }
        }
    }

    private void handleInvokeFailure(Collection<ActorRef> childrenNotToSuspend, Throwable cause) {
        // prevent any further messages to be processed until the actor has been restarted
        if (!isFailed()) try {
            suspendNonRecursive();
            Set<ActorRef> skip = new HashSet<>();
            if (currentMessage.getMessage() instanceof FailedSysMsg) {
                FailedSysMsg failedMessage = currentMessage.message();
                this.setFailed(failedMessage.getChild());
                skip.add(failedMessage.getChild());
            }
            skip.addAll(childrenNotToSuspend);
            suspendChildren(skip);
            if (cause instanceof InterruptedException) {
                parent.sendSystemMessage(FailedSysMsg.message(self, new ActorInterruptedException(cause), getAID()));
            } else {
                parent.sendSystemMessage(FailedSysMsg.message(self, cause, getAID()));
            }
        } catch (Throwable e) {
            handleNonFatalOrInterruptedException(e, (ex) -> {
                publish(Error.error(ex, self.getPath(), asClass(actor),
                        "紧急停止: 异常发生在处理异常 {} 的过程中\n {} " + cause.getClass(), causeToStackTrace(cause)));
                try {
                    childrenRefs().getAllChildren().forEach(this::stop);
                } finally {
                    finishTerminate();
                }
            });
        }
    }

    private String causeToStackTrace(Throwable cause) {
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter)) {
            cause.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (IOException closeEx) {
            closeEx.printStackTrace();
        }
        return "";
    }

    private void finishTerminate() {
        Actor<Object> finishActor = actor;
    /* The following order is crucial for things to work properly. Only change this if you're very confident and lucky.
     *
     * Please note that if finishActor parent is also finishActor watcher then ChildTerminated and Terminated must be processed in this
     * specific order.
     */
        try {
            if (finishActor != null)
                finishActor.aroundPostStop();
        } catch (Throwable e) {
            handleNonFatalOrInterruptedException(e, (ex) -> publish(Error.error(ex, self.getPath(), asClass(finishActor), ex.getMessage())));
        } finally {
            try {
                postman.detach(this);
            } finally {
                try {
                    parent.sendSystemMessage(DeathWatchedSysMsg.message(self, true, false));
                } finally {
                    try {
                        tellWatchersWeDied();
                    } finally {
                        try {
                            unwatchWatchedActors();
                        } finally {
                            publish(Debug.debug(self.getPath(), asClass(finishActor), "已终止!"));
                            clearActorFields(finishActor);
                            this.clearActorCellFields();
                            actor = null;
                        }
                    }
                }
            }
        }
    }

    private void finishRecreate(Throwable cause, Actor<?> failedActor) {

        // need to keep a snapshot of the surviving children before the new actor instance creates new ones
        Stream<ActorRef> survivorsStream = childrenRefs.getAllChildren();
        List<ActorRef> survivors = survivorsStream.collect(Collectors.toList());

        try {
            try {
                resumeNonRecursive();
            } finally {
                // must happen in any case, so that failure is propagated
                clearFailed();
            }

            Actor<Object> freshActor = newActor();
            actor = freshActor; // this must happen before postRestart has a chance to fail
            if (!freshActor.equals(failedActor))
                setActorFields(freshActor, this, self); // If the creator returns the same instance, we need to restore our null out fields.

            freshActor.aroundPostRestart(cause);
            publish(Debug.debug(self.getPath(), asClass(freshActor), "重启完成"));

            survivors.stream().forEach((child) -> {
                if (child instanceof InternalActorRef) {
                    try {
                        ((InternalActorRef) child).restart(cause);
                    } catch (Exception e) {
                        handleNonFatalOrInterruptedException(e, ex -> publish(Error.error(ex, self.getPath(), asClass(freshActor), "{} 正在重启中... ", child)));
                    }
                }
            });
            // only after parent is up and running again do restart the children which were not stopped
        } catch (Exception e) {
            handleNonFatalOrInterruptedException(e, ex -> {
                clearActorFields(actor);
                handleInvokeFailure(survivors, new PostRestartException(self, ex, cause));
            });
        }
    }

    private void handleFailure(FailedSysMsg failed) throws Throwable {
        currentMessage = Envelope.of(failed, failed.getChild(), system);
        Optional<ChildNode> optional = childrenRefs.getRefNodeByRef(failed.getChild());
        if (optional.isPresent()) {
            ChildNode childNode = optional.get();
            if (childNode.getAID() == failed.getAid()) {
                if (!actor.getSupervisorStrategy().handleFailure(this, failed.getChild(), failed.getCause(), childNode, childrenRefs.getAllChildrenRefNode())) {
                    throw failed.getCause();
                }
            } else {
                publish(Debug.debug(self.getPath(), asClass(actor),
                        "{} 抛出错误 Failed({}), 但查找到的接点Uid不对应 (uid={} != {}})", failed.getCause(), failed.getChild(), childNode.getAID(), failed.getAid()));
            }
        } else {
            publish(Debug.debug(self.getPath(), asClass(actor),
                    "未知Actor {} 抛出错误 Failed({})", failed.getChild(), failed.getCause()));
        }
    }


    private void handleChildTerminated(ActorRef child) {
        Optional<SuspendReason> reasonOptional = removeChildAndGetStateChange(child);
        if (child != null) {
            try {
                actor.getSupervisorStrategy().handleChildTerminated(this, child, childrenRefs.getAllChildren());
            } catch (Throwable e) {
                handleNonFatalOrInterruptedException(e, (ex) -> this.handleInvokeFailure(null, ex));
            }
        }
        if (reasonOptional.isPresent()) {
            SuspendReason reason = reasonOptional.get();
            switch (reason.getReasonType()) {
                case RECREATE:
                    RecreateReason recreate = (RecreateReason) reason;
                    finishRecreate(recreate.getCause(), actor);
                    break;
                case CREATION:
                    finishCreate();
                    break;
                case TERMINATION:
                    finishTerminate();
                    break;
            }
        }
    }

    private void handleNonFatalOrInterruptedException(Throwable e, Consumer<Throwable> func) {
        if (e instanceof InterruptedException) {
            func.accept(e);
            Thread.currentThread().interrupt();
        } else if (NonFatal.isNonFatal(e)) {
            func.accept(e);
        }
    }


    private void receiveMessage(Object message) {
        actor.aroundReceive(message);
    }

    private void clearActorCellFields() {
        this.unstashAll();
        this.props = null;
    }

    private void clearActorFields(Actor<Object> actorInstance) {
        setActorFields(actorInstance, null, system.deadLetters());
        this.currentMessage = null;
        this.behaviorStack = ImmutableList.of();
    }

    private void setActorFields(Actor<Object> actorInstance, ActorContext context, ActorRef actorRef) {
        if (actorInstance != null) {
            actorInstance.resetActor(context, actorRef);
        }
    }


    //endregion


    //region ActorCell Handing 方法实现
    private void supervise(ActorRef child, boolean async) {
        if (!isTerminating()) {
            Optional<ChildNode> childNode = initChildNode(child);
            if (childNode.isPresent()) {
                handleSupervise(child, async);
                publish(Debug.debug(self.getPath(), asClass(actor), "开始监控(supervise) {}", child));
            } else {
                publish(Error.error(self.getPath(), asClass(actor), "无法监控(supervise)未注册的节点 {}", child));
            }
        }
    }

    protected void handleSupervise(ActorRef child, boolean async) {
    }


    private int calculateState() {
        if (waitingForChildrenOrNull() != null)
            return SUSPENDED_WAIT_FOR_CHILDREN_STATE;
        else if (getPostbox().isScheduled())
            return SUSPENDED_STATE;
        else
            return DEFAULT_STATE;
    }

    //endregion

    private void unstashAll() {
    }


    private void subscribeAddressTerminated() {
        //TODO subscribeAddressTerminated
    }

    private void unsubscribeAddressTerminated() {
        //TODO unsubscribeAddressTerminated
    }


    private void checkReceiveTimeout() {
        //TODO unsubscribeAddressTerminated
    }

    private void cancelReceiveTimeout() {
    }

    protected final void publish(LogEvent event) {
        try {
            if (system.setting().isCanLog(event.getLevel()))
                system.eventStream().publish(event);
        } catch (Throwable e) {
            handleNonFatalOrInterruptedException(e, (cause) -> {
            });
        }
    }

    protected final Class<?> asClass(Object o) {
        if (o == null)
            return this.getClass();
        else
            return o.getClass();
    }


}

class UndefinedAIDActorRef extends MinimalActorRef {

    public UndefinedAIDActorRef(ActorRef ref) {
        super(ref.getPath().withUid(ActorCell.UNDEFINED_AID));
    }

    @Override
    public ActorRefProvider getProvider() {
        throw new UnsupportedOperationException("Nobody does not provide");
    }

}