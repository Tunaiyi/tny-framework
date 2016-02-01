import com.tny.game.zookeeper.GameKeeper;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class GameKeeperTest {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        final Object lock = new Object();

        GameKeeper keeper = new GameKeeper("192.168.20.180:2201,192.168.20.180:2202,192.168.20.180:2203", 1200000, null);
        //		GameKeeperMonitor monitor = new GameKeeperMonitor(keeper);
        keeper.start();
        //		monitor.monitorExists("/gkeeper", new MonitorHandler() {
        //
        //			@Override
        //			public boolean handle(WatchedEvent event) {
        //				System.out.println("monitorExists watch '/gkeeper' : - " + event.getPath() + " - " + event.getType() + " - " + event.getState());
        //				return true;
        //			}
        //
        //		}, new StatCallback() {
        //
        //			@Override
        //			public void processResult(int rc, String path, Object ctx, Stat stat) {
        //				System.out.println(rc + "  StatCallback callback '/gkeeper'  stat: " + stat);
        //			}
        //
        //		}, null);

        //		monitor.monitorChildren("/gkeeper2", new MonitorHandler() {
        //
        //			@Override
        //			public boolean handle(WatchedEvent event) {
        //				System.out.println("monitorChildren watch '/gkeeper2' : - " + event.getPath() + " - " + event.getType() + " - " + event.getState());
        //				return true;
        //			}
        //
        //		}, new Children2Callback() {
        //
        //			@Override
        //			public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        //				System.out.println(rc + "  Children2Callback callback '/gkeeper2' : " + children + "  stat: " + stat);
        //				if (children != null) {
        //					System.out.println(children);
        //				}
        //			}
        //		}, null);

        //		keeper.exists("/gkeeper", new Watcher() {
        //
        //			@Override
        //			public void process(WatchedEvent event) {
        //				System.out.println("exists watch '/gkeeper' : - " + event.getPath() + " - " + event.getType() + " - " + event.getState());
        //			}
        //
        //		}, new StatCallback() {
        //
        //			@Override
        //			public void processResult(int rc, String path, Object ctx, Stat stat) {
        //
        //				System.out.println(KeeperException.Code.get(rc) + "  StatCallback callback '/gkeeper' stat: " + stat);
        //				if (stat != null) {
        //					System.out.println(stat);
        //				}
        //			}
        //		}, null);
        //
        //		keeper.getData("/gkeeper1", new Watcher() {
        //
        //			@Override
        //			public void process(WatchedEvent event) {
        //				System.out.println("getData watch '/gkeeper1' : - " + event.getPath() + " - " + event.getType() + " - " + event.getState());
        //			}
        //
        //		}, new DataCallback() {
        //
        //			@Override
        //			public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        //				System.out.println(KeeperException.Code.get(rc) + "  DataCallback callback '/gkeeper1' stat: " + stat);
        //			}
        //		}, null);
        //
        //		keeper.getChildren("/gkeeper2", new Watcher() {
        //
        //			@Override
        //			public void process(WatchedEvent event) {
        //				System.out.println("getChildren watch '/gkeeper2' : - " + event.getPath() + " - " + event.getType() + " - " + event.getState());
        //			}
        //
        //		}, new Children2Callback() {
        //
        //			@Override
        //			public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        //				System.out.println(KeeperException.Code.get(rc) + "  Children2Callback callback '/gkeeper2' stat: " + stat + "  children : " + children);
        //			}
        //		}, null);

        //		keeper.exists("/gkeeper", new Watcher() {
        //
        //			@Override
        //			public void process(WatchedEvent event) {
        //				System.out.println("exists watch '/gkeeper' : - " + event.getPath() + " - " + event.getType() + " - " + event.getState());
        //			}
        //
        //		}, new StatCallback() {
        //
        //			@Override
        //			public void processResult(int rc, String path, Object ctx, Stat stat) {
        //
        //				System.out.println(KeeperException.Code.get(rc) + "  StatCallback callback '/gkeeper' stat: " + stat);
        //				if (stat != null) {
        //					System.out.println(stat);
        //				}
        //			}
        //		}, null);

        //		keeper.exists("/gkeeper", false, new StatCallback() {
        //
        //			@Override
        //			public void processResult(int rc, String path, Object ctx, Stat stat) {
        //
        //				System.out.println(KeeperException.Code.get(rc) + "  StatCallback callback '/gkeeper' stat: " + stat);
        //				if (stat != null) {
        //					System.out.println(stat);
        //				}
        //			}
        //
        //		}, null);

        //		if (keeper.exists("/gkeeper", false) == null) {
        //			keeper.create("/gkeeper", "gkeeper".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new StringCallback() {
        //
        //				@Override
        //				public void processResult(int rc, String path, Object ctx, String name) {
        //					System.out.println(rc + " : " + KeeperException.Code.get(rc) + " " + path + " " + name);
        //				}
        //
        //			}, null);
        //		} else {
        //			keeper.setData("/gkeeper", ("gkeeper" + new Random().nextInt(Integer.MAX_VALUE)).getBytes(), -1);
        //		}

        keeper.getData("/gkeeper-none", new Watcher() {

            @Override
            public void process(WatchedEvent event) {
                System.out.println("getData watch '/gkeeper-none' : - " + event.getPath() + " - " + event.getType() + " - " + event.getState());
            }

        }, new DataCallback() {

            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println(KeeperException.Code.get(rc) + "  DataCallback callback '/gkeeper-none' stat: " + stat);
            }
        }, null);
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
