/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.codec.jackson.*;
import com.tny.game.common.type.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.listener.*;
import com.tny.game.namespace.sharding.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 由 src/test 的 EtcdNamespaceExplorerTest 迁入集成通道（stabilize-build-test-infra design D2 形态 b）：
 * etcd 经 Testcontainers 容器提供（镜像与 DockerChannelSentinelIT/CI service 同基准），全部外部交互有界
 * 超时并以类级 {@code @Timeout} 兜底；用例断言逻辑本体零改动。默认 {@code integrationTest} 不含 docker
 * 档时不执行；{@code -PincludeDocker} 开启且容器环境不可用时 skip，不再挂起单测通道。
 *
 * @author kgtny
 * @date 2022/7/2 02:15
 **/
@Tag("integration")
@Tag("docker")
@Testcontainers(disabledWithoutDocker = true)
@Timeout(value = 3, unit = TimeUnit.MINUTES, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
class EtcdNamespaceExplorerIT {

    private static final ObjectCodecFactory objectCodecFactory = new JacksonObjectCodecFactory();

    private static final ObjectCodecAdapter objectCodecAdapter = new ObjectCodecAdapter(Collections.singletonList(objectCodecFactory));

    public static final ReferenceType<PartitionSlot<TestShadingNode>> TYPE = new ReferenceType<PartitionSlot<TestShadingNode>>() {

    };

    // 镜像与 DockerChannelSentinelIT/CI service 同基准钉版（preflight §4，design Open Question 落定）
    private static final String ETCD_IMAGE = "gcr.io/etcd-development/etcd:v3.5.11";

    private static final int ETCD_CLIENT_PORT = 2379;

    @Container
    static final GenericContainer<?> ETCD = new GenericContainer<>(DockerImageName.parse(ETCD_IMAGE))
            // etcd 默认仅监听容器内 127.0.0.1，客户端口须显式绑 0.0.0.0 才可经端口映射被 jetcd 连接
            .withCommand("/usr/local/bin/etcd",
                    "--listen-client-urls", "http://0.0.0.0:" + ETCD_CLIENT_PORT,
                    "--advertise-client-urls", "http://127.0.0.1:" + ETCD_CLIENT_PORT)
            .withExposedPorts(ETCD_CLIENT_PORT);

    private static NamespaceExplorer explorer;

    @BeforeAll
    static void startExplorer() {
        String endpoint = "http://" + ETCD.getHost() + ":" + ETCD.getMappedPort(ETCD_CLIENT_PORT);
        EtcdNamespaceExplorerFactory factory = new EtcdNamespaceExplorerFactory(
                new EtcdConfig().setEndpoints(endpoint), null, objectCodecAdapter);
        explorer = factory.create();
    }

    private static final String HEAD = "/ON_Test/";

    private static final String HEAD_OTHER = "/ON_Test/ON_Test_OTHER/";

    private static final String HASHING_PATH = "/ON_Test/ON_Hashing/";

    private static final String PLAYER_NODE_1_KEY = "/ON_Test/namespace/player/node1";

    private static final String PLAYER_NODE = "/ON_Test/namespace/player/";

    private static final String OTHER_PLAYER_NODE = "/ON_Test_OTHER/namespace/player/";

    private static final ObjectMimeType<Player> MINE_TYPE = ObjectMimeType.of(Player.class, JsonMimeType.JSON);

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        explorer.removeAll(HEAD).get(30, TimeUnit.SECONDS);
        explorer.removeAll(HASHING_PATH).get(30, TimeUnit.SECONDS);
        explorer.removeAll(HEAD_OTHER).get(30, TimeUnit.SECONDS);
    }

    @Test
    void getTT() throws ExecutionException, InterruptedException, TimeoutException {
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
    void get() throws ExecutionException, InterruptedException, TimeoutException {
        NameNode<Player> playerNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(playerNode);
        Player player = new Player("Lucy", 100);
        NameNode<Player> savePlayerNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        Player savePlayer = savePlayerNode.getValue();
        assertEquals(player, savePlayer);

        playerNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        Player getPlayer = playerNode.getValue();
        assertEquals(player, getPlayer);
    }

    @Test
    void findAll() throws ExecutionException, InterruptedException, TimeoutException {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
            players.add(player);
        }
        List<NameNode<Player>> findList = explorer.findAll(PLAYER_NODE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(players.size(), findList.size());
        findList.forEach(node -> assertTrue(players.contains(node.getValue())));
    }

    void lesseeTest() throws ExecutionException, InterruptedException, TimeoutException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Lessee lessee = explorer.lease("nodeWatcherWhenEmpty", 3000).get(30, TimeUnit.SECONDS);
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
        watcher.watch().get(30, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (lessee.isLive()) {
                    Player player = new Player(PLAYER_NODE + "PLA_1", ThreadLocalRandom.current().nextInt(0, 100));
                    explorer.save(player.getName(), MINE_TYPE, player, lessee).get(30, TimeUnit.SECONDS);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 10, 10, TimeUnit.SECONDS);

        countDownLatch.await(30, TimeUnit.SECONDS);
    }

    @Test
    void nodeWatcherWhenEmpty() throws ExecutionException, InterruptedException, TimeoutException {
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            players.add(player);
        }

        AtomicReference<CountDownLatch> latchReference = new AtomicReference<>();

        Lessee lessee = explorer.lease("nodeWatcherWhenEmpty", 3000).get(30, TimeUnit.SECONDS);
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

        watcher.watch().get(30, TimeUnit.SECONDS);
        check(checkList, 0, 0, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(0)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 0, 1, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(1)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 0, 1, 1, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(2)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 0, 1, 2, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 0, 1, 2, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(3), lessee).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 0, 2, 2, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(4), lessee).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 0, 2, 3, 1);

        latchReference.set(new CountDownLatch(1));
        lessee.shutdown();
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 0, 2, 3, 2);
    }

    @Test
    void nodeWatcherWhenNotEmpty() throws ExecutionException, InterruptedException, TimeoutException {
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

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(0)).get(30, TimeUnit.SECONDS);
        // explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(1)).get(30, TimeUnit.SECONDS);

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
        watcher.watch().get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 1, 0, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(1)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 1, 0, 1, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(2)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 1, 0, 2, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, players.get(3)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 1, 0, 3, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 1, 0, 3, 1);

    }

    @Test
    void allNodeWatcher() throws ExecutionException, InterruptedException, TimeoutException {

        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            players.add(player);
        }

        explorer.save(players.get(0).getName(), MINE_TYPE, players.get(0)).get(30, TimeUnit.SECONDS);
        explorer.save(players.get(1).getName(), MINE_TYPE, players.get(1)).get(30, TimeUnit.SECONDS);

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
        watcher.watch().get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 0, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(2).getName(), MINE_TYPE, players.get(2)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 1, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(3).getName(), MINE_TYPE, players.get(3)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 2, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(4).getName(), MINE_TYPE, players.get(4)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 3, 0, 0);

        latchReference.set(new CountDownLatch(1));
        explorer.remove(players.get(4).getName()).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 3, 0, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(0).getName(), MINE_TYPE, players.get(0)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 3, 1, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(1).getName(), MINE_TYPE, players.get(1)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 3, 2, 1);

        latchReference.set(new CountDownLatch(1));
        explorer.save(players.get(1).getName(), MINE_TYPE, players.get(2)).get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, 2, 3, 3, 1);

    }

    @Test
    void allNodeWatcherWithRange() throws ExecutionException, InterruptedException, TimeoutException {

        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i % 5 + "/" + i, 10 + i);
            players.add(player);
        }

        Player loadPlayer = new Player(PLAYER_NODE + "PLA_1/" + 1000, 1000);
        explorer.save(loadPlayer.getName(), MINE_TYPE, loadPlayer).get(30, TimeUnit.SECONDS);
        loadPlayer = new Player(PLAYER_NODE + "PLA_2/" + 1000, 1000);
        explorer.save(loadPlayer.getName(), MINE_TYPE, loadPlayer).get(30, TimeUnit.SECONDS);
        loadPlayer = new Player(PLAYER_NODE + "PLA_3/" + 1000, 1000);
        explorer.save(loadPlayer.getName(), MINE_TYPE, loadPlayer).get(30, TimeUnit.SECONDS);

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
        watcher.watch().get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);

        int createSize = 0;
        for (int i = 0; i < 10; i++) {
            Player player = players.get(i);
            int slot = i % 5;
            if (1 <= slot && slot < 3) {
                createSize++;
                latchReference.set(new CountDownLatch(1));
                explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
                latchReference.get().await(30, TimeUnit.SECONDS);
                check(checkList, 2, createSize, 0, 0);
            } else {
                explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
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
                explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
                latchReference.get().await(30, TimeUnit.SECONDS);
                check(checkList, 2, createSize, updateSize, 0);
            } else {
                explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
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
                explorer.remove(player.getName()).get(30, TimeUnit.SECONDS);
                latchReference.get().await(30, TimeUnit.SECONDS);
                check(checkList, 2, createSize, updateSize, deleteSize);
            } else {
                explorer.remove(player.getName()).get(30, TimeUnit.SECONDS);
                check(checkList, 2, createSize, updateSize, deleteSize);
            }
        }
    }

    @Test
    void testPublishSubscribe() throws ExecutionException, InterruptedException, TimeoutException {
        long maxSlot = 64;
        var nameHasher = HashAlgorithmHasher.hasher(Player::getName, maxSlot);
        var subscriber = explorer.hashingSubscriber(HASHING_PATH, nameHasher.getMax(), MINE_TYPE);
        var publisher = explorer.hashingPublisher(HASHING_PATH, nameHasher.getMax(), nameHasher, MINE_TYPE);
        long toSlot = maxSlot / 2;
        List<Player> playerList = new ArrayList<>();
        Map<Long, Player> prePlayerMap = new HashMap<>();
        List<Player> prePlayerList = new ArrayList<>();
        List<Player> opPlayerList = new ArrayList<>();

        List<Player> watchedList = new ArrayList<>();

        List<Player> loadList = new ArrayList<>();
        List<Player> createList = new ArrayList<>();
        List<Player> updateList = new ArrayList<>();
        List<Player> deleteList = new ArrayList<>();
        List<List<Player>> checkList = Arrays.asList(loadList, createList, updateList, deleteList);

        for (int i = 0; i < 100; i++) {
            Player player = new Player("PLA_" + i, 10 + i);
            long hash = nameHasher.hash(player, 0, maxSlot);
            if (hash <= toSlot) {
                if (prePlayerMap.putIfAbsent(hash, player) == null) {
                    prePlayerList.add(player);
                    publisher.publish(player.getName(), player).get(30, TimeUnit.SECONDS);
                } else {
                    opPlayerList.add(player);
                }
                watchedList.add(player);
                System.out.println("watched = " + player.getName() + " = " + hash);
            }
            playerList.add(player);
        }

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
                if (opPlayerList.size() == createList.size()) {
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
        var lessee = publisher.lease().get(30, TimeUnit.SECONDS);

        System.out.println("toSlot == " + toSlot);
        latchReference.set(new CountDownLatch(1));

        for (Player player : playerList) {
            publisher.publish(player.getName(), player).get(30, TimeUnit.SECONDS);
        }

        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, prePlayerList, opPlayerList, prePlayerList, List.of());

        latchReference.set(new CountDownLatch(1));
        lessee.revoke().get(30, TimeUnit.SECONDS);
        latchReference.get().await(30, TimeUnit.SECONDS);
        check(checkList, prePlayerList, opPlayerList, prePlayerList, watchedList);
    }

    @Test
    void testGetOrAdd() throws ExecutionException, InterruptedException, TimeoutException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.getOrAdd(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        assertEquals(player, nameNode.getValue());
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.getOrAdd(player.getName(), MINE_TYPE, newPlayer).get(30, TimeUnit.SECONDS);
        assertEquals(player, nameNode.getValue());
    }

    @Test
    void testAdd() throws ExecutionException, InterruptedException, TimeoutException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.add(player.getName(), MINE_TYPE, player)
                .whenComplete((n, c) -> {
                    System.out.println(n.getValue());
                })
                .get(30, TimeUnit.SECONDS);
        assertEquals(player, nameNode.getValue());
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.add(player.getName(), MINE_TYPE, newPlayer).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
    }

    @Test
    void testSave() throws ExecutionException, InterruptedException, TimeoutException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        assertEquals(player, nameNode.getValue());
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.save(player.getName(), MINE_TYPE, newPlayer).get(30, TimeUnit.SECONDS);
        assertEquals(newPlayer, nameNode.getValue());
    }

    @Test
    void testUpdate() throws ExecutionException, InterruptedException, TimeoutException {
        Player player = new Player(PLAYER_NODE_1_KEY, 100);
        NameNode<Player> nameNode = explorer.update(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        Player newPlayer = new Player(PLAYER_NODE_1_KEY, 200);
        nameNode = explorer.update(newPlayer.getName(), MINE_TYPE, newPlayer).get(30, TimeUnit.SECONDS);
        assertEquals(newPlayer, nameNode.getValue());
    }

    @Test
    void updateIfValue() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);
        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player2, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());
    }

    @Test
    void updateIfValueWithLessee() throws ExecutionException, InterruptedException, TimeoutException {
        Lessee lessee = explorer.lease("updateIfValueWithLessee", 3000).get(30, TimeUnit.SECONDS);
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);

        NameNode<Player> nameNode = explorer.updateIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1, player2, lessee).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().get(30, TimeUnit.SECONDS);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
    }

    @Test
    void testUpdateIfVersion() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);
        NameNode<Player> nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 2, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 1, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

    }

    @Test
    void testUpdateIfVersionWithLessee() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        Lessee lessee = explorer.lease("testUpdateIfVersionWithLessee", 3000).get(30, TimeUnit.SECONDS);

        NameNode<Player> nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 2, MINE_TYPE, player1, lessee).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, MINE_TYPE, player1, lessee).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 1, MINE_TYPE, player2, lessee).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().get(30, TimeUnit.SECONDS);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
    }

    @Test
    void testUpdateIfMinAndMaxVersion() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        // 0 x
        NameNode<Player> nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        // 0 -> 1
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        // 1 -> 2
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        // 2 -> 3
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        // 3 x
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);

        // 0 x
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        // 0 -> 1
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, -1, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        // 1 -> 2
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        // 2 x
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 2, RangeBorder.OPEN, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        // 2 -> 3
        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 3, RangeBorder.UNLIMITED, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 2, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 0, RangeBorder.CLOSE, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 2, RangeBorder.CLOSE, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 2, RangeBorder.UNLIMITED, 2, RangeBorder.OPEN, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.UNLIMITED, 3, RangeBorder.UNLIMITED, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);

        nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.UNLIMITED, 3, RangeBorder.UNLIMITED, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

    }

    @Test
    void testUpdateIfMinAndMaxVersionWithLessee() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        Lessee lessee = explorer.lease("testUpdateIfMinAndMaxVersionWithLessee", 3000).get(30, TimeUnit.SECONDS);

        NameNode<Player> nameNode = explorer.updateIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1,
                        lessee)
                .get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        lessee.shutdown().get(30, TimeUnit.SECONDS);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
    }

    @Test
    void testUpdateById() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        NameNode<Player> nameNode = explorer.updateById(PLAYER_NODE_1_KEY, 200, MINE_TYPE, player1)
                .get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);

        nameNode = explorer.updateById(PLAYER_NODE_1_KEY, nameNode.getId(), MINE_TYPE, player2)
                .get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());
    }

    @Test
    void testUpdateByIdWithLessee() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        Lessee lessee = explorer.lease("testUpdateByIdWithLessee", 3000).get(30, TimeUnit.SECONDS);

        NameNode<Player> nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);

        nameNode = explorer.updateById(PLAYER_NODE_1_KEY, nameNode.getId(), MINE_TYPE, player2, lessee).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().get(30, TimeUnit.SECONDS);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

    }

    @Test
    void updateByIdIfValue() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        NameNode<Player> nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, 100, MINE_TYPE, player1, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player2, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player1, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());
    }

    @Test
    void updateByIdValueWithLessee() throws ExecutionException, InterruptedException, TimeoutException {
        Lessee lessee = explorer.lease("updateByIdValueWithLessee", 3000).get(30, TimeUnit.SECONDS);
        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        NameNode<Player> nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player2, player1, lessee).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player1, player2, lessee).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().get(30, TimeUnit.SECONDS);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
    }

    @Test
    void testUpdateByIdIfVersion() throws ExecutionException, InterruptedException, TimeoutException {

        Player player1 = new Player(PLAYER_NODE + "PL_2", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_1", 102);

        NameNode<Player> nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, 100, 2, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, 100, 0, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 2, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

    }

    @Test
    void testUpdateByIdIfVersionWithLessee() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        Lessee lessee = explorer.lease("testUpdateByIdIfVersionWithLessee", 3000).get(30, TimeUnit.SECONDS);

        NameNode<Player> nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, 100, 2, MINE_TYPE, player1, lessee).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, 100, 0, MINE_TYPE, player1, lessee).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 2, MINE_TYPE, player2, lessee).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, MINE_TYPE, player2, lessee).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        lessee.shutdown().get(30, TimeUnit.SECONDS);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
    }

    @Test
    void testUpdateByIdIfMinAndMaxVersion() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 102);

        // 0 -> 1
        NameNode<Player> nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, 100, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE,
                        player1)
                .get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        // 1 -> 2
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        // 2 -> 3
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        // 3 -> 4
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        // 4 x
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        id = nameNode.getId();

        // 1 x
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        // 1 -> 2
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        // 2 -> 3
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        // 3 x
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        // 3 -> 4
        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.OPEN, 4, RangeBorder.UNLIMITED, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        id = nameNode.getId();

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 0, RangeBorder.UNLIMITED, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 3, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 2, RangeBorder.OPEN, 0, RangeBorder.UNLIMITED, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        id = nameNode.getId();

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 1, RangeBorder.CLOSE, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.CLOSE, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 4, RangeBorder.OPEN, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 4, RangeBorder.UNLIMITED, MINE_TYPE, player2)
                .get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

        explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        id = nameNode.getId();

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED, MINE_TYPE, player2)
                .get(30, TimeUnit.SECONDS);
        assertEquals(player2, nameNode.getValue());

    }

    @Test
    void testUpdateByIdIfMinAndMaxVersionWithLessee() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 200);

        Lessee lessee = explorer.lease("testUpdateByIdIfMinAndMaxVersionWithLessee", 3000).get(30, TimeUnit.SECONDS);

        NameNode<Player> nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.updateByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE, player1, lessee)
                .get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        lessee.shutdown().get(30, TimeUnit.SECONDS);

        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
    }

    @Test
    void remove() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        boolean result = explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        assertFalse(result);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);

        result = explorer.remove(PLAYER_NODE_1_KEY).get(30, TimeUnit.SECONDS);
        assertTrue(result);
    }

    @Test
    void removeAll() throws ExecutionException, InterruptedException, TimeoutException {
        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        }
        for (int i = 0; i < 5; i++) {
            Player player = new Player(OTHER_PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        }

        explorer.removeAll(PLAYER_NODE).get(30, TimeUnit.SECONDS);

        List<NameNode<Player>> players = explorer.findAll(PLAYER_NODE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertTrue(players.isEmpty());

        List<NameNode<Player>> otherPlayers = explorer.findAll(OTHER_PLAYER_NODE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(5, otherPlayers.size());
    }

    @Test
    void removeAllAndGet() throws ExecutionException, InterruptedException, TimeoutException {
        List<Player> players1 = new ArrayList<>();
        List<Player> players2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Player player = new Player(PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
            players1.add(player);
        }
        for (int i = 0; i < 5; i++) {
            Player player = new Player(OTHER_PLAYER_NODE + "PLA_" + i, 10 + i);
            explorer.save(player.getName(), MINE_TYPE, player).get(30, TimeUnit.SECONDS);
            players2.add(player);
        }

        List<NameNode<Player>> removes = explorer.removeAllAndGet(PLAYER_NODE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertIterableEquals(players1, removes.stream().map(NameNode::getValue).collect(Collectors.toList()));

        List<NameNode<Player>> players = explorer.findAll(PLAYER_NODE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertTrue(players.isEmpty());

        List<NameNode<Player>> otherPlayers = explorer.findAll(OTHER_PLAYER_NODE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertIterableEquals(players2, otherPlayers.stream().map(NameNode::getValue).collect(Collectors.toList()));

    }

    @Test
    void testRemoveIfValue() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 200);

        NameNode<Player> nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIf(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveIfVersion() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 2, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 2, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 1, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveIfMinAndMaxVersion() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 4, RangeBorder.CLOSE, 5, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 3, RangeBorder.CLOSE, 4, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 3, RangeBorder.OPEN, 5, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 2, RangeBorder.OPEN, 4, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 3, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 2, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 4, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 3, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 4, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        saveToVersion(PLAYER_NODE_1_KEY, player1, 3);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 2, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeIfVersion(PLAYER_NODE_1_KEY, 0, RangeBorder.UNLIMITED, 3, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

    }

    @Test
    void removeById() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeById(PLAYER_NODE_1_KEY, 100, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.removeById(PLAYER_NODE_1_KEY, 100, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeById(PLAYER_NODE_1_KEY, id, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveByIdAndIfValue() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);
        Player player2 = new Player(PLAYER_NODE + "PL_2", 100);

        NameNode<Player> nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, 100, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player2).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, id, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveByIdAndIfVersion() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeByIdAndIf(PLAYER_NODE_1_KEY, 100, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        saveToVersion(PLAYER_NODE_1_KEY, player1, 2);
        nameNode = explorer.get(PLAYER_NODE_1_KEY, MINE_TYPE).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);
        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 3, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 2, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());
    }

    @Test
    void testRemoveByIdAndIfMinAndMaxVersion() throws ExecutionException, InterruptedException, TimeoutException {
        Player player1 = new Player(PLAYER_NODE + "PL_1", 100);

        NameNode<Player> nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, 100, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE)
                .get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.save(PLAYER_NODE_1_KEY, MINE_TYPE, player1).get(30, TimeUnit.SECONDS);
        long id = nameNode.getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 1, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        nameNode = saveToVersion(PLAYER_NODE_1_KEY, player1, 3);
        id = nameNode.getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 4, RangeBorder.CLOSE, 5, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 2, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 3, RangeBorder.CLOSE, 4, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 1, RangeBorder.CLOSE, 3, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 3, RangeBorder.OPEN, 5, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.OPEN, 3, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 2, RangeBorder.OPEN, 4, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 3, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 2, RangeBorder.OPEN, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 4, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 3, RangeBorder.CLOSE, 5, RangeBorder.UNLIMITED, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 4, RangeBorder.OPEN, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());

        id = saveToVersion(PLAYER_NODE_1_KEY, player1, 3).getId();

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 2, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertNull(nameNode);

        nameNode = explorer.removeByIdAndIfVersion(PLAYER_NODE_1_KEY, id, 0, RangeBorder.UNLIMITED, 3, RangeBorder.CLOSE, MINE_TYPE).get(30, TimeUnit.SECONDS);
        assertEquals(player1, nameNode.getValue());
    }

    //    @Test
    void hashingTest() throws ExecutionException, InterruptedException, TimeoutException {
        var keyHash = HashAlgorithmHasher.<String>hasher();
        var nodeHash = HashAlgorithmHasher.<PartitionSlot<TestShadingNode>>hasher(p -> p.getNode().getKey());
        NodeHashing<TestShadingNode> ring1 = explorer.nodeHashing("/T2/Nodes", keyHash.getMax(), keyHash, nodeHash, TYPE,
                EtcdNodeHashingRingFactory.getDefault(), options -> options.setName("Harding1").setPartitionCount(3));
        NodeHashing<TestShadingNode> ring2 = explorer.nodeHashing("/T2/Nodes", keyHash.getMax(), keyHash, nodeHash, TYPE,
                EtcdNodeHashingRingFactory.getDefault(), options -> options.setName("Harding2").setPartitionCount(3));

        ring1.start().get(30, TimeUnit.SECONDS);
        ring2.start().get(30, TimeUnit.SECONDS);

        ring1.register(new TestShadingNode("Server1")).get(30, TimeUnit.SECONDS);
        ring2.register(new TestShadingNode("Server2")).get(30, TimeUnit.SECONDS);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(30, TimeUnit.SECONDS);
    }

    private void check(List<List<Player>> checkList, int loadSize, int createSize, int updateSize, int deleteSize) {
        assertEquals(checkList.get(0).size(), loadSize, "Load");
        assertEquals(checkList.get(1).size(), createSize, "Create");
        assertEquals(checkList.get(2).size(), updateSize, "Update");
        assertEquals(checkList.get(3).size(), deleteSize, "Delete");
    }

    private void check(List<List<Player>> checkList, Collection<Player> loadList, Collection<Player> createList, Collection<Player> updateList,
            Collection<Player> deleteList) {
        assertCollection(checkList.get(0), loadList, "Load");
        assertCollection(checkList.get(1), createList, "Create");
        assertCollection(checkList.get(2), updateList, "Update");
        assertCollection(checkList.get(3), deleteList, "Delete");
    }

    private void assertCollection(Collection<Player> expect, Collection<Player> check, String message) {
        assertEquals(expect.size(), check.size(), message);
        assertTrue(check.containsAll(expect), message);
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
            Player player = (Player) o;
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

    private NameNode<Player> saveToVersion(String path, Player player, int version) throws ExecutionException, InterruptedException, TimeoutException {
        NameNode<Player> node = null;
        for (int i = 0; i < version; i++) {
            node = explorer.save(path, MINE_TYPE, player).get(30, TimeUnit.SECONDS);
        }
        return node;
    }

}