package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.InternalActorRef;

import java.util.Map;
import java.util.Optional;

public interface Children extends Cell {

	ActorCell actorCell();

	/**
	 * 通过名字获取节点
	 *
	 * @param name 获取节点的名字
	 * @return 返回获取的接点
	 */
	default Optional<ActorRef> getChild(String name) {
		Optional<ChildNode> node = actorCell().childrenRefs().getRefNodeByName(name);
		return node.isPresent() ? Optional.of(node.get().getActorRef()) : Optional.empty();
	}

	/**
	 * 保存指定名字到名字占用(未初始化)节点
	 *
	 * @param name 保存的名字
	 */
	default void reserveChild(String name) {
		actorCell().childrenRefs().reserve(name);
	}

	/**
	 * 移除指定名字的名字占用(未初始化)接点
	 *
	 * @param name 移除的名字
	 */
	default void unreserveChild(String name) {
		actorCell().childrenRefs().unreserve(name);
	}

	/**
	 * 初始化actor到actor对应的节点 若无对应节点则不初始化
	 *
	 * @param actor 初始化节点对应的actor
	 * @return 返回初始化的节点
	 */
	default Optional<ChildNode> initChildNode(ActorRef actor) {
		return actorCell().childrenRefs().initChildNode(actor);
	}

	/**
	 * 获取指定名字的ChildNode
	 * @param name 指定的名字
	 * @return 返回指定名字的ChildNode
	 */
	@Override
	default Optional<ChildNode> getChildNodeByName(String name) {
		return actorCell().childrenRefs().getNodeByName(name);
	}

	/**
	 * 获取指定名字的InternalActorRef
	 * @param name 指定的名字
	 * @return 返回指定名字的InternalActorRef
	 */
	@Override
	default InternalActorRef getSingleChild(String name) {
		if (name.indexOf('#') == -1) {
			Optional<ChildNode> node = actorCell().childrenRefs().getRefNodeByName(name);
			ActorRef actorRef = node.isPresent() ? node.get().getActorRef() : null;
			if (actorRef instanceof InternalActorRef)
				return (InternalActorRef) actorRef;
			return ActorUtils.noBody();
		} else {
			Map.Entry<String, Optional<Integer>> result = ActorCell.splitNameAndAid(name);
			Optional<ChildNode> actor = actorCell().childrenRefs().getRefNodeByName(name);
			if (actor.isPresent()) {
				Optional<Integer> id = result.getValue();
				if (!id.isPresent() || id.get().equals(actor.get().getAID())) {
					return (InternalActorRef) actor.get().getActorRef();
				}
			}
			return ActorUtils.noBody();
		}
	}
}
