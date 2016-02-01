package com.tny.game.monitor;

import com.tny.game.zookeeper.GameKeeper;
import com.tny.game.zookeeper.GameKeeperMonitor;
import com.tny.game.zookeeper.MonitorTask;
import com.tny.game.zookeeper.RenewTask;
import com.tny.game.zookeeper.retry.UntilSuccRetryPolicy;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RemoteMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteMonitor.class);

    private static final byte[] NOTE_DATE = new byte[0];

    private NodeDataFormatter defaultFormatter;

    /**
     * keeper
     */
    private GameKeeper keeper;

    /**
     * 节点监视器
     */
    private GameKeeperMonitor monitor;

    /**
     * 服务节点变化监听器
     */
    private List<NodeHandler<?>> childHandlerList = new CopyOnWriteArrayList<NodeHandler<?>>();

    /**
     * 服务节点监视器集合
     */
    private ConcurrentMap<String, MonitoredNode<?>> monitoredNodeMap = new ConcurrentHashMap<String, MonitoredNode<?>>();

    /**
     * 节点数据
     */
    private volatile ConcurrentHashMap<String, RemoteNode<?>> nodeMap = new ConcurrentHashMap<String, RemoteNode<?>>();

    public RemoteMonitor(String keeperIP, NodeDataFormatter defaultFormatter) throws IOException {
        super();
        this.defaultFormatter = defaultFormatter;
        this.keeper = new GameKeeper(keeperIP, 2000, null);
        //断线重新执行注册任务
        this.keeper.addRenewTask(new RenewTask() {

            @Override
            public void renew(KeeperState state, GameKeeper gameKeeper) {
                List<RemoteNode<?>> node = new ArrayList<RemoteNode<?>>(RemoteMonitor.this.nodeMap.values());
                RemoteMonitor.this.nodeMap.clear();
                while (!RemoteMonitor.this.putNodeData(node)) {
                }
            }

        });
        this.keeper.start();
        this.monitor = new GameKeeperMonitor(this.keeper);
    }

    /**
     * 注册
     *
     * @return
     */
    private synchronized boolean putNodeData(Collection<RemoteNode<?>> nodeCollection) {
        for (RemoteNode<?> node : nodeCollection) {
            if (!this.putNodeData(node))
                return false;
        }
        return true;
    }

    public synchronized boolean putNodeData(CreateMode createMode, Object data, String path) {
        RemoteNode<Object> node = new RemoteNode<Object>(createMode, data, path, this.defaultFormatter);
        return this.putNodeData(node);
    }

    public synchronized boolean putNodeData(CreateMode createMode, Object data, String path, NodeDataFormatter formatter) {
        RemoteNode<Object> node = new RemoteNode<Object>(createMode, data, path, formatter == null ? this.defaultFormatter : formatter);
        return this.putNodeData(node);
    }

    public <T> T getNodeData(String path, CreateMode createMode) {
        return this.getNodeData(path, createMode, this.defaultFormatter);
    }

    @SuppressWarnings("unchecked")
    public <T> T getNodeData(String path, CreateMode createMode, NodeDataFormatter formatter) {
        RemoteNode<T> node = (RemoteNode<T>) this.nodeMap.get(path);
        if (node != null)
            return node.getData();
        Stat stat = new Stat();
        byte[] bytes;
        try {
            bytes = this.keeper.getData(path, false, stat);
        } catch (KeeperException e) {
            LOGGER.error("获取{}节点属性失败", path, e);
            return null;
        } catch (InterruptedException e) {
            LOGGER.error("获取{}节点属性失败", path, e);
            return null;
        }
        if (bytes == null || bytes.length == 0)
            return null;
        T data = formatter.bytes2Data(bytes);
        node = new RemoteNode<T>(createMode, data, path, formatter);
        RemoteNode<T> lastOne = null;
        if ((lastOne = (RemoteNode<T>) this.nodeMap.putIfAbsent(node.getPath(), node)) == null) {
            return data;
        } else {
            return lastOne.getData();
        }
    }

    private synchronized boolean putNodeData(RemoteNode<?> node) {
        if (this.nodeMap.putIfAbsent(node.getPath(), node) == null) {
            Stat stat = this.createFullPath(node.getPath(), node.getDataBytes(), node.getCreateMode(), true);
            if (stat == null)
                return this.putNodeData(node);
            node.setKeeperStat(stat);
            return true;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public synchronized boolean syncNode(String path, Object data) {
        final RemoteNode<Object> node = (RemoteNode<Object>) this.nodeMap.get(path);
        if (node == null)
            return false;
        node.change(data);
        if (node.sync()) {
            try {
                this.keeper.setData(node.getPath(), node.getDataBytes(), -1);
                node.finish();
                return true;
            } catch (KeeperException e) {
                if (e.code() == Code.CONNECTIONLOSS || e.code() == Code.SESSIONEXPIRED) {
                    node.syncFail();
                    return this.syncNode(path, data);
                } else {
                    LOGGER.error("设置{}节点属性{}失败", path, node.getData(), e);
                }
            } catch (InterruptedException e) {
                LOGGER.error("设置{}节点属性{}失败", path, node.getData(), e);
            }
        }
        return false;
    }

    public synchronized void removeNodeData(String path) {
        final RemoteNode<?> node = this.nodeMap.get(path);
        if (node != null) {
            try {
                this.keeper.delete(node.getPath(), node.getKeeperStat().getVersion());
                this.nodeMap.remove(path, node);
            } catch (KeeperException e) {
                if (e.code() == Code.CONNECTIONLOSS || e.code() == Code.SESSIONEXPIRED) {
                    this.removeNodeData(path);
                } else {
                    LOGGER.error("删除{}节点属性{}失败", path, node.getData(), e);
                }
            } catch (InterruptedException e) {
                LOGGER.error("删除{}节点属性{}失败", path, node.getData(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private MonitoredNode<Object> getNode(String path) {
        return (MonitoredNode<Object>) this.monitoredNodeMap.get(path);
    }

    public Stat createFullNode(String path, Object leafValue, CreateMode mode, boolean setIfExist) {
        return this.createFullPath(path, this.object2Bytes(leafValue, null), mode, setIfExist);
    }

    public Stat createFullNode(String path, Object leafValue, CreateMode mode, boolean setIfExist, NodeDataFormatter formater) {
        return this.createFullPath(path, this.object2Bytes(leafValue, formater), mode, setIfExist);
    }

    /**
     * 创建全路径
     *
     * @param path      路径
     * @param nodeValue 节点值
     * @param leafValue 叶子值
     * @param mode      叶子节点创建模式
     * @return
     */
    private Stat createFullPath(String path, byte[] leafValue, CreateMode mode, boolean setIfExist) {
        Stat stat = null;
        boolean no_path = false;
        try {
            stat = this.keeper.exists(path, false);
            //若存在路径设置值
            if (stat != null) {
                if (setIfExist)
                    return this.keeper.setData(path, leafValue, stat.getVersion());
                return stat;
            } else {
                no_path = true;
            }
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                no_path = true;
            } else {
                LOGGER.error("", e);
                return null;
            }
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
        if (no_path) {
            String[] paths = path.split("/");
            if (paths.length < 2)
                return null;
            String currentPath = "";
            for (int index = 1; index < paths.length; index++) {
                currentPath += ("/" + paths[index]);
                boolean last = index == paths.length - 1;
                stat = this.createPath(currentPath, last ? leafValue : NOTE_DATE, last ? mode : CreateMode.PERSISTENT, last ? setIfExist : false);
                if (stat == null)
                    return null;
            }
        }
        return stat;
    }

    /**
     * 创建指定路径
     *
     * @param path  路径
     * @param value 值
     * @param mode  节点创建模式
     * @return
     */
    private Stat createPath(String path, byte[] value, CreateMode mode, boolean setIfExist) {
        try {
            Stat stat = this.keeper.exists(path, false);
            if (stat == null) {
                LOGGER.debug("创建路径{}", path);
                this.keeper.create(path, value, Ids.OPEN_ACL_UNSAFE, mode);
                LOGGER.debug("创建路径{}成功", path);
            } else {
                if (setIfExist)
                    this.keeper.setData(path, value, stat.getVersion());
            }
            return new Stat();
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                return this.createPath(path, value, mode, setIfExist);
            } else {
                LOGGER.error("", e);
                return null;
            }
        } catch (InterruptedException e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public void monitorChildren(String nodePath) {
        this.monitorChildren(nodePath, this.defaultFormatter);
    }

    private void monitorChidren(String rootPath, List<String> children, NodeDataFormatter formatter) {
        for (String child : children) {
            String childPath = rootPath + "/" + child;
            if (!this.monitoredNodeMap.containsKey(childPath)) {
                MonitoredNode<?> node = new MonitoredNode<Object>(formatter);
                RemoteMonitor.this.doMonitorNode(node, childPath);
            }
        }
    }

    public void monitorChildren(String nodePath, final NodeDataFormatter formatter) {
        MonitoredNode<?> current = RemoteMonitor.this.monitoredNodeMap.get(nodePath);
        if (current != null)
            return;
        this.createFullPath(nodePath, NOTE_DATE, CreateMode.PERSISTENT, false);
        final ChildrenCallback callback = new ChildrenCallback() {

            @Override
            public void processResult(int rc, String path, Object ctx, List<String> children) {
                Code code = KeeperException.Code.get(rc);
                if (code == Code.OK) {
                    RemoteMonitor.this.monitorChidren(path, children, formatter);
                }
            }

        };
        RemoteMonitor.this.monitor.monitorChildren(nodePath, null, callback, null);
    }

    public void monitorNode(String nodePath) {
        this.monitorNode(nodePath, this.defaultFormatter);
    }

    public void monitorNode(String nodePath, final NodeDataFormatter formatter) {
        MonitoredNode<?> node = new MonitoredNode<Object>(formatter);
        this.doMonitorNode(node, nodePath);
    }

    private void doMonitorNode(MonitoredNode<?> current, final String nodePath) {
        MonitoredNode<?> oldNode = RemoteMonitor.this.monitoredNodeMap.putIfAbsent(nodePath, current);
        if (oldNode == null) {
            final MonitorTask task = this.monitor.monitorData(nodePath, new Watcher() {

                @Override
                public void process(WatchedEvent event) {
                    EventType eventType = event.getType();
                    if (eventType == EventType.NodeDeleted) {
                        RemoteMonitor.this.removeMonitorNode(nodePath);
                    }
                }

            }, new DataCallback() {

                @Override
                public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                    Code code = Code.get(rc);
                    if (code == Code.OK) {
                        RemoteMonitor.this.notifyNodeChange(nodePath, data);
                    } else if (code == Code.NONODE) {
                        RemoteMonitor.this.removeMonitorNode(nodePath);
                    }
                }

            }, null, new UntilSuccRetryPolicy(1000));
            current.setMonitorTask(task);
        }
    }

    @SuppressWarnings("unchecked")
    private void removeMonitorNode(String nodePath) {
        MonitoredNode<?> node = RemoteMonitor.this.monitoredNodeMap.remove(nodePath);
        if (node != null) {
            node.getMonitorTask().cancel();
            LOGGER.debug("移除监听服务器节点 {}", nodePath);
            this.fireNodeDelected(new NodeEvent<Object>(nodePath, (MonitoredNode<Object>) node));
        }
    }

    private void notifyNodeChange(String nodePath, byte[] data) {
        MonitoredNode<Object> node = this.getNode(nodePath);
        if (node != null) {
            try {
                node.change(data);
            } catch (Throwable e) {
                LOGGER.error("解析 {} 节点数据错误", nodePath, e);
            }
            if (node.isMonitor()) {
                this.fireNodeChange(new NodeEvent<Object>(nodePath, node));
            } else {
                node.monitored();
                this.fireNodeCreaate(new NodeEvent<Object>(nodePath, node));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void fireNodeChange(NodeEvent<Object> event) {
        for (NodeHandler<?> handler : this.childHandlerList) {
            try {
                if (handler.isCanHandle(event.getPath()))
                    ((NodeHandler<Object>) handler).notifyNodeChange(event);
            } catch (Exception e) {
                LOGGER.error("{} 处理器执行 {} 路径 ChildChange 事件异常 |", handler.getClass(), event.getPath(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void fireNodeCreaate(NodeEvent<Object> event) {
        for (NodeHandler<?> handler : this.childHandlerList) {
            try {
                if (handler.isCanHandle(event.getPath()))
                    ((NodeHandler<Object>) handler).notifyNodeCreaate(event);
            } catch (Exception e) {
                LOGGER.error("{} 处理器执行 {} 路径 ChildCreaate 事件异常 |", handler.getClass(), event.getPath(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void fireNodeDelected(NodeEvent<Object> event) {
        for (NodeHandler<?> handler : this.childHandlerList) {
            try {
                if (handler.isCanHandle(event.getPath()))
                    ((NodeHandler<Object>) handler).notifyNodeDelected(event);
            } catch (Exception e) {
                LOGGER.error("{} 处理器执行 {} 路径 ChildDelected 事件异常 |", handler.getClass(), event.getPath(), e);
            }
        }
    }

    public Map<String, MonitoredNode<?>> getMonitoredNodeMap() {
        return Collections.unmodifiableMap(this.monitoredNodeMap);
    }

    /**
     * 添加服务节点监听器
     *
     * @param listener
     */
    public void addChildListener(NodeHandler<?> listener) {
        this.childHandlerList.add(listener);
    }

    /**
     * 添加服务节点监听器
     *
     * @param listener
     */
    public void addChildListener(Collection<NodeHandler<?>> listener) {
        this.childHandlerList.addAll(listener);
    }

    /**
     * 移除服务节点监听器
     *
     * @param listener
     */
    public void removeChildListener(NodeHandler<?> listener) {
        this.childHandlerList.remove(listener);
    }

    /**
     * 清楚服务器节点监听
     */
    public void clearChildListener() {
        this.childHandlerList.clear();
    }

    public GameKeeper getKeeper() {
        return this.keeper;
    }

    public GameKeeperMonitor getMonitor() {
        return this.monitor;
    }

    private byte[] object2Bytes(Object object, NodeDataFormatter formater) {
        if (object == null)
            return null;
        if (formater == null)
            formater = this.defaultFormatter;
        if (formater != null)
            return formater.data2Bytes(object);
        return (byte[]) object;
    }

    //	@SuppressWarnings("unchecked")
    //	private <T> T bytes2Object(byte[] bytes, NodeDataFormatter formater) {
    //		if (bytes == null)
    //			return null;
    //		if (formater == null)
    //			formater = this.defaultFormatter;
    //		if (formater != null) {
    //			try {
    //				return formater.bytes2Data(bytes);
    //			} catch (Exception e) {
    //				LOGGER.error("", e);
    //				return null;
    //			}
    //		}
    //		return (T) bytes;
    //	}

    //	public <T> T getData(final String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
    //		return this.getData(path, watcher, stat, null);
    //	}
    //
    //	public <T> T getData(final String path, Watcher watcher, Stat stat, NodeDataFormatter formater) throws KeeperException, InterruptedException {
    //		byte[] bytes = this.keeper.getData(path, watcher, stat);
    //		return this.bytes2Object(bytes, formater);
    //	}
    //
    //	public <T> T getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
    //		return this.getData(path, watch, stat, null);
    //	}
    //
    //	public <T> T getData(String path, boolean watch, Stat stat, NodeDataFormatter formater) throws KeeperException, InterruptedException {
    //		byte[] bytes = this.keeper.getData(path, watch, stat);
    //		return this.bytes2Object(bytes, formater);
    //	}
    //
    //	public <T> void getData(String path, Watcher watcher, GameKeeperDataCallback<T> cb, Object ctx) {
    //		this.getData(path, watcher, null, cb, ctx);
    //	}
    //
    //	public <T> void getData(final String path, Watcher watcher, final NodeDataFormatter formater, final GameKeeperDataCallback<T> cb, Object ctx) {
    //		this.keeper.getData(path, watcher, new DataCallback() {
    //
    //			@Override
    //			public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
    //				T value = RemoteMonitor.this.bytes2Object(data, formater);
    //				cb.processResult(rc, path, ctx, value, stat);
    //			}
    //
    //		}, ctx);
    //	}
    //
    //	public <T> void getData(String path, boolean watch, final GameKeeperDataCallback<T> cb, final Class<T> clazz, Object ctx) {
    //		this.getData(path, watch, null, cb, clazz, ctx);
    //	}
    //
    //	public <T> void getData(String path, boolean watch, final NodeDataFormatter formater, final GameKeeperDataCallback<T> cb, final Class<T> clazz, Object ctx) {
    //		this.keeper.getData(path, watch, new DataCallback() {
    //
    //			@Override
    //			public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
    //				T value = RemoteMonitor.this.bytes2Object(data, formater);
    //				cb.processResult(rc, path, ctx, value, stat);
    //			}
    //
    //		}, ctx);
    //	}
    //
    //	public Stat setData(final String path, Object object, int version) throws KeeperException, InterruptedException {
    //		return this.setData(path, object, version, null);
    //	}
    //
    //	public Stat setData(final String path, Object object, int version, NodeDataFormatter formater) throws KeeperException, InterruptedException {
    //		return this.keeper.setData(path, this.object2Bytes(object, formater), version);
    //	}
    //
    //	public void setData(final String path, Object object, int version, StatCallback cb, Object ctx) {
    //		this.setData(path, object, version, null, cb, ctx);
    //	}
    //
    //	public void setData(final String path, Object object, int version, NodeDataFormatter formater, StatCallback cb, Object ctx) {
    //		this.keeper.setData(path, this.object2Bytes(object, this.defaultFormatter), version, cb, ctx);
    //	}

    public static void main(String[] args) throws IOException, InterruptedException {
        //		Object object = new Object();
        //		final GameServerKeeper keeper = new GameServerKeeper("keeper.properties");
        //		keeper.addChildListener(new ChildListener() {
        //
        //			@Override
        //			public void notifyChildDelected(ChildEvent event) {
        //				System.out.println("##----------## notifyChildDelected -- " + event.getPath());
        //			}
        //
        //			@Override
        //			public void notifyChildChange(ChildEvent event) {
        //				System.out.println("##+++++++++++## notifyChildChange -- " + event.getPath() + "--" + new String(event.getValue()));
        //			}
        //
        //			@Override
        //			public void notifyChildCreaate(ChildEvent event) {
        //				System.out.println("##+++++++++++## notifyChildCreate -- " + event.getPath() + "--" + new String(event.getValue()));
        //			}
        //
        //		});
        //		keeper.addCapacityListener(new CapacityListener() {
        //
        //			@Override
        //			public void notifyCapacityChange(CapacityChangeEvent event) {
        //				System.out.println("notifyCapacityChange -- " + event.getNewCapacity());
        //			}
        //
        //		});
        //		System.out.println("starting");
        //		Set<NodeData> dataSet = new HashSet<NodeData>();
        //		Config config = ConfigLib.getConfig("keeper.properties");
        //		final WorldServerInfo info = new WorldServerInfo(1, config);
        //		final NodeData ndata = new NodeData(CreateMode.EPHEMERAL, info, config.getStr("game.keeper.children_path") + "/1");
        //		config.addConfigReload(new ConfigReload() {
        //
        //			@Override
        //			public void reload(Config config) {
        //				info.updateInfo(config);
        //				ndata.change();
        //				keeper.syncNodeData(ndata.getPath());
        //			}
        //
        //		});
        //		dataSet.add(ndata);
        //		keeper.start(dataSet);
        //		System.out.println("started");
        //		synchronized (object) {
        //			object.wait();
        //		}
    }
}
