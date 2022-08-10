package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.codec.jackson.*;
import com.tny.game.common.type.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.listener.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/2 02:15
 **/
class EtcdNamespaceExplorerTest {

    private static final ObjectCodecFactory objectCodecFactory = new JacksonObjectCodecFactory();

    private static final ObjectCodecAdapter objectCodecAdapter = new ObjectCodecAdapter(Collections.singletonList(objectCodecFactory));

    public static final ReferenceType<PartitionedNode<TestShadingNode>> TYPE = new ReferenceType<PartitionedNode<TestShadingNode>>() {

    };

    private static final EtcdNamespaceExplorerFactory etcdNamespaceExplorerFactory = new EtcdNamespaceExplorerFactory(
            new EtcdConfig().setEndpoints("http://127.0.0.1:2379"), null, objectCodecAdapter);

    private static final NamespaceExplorer explorer = etcdNamespaceExplorerFactory.create();

    private static final String HEAD = "/ON_Test/";

    private static final String HEAD_OTHER = "/ON_Test/ON_Test_OTHER/";

    private static final String HASHING_PATH = "/ON_Test/ON_Hashing/";

    private static final String PLAYER_NODE_1_KEY = "/ON_Test/namespace/player/node1";

    private static final String PLAYER_NODE = "/ON_Test/namespace/player/";

    private static final String OTHER_PLAYER_NODE = "/ON_Test_OTHER/namespace/player/";

