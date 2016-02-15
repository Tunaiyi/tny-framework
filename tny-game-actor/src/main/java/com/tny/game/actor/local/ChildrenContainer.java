package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.local.reason.SuspendReason;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 子ActorRef容器
 * @author KGTny
 *
 */
public abstract class ChildrenContainer {

//	/**
//	 * 添加指定的child
//	 * @param child ActorRef
//	 * @return 返回添加的节点
//	 */
//	protected abstract Optional<ChildNode> add(ActorRef child);

	/**
	 * 移除指定的child
	 * @param child ActorRef
	 * @return 返回移除后SuspendReason是否有改变,如果有改变返回移除前的SuspendReason
	 */
	protected abstract Optional<SuspendReason> remove(ActorRef child);

	/**
	 * 获取指定名字的ChildNode
	 * @param name 指定的名字
	 * @return 返回指定名字的ChildNode
	 */
	public abstract Optional<ChildNode> getNodeByName(String name);

	/**
	 * 通过actorRef获取存在有ActorRef
	 * @param actorRef 指定的actor
	 * @return 返回指定actorRef的ChildNode
	 */
	public abstract Optional<ActorRef> getRefByRef(ActorRef actorRef);

	/**
	 * 通过actorRef获取存在有ActorRef的ChildNode
	 * @param actorRef 指定的actor
	 * @return 返回指定actorRef的ChildNode
	 */
	public abstract Optional<ChildNode> getRefNodeByRef(ActorRef actorRef);

	/**
	 * 通过name获取存在有ActorRef的ChildNode
	 * @param name 指定的名字
	 * @return 返回指定name的ChildNode
	 */
	public abstract Optional<ChildNode> getRefNodeByName(String name);

	/**
	 * @return 获取所以ChildNode
	 */
	public abstract Iterable<? extends ChildNode> nodes();

	/**
	 * 将指定actor移入死亡结合中
	 * @param actor 指定对象
	 */
	public abstract void shallDie(ActorRef actor);

	/**
	 * 添加名字占位符
	 * @param name
	 */
	public abstract void reserve(String name);

	/**
	 * 移除名字占位符
	 * @param name
	 */
	public abstract void unreserve(String name);

	/**
	 * @return 容器是否为空
     */
	public abstract boolean isEmpty();

	/**
	 * 将指定actor初始化给节点,并且获取节点,如果不存在actor的节点择不初始化
	 * @param actorRef 指点的actor
	 * @return 返回初始化的接点
	 */
	protected abstract Optional<ChildNode> initChildNode(ActorRef actorRef);

	/**
	 * 终止完成
	 */
	protected abstract void setTerminated();

	/**
	 * 终止中
	 * @return 是否可以进入终止程序
     */
	protected abstract boolean setTerminating();

	/**
	 * @return 是否正在关闭
	 */
	public abstract boolean isTerminating();

	/**
	 * @return 是否正常状态
	 */
	public abstract boolean isNormal();

	/**
	 * 设置挂起状态原因
	 * @param reason 原因
	 * @return
	 * 	返回是否设置成功Optional, 包含设置前的SuspendReason Optional 状态
	 */
	protected abstract boolean setSuspendReason(SuspendReason reason);

	/**
	 * @return 返回获取挂起原因
	 */
	protected abstract Optional<SuspendReason> getSuspendReason();

	/**
	 * @return 获取所有ActorRef返回Stream
     */
	protected abstract Stream<ActorRef> getAllChildren();

	/**
	 * @return 获取所有已经初始化ActorRef的ChildNode,返回Stream
	 */
	protected abstract Stream<ChildNode> getAllChildrenRefNode();


}