    private static final ObjectMineType<Player> MINE_TYPE = ObjectMineType.of(Player.class, JsonMimeType.JSON);

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        explorer.removeAll(HEAD).get();
        explorer.removeAll(HASHING_PATH).get();
        explorer.removeAll(HEAD_OTHER).get();
    }

    @Test
    void getTT() throws ExecutionException, InterruptedException {
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(5, "A");
        map.put(10, "B");
        map.put(20, "C");
        map.put(30, "D");
        map.tailMap(10).values().forEach(System.out::println);
        System.out.println("========================");
        map.headMap(10).values().forEach(System.out::println);
        System.out.println("========================");
        System.out.println(map.ceilingEntry(10));
        System.out.println(map.ceilingEntry(9));
        System.out.println("========================");
        System.out.println(map.lowerEntry(10));
        System.out.println(map.floorEntry(10));
        System.out.println(map.floorEntry(3));
        System.out.println(map.lastEntry());
    }

    @Test
    void get() throws ExecutionException, InterruptedException {
        NameNode<Player> playerNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(playerNode);
        Player player = new Player("Lucy", 100);
        NameNode<Player> savePlayerNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player).get();
        Player savePlayer = savePlayerNode.getValue();
        assertEquals(player, savePlayer);

        playerNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        Player getPlayer = playerNode.getValue();
        assertEquals(player, getPlayer);
    }

    @Test
    void findAll() throws ExecutionException, InterruptedException {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get();
            players.add(player);
        }
        List<NameNode<Player>> findList = explorer.findAll(PLAYER_NODE, MINE_TYPE).get();
        assertEquals(players.size(), findList.size());
        findList.forEach(node -> assertTrue(players.contains(node.getValue())));
    }

    //    @Test
    void hashingTest() throws ExecutionException, InterruptedException {
        var keyHash = HashAlgorithmHasher.<String>hasher();
        var nodeHash = HashAlgorithmHasher.<PartitionedNode<TestShadingNode>>hasher(p -> p.getNode().getKey());
        NodeHashing<TestShadingNode> ring1 = explorer.nodeHashing("/T2/Nodes", keyHash.getMax(), keyHash, nodeHash, TYPE,
                EtcdNodeHashingRingFactory.getDefault(), options -> options.setName("Harding1").setPartitionCount(3));
        NodeHashing<TestShadingNode> ring2 = explorer.nodeHashing("/T2/Nodes", keyHash.getMax(), keyHash, nodeHash, TYPE,
                EtcdNodeHashingRingFactory.getDefault(), options -> options.setName("Harding2").setPartitionCount(3));

        ring1.start().get();
        ring2.start().get();

        ring1.register(new TestShadingNode("Server1")).get();
        ring2.register(new TestShadingNode("Server2")).get();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    void lesseeTest() throws ExecutionException, InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Lessee lessee = explorer.lease("nodeWatcherWhenEmpty", 3000).get();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        NameNodesWatcher<Player> watcher = explorer.allNodeWatcher(PLAYER_NODE, MINE_TYPE);
        watcher.createEvent().add((w, node) -> System.out.println("create " + node.getValue()));
        watcher.loadEvent().add((w, nodes) -> System.out.println("create " + nodes.stream().map(NameNode::getValue).collect(Collectors.toList())));
        watcher.updateEvent().add((w, node) -> System.out.println("update " + node.getValue()));
        watcher.deleteEvent().add((w, node) -> System.out.println("delete " + node.getValue()));
        watcher.watcherEvent().add(new WatcherListener() {

            @Override
            public void onCompleted(NameNodesWatcher<?> watcher) {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(NameNodesWatcher<?> watcher, Throwable cause) {
                System.out.println("onError ===========================");
                cause.printStackTrace();
            }
        });
        watcher.watch().get();

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (lessee.isLive()) {
                    Player player = new Player(PLAYER_NODE + "PLA_1", ThreadLocalRandom.current().nextInt(0, 100));
                    explorer.save(player.getName(), MINE_TYPE, player, lessee).get();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 10, 10, TimeUnit.SECONDS);

        countDownLatch.await();
    }

    @Test
    void nodeWatcherWhenEmpty() throws ExecutionException, InterruptedException {
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            players.add(player);
        }

        AtomicReference<CountDownLatch> latchReference = new AtomicReference<>();

        Lessee lessee = explorer.lease("nodeWatcherWhenEmpty", 3000).get();
        NameNodesWatcher<Player> watcher = explorer.nodeWatcher(PLAYER_NODE_1_KEY, MINE_TYPE);
        List<Player> loadList = new ArrayList<>();
        List<Player> createList = new ArrayList<>();
        List<Player> updateList = new ArrayList<>();
        List<Player> deleteList = new ArrayList<>();
        List<List<Player>> checkList = Arrays.asList(loadList, createList, updateList, deleteList);

        watcher.createEvent().add((w, node) -> {
            createList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.loadEvent().add((w, nodes) -> loadList.addAll(nodes.stream().map(NameNode::getValue).collect(Collectors.toList())));
        watcher.updateEvent().add((w, node) -> {
            updateList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.deleteEvent().add((w, node) -> {
            deleteList.add(node.getValue());
            latchReference.get().countDown();
        });

        watcher.watch().get();
        check(checkList, 0, 0, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(0)).get();
        latchReference.get().await();
        check(checkList, 0, 1, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(1)).get();
        latchReference.get().await();
        check(checkList, 0, 1, 1, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(2)).get();
        latchReference.get().await();
        check(checkList, 0, 1, 2, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.remove(PLAYER_NODE_1_KEY).get();
        latchReference.get().await();
        check(checkList, 0, 1, 2, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(3), lessee).get();
        latchReference.get().await();
        check(checkList, 0, 2, 2, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(4), lessee).get();
        latchReference.get().await();
        check(checkList, 0, 2, 3, 1);

        latchReference.set(new CountDownLatch(1));
        lessee.shutdown();
        latchReference.get().await();
        check(checkList, 0, 2, 3, 2);
    }

    @Test
    void nodeWatcherWhenNotEmpty() throws ExecutionException, InterruptedException {
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            players.add(player);
        }

        AtomicReference<CountDownLatch> latchReference = new AtomicReference<>();

        NameNodesWatcher<Player> watcher = explorer.nodeWatcher(PLAYER_NODE_1_KEY, MINE_TYPE);
        List<Player> loadList = new ArrayList<>();
        List<Player> createList = new ArrayList<>();
        List<Player> updateList = new ArrayList<>();
        List<Player> deleteList = new ArrayList<>();
        List<List<Player>> checkList = Arrays.asList(loadList, createList, updateList, deleteList);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(0)).get();
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(1)).get();

        watcher.createEvent().add((w, node) -> {
            createList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.loadEvent().add((w, nodes) -> {
            loadList.addAll(nodes.stream().map(NameNode::getValue).collect(Collectors.toList()));
            latchReference.get().countDown();
        });
        watcher.updateEvent().add((w, node) -> {
            updateList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.deleteEvent().add((w, node) -> {
            deleteList.add(node.getValue());
            latchReference.get().countDown();
        });

        latchReference.set(new CountDownLatch(1));
        watcher.watch().get();
        latchReference.get().await();
        check(checkList, 1, 0, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(1)).get();
        latchReference.get().await();
        check(checkList, 1, 0, 1, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(2)).get();
        latchReference.get().await();
        check(checkList, 1, 0, 2, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(3)).get();
        latchReference.get().await();
        check(checkList, 1, 0, 3, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.remove(PLAYER_NODE_1_KEY).get();
        latchReference.get().await();
        check(checkList, 1, 0, 3, 1);

    }

    @Test
    void allNodeWatcher() throws ExecutionException, InterruptedException {

        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            players.add(player);
        }

        explorer.save(players.get(0).getName(), MINE_TYPE, players.get(0)).get();
        explorer.save(players.get(1).getName(), MINE_TYPE, players.get(1)).get();

        AtomicReference<CountDownLatch> latchReference = new AtomicReference<>();

        NameNodesWatcher<Player> watcher = explorer.allNodeWatcher(PLAYER_NODE, MINE_TYPE);
        List<Player> loadList = new ArrayList<>();
        List<Player> createList = new ArrayList<>();
        List<Player> updateList = new ArrayList<>();
        List<Player> deleteList = new ArrayList<>();
        List<List<Player>> checkList = Arrays.asList(loadList, createList, updateList, deleteList);

        watcher.createEvent().add((w, node) -> {
            createList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.loadEvent().add((w, nodes) -> {
            loadList.addAll(nodes.stream().map(NameNode::getValue).collect(Collectors.toList()));
            latchReference.get().countDown();
        });
        watcher.updateEvent().add((w, node) -> {
            updateList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.deleteEvent().add((w, node) -> {
            deleteList.add(node.getValue());
            latchReference.get().countDown();
        });

        latchReference.set(new CountDownLatch(1));
        watcher.watch().get();
        latchReference.get().await();
        check(checkList, 2, 0, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(2).getName(), MINE_TYPE, players.get(2)).get();
        latchReference.get().await();
        check(checkList, 2, 1, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(3).getName(), MINE_TYPE, players.get(3)).get();
        latchReference.get().await();
        check(checkList, 2, 2, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(4).getName(), MINE_TYPE, players.get(4)).get();
        latchReference.get().await();
        check(checkList, 2, 3, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.remove(players.get(4).getName()).get();
        latchReference.get().await();
        check(checkList, 2, 3, 0, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(0).getName(), MINE_TYPE, players.get(0)).get();
        latchReference.get().await();
        check(checkList, 2, 3, 1, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(1).getName(), MINE_TYPE, players.get(1)).get();
        latchReference.get().await();
        check(checkList, 2, 3, 2, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(1).getName(), MINE_TYPE, players.get(2)).get();
        latchReference.get().await();
        check(checkList, 2, 3, 3, 1);

    }

    @Test
    void allNodeWatcherWithRange() throws ExecutionException, InterruptedException {

        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i % 5 + "/" + i, 10 + i);
            players.add(player);
        }

        Player loadPlayer = new Player(PLAYER_NODE + "PLA_1/" + 1000, 1000);
        explorer.save(loadPlayer.getName(), MINE_TYPE, loadPlayer).get();
        loadPlayer = new Player(PLAYER_NODE + "PLA_2/" + 1000, 1000);
        explorer.save(loadPlayer.getName(), MINE_TYPE, loadPlayer).get();
        loadPlayer = new Player(PLAYER_NODE + "PLA_3/" + 1000, 1000);
        explorer.save(loadPlayer.getName(), MINE_TYPE, loadPlayer).get();

        AtomicReference<CountDownLatch> latchReference = new AtomicReference<>();

        NameNodesWatcher<Player> watcher = explorer.allNodeWatcher(PLAYER_NODE + "PLA_1", PLAYER_NODE + "PLA_3", MINE_TYPE);
        List<Player> loadList = new ArrayList<>();
        List<Player> createList = new ArrayList<>();
        List<Player> updateList = new ArrayList<>();
        List<Player> deleteList = new ArrayList<>();
        List<List<Player>> checkList = Arrays.asList(loadList, createList, updateList, deleteList);

        watcher.createEvent().add((w, node) -> {
            System.out.println("Create " + node);
            createList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.loadEvent().add((w, nodes) -> {
            loadList.addAll(nodes.stream().map(NameNode::getValue).collect(Collectors.toList()));
            latchReference.get().countDown();
        });
        watcher.updateEvent().add((w, node) -> {
            System.out.println("Update " + node);
            updateList.add(node.getValue());
            latchReference.get().countDown();
        });
        watcher.deleteEvent().add((w, node) -> {
            System.out.println("Delete " + node);
            deleteList.add(node.getValue());
            latchReference.get().countDown();
        });

        latchReference.set(new CountDownLatch(1));
        watcher.watch().get();
        latchReference.get().await();

        int createSize = 0;
        for (int i = 0; i < 10; i++) {
            Player player = players.get(i);
            int slot = i % 5;
            if (1 <= slot && slot < 3) {
                createSize++;
                latchReference.set(new CountDownLatch(1));
                explorer.save(player.getName(), MINE_TYPE, player).get();
                latchReference.get().await();
                check(checkList, 2, createSize, 0, 0);
            } else {
                explorer.save(player.getName(), MINE_TYPE, player).get();
                check(checkList, 2, createSize, 0, 0);
            }
        }

        int updateSize = 0;
        for (int i = 0; i < 10; i++) {
            Player player = players.get(i);
            int slot = i % 5;
            if (1 <= slot && slot < 3) {
                updateSize++;
                latchReference.set(new CountDownLatch(1));
                explorer.save(player.getName(), MINE_TYPE, player).get();
                latchReference.get().await();
                check(checkList, 2, createSize, updateSize, 0);
            } else {
                explorer.save(player.getName(), MINE_TYPE, player).get();
                check(checkList, 2, createSize, updateSize, 0);
            }
        }

        int deleteSize = 0;
        for (int i = 0; i < 10; i++) {
            Player player = players.get(i);
            int slot = i % 5;
            if (1 <= slot && slot < 3) {
                deleteSize++;
                latchReference.set(new CountDownLatch(1));
                explorer.remove(player.getName()).get();
                latchReference.get().await();
                check(checkList, 2, createSize, updateSize, deleteSize);
            } else {
                explorer.remove(player.getName()).get();
                check(checkList, 2, createSize, updateSize, deleteSize);
            }
        }
    }

    @Test
    void testPublishSubscribe() throws ExecutionException, InterruptedException {
        var nameHasher = HashAlgorithmHasher.hasher(Player::getName);
        var subscriber = explorer.hashingSubscriber(HASHING_PATH, nameHasher.getMax(), MINE_TYPE);
        var publisher = explorer.hashingPublisher(HASHING_PATH, nameHasher.getMax(), nameHasher, MINE_TYPE);
        long maxSlot = -1L >>> 32;
        long toSlot = maxSlot / 2;
        List<Player> playerList = new ArrayList<>();
        List<Player> watchedList = new ArrayList<>();

        List<Player> loadList = new ArrayList<>();
        List<Player> createList = new ArrayList<>();
        List<Player> updateList = new ArrayList<>();
        List<Player> deleteList = new ArrayList<>();
        List<List<Player>> checkList = Arrays.asList(loadList, createList, updateList, deleteList);

        AtomicReference<CountDownLatch> latchReference = new AtomicReference<>();
        subscriber.addListener(new WatchListener<>() {

            @Override
            public void onLoad(NameNodesWatcher<Player> watcher, List<NameNode<Player>> nameNodes) {
                loadList.addAll(nameNodes.stream().map(NameNode::getValue).collect(Collectors.toList()));
            }

            @Override
            public void onCreate(NameNodesWatcher<Player> watcher, NameNode<Player> node) {
                createList.add(node.getValue());
                System.out.println("OnCrate + " + node.getName() + " | size = " + createList.size() + " | total size = " + watchedList.size());
                if (watchedList.size() == createList.size()) {
                    latchReference.get().countDown();
                }
            }

            @Override
            public void onUpdate(NameNodesWatcher<Player> watcher, NameNode<Player> node) {
                updateList.add(node.getValue());
            }

            @Override
            public void onDelete(NameNodesWatcher<Player> watcher, NameNode<Player> node) {
                deleteList.add(node.getValue());
                if (watchedList.size() == deleteList.size()) {
                    latchReference.get().countDown();
                }
            }
        });

        subscriber.subscribe(List.of(new ShardingRange<>(0, toSlot, maxSlot)));
        var lessee = publisher.lease().get();

        System.out.println("toSlot == " + toSlot);
        latchReference.set(new CountDownLatch(1));
        for (int i = 0; i < 100; i++) {
            Player player = new Player("PLA_" + i, 10 + i);
            long hash = nameHasher.hash(player, 0, maxSlot);
            if (hash <= toSlot) {
                watchedList.add(player);
                System.out.println("watched = " + player.getName() + " = " + hash);
            }
            playerList.add(player);
        }
        for (Player player : playerList) {
            publisher.publish(player.getName(), player).get();
        }

        latchReference.get().await();
        check(checkList, 0, watchedList.size(), 0, 0);

        latchReference.set(new CountDownLatch(1));
        lessee.revoke().get();
        latchReference.get().await();
        check(checkList, 0, watchedList.size(), 0, watchedList.size());
    }

    @Test
    void testGetOrAdd() throws ExecutionException, InterruptedException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.getOrAdd(player.getName(), MINE_TYPE, player).get();
        assertEquals(player, nameNode.getValue());
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.getOrAdd(player.getName(), MINE_TYPE, newPlayer).get();
        assertEquals(player, nameNode.getValue());
    }

    @Test
    void testAdd() throws ExecutionException, InterruptedException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.add(player.getName(), MINE_TYPE, player)
                .whenComplete((n, c) -> {
                    System.out.println(n.getValue());
                })
                .get();
        assertEquals(player, nameNode.getValue());
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.add(player.getName(), MINE_TYPE, newPlayer).get();
        assertNull(nameNode);
    }

    @Test
    void testSave() throws ExecutionException, InterruptedException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.save(player.getName(), MINE_TYPE, player).get();
        assertEquals(player, nameNode.getValue());
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.save(player.getName(), MINE_TYPE, newPlayer).get();
        assertEquals(newPlayer, nameNode.getValue());
    }

    @Test
    void testUpdate() throws ExecutionException, InterruptedException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.update(player.getName(), MINE_TYPE, player).get();
        assertNull(nameNode);

        explorer.save(player.getName(), MINE_TYPE, player).get();
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.update(newPlayer.getName(), MINE_TYPE, newPlayer).get();
        assertEquals(newPlayer, nameNode.getValue());
    }

    @Test
    void updateIfValue() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);
        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1, player2).get();
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player2, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1, player2).get();
        assertEquals(player2, nameNode.getValue());
    }

    @Test
    void updateIfValueWithLessee() throws ExecutionException, InterruptedException {
        Lessee lessee = explorer.lease("updateIfValueWithLessee", 3000).get();
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();

        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1, player2, lessee).get();
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().join();

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(nameNode);
    }

    @Test
    void testUpdateIfVersion() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);
        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 2, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 1, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

    }

    @Test
    void testUpdateIfVersionWithLessee() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        Lessee lessee = explorer.lease("testUpdateIfVersionWithLessee", 3000).get();

        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 2, MINE_TYPE, player1, lessee).get();
        assertNull(nameNode);

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, MINE_TYPE, player1, lessee).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 1, MINE_TYPE, player2, lessee).get();
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().join();

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(nameNode);
    }

    @Test
    void testUpdateIfMinAndMaxVersion() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        // 0 x
        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1).get();
        assertNull(nameNode);

        // 0 -> 1
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        // 1 -> 2
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        // 2 -> 3
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        // 3 x
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player2).get();
        assertNull(nameNode);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();

        // 0 x
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player2).get();
        assertNull(nameNode);

        // 0 -> 1
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, -1, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        // 1 -> 2
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        // 2 x
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player2).get();
        assertNull(nameNode);

        // 2 -> 3
        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 3, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 2, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 1, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 0, RangeBorder.CLOSE, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 2, RangeBorder.CLOSE, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 2, RangeBorder.UNLIMITED, 2, RangeBorder.OPEN, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 1, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 1, RangeBorder.UNLIMITED, 3, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 1, RangeBorder.UNLIMITED, 3, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertNull(nameNode);

    }

    @Test
    void testUpdateIfMinAndMaxVersionWithLessee() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        Lessee lessee = explorer.lease("testUpdateIfMinAndMaxVersionWithLessee", 3000).get();

        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1, lessee)
                .get();
        assertEquals(player1, nameNode.getValue());

        lessee.shutdown().join();

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(nameNode);
    }

    @Test
    void testUpdateById() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        NameNode<Player> nameNode = explorer.updateById(PLAYER_NODE_1_KEY, 200, MINE_TYPE, player1)
                .get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();

        nameNode = explorer.updateById(PLAYER_NODE_1_KEY, nameNode.getId(), MINE_TYPE, player2)
                .get();
        assertEquals(player2, nameNode.getValue());
    }

    @Test
    void testUpdateByIdWithLessee() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        Lessee lessee = explorer.lease("testUpdateByIdWithLessee", 3000).get();

        NameNode<Player> nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();

        nameNode = explorer.updateById(PLAYER_NODE_1_KEY, nameNode.getId(), MINE_TYPE, player2, lessee).get();
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().join();

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(nameNode);

    }

    @Test
    void updateByIdIfValue() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        NameNode<Player> nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, 100, MINE_TYPE, player1, player2).get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player2, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player1, player2).get();
        assertEquals(player2, nameNode.getValue());
    }

    @Test
    void updateByIdValueWithLessee() throws ExecutionException, InterruptedException {
        Lessee lessee = explorer.lease("updateByIdValueWithLessee", 3000).get();
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        NameNode<Player> nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player2, player1, lessee).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player1, player2, lessee).get();
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().join();

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(nameNode);
    }

    @Test
    void testUpdateByIdIfVersion() throws ExecutionException, InterruptedException {

        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        NameNode<Player> nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, 100, 2, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, 100, 0, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 2, MINE_TYPE, player2).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

    }

    @Test
    void testUpdateByIdIfVersionWithLessee() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        Lessee lessee = explorer.lease("testUpdateByIdIfVersionWithLessee", 3000).get();

        NameNode<Player> nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, 100, 2, MINE_TYPE, player1, lessee).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, 100, 0, MINE_TYPE, player1, lessee).get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 2, MINE_TYPE, player2, lessee).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, MINE_TYPE, player2, lessee).get();
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().join();

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(nameNode);
    }

    @Test
    void testUpdateByIdIfMinAndMaxVersion() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        // 0 -> 1
        NameNode<Player> nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, 100, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1)
                .get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get();
        long id = nameNode.getId();

        // 1 -> 2
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        // 2 -> 3
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        // 3 -> 4
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        // 4 x
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player2).get();
        assertNull(nameNode);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get();
        id = nameNode.getId();

        // 1 x
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player2).get();
        assertNull(nameNode);

        // 1 -> 2
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        // 2 -> 3
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        // 3 x
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player2).get();
        assertNull(nameNode);

        // 3 -> 4
        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 4, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get();
        id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 3, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 2, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get();
        id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 1, RangeBorder.CLOSE, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.CLOSE, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 4, RangeBorder.OPEN, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 4, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get();
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get();
        id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED, MINE_TYPE, player2).get();
        assertEquals(player2, nameNode.getValue());

    }

    @Test
    void testUpdateByIdIfMinAndMaxVersionWithLessee() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 200);

        Lessee lessee = explorer.lease("testUpdateByIdIfMinAndMaxVersionWithLessee", 3000).get();

        NameNode<Player> nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get();
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1, lessee)
                .get();
        assertEquals(player1, nameNode.getValue());

        lessee.shutdown().join();

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        assertNull(nameNode);
    }

    @Test
    void remove() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        boolean result = explorer.remove(PLAYER_NODE_1_KEY).get();
        assertFalse(result);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();

        result = explorer.remove(PLAYER_NODE_1_KEY).get();
        assertTrue(result);
    }

    @Test
    void removeAll() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get();
        }
        for (int i = 0; i < 5; i++) {
            Player player = new Player(OTHER_PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get();
        }

        explorer.removeAll(PLAYER_NODE).get();

        List<NameNode<Player>> players = explorer.findAll(PLAYER_NODE, MINE_TYPE).get();
        assertTrue(players.isEmpty());

        List<NameNode<Player>> otherPlayers = explorer.findAll(OTHER_PLAYER_NODE, MINE_TYPE).get();
        assertEquals(5, otherPlayers.size());
    }

    @Test
    void removeAllAndGet() throws ExecutionException, InterruptedException {
        List<Player> players1 = new ArrayList<>();
        List<Player> players2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get();
            players1.add(player);
        }
        for (int i = 0; i < 5; i++) {
            Player player = new Player(OTHER_PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get();
            players2.add(player);
        }

        List<NameNode<Player>> removes = explorer.removeAllAndGet(PLAYER_NODE, MINE_TYPE).get();
        assertIterableEquals(players1, removes.stream().map(NameNode::getValue).collect(Collectors.toList()));

        List<NameNode<Player>> players = explorer.findAll(PLAYER_NODE, MINE_TYPE).get();
        assertTrue(players.isEmpty());

        List<NameNode<Player>> otherPlayers = explorer.findAll(OTHER_PLAYER_NODE, MINE_TYPE).get();
        assertIterableEquals(players2, otherPlayers.stream().map(NameNode::getValue).collect(Collectors.toList()));

    }

    @Test
    void testRemoveIfValue() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 200);

        NameNode<Player> nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveIfVersion() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 2, MINE_TYPE).get();
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 2, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 1, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveIfMinAndMaxVersion() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 4, RangeBorder.CLOSE, 5, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 3, RangeBorder.CLOSE, 4, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 3, RangeBorder.OPEN, 5, RangeBorder.OPEN, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 2, RangeBorder.OPEN, 4, RangeBorder.OPEN, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 3, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 2, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 4, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 3, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 4, RangeBorder.OPEN, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 2, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 3, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

    }

    @Test
    void removeById() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeById(PLAYER_NODE_1_KEY, 100, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        long id = nameNode.getId();

        nameNode = explorer.removeById(PLAYER_NODE_1_KEY, 100, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeById(PLAYER_NODE_1_KEY, id, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveByIdAndIfValue() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 100);

        NameNode<Player> nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, 100, MINE_TYPE, player1).get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        long id = nameNode.getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player2).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player1).get();
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveByIdAndIfVersion() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, 100, MINE_TYPE, player1).get();
        assertNull(nameNode);

        saveToVersion(PLAYER_NODE_1_KEY, player1, 2);
        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get();
        long id = nameNode.getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 1, MINE_TYPE).get();
        assertNull(nameNode);
        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 3, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 2, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveByIdAndIfMinAndMaxVersion() throws ExecutionException, InterruptedException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, 100, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get();
        long id = nameNode.getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        nameNode = saveToVersion(PLAYER_NODE_1_KEY, player1, 3);
        id = nameNode.getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 4, RangeBorder.CLOSE, 5, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 3, RangeBorder.CLOSE, 4, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 3, RangeBorder.OPEN, 5, RangeBorder.OPEN, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 2, RangeBorder.OPEN, 4, RangeBorder.OPEN, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 3, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 2, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 4, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 3, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 4, RangeBorder.OPEN, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 2, RangeBorder.CLOSE, MINE_TYPE).get();
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.CLOSE, MINE_TYPE).get();
        assertEquals(player1, nameNode.getValue());
    }

    private void check(List<List<Player>> checkList, int loadSize, int createSize, int updateSize, int deleteSize) {
        assertEquals(checkList.get(0).size(), loadSize, "Load");
        assertEquals(checkList.get(1).size(), createSize, "Create");
        assertEquals(checkList.get(2).size(), updateSize, "Update");
        assertEquals(checkList.get(3).size(), deleteSize, "Delete");
    }

    public static class Player {

        private String name;

        private int age;

        public Player() {
        }

        public Player(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Player setName(String name) {
            this.name = name;
            return this;
        }

        public int getAge() {
            return age;
        }

        public Player setAge(int age) {
            this.age = age;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Player)) {
                return false;
            }
            Player player = (Player)o;
            return getAge() == player.getAge() && Objects.equals(getName(), player.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getAge());
        }

        @Override
        public String toString() {
            return "Player{" + "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

    }

    private NameNode<Player> saveToVersion(String path, Player player, int version) throws ExecutionException, InterruptedException {
        NameNode<Player> node = null;
        for (int i = 0; i < version; i++) {
            node = explorer.save(path, MINE_TYPE, player).get();
        }
        return node;
    }

}