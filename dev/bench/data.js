window.BENCHMARK_DATA = {
  "lastUpdate": 1791387825698,
  "repoUrl": "https://github.com/Tunaiyi/tny-framework",
  "entries": {
    "Benchmark": [
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "9d5280004e45f9cadef6a1f330d383a956662fb3",
          "message": "docs(bench): Store 步骤归因终案——action 无自建分支路径，预建 gh-pages 空分支\n\n第五轮实证 rebase 修复生效（bot 回写 00e4c9da 成功）；本轮日志证实 auto-push\n首步 fetch gh-pages:gh-pages 缺分支即 128。空树孤儿分支经 commit-tree 预建，\n不触碰共享 worktree。本 push 为首个 token+rebase+预建三条件齐备的验证轮。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T00:37:41+08:00",
          "tree_id": "ac59223522d513a96f5a2e93830a409f5b75de25",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/9d5280004e45f9cadef6a1f330d383a956662fb3"
        },
        "date": 1790873744542,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2699847695.673565,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1612261801.7863839,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 126119965.2949523,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 228167018.39354014,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 30158299.98360014,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 227741196.93387285,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 164576826.74186575,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3613968.923474671,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1302873.2573376754,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 898755.7874176825,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 138341.1832939007,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1051747.5180869377,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 141454.89856096468,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1139963.7076490722,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 269313.09033650614,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1303620.5626355116,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 277194.0215615665,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1411645.5740703433,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 446293.9764475472,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1772497.5652085617,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 483769.2656123421,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1377408.391248729,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 216283.5909526608,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1226852.1024860376,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 233113.09934743578,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1739074.9330697204,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 324901.0974637831,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1608118.8920825652,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 375551.7771936181,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 800512.2115907217,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 316916.54761244066,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 791424.6865990015,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 318207.9170055374,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 128697533.82651964,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 110773441.27499075,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18837385.201052215,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17588325.15710857,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "aa5fb7f1de46e105c4e81c8fb3bc84a3c5e2b6f4",
          "message": "docs(bench): 第六轮终验闭环——gh-pages 37 序列全起线，split-bench-suites 20/20\n\naction 日志实证 fetch 命中预建分支、empty default 起步、dev/bench/{data.js,index.html}\n生成并推送 7f51dc2；数据分布与本地对账一致，1258 行疑点消解。6.2 销账，\n全部 20 任务完成，备 /opsx:verify 终版与归档。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T01:00:34+08:00",
          "tree_id": "fe921037648f39a4fa974d1fcb886d8e735533b9",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/aa5fb7f1de46e105c4e81c8fb3bc84a3c5e2b6f4"
        },
        "date": 1790875144887,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2109672085.3648343,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1274443425.0537868,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91938527.72087547,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199901803.21655124,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24367144.57901012,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199807464.3255954,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163931621.76722068,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2326581.5917361737,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 889958.1327625818,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 734288.7988021446,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 118967.83163675814,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 813607.2036844434,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 122110.3895089343,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 844099.8765162459,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 230127.92686118055,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1012576.9246086975,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 240331.88346583932,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 927758.2424625307,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 363664.15068667725,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1094838.791901626,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 371401.6308894252,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1017966.1641857976,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 182229.36920919042,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 940169.0006840667,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 194028.36075202102,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1091414.745236739,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 257902.40595915512,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1032071.8281985484,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 273197.4129780027,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 778158.5729030266,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 237764.3395971839,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 777189.1498110543,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 234541.3920534178,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 110458522.33995399,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 92974083.63589363,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15839838.911185723,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14990501.083546702,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "2ebdcbb91c07ccb33def296076342ae8a834a6ad",
          "message": "docs(stabilize): 案卷对表——IT job 级连绿 8/10，CI 端 5 轮一致达成；\nrun#21/#22 主犯登记（bench 回写步复发→9d528000 根治、unit 第三红另案）\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T01:02:32+08:00",
          "tree_id": "7ed36f77f1efa4c3d37c458c4deff432171862f7",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/2ebdcbb91c07ccb33def296076342ae8a834a6ad"
        },
        "date": 1790875258063,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2103379734.4868534,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1275378959.4322598,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91712609.53258285,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199846474.6843324,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24711548.48104774,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199820250.06259006,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163057808.4741859,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2321746.901135856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 978179.7425134203,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 723285.0709730879,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 118451.17216056495,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 809964.3954735522,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 120031.25469641201,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 859552.8458591644,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 219057.8728444067,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1009815.7993051361,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 238855.75528157555,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 933148.128249947,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 359103.2342336053,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1094321.4456329437,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 377026.58241366677,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1029926.988413509,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 181591.41006433585,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 951600.9171613634,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 193747.6015573502,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1088788.8784860559,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 255586.11660027917,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1024718.4923107444,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 270039.38254310616,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 775430.2912364483,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 236463.60902865016,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 774811.6581817938,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 235249.6176844032,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 111913895.82134604,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 92878060.43198198,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15761231.72676889,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14774807.923132857,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "b05ec659d8b870a1e06ce150daddaba105e6e92f",
          "message": "openspec(split-bench-suites): 归档并同步 benchmark-harness 主账本\n\n净影响：主 spec 新增「基准二分分类契约」（两族归置+目录双向对账+完备性判红，4 Scenario）；\n修订「CI 双通道职责边界」为执行通道三触发（push/定时/手动，4 Scenario，原两名保留）；\n修订「结果产物结构化可对比」为常规族含探针口径；Purpose 相应扩写。\n变更全生命周期证据随目录归档（六轮 CI、三轮修复归因案卷 verification.md）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T01:05:13+08:00",
          "tree_id": "44abe2a677e8f32873543a432e41951439fbdb4c",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/b05ec659d8b870a1e06ce150daddaba105e6e92f"
        },
        "date": 1790875416177,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2111444083.7317948,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1283734851.5926666,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 71090971.77939865,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199835960.1050162,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 25421152.292770628,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199853337.58310813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163848578.62322852,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2273523.051388026,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 900404.4718283241,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 711062.2171726234,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 113919.69115861742,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 816941.0529120436,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 118079.57282177915,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 846398.5671634827,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 228984.72386038955,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 995213.5012480017,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 237691.55031791926,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 911901.3680022669,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 358676.6931665399,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1082989.816871363,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 370687.15117426135,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1027980.5242406813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 180925.72717826458,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 948895.228163662,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 189816.346133439,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1142363.6491384294,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 253565.48101442665,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1039981.3857298592,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 294515.47645135,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 779770.9427860297,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 235370.03310137644,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 772919.9149681348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 234914.9432639846,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 106460873.96521348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 90585402.37971734,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15655629.481653143,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14795298.861345494,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "d6c737e07936e644743804471ea2a808f40d7eaa",
          "message": "docs(stabilize): 组 5 收口——IT 零复现 10/10 达成（run#18→#27，电路零新卷）\n\n- tasks.md：5.4/5.5/5.6 划勾（全变更 18/18，validate --strict 过）；\n- verification.md §4：组 5 验收纪要（含无 docker 显性 skip 探针补实组 3\n  时代的\"未实测项\"同源语义）；\n- it-diagnosis.md §6：run#26/#27 入账，10/10 钉死；run#28 为在途加分。\nmemory docker-it-rerun-discipline 划账\"根治已收口\"（本地纪律本体保留，\n新红取证入口=ci-it-diag 案卷）；unit 间歇红（#16/#19/#22）边界外另案在册。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T01:18:36+08:00",
          "tree_id": "f487e75a4ea55d2a0588dbc1042e5689bf2c1691",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/d6c737e07936e644743804471ea2a808f40d7eaa"
        },
        "date": 1790876203490,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2693852838.7537017,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1618686276.0974813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 126055506.44585991,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 227923120.53843594,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 30056528.828550804,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 227930111.62596148,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 164102466.51333934,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3694625.8540326683,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1157542.791969265,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 909532.5507190522,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 138734.76700634832,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1041703.1443515869,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 137518.69099466648,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1126132.79641328,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 259175.71548440136,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1315485.1309538167,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 277603.5905182691,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1435126.0009459557,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 448589.30428890267,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1771592.8482732482,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 447045.1136126743,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1356662.3669817103,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 217687.91500635905,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1241933.693863762,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 221496.27968768115,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1798734.7493687018,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 326300.83842437284,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1603181.6444324923,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 354042.5174695775,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 790989.6783064036,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 317452.43092720804,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 787979.7918376512,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 321312.64381369494,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 129183281.93889746,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 110391072.67882785,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18842074.284511082,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17386122.2815572,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "7681f5cd3ca9a235d033ae4924372b36d9857d21",
          "message": "docs(stabilize): verify SUGGESTION 1 回填——design.md 末补组 5 决策承载指引\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T01:22:46+08:00",
          "tree_id": "8baf6773ba6c7099a5fbd7a9b28577f3e8958ab8",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/7681f5cd3ca9a235d033ae4924372b36d9857d21"
        },
        "date": 1790876481217,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2118871575.8748138,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1282197747.102888,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92381597.98666891,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199690388.93943962,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22767853.64179372,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199868432.36371857,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163750441.30580872,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2345873.023593578,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1083517.2867134279,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 722171.8922337345,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 114650.32929926037,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 815105.5077102813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 117597.69644363807,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 859899.3755279677,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 226248.15996581627,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 999011.8008417638,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 230871.63842601835,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 938792.7403754585,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 357983.0572874232,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1100291.9500238597,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 372743.4277474799,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1024019.2385441599,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 173662.7900324902,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 936776.8657368079,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 189470.78926989288,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1110140.8486767584,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 253870.33273660284,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1036651.8429335144,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 285102.39411931817,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 769734.924287463,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 233592.23592497237,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 769751.3576470653,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 236529.10779620335,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108281038.18629985,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95989563.51196516,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15713398.22153452,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14830284.229130995,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "c65b007be68d960cd0421d8a93acfc1036c3fcfa",
          "message": "openspec: 归档 stabilize-build-test-infra（组 1-4+组 5 全 18/18，verify 无 critical/warning）\n\n组 5 收口链：电路首卷定罪支②→两改根治（ae80463d）→CI IT 11 轮零复现\n（门槛 10/10 钉死，#29 加分在途）→本地 5/5+显性 skip 探针→memory 划账\n→verification §4 纪要→design 组 5 承载指引。unit 间歇红（#16/#19/#22）\n边界外另案在册（it-diagnosis §3/§6）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T01:24:13+08:00",
          "tree_id": "63bad4bf25e68c4c3f4031640410de4c434fae73",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/c65b007be68d960cd0421d8a93acfc1036c3fcfa"
        },
        "date": 1790876587927,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2231528569.3690715,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1502314342.2190297,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 51600060.544634774,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 52685359.346358486,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 18990110.985155605,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 52669569.5585431,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 44724326.16296103,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1718505.4201639015,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 834511.659371968,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 682143.7398750808,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 117870.63615650989,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 762854.7835357167,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 120314.09451158038,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 805772.0683713423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 244964.7809948002,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 930073.7225104559,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 255339.76191701577,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 909034.3808477151,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 361344.71211332205,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1065699.8590870553,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 383877.7064361918,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 970384.0612652054,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 216001.31964672272,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 876945.4319482936,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 185357.43229761388,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1096809.945970264,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 297254.90015618154,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 985094.6023473687,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 239356.30231396985,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 635052.1873533858,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 258248.4086616206,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 634079.3651750316,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 260006.96015750416,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 44457119.74871464,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 39495458.35259555,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 8874054.603927236,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 8455047.877611097,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "2a1ea43d206c0665227a17a2162f08d11cf57a7c",
          "message": "openspec(promote-netbench): 终裁记录——4.2/4.3/4.4/6.3 凭 dispatch 双绿+gh-pages+results 回提交勾销，20/20 闭合\n\n证据锚：run#4/#5（workflow_dispatch 全绿）、gh-pages 7394ce2a（dev/bench/data.js 曲线\n4 点、action 结果提交两枚）、5.7.x c65b007b（results/anchor+routine 双件 bot 回提交，\nroutine 37 用例 5 族）、alert-threshold 150%/comment-on-alert/fail-on-alert=false。\n形态演化注记：bench-nightly 已由并行会话演化为 bench-routine（push/schedule/dispatch\n三触发），spec 双通道与人裁决语义不变。design Open Question 定案：阈值 150% 起步，\n≥3 个 schedule 基线点后再收紧。今晚 02:37 CST schedule 为第二次自然验证，非欠账。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T01:35:02+08:00",
          "tree_id": "e23e40f6e295aeaa1501b9414a51a0fe49a11eda",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/2a1ea43d206c0665227a17a2162f08d11cf57a7c"
        },
        "date": 1790877216492,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2085872355.3325965,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1249994352.989348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 97574137.92512922,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 176846748.02021176,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23075954.012646522,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 176867318.539025,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 145846537.30424464,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2788623.6367420377,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 992373.2764062153,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 693552.563339215,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 107219.64851867626,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 805571.8656957005,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 109226.79190534758,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 872858.0924142161,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 210598.51532296633,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1023541.8360480089,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 211101.22145106122,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 926115.7785660133,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 337361.2020527354,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1169880.3928034431,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 352013.8931675813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1044952.7422732785,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 170043.3331734018,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 954803.8801181575,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 174523.47356624037,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1162452.1794722169,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 242854.6335997417,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1038720.5490577755,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 257939.47581163794,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 739756.8585839452,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 211902.33220095895,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 754529.9210862959,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 208602.06343848756,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 102020870.57845856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 85143377.2379398,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14434890.422852475,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14001756.772989884,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "670c3e950346b3cac453b27c1be79f278363c3be",
          "message": "openspec(fix-ci-unit-flakes): 补提提案工件全量 + 勾销组 1（电路已上线 e942a878）\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:19:58+08:00",
          "tree_id": "6f819f0486de3f2038d3b4fa1fbcf80e34755c3d",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/670c3e950346b3cac453b27c1be79f278363c3be"
        },
        "date": 1790879896737,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2693045831.0257425,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1611128240.842318,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 124325104.08495787,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 228038232.23448572,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 29963575.45556722,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 227796217.55037284,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 162485633.405022,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3579017.9483515634,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1150780.2054528364,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 910749.0390110547,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 137406.22391405882,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1024327.9196922553,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 140968.51719428124,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1114080.7655494951,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 267311.82547214103,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1337871.2505379,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 277001.66187564656,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1379579.4893592931,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 450959.4276250218,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1724432.235218469,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 481674.07720685715,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1343277.2122213345,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 216842.30623997882,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1187354.6298318738,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 223436.45449827836,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1766409.9185851458,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 324102.37746294827,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1604607.6512680189,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 351912.14256736543,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 790613.5574926197,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 314361.5990804096,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 795945.0401539903,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 317808.11483506544,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 127394140.4575319,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 111139819.57333657,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18674244.050763234,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17602196.869466983,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "e942a878de77974a6e76783eeb37e9a1c5872bc3",
          "message": "ci(unit): 诊断电路上线——红时四件案卷投递 ci-unit-diag（fix-ci-unit-flakes 1.1/1.2）\n\n与 ci-it-diag 同型三件套：continue-on-error 捕获 + if:failure 投递孤儿分支 +\n末尾 exit 1 保硬门禁。unit 特有采集面按 D1：etcd services 容器 docker ps/logs\n（头号嫌疑人自证）、gradle 输出尾 200KB、失败用例 XML、java/uname 环境自述；\n全部采集步 || true 降级——电路永不因取证而更红。\n本地 :tny-game-namnspace-etcd:test --rerun 12s 绿（OrbStack etcd:2379 在位）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:19:23+08:00",
          "tree_id": "c193cb8a074d236c309676a7162e0265f4bb5a3f",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/e942a878de77974a6e76783eeb37e9a1c5872bc3"
        },
        "date": 1790879914674,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2110895292.596932,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1275276646.2838483,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92763028.83816476,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199661241.4307595,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23786851.442262165,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199634743.62714672,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163157752.11685294,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2300242.248896266,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 978953.6723621156,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 708109.0981671213,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 119196.152033835,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 803623.5038038119,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 121590.33095821575,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 866194.7698102619,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 225396.34496489452,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 971012.1526914729,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 239018.1339953471,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 934687.8810399056,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 336867.7884321591,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1102319.411284772,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 377813.22540207405,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1015796.6422348872,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 180486.06402105227,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 954151.368622842,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 193056.751971227,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1117108.146613667,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 236844.3265673917,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1007936.6029922308,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 282172.624991137,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 779399.3964592128,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 235313.90524545856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 781922.2228111721,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 235093.94298224314,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 105857491.45295605,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 90410651.08142032,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15726350.071742866,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14811417.704594215,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "9979a681ff63b6ab637c2bb7513de7ccf9daee45",
          "message": "ci(it): consolidate-ci-it-lanes 立项工件+实施落账（e2e job 删除已上线）\n\n规划三件（proposal/design/tasks，skip_specs 纯设施）。实施注记：删段\n实际载体=兄弟会话提交 e942a878（共享树并发窗口吞并，功能面正确、\n远端 jobs=unit/integration/bench-compile/bench-routine），记账偏差与\n回退口径修正登记于 tasks 1.2。观察窗起点=run#33（e942a878 触发）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:21:03+08:00",
          "tree_id": "6e213976115c69eb92daad42c3cd2a45b34a6b0c",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/9979a681ff63b6ab637c2bb7513de7ccf9daee45"
        },
        "date": 1790879922838,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 3936551202.3542747,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2650823799.4281073,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 78844638.26852535,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 97531700.61051328,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 37435254.79637413,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 97849549.36573239,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 84261892.27325144,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2460176.2717990205,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1157805.2455090438,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 978114.3657608865,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 77085.58449513774,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1060210.4057147224,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 76027.01361384956,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1136101.2336483852,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 101044.01598442024,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1368524.4946840855,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 100753.21305117445,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1317944.955470563,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 111455.5040457036,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1554906.2055390072,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 113855.45246350423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1358515.6713451655,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 297235.08103178634,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1284279.0625119633,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 317546.34325447056,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1562317.7405720216,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 413007.4871800394,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1415318.2624537232,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 469266.50638083264,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 986944.468488705,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 417019.4148098409,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 986238.7977494085,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 418979.1849548544,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 84381042.02142365,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 73912361.68868887,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 12780113.777068797,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12398013.403635522,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "f3bebdcbd8a93bc5e06c2e9aa2ffffdf7bb1db8d",
          "message": "test(ci): PROBE fix-ci-unit-flakes 2.1——电路自证空弹（下轮 revert）\n\n独立新类必然失败，制造一次计划内 unit 红以自证 ci-unit-diag 投递路径。\n勿修此断言；探针案卷验证后由 2.3 回滚提交。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:20:26+08:00",
          "tree_id": "d1aa670f3eacf7c85bc90ebfacc945d89b71987b",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/f3bebdcbd8a93bc5e06c2e9aa2ffffdf7bb1db8d"
        },
        "date": 1790879929219,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2109503217.901954,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1285516789.3582053,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92235515.51499481,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199743245.86970216,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24020407.481049642,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 200066109.35136396,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163914373.01826778,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2356369.5333618433,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 892250.3476589002,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 705315.4855937524,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 119700.6355278743,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 806490.9382088949,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 122187.52419855767,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 873667.6188365526,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 230520.18441811213,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1012255.3039689843,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 240903.12755570136,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 924732.8322867615,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 362803.00347037014,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1058920.9309617602,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 385061.378690914,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1042259.3317201097,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 184480.48439031563,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 955170.3867888479,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 200756.84717633988,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1132874.1296575032,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 255289.04102795073,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1017226.0669613384,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 303316.19235713675,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 777641.8500023917,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 236851.66342327028,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 781784.9660589376,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 237111.89493014547,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 105932487.77141915,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 93642935.99147809,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15697701.398094296,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14859838.490000924,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "edd52dbd96667ba4f03427c409be6ce5ac90e5e9",
          "message": "Revert \"test(ci): PROBE fix-ci-unit-flakes 2.1——电路自证空弹（下轮 revert）\"\n\nThis reverts commit f3bebdcbd8a93bc5e06c2e9aa2ffffdf7bb1db8d.",
          "timestamp": "2026-10-02T02:32:18+08:00",
          "tree_id": "a50c99dde7bf34b607b0d3cbeada59306c2343c8",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/edd52dbd96667ba4f03427c409be6ce5ac90e5e9"
        },
        "date": 1790880632444,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2695079992.7858706,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1625057509.6122727,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 114922392.68851402,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 227710262.70426518,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 29749560.911458623,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 228201936.20326725,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 148707308.7207299,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3570505.250939173,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1290315.0369824243,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 909696.380794888,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 138768.67648185167,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1054356.4569792112,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 141081.3116220891,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1151794.051414253,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 259798.84716355632,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1326694.0096342238,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 280267.49984647083,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1413352.5550126317,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 447907.52808332926,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1773232.6639927127,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 477838.54803194385,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1380694.1189181623,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 220044.23875590507,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1235862.728718988,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 222108.60559003396,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1704101.936601696,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 325412.73914474854,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1592767.3956039394,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 377147.88413879205,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 782012.4704880023,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 319917.10367020796,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 798717.7977435265,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 320056.7453715988,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 126854460.77476545,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 111380532.60016823,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18636186.952227443,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17527013.252832692,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "b72d610353f235787bf754c01f4f904f4d6356e8",
          "message": "docs(fix-ci-unit-flakes): 探针闭环——案卷固化 probe-evidence/ + 登记簿 diagnosis.md 建档（组 2/3.1 完成）\n\nrun#35 计划内红完整走完 红→投递→3 分钟取卷→回滚 闭环；docker 可见性/等号过滤定位/\n五件非空全部实证。'绿时不投递'负半程待 run#37。自然红定罪窗口开启。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:33:20+08:00",
          "tree_id": "55958b3ee6d7f7a5b7e6ce0e05a73a931488b2fa",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/b72d610353f235787bf754c01f4f904f4d6356e8"
        },
        "date": 1790880711659,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2101938092.825789,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1275594592.0940955,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92288797.9789754,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199508582.134254,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23711765.085332636,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199857478.2704553,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 162996260.22544947,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2361433.44883946,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 891183.2697438856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 707586.7982669236,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 117717.09884837014,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 810318.5940524659,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 119486.28485538627,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 870208.3581669327,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 229194.38227449055,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1022709.7692414935,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 231291.6817017265,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 928014.978723097,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 356736.66497139435,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1096547.8234392244,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 374557.47661792306,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 995829.9709444402,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 181147.1557708717,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 942989.6801209248,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 195184.97077773372,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1085982.9305227844,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 251943.12241477598,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1034288.3371127611,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 297234.29477264674,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 779953.4380601107,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 232630.76689382448,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 759612.4252148318,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 231684.8649179338,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 111417620.91436684,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 92818727.46953169,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15664047.806344952,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14768496.258978983,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "a1f04f268b1e971efe9275c6c3036bc721094963",
          "message": "docs(ci): 观察窗核账——consolidate IT 4/5（#33-#36 job 级全绿，卷零新投）；\nstabilize 案卷 §6 续账（unit 登记簿延伸 #35/#36 红源=CiCircuitProbeTest\n探针，ci-unit-diag 首投自证，判读归 fix-ci-unit-flakes 线）\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:44:26+08:00",
          "tree_id": "4173fd94ab23806e9fb5747316b97f77f736b365",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/a1f04f268b1e971efe9275c6c3036bc721094963"
        },
        "date": 1790881342240,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2522253875.945079,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1715546506.8405366,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 59046831.17203325,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 60354982.63167703,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 19658971.777969323,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 60504704.07015222,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 59097002.36988269,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1823870.2490473674,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 920701.8738355677,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 748336.2559110251,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 130447.69607687772,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 838408.3813180693,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 133768.79533403047,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 891238.9422700458,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 267901.464185509,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1021161.8980408158,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 279658.83366316755,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1003102.470380258,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 387975.2804240036,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1151833.3122559066,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 382196.8862567259,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1038003.3562840084,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 237726.40226946102,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 973444.1388158795,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 206572.51757547402,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1189587.553112226,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 322991.33727668633,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1058629.0879726019,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 302680.89270442526,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 703609.88908446,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 290479.655548667,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 701388.7647507151,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 289235.46107004635,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 50404801.57023043,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 44418949.68583548,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 10248818.035351532,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 9569542.006921694,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "b26abd895097801f514eaa36deca31a85dc352b7",
          "message": "docs(fix-ci-unit-flakes): 案#1 定罪登记——run#38 自然红跨线移交（支 c），组 3 闭合\n\ntny-game-actor TypeStageTest 墙钟 200ms 窗口断言在 runner 排队挤压下翻转；etcd 假设\n未证伪未证实（本轮与 etcd 无关）。案卷固化 natural-red-1/，处方三选交 actor 线，\n跨线不动刀。tasks 3.2 以'支 c 定罪=移交即闭合'达成；10 轮计数由 actor 修复后重累计。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:48:36+08:00",
          "tree_id": "bdea9429fc1a5cbcbddb45359c9512865210316d",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/b26abd895097801f514eaa36deca31a85dc352b7"
        },
        "date": 1790881619419,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2086559187.7485523,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1259272666.4907496,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 97536656.41573042,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 176834750.52010196,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22732772.86491877,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 176775451.6821274,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 145163273.86878258,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2736572.1625494994,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 886012.2591173794,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 700971.2709725813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 107870.92823349,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 787540.8612864012,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 109814.06854538333,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 859992.162204012,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 205276.3848441565,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 999502.2284405353,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 220875.33905292707,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 944708.3746122807,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 332921.4568368662,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1137982.1225957465,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 359166.96047065913,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1060693.9087880698,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 171013.14066136949,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 951129.5256601948,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 172842.14868253795,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1159279.801660544,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 245451.24648195674,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1041519.7281432243,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 253087.61666504698,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 750564.6723201053,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 214967.76316657718,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 750392.8989769904,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 212310.6493873527,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 98869361.70891199,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 81418887.3843151,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14468615.586328458,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13852768.917372663,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "4109ee396569ed7edabe2e1ef8bed8fd83ca9b3a",
          "message": "ci(it): consolidate 观察窗满格——2.2 划勾（#33→#39 七连自然 push IT job 级全绿，\nci-it-diag 零新投递；门槛 5 轮超额）\n\n余 2.1 dispatch 首验待手动触发；3.x 收口随其后。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T03:04:01+08:00",
          "tree_id": "0c02a3a086fcaa9fd8e790f108560695d84af498",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/4109ee396569ed7edabe2e1ef8bed8fd83ca9b3a"
        },
        "date": 1790882553531,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2114661872.1265442,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1285044285.0634408,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92306868.00994137,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199797173.63455594,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23914711.55456109,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199971060.90885073,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163638659.71648675,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2224695.127753181,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 991698.5984586565,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 709364.3955552363,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 118463.57934900979,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 810011.960097606,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 120609.90593644266,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 872107.4192578333,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 229891.70679377235,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1018004.6976487361,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 227469.11735730394,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 943442.8403012801,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 360002.7860347243,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1125073.4309800162,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 378863.60513832804,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1015365.8250113508,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 182437.15955419943,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 958136.5043768005,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 190628.43247243162,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1128068.6433839202,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 253460.79393588123,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1032913.7341757696,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 268146.8809107853,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 792636.0437771196,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 235579.86734550033,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 786747.7917060701,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 235178.97798937972,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108348764.06921816,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95978454.4538239,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15692227.63866509,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14792062.427753795,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "14457e8a9166ca14af62233b6f23957670e4b2f8",
          "message": "docs(fix-ci-unit-flakes): IT 10/10 终裁 + 负半程'绿不投递'实证——5.1/5.2 收口\n\nrun#39 全 job 绿且双案卷分支零新投（ci-it-diag 恒 889e0942 / ci-unit-diag 恒 49694f16），\nstabilize 组 5 根治自 #18→#39 满 10 轮定案；handoff §3 阶段二（保持硬门禁/不降级/不重试）\n由临时状态转为正式结论（归档体仅追加终裁注记，历史正文不动）。\n本案余 4.1a/4.2/6.x 悬于两事件：etcd 假设等自然红、actor 处方待落地。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T03:05:08+08:00",
          "tree_id": "4790a2c0c39b8845be7dfe17a16066306cf1a861",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/14457e8a9166ca14af62233b6f23957670e4b2f8"
        },
        "date": 1790882606130,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2108860523.0111015,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1280707974.9385276,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91952371.9400717,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199871731.0298768,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22843909.16769377,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199891044.95735937,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163952374.65324348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2268664.683445952,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 894648.8835278722,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 717107.6533065161,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 117936.83444908245,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 818278.9581740035,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 120548.06268603487,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 868121.1685814842,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 229118.2970752328,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 990072.3774335373,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 234667.4837722713,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 928412.3636218163,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 363902.19222831784,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1095553.3764126506,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 383697.66715552023,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 997833.0436891532,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 182770.91579456005,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 928322.7871966796,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 190073.88170839316,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1093139.0619050928,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 254827.21467718008,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1004559.6278500136,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 302663.0515036379,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 772401.4899769307,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 235908.038217342,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 783107.8933267521,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 237381.44892657394,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108559993.66850749,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95780278.96855447,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15653999.82175165,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12674506.967191372,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "b26abd895097801f514eaa36deca31a85dc352b7",
          "message": "docs(fix-ci-unit-flakes): 案#1 定罪登记——run#38 自然红跨线移交（支 c），组 3 闭合\n\ntny-game-actor TypeStageTest 墙钟 200ms 窗口断言在 runner 排队挤压下翻转；etcd 假设\n未证伪未证实（本轮与 etcd 无关）。案卷固化 natural-red-1/，处方三选交 actor 线，\n跨线不动刀。tasks 3.2 以'支 c 定罪=移交即闭合'达成；10 轮计数由 actor 修复后重累计。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T02:48:36+08:00",
          "tree_id": "bdea9429fc1a5cbcbddb45359c9512865210316d",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/b26abd895097801f514eaa36deca31a85dc352b7"
        },
        "date": 1790884939219,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2115062287.1385295,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1270540777.199812,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92189358.12909149,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199729443.90910095,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24613238.96449025,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199790687.5567377,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163411050.26451412,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2226871.159291989,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 974507.5512032835,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 709940.5414245857,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 115764.3462201267,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 810015.470450816,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 121406.38221435621,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 870416.7974102512,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 228296.07172957208,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1002326.0608546166,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 236704.24863324533,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 944906.6085205913,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 360002.3102182769,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1143198.439854466,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 326479.1428930194,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1037791.2121669434,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 184273.298547462,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 960148.1738776875,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 187470.16017733663,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1104896.771809094,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 256605.2004645709,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1035064.5044260971,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 295575.4611350825,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 776019.5878890478,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 236307.80718318513,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 782258.3686049853,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 235943.676986264,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108160901.15721849,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 94886354.30529405,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15702680.175369084,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14853927.227912745,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      }
    ],
    "Routine quick (5.7.x)": [
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "9f2a40c861804162d23bf1025fc86cc829f64ad5",
          "message": "ci(bench): 执行通道内容门禁与速览/完整双规模（refine-bench-routine-triggering）\n\n非行为性合入（文档、案卷、流水线脚本）不再触发约二十分钟的基准计时与结果回写；\n框架改动合入以速览规模（十三组合：全管线、发送队列、RPC 配对、设施探针）计时，\n约七至九分钟出数据点，加密装配矩阵由夜间与手动的完整规模兜底。\n实现：build.gradle 新增 benchQuickRun 清单与 benchScope 枚举例并区分产物命名；\nbuild.yml 新增 bench-scope-gate 判定作业（push 比对改动文件、定时与手动恒完整、\nPR 不放行、前序提交不可达安全侧放行），bench-routine 经 needs 取门禁结果决定\n起跑与规模，曲线分组键改为分支乘规模三元组合消除 main 与 5.7.x 基线混线。\nREADME 同步两规模口径与产物指纹约定。基线参数、评审通道、人裁决语义零改动。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T03:29:17+08:00",
          "tree_id": "126ca43a017c03ed778796fe35ea2fd42b4fca52",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/9f2a40c861804162d23bf1025fc86cc829f64ad5"
        },
        "date": 1790883405058,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2110272247.794252,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1272899334.794435,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92111450.53937924,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199712165.8360718,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 25303524.689479884,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199566260.34607562,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163248705.08329833,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2259079.2878727424,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 898782.1709553627,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 105668301.36210269,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 92403209.91565648,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15331188.948456388,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14793870.453647876,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "c7b772c056fa0b94571369caa884007df524a694",
          "message": "fix(bench): 导出产物日期钉 UTC 口径（速览实况暴露时区错位）\n\nrun 369146306 实证 runner 时区为 UTC，本地与 CI 对同一日期指纹口径相差一日。\n钉定 UTC 后与回写提交时间戳同基准；附 refine-bench-routine-triggering\n实况记录（速览 7 分 29 秒端到端、案卷批 skipped、曲线分组隔离生效）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T03:40:00+08:00",
          "tree_id": "a4cd7114df2e9a4bc6902d3a785e1c76928e69d0",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/c7b772c056fa0b94571369caa884007df524a694"
        },
        "date": 1790884028142,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2108333670.1719348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1273489368.5278857,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92236421.01407441,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199646526.92847103,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 25659853.997061104,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199692022.3513444,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 162523511.22596964,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2258799.2581968927,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 979796.0090733593,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108496178.09527035,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95348456.32207178,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15796874.980824878,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14828281.859699745,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "b2390be085fe3476b48014dba3e0cd9e19f45ead",
          "message": "feat(pages): 站点首页与 GitHub Pages 自动开通部署\n\n项目此前从未开通 GitHub Pages，基准趋势所在的 gh-pages 分支有数据但没有服务，\n站点裸地址返回 404。本提交补齐整条链路：docs/site/index.html 是站点首页源码\n（项目介绍、55 个模块按层导航、基准与文档入口）；pages.yml 工作流把首页部署到\ngh-pages 分支根目录，只改写 index.html 与 .nojekyll，保留 github-action-benchmark\n推送的 dev/bench 基准数据，并以 GITHUB_TOKEN 幂等开通 Pages（source 选择 gh-pages\n分支根目录）。README 顶部补充站点与基准页地址。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T11:33:29+08:00",
          "tree_id": "50f5173a2f3bfd1ce9d08193fde8e532581e26bf",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/b2390be085fe3476b48014dba3e0cd9e19f45ead"
        },
        "date": 1790912443292,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2700144419.016275,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1613767005.8140588,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 126704537.56475821,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 228149505.1706171,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 29685265.51917684,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 227992234.73337618,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 164352018.65921623,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3752964.1402723067,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1169979.9152184469,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 125732802.76378044,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 112458148.3168092,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18768095.530419774,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17697306.495891243,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "5781a6d1035f81482c7880ee81ca8e0073c7d39e",
          "message": "chore(license): 为 Lua 脚本补齐 Apache-2.0 许可证头\n\nobsolete 消息队列测试模块的 9 个 Redis Lua 脚本此前没有许可证头注释，按\nApache-2.0 逐文件声明惯例补齐。构建脚本（build.gradle、settings.gradle、\ngradle/*.gradle、gradlew.bat）、集成测试日志配置与 proto 定义经评审不纳入\n逐文件头注释范围：Apache-2.0 的授权完整性由 LICENSE 文件、README 与发布\nPOM 元数据保证，构建基础设施按开源社区惯例不参与逐文件声明。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T12:25:36+08:00",
          "tree_id": "6aff184dc4fe0b0d61a85f8c7a84a57f6d62fd24",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/5781a6d1035f81482c7880ee81ca8e0073c7d39e"
        },
        "date": 1790915565715,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 4287746313.0748115,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2866698729.6579466,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 84287269.76750985,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 103446758.3217221,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 39957602.68719104,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 103899841.85105762,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 88618261.72422856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2840664.0447291015,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1395851.509844688,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 85264772.38758744,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 78722639.34439555,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 13922179.192454582,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12906242.094417717,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "cee51b8f1d07ab22e00354895fc0ae9ecbe59aa2",
          "message": "docs(openspec): 新增 gradle-build-style 构建脚本形态能力规格并挂载执行钩子\n\n- 主账本新增能力 gradle-build-style（八条需求十六个场景），把构建脚本\"读起来像配置说明书而不是程序\"落成可验收契约：声明式配置与程序性行为分离、任务惰性形态、版本坐标单一事实源、Groovy 语言纪律、区块顺序与版面、注释来由与 dryRun 预览、扫读测试与长度界线、存量违例触碰即改。\n- 变更工件随归档移入 openspec/changes/archive/2026-10-02-add-gradle-build-style/，含 proposal、design、tasks、实施记录与 27 处存量违例基线。\n- CLAUDE.md 的 Gradle 构建脚本一节收缩为指针，规则正文唯一权威出处为主账本规格。\n- openspec/config.yaml 挂载两处钩子（context 构建脚本形态条目、apply guidance 触碰即改条目），并把过时的组号上下文修正为 com.tnydev.game 单一事实源表述。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T16:12:29+08:00",
          "tree_id": "1ea77044e5dfe824f7b84e52b433df8384d14f54",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/cee51b8f1d07ab22e00354895fc0ae9ecbe59aa2"
        },
        "date": 1790929204796,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2689356798.8624587,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1623921275.551323,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 126249951.37207651,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 228006705.32691866,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 34512083.659637675,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 228171619.55723733,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 162442629.54409063,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3674038.9327053195,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1284253.2482536559,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 125876424.6262862,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 112226890.25787506,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18565171.38296824,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17457975.10927493,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "3c8e723d595d086aefa034040e2e1f93bd6caa66",
          "message": "fix(build): nmcp 依赖组镜像钉选陷阱修复\n\ntencent 镜像存有 xmlutil rc 版 pom 但缺 jar，Gradle 元数据解析后即钉选该仓找\n构件、其余仓库不再尝试，致 root :nmcpTasks 与 bom 暂存任务解析失败。nmcp 与\nxmlutil 两个组经 exclusiveContent 独占路由直连 Central，其余依赖维持镜像优先；\n根工程补自身仓库声明（root 聚合插件解析 :nmcpTasks 此前无仓可查）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-02T16:16:11+08:00",
          "tree_id": "91fb91eef50e125e9e57117e6b27b5f47a946e5e",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/3c8e723d595d086aefa034040e2e1f93bd6caa66"
        },
        "date": 1790929452385,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.bench.net.devtest.SmokeBenchmark.noop",
            "value": 2108996171.966576,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1284658792.6726623,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92413648.64978687,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 200089119.8225311,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24853979.30174031,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199965836.9213621,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 162818435.1968165,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2284805.4768354213,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 977964.9096875645,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 106192860.62094025,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 93429619.53416124,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15696413.337240573,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.bench.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14874399.633262014,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "d7464cfd8a9c5e4db0c05d34b80f9439716394fc",
          "message": "refactor(build): 基准模块与包根改名 tny-benchmark（rename-bench-to-benchmark）\n\n模块目录与包根原子换批（12 源文件、四条族正则、settings include、CI 七行九处、\nbench-suite 归属注、文档五面与 security 文档路径指针）；历史取证面原样随行；\n键换代断点与换算规则记 README 基线纪律与本变更 verification-notes。\nCI 实况观察随推送补录。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-03T11:04:05+08:00",
          "tree_id": "e62a20a87859f40e1b3fcb4e485abed8945ef187",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/d7464cfd8a9c5e4db0c05d34b80f9439716394fc"
        },
        "date": 1790997125810,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2116030329.0485368,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1269968667.3884625,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92418787.51243842,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199680762.9405335,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23838604.763507597,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 200071953.53336665,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 164021371.54158404,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2266022.787719573,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 898167.649445316,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 109281143.88109753,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 93503685.58982994,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15677914.12494291,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14822574.805725181,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "138d98fb9c240b259add56791f511a9c0b3ffc59",
          "message": "refactor(build): 基准插件改名 tny.benchmark-module 并随动扩展名 benchmarkSuite\n\nrename-bench-suite-to-benchmark-module 落地：插件文件与 id 移名、扩展名两层\n同动（类名 BenchSuite 与属性、任务名按案卷保留）、模块八处读取与指称随迁、\njmhListVerify 报错文案前缀换名。枚举 31 键与清单逐行零差异、对账与速览键集\n一致、全量构建一次全绿。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-03T12:43:19+08:00",
          "tree_id": "5b5f2da234618ee045f1304322870cf742e441ee",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/138d98fb9c240b259add56791f511a9c0b3ffc59"
        },
        "date": 1791003094662,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2113560626.5134003,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1283558953.0646882,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92361536.01472755,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199751289.83664387,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24803299.78186836,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 200004277.66387707,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163333631.81614155,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2264805.829673264,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 887211.2304617934,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 109603728.05001545,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 94938139.52292441,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15687072.666681986,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14763931.558597555,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "5656f97158accdcdfe1201989189c56b43209ac8",
          "message": "chore(build): 插件文件改名正式入库（module-modes→module-setting，此前裸 mv 滞留暂存）+ 两册归档卷复验补记\n\nHEAD 此前仍持旧文件名（六本批量提交时以裸 mv 移名未被捕获），磁盘与构建一直\n以新名运行；本次暂存态入库并复验 projects/clean build 全绿。归档的插件 id 册\n与类册 verification-notes 追加本轮全仓复扫与探针复验记录。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-03T13:40:22+08:00",
          "tree_id": "5857a48533cd0219c78f17e852972799c12b5285",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/5656f97158accdcdfe1201989189c56b43209ac8"
        },
        "date": 1791006460160,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 4258955719.6074166,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2832676232.797005,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 83671513.44170175,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 103928920.38571908,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 39787403.08570562,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 103122241.65563981,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 88208022.46915153,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2852748.8550374643,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1198685.9669989615,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 88320567.04901469,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 78052173.03930174,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 13858750.249922985,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13051185.217672616,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "e0428ebe38da9c307922935211a5cb8a7ad7fa9b",
          "message": "chore(build): 实验残留两处清理——publish 意外空行还原、误建空壳基线目录 git rm\n\n空壳目录（rename-module-modes-plugin 真案卷已在 archive/）系本会话复测批 mkdir\n误建并被 -A 误入库，经用户授权删除；publish 的一枚空行系探针实验暂存裹入，还原。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-03T13:41:14+08:00",
          "tree_id": "f5e308faf0cd91b6a8ecb775028d951ed22b0e70",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/e0428ebe38da9c307922935211a5cb8a7ad7fa9b"
        },
        "date": 1791006575216,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2113449888.209418,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1279436291.4066246,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92678165.39785385,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 199465527.0622167,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23831012.576945208,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 199561144.23774007,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 163457935.04171878,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2349164.722977275,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 993883.0151265856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 109611682.51290056,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 93266182.93098679,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15692891.236606354,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14777028.41480647,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "2eb90365e23fdfbf408872ac9079813af8851c25",
          "message": "docs(openspec): expose-git-info-extension 与 gitinfo-self-init-and-getters 两册案卷归档——各 5/5 验收记录在册，verify 无 CRITICAL，skip_specs 净账本零影响\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T05:37:25+08:00",
          "tree_id": "cfa6a56448d2ff509a28eba6323c2079eebaa04c",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/2eb90365e23fdfbf408872ac9079813af8851c25"
        },
        "date": 1791063922872,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 4274492370.0630097,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2882689189.0423017,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 83351082.63769302,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 103481219.63943148,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 33992147.06931003,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 103116631.84286386,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 63199462.91756491,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2904625.3488022853,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1244846.439612348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 87141373.99943687,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 77829048.53192098,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 13513133.167699859,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12875689.761831377,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "c7a4ce419831114059a43da6396ca3bab4fd4ab9",
          "message": "docs(openspec): 五册归档卷宗入库＋零差异抓样口径固化＋codec-bridge-assembly 能力入主账本\n\n1. 归档五册（实施面见上一提交与并行收编提交，卷宗含全部零差异证据件）：\n   rename-bench-suite-class、trim-it-module-dependencies-block（前身 move-it-fixture-loop\n   案卷前提经现场证伪后更名重写，catalog containerStack bundle 收编形态）、\n   move-bench-suite-selection-into-plugin、split-publish-consistency-closure、\n   fix-protoex-bridge-stray-deps。\n2. 口径治理批：openspec/config.yaml 新增\"零差异验收基线抓样口径\"唯一权威条目（context）\n   与 apply guidance 呼应句——转存型样件先运行生成任务再转存、逐行判据前后同 daemon 且\n   UTF-8 钉定；活跃案卷任务文本按该条补齐；审计册落\"归档后勘误\"段（IT 模块\"each 循环\"\n   记载经 git 全史检索证伪，超行主因重定为纯声明依赖累积）；三册归档件各追加\n   \"归档后补跑记\"兑现 clean build 全绿条款（词表守卫修复入库后全仓恢复绿）。\n3. 主账本新增能力规格 codec-bridge-assembly（fix-protoex-bridge-stray-deps 差量同步，\n   同步核验与差量逐字一致）：编解码桥发布依赖面自声明所装配协议栈，1 需求 3 场景。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T08:10:29+08:00",
          "tree_id": "658cc8753b8a4c5c8c909488a18a6be842dfdf79",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/c7a4ce419831114059a43da6396ca3bab4fd4ab9"
        },
        "date": 1791073094834,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2533008265.200896,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1639319419.6166549,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 58195258.3200431,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 58928607.787568174,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 18959898.55997894,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 59303247.383990884,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 42085672.206204906,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1822628.3807988963,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 921743.0408003072,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 49951281.193926126,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 43876136.63886781,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 10050648.92670497,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 9362880.441358617,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "e33c4c333dcdc95418a11e73a90f65426a867d27",
          "message": "ci(build): push 触发面转正——main 转活主干、线入口改形态通配\n\nrevise-release-branch-flow 组 4：main 从两年死项转为特性开发顶点常驻触发；\n'5.7.x' 显式条目改为 '*.*.x' 通配，冻结线形态自动覆盖未来新线，开线仪式不再\n需要修改本文件；容器分支与个人工位不入触发面（发布前验证由 PR 通道与门禁 check 承担）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T08:23:54+08:00",
          "tree_id": "a15210e7b0d41fe137bfab75af2e92d404513eb2",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/e33c4c333dcdc95418a11e73a90f65426a867d27"
        },
        "date": 1791073892677,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2981885762.0905857,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1964431951.3286865,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 73090849.0969647,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 77843592.51863483,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22562421.195759412,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 74985750.46088873,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 57592573.08562573,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2059078.1971384722,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1032995.9094809091,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 68378955.47785851,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 61325055.54678564,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 12324208.694974722,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 11584859.538109476,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "4e05a44b033ee3e6a5fd342e8de5f4f13eb106df",
          "message": "fix(release): verify 修复轮——D10 九项全部落地\n\n代码：重放失败按状态保全协议回退（abort、reset --hard 清回原线头含先前成功笔、\n切回容器）；rev-list/cherry/rev-parse 全部查退出码；releaseCut 补回本地同名标签\n检查；releaseFrom 缺省推断收窄为仅冻结线形态（D3 字面成立）；三任务 -PdryRun\n预览路径纯 grgit 化（线缺失预览不 fetch，区间计数走 grgit log）；releaseTag 幂等\n重跑（本地标签恰指构建提交时跳过创建只补推送）；文件压缩至 250 行内。\n文档：git cherry 对账方向修正并释义参数位；新增线谱系登记表；夜间快照表述改为\n可执行事实（无自动夜间发布）；恢复条目的标签重建窗口收窄到未推送（推送后绝对\n不动，与规格一致）。演练 v2 全绿（drill/run-v2-output.txt：多笔冲突清回、pending\n取空跳过、幂等补推、退出码防护均留证）；账本三处失实已加更正声明。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T15:29:02+08:00",
          "tree_id": "00e6f79f77497fc2c85c9c920a67ecfb662b9018",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/4e05a44b033ee3e6a5fd342e8de5f4f13eb106df"
        },
        "date": 1791099429711,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 4100851719.3071723,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2821849557.313333,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 82239260.93727937,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 102089040.51022293,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 33701983.56500649,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 101799747.2714192,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 63122086.89069952,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2847394.7023732346,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1272439.786744885,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 86453052.31694928,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 76984311.48050423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 13777914.318349604,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12557689.96084253,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "f413cc732f2be5f7672319a2a754d9114764936d",
          "message": "fix(release): D11 收口轮——轻量标签拒绝、查询前置、账实校准与登记纪律\n\n代码：releaseTag 幂等仅放行附注形态且恰指构建提交的本地标签（轻量标签无解引用\n必死门禁，同错标一并拒绝并指引按未推送/已推送划线处置，D11（一））；releaseMergeBack\n的 cherry 查询前置到检出线之前，失败不动 HEAD（D11（二））；cherry-pick --abort\n自身失败记录告警不静默（D11（三））；推送失败文案先因后果（D11（四））；头注释\n通道表述修正为与重放同源的引用查询随写侧走 CLI。文件 230 行。\n文档与命令：角色总表容器行补标签创建后容器头一并封存窗口边界、标签行改按\n已推送/未推送划线；制品库一节残留的每夜构建过度声称改为人工 publish 口径；\n恢复表合回冲突行同步 reset 清回细节；流程六 gitExe 统一注记；线谱系列名与流程五\n字段对齐；命令文件 Central 步骤补 -PgitExe、安全栏错标处置按收窄口径重写。\n账本与判据：任务 1.3 判据限定可执行代码中零命中（头注释退役说明属需求六应留",
          "timestamp": "2026-10-04T19:21:32+08:00",
          "tree_id": "6ca5e4d4383f5efc5a2b88cafe21545cd3f93f68",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/f413cc732f2be5f7672319a2a754d9114764936d"
        },
        "date": 1791113365429,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2110000403.166882,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1284828775.7351704,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91898204.92060414,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 203626794.84175617,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24028314.79991544,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204290689.70625663,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109828436.45371564,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2481438.5396584137,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 913675.1619666114,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 109156574.45921269,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 91430707.72429273,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15921163.36159486,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14736702.193731332,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "8f7333c47de9a075934295e0275dba563f3988f9",
          "message": "docs(openspec): fix-publish-gate-upstream-ref 验证记录与任务勾选（1.1-1.3）\n\n对照实验两组输出、开发线回归、挂账交接（真实发布同池核对）与 worktree 附带发现\n登记于 verify-notes.md。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T20:19:07+08:00",
          "tree_id": "46d100be3e1bd665b48f192bbafc3c25f123afe1",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/8f7333c47de9a075934295e0275dba563f3988f9"
        },
        "date": 1791116820651,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 1725542003.970726,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1150426236.0622833,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 53899739.7713748,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 56578218.7023013,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 17592910.302009072,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 56610794.54772937,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 40927821.64327939,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1605436.5541224869,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 833890.2598867868,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 45196591.93985169,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 38590216.26200207,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 10308473.950467728,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 9610577.206378717,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "ad69e079350bc5c8780e758db6ff2f868fd574e0",
          "message": "build(deps): grpcVersion 1.60.0 升至 1.84.0——与 Netty 137 事实源共存的矩阵选档落库\n\n矩阵实测（EtcdNamespaceExplorerIT 36 用例、钉 netty 4.1.137 逐档覆写）：\n绿区间 [1.64.2, 1.84.0]，红区 ≤1.61.1，签名族 gRPC 流帧中途截断；\n按 design D2 取最高通过档 1.84.0（发布线最新，与 Netty 137 代际差最小）。\njetcd 保持 0.7.7（高档零二进制兼容问题，副轴未激活）。\n归因修正已登记（matrix-plan 末节）：致红变量为 Netty 升钉（治理册 32273604），\n本册为客户端栈升配正解路线。落库后本地终验：对账守卫绿、\nnamnspace-etcd 与 starter-namnspace 测试绿、docker 档 IT 36 全绿、\n全仓 build --continue 成功（52s，82 任务执行）。\n\nopenspec: upgrade-grpc-for-netty-137 组 1/2/3.1 完成，3.2 本地部分完成。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T21:46:31+08:00",
          "tree_id": "6bdc69c7be89e87b68be10976bf620c8e9f482d7",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/ad69e079350bc5c8780e758db6ff2f868fd574e0"
        },
        "date": 1791122084320,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2114851444.2079911,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1282688433.2725778,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 89080424.45543021,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 203937486.28830114,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23138568.53119484,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 203942696.67679277,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109791970.4277281,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2400162.5662792036,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 902849.3427892936,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 110993134.24807148,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 96700701.9835715,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15850803.398217702,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14847708.571215054,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "5fc5a3682695a1786abec35a2b29e6efd5cfdba6",
          "message": "build(deps): 选定档 1.84.0 回落 1.82.4——CI 镜像可用性新约束（run#93 案卷定罪）\n\nrun#93 编译红非 API 问题：腾讯镜像缺 grpc-grpclb-1.84.0.jar，海外出口下镜像系\n（tencent/aliyun）不可齐；本地矩阵绿系 ~/.gradle 缓存遮蔽。1.82.4 满足双条件：\n矩阵 36/36 绿档 + 腾讯系 grpc 全家实测 200（CI 已验证可达的镜像）。\n新增选择约束已记 matrix.md：候选档必须镜像系可用，CI 冷缓存终验不可用本地绿替代。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T22:06:01+08:00",
          "tree_id": "7370bf8b6a263920f5163997bdf3dbea8a11b3b4",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/5fc5a3682695a1786abec35a2b29e6efd5cfdba6"
        },
        "date": 1791123212111,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2689627886.1329203,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1616130746.2425928,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 122045380.83039661,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 232271520.5151936,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 29428966.468785077,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 232235143.10430962,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 135509520.3050332,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3766252.2604857134,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1166199.2724508476,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 125916000.91557348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 112286687.93150923,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18816511.956948567,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17541518.44281236,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "b665baec99fc98c45741fde51fa20a2fca260cc5",
          "message": "docs(openspec): fix-ci-unit-flakes 勾任务 4.1b 并登记案 #1、案 #2 执行记录\n\ntasks.md 任务 4.1b 勾选并附完成注记（改造范围、验证兑现、任务 4.2 计数起算说明）。\ndiagnosis.md 案 #1 补执行记录：尝试计数判据替换墙钟窗口判据的落地形态、\n@Timeout 与 jmock 线程策略不相容的化解方式、负载下 28 用例全绿的验证。案 #2 补\n执行记录：MapperLocker 四条非获取成功路径残留零引用条目的生产修复定罪与测试侧\n会合形态、有界轮询改造。本地登记 #2/3/4 加销账注，修复轮已至并按处方族落地。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-05T00:09:44+08:00",
          "tree_id": "2cae3de16c8dfecac25bad98d5b70c795ad12ff0",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/b665baec99fc98c45741fde51fa20a2fca260cc5"
        },
        "date": 1791130684883,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2604005577.1739416,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1704578392.6016355,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 59816643.98304601,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 61242944.32454427,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 20502086.41170043,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 61239790.972602725,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 43734178.95521996,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2006061.6001299326,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 976157.6004944084,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 51731158.31004996,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 45511621.744041786,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 10479947.660022568,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 9765473.586314406,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "8f7e3445d5cb0411b0f538b5dfafd7309371508e",
          "message": "docs(openspec): upgrade-grgit-for-worktrees 实施账本（守卫落地与验证矩阵）\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-05T01:35:06+08:00",
          "tree_id": "06bf2d50ca5b048f16c1858727d86922dccf304d",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/8f7e3445d5cb0411b0f538b5dfafd7309371508e"
        },
        "date": 1791135754888,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 4059533280.681689,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2720474821.0518656,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 78270250.27061282,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 97001372.5635247,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 31720200.79112967,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 96701293.07623902,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 59086746.251898624,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2471958.6866121804,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1136219.192058854,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 81303194.6879839,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 71674625.71642178,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 12924033.434238076,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12357991.421355223,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "7a9b30fa8b6a0c791bdfcd03adb6b7f237b90872",
          "message": "docs(release): 分支术语统一为拍板口径并收口入库\n\n全仓现行文本 53 处旧术语统一：冻结线改版本发布分支口径中的版本开发分支、发布容器与\n简称容器改版本发布分支（脚本中文文案、英文 description 的 container 字样同步），规格\n需求标题引用与 Docker/TaskContainer 他义保留；docs 角色总表前立规格映射行\n（版本开发分支=开发线分支、版本发布分支=发布分支）；工位命名模板入流程文档\n（feat/<主>.<次>.x-<名>、fix/<主>.<次>.<补丁>-<名>，含目标预期注记，不设工具校验）；\ndocs/branch-model.html 关系图重绘（补 feat/fix 工位、PR rebase-merge 合入 main、\nrebase 同步点线与图例）并纳入版本管理。文案回归 diff 全部落在名词内\n（基线与改后抓样在册内 terminology-*.txt）。主账本第七条标题仍用旧词，\n是否 RENAMED 差量留用户裁决（rename-notes.md）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-05T02:23:22+08:00",
          "tree_id": "73168e558f3d5768c28c078c57308fc563bd6b17",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/7a9b30fa8b6a0c791bdfcd03adb6b7f237b90872"
        },
        "date": 1791138655654,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2700414836.885347,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1618973503.1747706,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 118675672.73084831,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 231983458.15906724,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 27355448.10962336,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 231948654.7217663,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 126496950.93330038,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3764144.1529836417,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1176067.609548395,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 124423259.45949917,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 104086446.26896083,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18507641.167776205,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17425162.097591456,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": false,
          "id": "656196765d4858e6dd77858160cb7ef819726a43",
          "message": "docs(openspec): 修复 add-github-packages-channel 规划工件的四处账目错位\n\n验证核查判定四处规划文本矛盾仍存在于提交版工件，本提交逐一修正：其一，proposal.md\n\"Modified Capabilities（无）\"的时序理由句原预设本册将产出 Modified 差量，改写为\ndesign.md D9 的实际口径（扩写核对由在途变更归档后的独立小变更承接）；其二，proposal.md\n称 gradle-build-style 十一项需求逐条核验记录\"见 design.md\"属指路错位，完整记录实在\nPLAN 文档第 7.4 节，指路改正；其三，proposal.md 与 design.md 两处\"账户名下无 Maven 包\"\n的现在时句补充时间基准与清理经过（探针包已随任务 5.4 删仓清理，2026-10-05 复查为空）；\n其四，design.md 风险句声称\"任务册含体量比对步骤\"与实际不符，改为如实登记——该比对\n属遗留观察项，预检可在任务 5.5 首发前后以只读动作执行。openspec validate --strict\n在本修订后仍通过。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-05T14:36:29+08:00",
          "tree_id": "3af2a30877ebe0206fb9e7948313194caf4fcbc9",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/656196765d4858e6dd77858160cb7ef819726a43"
        },
        "date": 1791182951328,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2104455976.7085514,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1284722750.8749719,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 94276000.57130283,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204035756.69858223,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22068407.62428788,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204057507.69908068,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109806532.63109899,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2344760.0847070315,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 924807.9201032767,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 106342363.65851015,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 93199167.43261002,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15446215.730538696,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14807428.90393936,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "720d603920b5120f7740ca1ee4dea784848f5c86",
          "message": "build(ci): 开放 GitHub Packages 快照镜像——守卫翻转、逐线定时工作流与账本文案联动\n\n按 open-github-packages-snapshot-mirror 变更落地快照开通（用户 2026-10-05 裁决，复测依据见\nprobe-github-packages-snapshot-support 的 verification/snapshot-spike.md）：其一，\ntny.github-packages 插件守卫删去版本子句、收敛为 GITHUB_PACKAGES_KEY 单凭据条件，正式与快照\n两形态均可声明镜像目的地，注释与规格指路按复测终版改写；守卫三态回归（开发线注入即现、\n清空即隐、发布形态 dry-run 门禁前置）存档于变更目录。其二，新建 snapshot-mirror.yml：每日\nUTC19:23 错峰定时逐活跃开发线（git ls-remote 动态枚举、按线 concurrency 串行）执行全模块\n逐仓镜像任务，附 workflow_dispatch 人工补跑入口，checkout -B 兜底门禁的分支名读取；NEXUS\n两值与单密钥注入、快照跳签不注 SIGNING。其三，publish.yml 功能零改动、三处\"不收快照\"类\n失实注释修正；docs 镜像节改题并十二处联动（含勘误段、Maven 消费快照须开 snapshots 开关、\n体量条目累积限制与清理归属、流程五与运维前置两处自动发布断言限定），branch-model.html、\nREADME 第 4 条、config.yaml、tny.release.gradle 注释同步；openspec 四件规划工件同提交。\nvalidate --strict 通过，schedule 生效以本文件合入 main 为准。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-05T23:33:36+08:00",
          "tree_id": "b5269ee59c8f227dcc7eb5369f87bf23bbec9e4d",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/720d603920b5120f7740ca1ee4dea784848f5c86"
        },
        "date": 1791214925925,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2112358577.834996,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1279855223.4822655,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91839005.16478927,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204080555.75136164,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23128152.499917973,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204003860.75780624,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109844913.09104438,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2355496.20177642,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 897333.5307693665,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 105440920.0232332,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95148639.45776804,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15927919.405764956,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14801562.463256037,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "00274a53d499a47eb85b089c4c855298009bd6bf",
          "message": "ci: 快照镜像选线改为线谱系登记唯一依据——首跑教训与全账本同步\n\n首跑 run 37336175323 双线结论：其一，正面判据全部达成——Mirror snapshot of 5.7.x 作业\nsuccess，detached-HEAD 兜底经受门禁核对，51 个构件包落库，tny-game-net 快照目录级 unique\n元数据（timestamp 20261005.155147 / buildNumber 1）实证注册表自动维护；其二，运行整体\nfailure 暴露选线缺口——schedule 口径若按远端分支形态动态枚举，会把退役但分支未删的历史线\n（2.0.x 至 5.6.x，旧 wrapper 缺 jar）纳入矩阵批量失败（叠加本次 dispatch 漏传线名输入走了\n枚举路径）。用户 2026-10-06 复裁：定时每日仅镜像当前开发线一次，选线解析 docs/release-process.md\n线谱系登记中状态为\"版本开发分支\"的行（与流程五内网快照发布同一事实源，零新增维护面）；维护态\n与退役线仅经 workflow_dispatch 显式指定线名补跑，人工指定即授权；解析失败输出空矩阵并告警，\n绝不回退分支存在性枚举。snapshot-mirror.yml 的 enumerate 步骤、头注教训记录与全账本措辞\n（specs 差量触发形态括注、proposal、design D1 修订记录与新增被否决备选、docs 五处、\nbranch-model 两处、README、tny.release 注释）随之同步；tasks 收口 5.2 并新增第 6 组登记\n本修订，3.2 历史执行记录措辞保留。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T00:23:01+08:00",
          "tree_id": "835e3684759aea0df3124ce4a250297623623847",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/00274a53d499a47eb85b089c4c855298009bd6bf"
        },
        "date": 1791217871582,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2091781653.5840747,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1255946732.151533,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 94666433.73537287,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 179338910.72912794,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22071520.999412816,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 179754843.76138553,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 103331376.90830003,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2846719.4040382546,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 887875.4055143308,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 95282184.50864302,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 89500391.35013598,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14523777.214480823,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13917103.443888858,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "8b905b1db3f91a5d3ba7d808e5c9ec23a9ec39fc",
          "message": "docs: 补提交调查归档时的引用改指漏网部分并修正\"见归档目录\"间距\n\n归档 2026-10-06-probe-github-packages-snapshot-support 那批引用改指存在提交范围遗漏——\npublish.yml 镜像步骤注、tny.github-packages 头注、docs 勘误段的三处引用仍指活动路径，\n本提交补入；同时统一修正替换产生的\"记录见 归档目录\"多余空格（含开放变更归档目录内\nspecs 差量与 proposal/design 同步修正）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T01:23:03+08:00",
          "tree_id": "b5b41f9c9c60393cefe5261ffc8a8e3b822232cf",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/8b905b1db3f91a5d3ba7d808e5c9ec23a9ec39fc"
        },
        "date": 1791221465410,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2113020822.1733327,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1285064893.1917722,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92079629.15599662,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204174555.41917345,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23915464.225821566,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204145967.6107151,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 110761267.41993172,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2402096.498453914,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 893255.1790219254,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 102453214.5437348,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 92979007.82256453,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15869257.377468646,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14810256.912910992,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "c5cbbf6df3cc2cb344e56c54da7b92db8ce20eef",
          "message": "build(ci): 镜像凭据改回用户名令牌双属性并开放本机发布来源\n\n按 publish-mirror-via-credential-pair 变更落地用户 2026-10-06 双复裁：其一，\ntny.github-packages 守卫从单密钥判定改为 githubPackagesUsername 与 githubPackagesToken\n成对且均非空才声明镜像目的地（取值与判定同源，半配置与空值两类窗口一并封死，\n空值视同不在位与 tny.publish 属性断言口径对齐）；credentials 用户名由硬编码改读属性。\n其二，本机成为镜像合法来源——publish.yml 与 snapshot-mirror.yml 的凭据注入改回\n运行账户与内置令牌两行，docs 触发形态段反转为本机与持续集成双来源并写明正式版统一\n执行推荐与本机先发后持续集成收 409 的判读承接，tny.release 注释同步。回归六态全绿\n（verification/credential-pair-regression.txt，含临时注释发布者用户级凭据文件取证后\nsha256 逐字节恢复）；本机扇出首发实证（verification/local-fanout-first-run.txt）：\ntny-game-net 快照入镜像、目录级元数据 buildNumber 推进至 3（定时与本机构建共存单调\n递增）、构件 302 可取回。规格三条成对改题差量随归档入账本。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T02:04:37+08:00",
          "tree_id": "3fa8b65efed08384ee6db06102326fbe5e87129c",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/c5cbbf6df3cc2cb344e56c54da7b92db8ce20eef"
        },
        "date": 1791223920786,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 4003660403.6125383,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2785657620.0485444,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 79301087.54233573,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 99802439.90016416,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 31384180.11646945,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 99908061.19440573,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 61670885.91385511,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2744914.487578646,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1246912.8251350732,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 85940237.52245115,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 76656214.2102138,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 13479227.043985177,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12486216.932767507,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "fc0af9bbd360e2fe24d9ce8908f0cdf9dc874e99",
          "message": "build(release): verify 第一轮处置——退役检查任务、PR 特性守卫、例外入账可见化\n\n- tny.integrate 新增 retireGuard（未集成提交枚举加登记表处置核对），补齐规格\n  MUST 的生产执法点（verify W2）\n- build.yml 新增 release-line-patch-only 作业：目标为 release/** 的 PR 含 feat\n  提交即拒绝（verify W3）\n- releaseTag 输出本次发版携带的例外登记提交清单，规格例外通道措辞改为诚实\n  执法点表述（verify W4）；首发无标签边界防护\n- 平台三层保护已配置：main 经典保护禁强推禁删；dev/** 禁强推 ruleset；\n  release/** 禁强推禁删禁非快进 ruleset（管理员旁路为退役出口）（verify W1）\n- 动画祖父标签统一为定稿词；第十三轮端到端 18/18 全绿回归\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T05:14:45+08:00",
          "tree_id": "527d53b4bb54ae624d558e0089a7f9f81d383d24",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/fc0af9bbd360e2fe24d9ce8908f0cdf9dc874e99"
        },
        "date": 1791236247398,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2697700770.4502935,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1613176573.9378781,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 120727809.65919957,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 231451402.40010786,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 28447475.9875623,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 231710567.66010946,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 149269822.31717196,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3651194.3463523723,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1166849.5242520098,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 127196305.36011145,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 112729950.26565647,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18887918.08200897,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17468760.074719086,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "1ca77dc6e05018c28054a0fc59a1ce011961afa2",
          "message": "ci: 发布工作流合并为单文件三作业并补正式版链检出兜底\n\nconsolidate-publish-workflows 实施收口（8 项中在线验证随本提交后执行）：两条发布链\n合并进 .github/workflows/publish.yml——route 作业统一判定 release 事件、dispatch 两类\n输入与 schedule 线谱系枚举，publish-release 作业承接 Nexus/Central/镜像三步并新增\ngit checkout -B 兜底（消除分离 HEAD 使门禁分支形态核对误拒的既有隐患，与快照链已\n实证模式同构），snapshot-mirror 作业原样迁移（矩阵、按线串行、跳签）；\nsnapshot-mirror.yml 与合并同提交删除，避免定时注册双盲窗口。合并版吸收了并行在途\n变更 redesign-devline-integration-model 的最新选线口径（dev/ 前缀、在途/快照在维状态），\nschedule 分支的 awk 与其解析器逐字同源。文档五处、两插件注释按单文件三作业改写，\n触发语义与凭据面零变化（skip_specs 判定依据）；两个归档账本追加文件迁移引用注记；\nroute 五态本地模拟全绿（verification/route-walkthrough.txt），观察交接落 handoff.md。\nYAML 解析与 openspec validate --strict 通过。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T10:14:19+08:00",
          "tree_id": "b79a1ea75f46bc2d093b6c450e4e721034143c22",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/1ca77dc6e05018c28054a0fc59a1ce011961afa2"
        },
        "date": 1791253305139,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 4031645978.3647184,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2729154363.409024,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 79199363.17284079,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 98154313.41293815,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 33254031.457838707,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 100640932.81801993,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 61732318.42928734,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2625599.032604224,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1258274.6816533774,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 82801569.57558492,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 75117576.62109773,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 13103371.2416641,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 12565822.84655473,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "0faf6edbec28f178731f1493f481caeb1e402f04",
          "message": "build(git): git CLI 通道收编为项目绑定门面，门禁层拆出姊妹插件\n\n- GitCli 升级为 forProject 门面（rootDir 与 -PgitExe 解析入内），\n  run/require/remoteTagPatches 实例方法；release/integrate 三行模板与\n  publish 标签存证段 providers.exec 第三实现全部收编，全仓仅存一份通道实现\n- 远端名推定统一走 GitFlow.resolveRemoteName（局部闭包删除，等价性注随段）\n- tny.publish 286 行越线触发条件拆段：一致性门禁七段移入 tny.publish.gate\n  （241 行），凭据层留 49 行并合并 matching 注册（属性断言+任务名 dependsOn）；\n  装配线两行引入，谓词唯一保留\n- 两类不合（design D1）：派生事实与 CLI 通道分界保留，历史事故链注记入门面头注\n- 回归：e2e 十八断言全绿；门禁矩阵三例如期；openspec validate 通过\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T10:19:55+08:00",
          "tree_id": "d8a53b02d342410a6752da5076bd494e86f084e6",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/0faf6edbec28f178731f1493f481caeb1e402f04"
        },
        "date": 1791253650174,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2691816487.691254,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1615506909.2872953,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 121938288.5832198,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 231624337.28299809,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 29385862.108303647,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 232211784.33801812,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 135641481.84681982,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3723988.0338866175,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1172713.732597064,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 126675166.47003093,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 112309736.1266469,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18786524.540860467,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17448329.57089657,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "8d00ef5620d0a76300fc70044a62d77c0ed21357",
          "message": "docs(opsx): retire-grgit-channel 回归记录与任务收口——14/14 全勾\n\ne2e 十八断言、门禁矩阵三例、worktree 冒烟、脏检查五态全通过；\n守卫化修复的回归战果记录在案。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T12:26:29+08:00",
          "tree_id": "7584147b3c8a13641fefccd5dba1ff6607c40f38",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/8d00ef5620d0a76300fc70044a62d77c0ed21357"
        },
        "date": 1791261269396,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2088620857.908383,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1258271647.823986,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 94407747.68469878,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 179730056.8734274,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22470011.547463115,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 180009828.8417595,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 124711506.26677103,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2832228.0097241206,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 887854.3949844254,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 96145067.13023007,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 88636313.09352002,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14052371.088710299,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13813384.084845733,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "5b1a26ff7c00d07c4d7e6b2b2d53f5dd1225e4a9",
          "message": "refactor(buildSrc): consolidate git remote queries into GitFlow method surface\n\nGitFlow 新增 remoteRefs（全仓唯一 ls-remote 解析点，守卫继承上一变更的越界教训）、\nremoteRefNames、remoteReleasedPatches、remoteBranchExists；release/integrate/gate\n三插件的七处远端读取（含爆炸半径清单外被 grep 兜出的远端头对齐一处）全部改走\n派生方法；GitCli 删除 remoteTagPatches 回归纯通道（forProject/run/require）。\ne2e 十八断言全绿、门禁矩阵与切换前一致、机械化验收 grep 零命中。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T22:44:13+08:00",
          "tree_id": "8148d313fa220ac7b9c55ac657cadcdf089c48e6",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/5b1a26ff7c00d07c4d7e6b2b2d53f5dd1225e4a9"
        },
        "date": 1791298378336,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2093571721.5310147,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1252869420.2866728,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 94436352.5953422,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 179470758.11881357,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22028456.850533742,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 179758458.32463104,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 103759738.65483934,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2807967.326803285,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 892535.7168581396,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 102037577.24856675,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 89102916.07666305,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14496732.785809796,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13599810.43405671,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "62165c4857d99424addc00d0e3e37b6eaf8f5356",
          "message": "Merge branch \"5.7.x\" pilot-binary-build-conventions 四提交进远端顶端\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T23:31:25+08:00",
          "tree_id": "667801fa2706a9548f8751f2582c284edbaa0630",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/62165c4857d99424addc00d0e3e37b6eaf8f5356"
        },
        "date": 1791301589687,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2112627966.2808843,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1284200229.3414752,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 90433040.87982807,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204035664.9763007,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24705644.78551484,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 203978030.13354832,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 110945968.09627187,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2363659.419439381,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 908760.7594609966,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108608022.35398242,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95990508.73192897,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15711072.285131529,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14743177.508962799,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "3fd29002b5f48a3812b378872f5c15c7433f92ea",
          "message": "Merge branch \"5.7.x\" pilot verify 修复提交\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-06T23:58:40+08:00",
          "tree_id": "1b3d17a37c15a8b545a413dc67c56a7f7e7fe3a1",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/3fd29002b5f48a3812b378872f5c15c7433f92ea"
        },
        "date": 1791302814186,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2112243022.3007247,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1282475874.8801978,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91989613.40604703,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 203971302.2880515,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23563327.082324125,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204087911.9422758,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109830208.20525186,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2459908.5417243824,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 919803.3311355526,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 109840045.47462925,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95585636.43505034,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15722146.045642486,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14810175.347942919,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "f2b007534e86ce96cc3c5e4c49b75d2dd0613923",
          "message": "fix(conventions): 复验 CRITICAL-2 闭环——零发布合同映射判定收回检查类\n\n用户拍板路线：按 design D3 原设想把\"输入 ProjectEdge 事实清单、\n输出违例清单\"的判定收回 UnpublishedContractCheck（自依赖双保险\n规则入函数可测），接线类只保留逐边解析、强制评估与角色声明位\n采集；单测补齐通过方向（tests 2→4，红绿双向满足 ADDED 需求 MUST）\n与两处 javadoc 一致性。端到端回归：破坏探针 B 复跑报红文案逐字\n一致、还原 sha 一致、复绿。批量清账同批落地：ADDED 措辞改可执行\n单测口径（纯判定允许事实夹具，装配面 ProjectBuilder，用户拍板）、\ndesign D3 接线时机与 Unpublished 分工文本更正、爆炸半径行号注记\n（47→现 39）、tasks 4.2 括注按 D8 语义改写、adopt-gradle-official-dsl\n拼写、Open Questions 预登记 GUARD_COORDS 与需求六词表两条判据歧义。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-07T00:49:03+08:00",
          "tree_id": "8050c97230be0a1ea21fb3873fc03073e7da9bdc",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/f2b007534e86ce96cc3c5e4c49b75d2dd0613923"
        },
        "date": 1791305790951,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2922255087.6179705,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1945504630.183937,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 74490383.33962014,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 78405481.40220997,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23312461.228369422,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 77909552.76498732,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 51324586.69300659,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2060002.5004973717,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1074355.4547039338,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 69522850.83914423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 61774617.2225157,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 12671858.20162187,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 11514517.892846305,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "8fab5621a3db87dc6a780830a5035fd104d0fdc8",
          "message": "docs(opsx): 最终复验文字账清尽——四处措辞残留与探针形态失实更正\n\n功能面复验五路全 PASS（20 用例红绿双向、探针 A/B 端到端逐字报红、\n删除幸存、validate 绿）；对抗者仅推翻文字账面，本轮清尽：tasks 5.1\n措辞改与差量新口径一致、5.2 接线时机改双回调表述（与 design D3 同）、\n5.4 与 design D7 的破坏探针两形态更正为实证成立的形态并注明两个\n直觉形态为何不成立（改根事实源自洽不报红、给 -integration-test 自身\n加 publishing 不属 javaProjects 不报红）、apply-notes 过时状态标题\n加闭环注记与现数 20 用例、Open Questions 两条预登记拆独立条目、\n补双违例并存首条选取规则用例。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-07T01:16:28+08:00",
          "tree_id": "29cc10dba00519ab6897980f887ab42260a37bf5",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/8fab5621a3db87dc6a780830a5035fd104d0fdc8"
        },
        "date": 1791307517300,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2101197097.1085892,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1280082314.3579712,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91450428.1974482,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 203975566.9451113,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22989379.359809853,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204183489.6867376,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109546617.84155321,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2295159.8025924885,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 902126.2234483112,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 107901139.45858411,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 94576450.94477648,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15771859.625248944,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14734823.111667305,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "ce7d6af498bbaac8283bf0e0e2fe972ff96cbadc",
          "message": "docs(ledger): consolidate-assembly-line 归档——规格账本净效果为零（skip_specs，13 条需求现文即本册执行依据）；装配面净变化为注册 id 十八枚收至十七枚（九枚二进制实现加八枚预编译脚本），dependency-management/java-module/plugin-module/compile-baseline 四枚随三入口吸收注销，integration-test/benchmark-module 同名 Java 化保留，jmh 与 dm 版本落点归 buildSrc 单一事实源，根装配十四行收十行，九样件与两线外任务图零漂移、CodOD 双形态一致、耗时 0.3 秒内无劣化；跨册登记落 redesign 册卷宗\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-07T20:42:04+08:00",
          "tree_id": "16bf566823576faaf8b4ff82458f7090db9cbee9",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/ce7d6af498bbaac8283bf0e0e2fe972ff96cbadc"
        },
        "date": 1791377427085,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2696729118.4947886,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1614655058.7066207,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 121866211.03553765,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 231136666.82131296,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 28388651.16089791,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 231981579.69346818,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 131414161.48033014,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3736339.4432069324,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1166538.5358795288,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 126223606.6341172,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 107829962.792383,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18711787.661345243,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17545058.377832003,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "47147525def6385b7692a799c44d9739ea62d57f",
          "message": "收口修复——三视角对抗复核十项发现的处置\n\n复核确认归档与回写主体事实全部在位（旧活动路径全仓零悬空引用、插件描述符\n与提交链完整、修复落点文件清单逐一存在），发现面处置如下：\n一、ProjectsPlugin 与 ProjectsExtension 的 javadoc 清除\"册\"代号与截短名\n（consolidate-assembly-line 变更实名、探针五出处写全 design.md 探针结论\n小节、redesign-devline-integration-model 设计决策 D2 写全名），ProjectsExtension\n的派生版本注释更正两处过时事实——注入来源改实名 tny.release-ops，\n\"未应用即抛\"改为与代码实况一致的\"留空不抛、报红由消费方版本派生承担\"。\n二、DependencyConventionsPlugin 判空报错文案与注释把已退役 id tny.git 改为\ntny.release-ops，报错指引与根脚本现实对齐（tests 无该文案断言，buildSrc\n测试与根 help 复验绿）。\n三、redesign 跨册文件第 4 条与发布族 change 留档段按 CLAUDE.md 文字规则\n重写：完整语句、实名指代、消除\"本册/该册\"混叠；发布族移交计数由误写的\n\"约十处\"改为照录移交行原文的分段计数（七处加两处加合计三处），其任务 1.5\n同步修正；判例表位置引述改为照录实际节名\"正则选点判例表（组 1 起草，组 7\n勾验）\"。\n四、归档 change 的 apply-notes 组 8 段今日新写文字同样清违例称谓（历史\n归档目录不追改既有记录，仅修本次写入部分）。\n根目录临时文件 convert-orchestration-to-java-tmp-do-not-use.md 删除权限\n此前已拒，仍由用户执行 rm 清理。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-07T23:35:14+08:00",
          "tree_id": "1461d3af29e516c496f8c67e3602b4d1eeede647",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/47147525def6385b7692a799c44d9739ea62d57f"
        },
        "date": 1791387824438,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2108828343.5274587,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1285636080.2162807,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91091001.57631308,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204089206.54278356,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22789613.641144384,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 203970163.53905997,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109638609.51586135,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2477099.41058912,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 904026.097825427,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 107943874.9871376,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95735153.90495056,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15834712.674528038,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14789816.627660075,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      }
    ],
    "Routine quick (main)": [
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "35b8f8c7dd4974430cef1bcc5ebd953f767ea886",
          "message": "ci(build): push 触发面转正——main 转活主干、线入口改形态通配\n\nrevise-release-branch-flow 组 4：main 从两年死项转为特性开发顶点常驻触发；\n'5.7.x' 显式条目改为 '*.*.x' 通配，冻结线形态自动覆盖未来新线，开线仪式不再\n需要修改本文件；容器分支与个人工位不入触发面（发布前验证由 PR 通道与门禁 check 承担）。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T08:22:56+08:00",
          "tree_id": "c625a75a557438f762b54953eb9d6192bf4d9733",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/35b8f8c7dd4974430cef1bcc5ebd953f767ea886"
        },
        "date": 1791073885785,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 1725534057.9394474,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1150834928.2544813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 53957044.58217283,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 56581228.88898206,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 17884715.091204423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 56603135.38661109,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 48049823.08337703,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1717775.334093883,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 833330.7517769994,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 45119356.91929536,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 38474112.237132356,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 10284568.482333133,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 9597774.584858183,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "77b8b655321352672ebb36b72915e5bbc029b28b",
          "message": "fix(release): verify 修复轮——D10 九项全部落地\n\n代码：重放失败按状态保全协议回退（abort、reset --hard 清回原线头含先前成功笔、\n切回容器）；rev-list/cherry/rev-parse 全部查退出码；releaseCut 补回本地同名标签\n检查；releaseFrom 缺省推断收窄为仅冻结线形态（D3 字面成立）；三任务 -PdryRun\n预览路径纯 grgit 化（线缺失预览不 fetch，区间计数走 grgit log）；releaseTag 幂等\n重跑（本地标签恰指构建提交时跳过创建只补推送）；文件压缩至 250 行内。\n文档：git cherry 对账方向修正并释义参数位；新增线谱系登记表；夜间快照表述改为\n可执行事实（无自动夜间发布）；恢复条目的标签重建窗口收窄到未推送（推送后绝对\n不动，与规格一致）。演练 v2 全绿（drill/run-v2-output.txt：多笔冲突清回、pending\n取空跳过、幂等补推、退出码防护均留证）；账本三处失实已加更正声明。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T15:29:13+08:00",
          "tree_id": "68807525cd4139ae14305b2dec209e05a0c3da54",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/77b8b655321352672ebb36b72915e5bbc029b28b"
        },
        "date": 1791099432030,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2942446112.3940687,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2019528979.1556046,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 74193840.86548254,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 77123360.92933664,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22812330.843588825,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 76057592.54991302,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 51334397.13728984,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2168302.646331762,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1067358.499732757,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 71506500.13768819,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 62028964.08929406,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 12633446.783844152,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 11743748.643366674,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "598d40b5009fd880585151e41eee25a2c23c9bd0",
          "message": "fix(release): D11 收口轮——轻量标签拒绝、查询前置、账实校准与登记纪律\n\n代码：releaseTag 幂等仅放行附注形态且恰指构建提交的本地标签（轻量标签无解引用\n必死门禁，同错标一并拒绝并指引按未推送/已推送划线处置，D11（一））；releaseMergeBack\n的 cherry 查询前置到检出线之前，失败不动 HEAD（D11（二））；cherry-pick --abort\n自身失败记录告警不静默（D11（三））；推送失败文案先因后果（D11（四））；头注释\n通道表述修正为与重放同源的引用查询随写侧走 CLI。文件 230 行。\n文档与命令：角色总表容器行补标签创建后容器头一并封存窗口边界、标签行改按\n已推送/未推送划线；制品库一节残留的每夜构建过度声称改为人工 publish 口径；\n恢复表合回冲突行同步 reset 清回细节；流程六 gitExe 统一注记；线谱系列名与流程五\n字段对齐；命令文件 Central 步骤补 -PgitExe、安全栏错标处置按收窄口径重写。\n账本与判据：任务 1.3 判据限定可执行代码中零命中（头注释退役说明属需求六应留",
          "timestamp": "2026-10-04T19:20:44+08:00",
          "tree_id": "9bf040359419da999960e7e3e3c96f173ec3bbc8",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/598d40b5009fd880585151e41eee25a2c23c9bd0"
        },
        "date": 1791113318613,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2693552186.8659554,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1620366372.0520005,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 121827080.32761645,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 231553381.36544633,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 28480578.679278173,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 231697955.52372575,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 135061410.8563397,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3739690.150430806,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1170321.6802123364,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 126307076.73538136,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 112182844.92242432,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18866920.553471986,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17651357.87793117,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "YangKun",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "35755b8526470a2682066d866275e196aa14e85c",
          "message": "docs(openspec): fix-publish-gate-upstream-ref 验证记录与任务勾选（1.1-1.3）\n\n对照实验两组输出、开发线回归、挂账交接（真实发布同池核对）与 worktree 附带发现\n登记于 verify-notes.md。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-04T20:19:25+08:00",
          "tree_id": "30bbccbf16953d704ac0d96818362b92b852c1c3",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/35755b8526470a2682066d866275e196aa14e85c"
        },
        "date": 1791116845045,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2113377094.4447017,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1285480757.8471859,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91811838.14530134,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 203742620.46808597,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23882526.658545244,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 203879499.5643536,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109768367.530178,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2427577.842552009,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 903532.715127375,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 111223434.30413917,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95275827.08393471,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15737920.446924537,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14746425.966081742,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "aef8f77b9f1a7c9969f33b8ca315202823b314ec",
          "message": "merge 5.7.x：main 同步线头（main 不受线性纪律约束，采用合并收编）",
          "timestamp": "2026-10-05T02:25:22+08:00",
          "tree_id": "b96ae8ba2e42eec90e457908ff7dd6411051c1d9",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/aef8f77b9f1a7c9969f33b8ca315202823b314ec"
        },
        "date": 1791138807992,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2109976648.8406284,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1277473831.64393,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91851930.73298977,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204093868.99386886,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23295518.577711843,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204038715.7799503,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 110027262.68202576,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2420605.3427964086,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 887365.1395041464,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 105956991.70020391,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 96226565.32640946,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15403869.076855332,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14838507.945109606,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "bbeff547fe53cdc40c3b59111dd7e23851a236a8",
          "message": "merge 5.7.x：main 同步——GitHub Packages 镜像通道、快照复测调查与快照开放落地\n\n唯一冲突 tny-benchmark/results/bench-20261004-quick.json 按用户裁决取 5.7.x 侧。\n镜像工作流（正式版 publish.yml 第三步骤与快照 snapshot-mirror.yml）自本合并起在默认分支生效。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>\n\n# Conflicts:\n#\ttny-benchmark/results/bench-20261004-quick.json",
          "timestamp": "2026-10-05T23:50:58+08:00",
          "tree_id": "87064e00c0410c31de9890ba1d018ecfc83425cb",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/bbeff547fe53cdc40c3b59111dd7e23851a236a8"
        },
        "date": 1791215929001,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2084655051.9602082,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1249221241.4271955,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 94161759.4690974,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 179552045.560181,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22340630.72496565,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 179627277.27948937,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 103120644.29167333,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2805921.585116051,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 889704.7488887189,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 101225521.81923746,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 87841528.6083186,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14356444.269506404,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13886166.662236456,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "9207eea15a1213859893414e9e881ec8eda94362",
          "message": "merge 5.7.x：main 同步——快照调查收口与开放变更进度勾选",
          "timestamp": "2026-10-05T23:54:23+08:00",
          "tree_id": "4dc59e11e3c1be7b93b1b127b7330702e270fa84",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/9207eea15a1213859893414e9e881ec8eda94362"
        },
        "date": 1791216144767,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2085511000.7444096,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1242117211.346942,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 93591070.99707422,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 179531119.00008672,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22369578.590187423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 179594164.17120594,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 103054878.89067528,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2819856.3468258325,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 885872.609120195,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 101945096.44572717,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 89216135.91051091,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14404131.982640684,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13960980.81498681,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "0b2b8457390c575845ecdf8ab32817bc02a0bef4",
          "message": "merge 5.7.x：main 同步——快照镜像选线口径修订（线谱系登记唯一依据）",
          "timestamp": "2026-10-06T00:23:10+08:00",
          "tree_id": "df58fa052910162901c38952f10dc5273bb598ba",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/0b2b8457390c575845ecdf8ab32817bc02a0bef4"
        },
        "date": 1791217876699,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2082856618.0007293,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1248143536.9242625,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92995876.1429688,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 179280304.2161588,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22195553.18677081,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 179759654.00852388,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 103513102.79124638,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2737243.859520732,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 891585.1074228957,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 98664382.47238544,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 89614198.58779109,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 14524971.31705063,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13896737.94133375,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "3793076c1209a04a3e544b2453c2702b13e68406",
          "message": "merge 5.7.x：main 同步——快照镜像复验收口\n\n# Conflicts:\n#\ttny-benchmark/results/bench-20261005-quick.json",
          "timestamp": "2026-10-06T00:33:00+08:00",
          "tree_id": "91b58cd15405011ecce3e310cac1b29f0e8bb3cc",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/3793076c1209a04a3e544b2453c2702b13e68406"
        },
        "date": 1791218446967,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2225218143.5027404,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1482787357.8423586,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 51412644.686301425,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 52514509.31917791,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 18899722.632009503,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 52616125.09907664,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 37580030.106860675,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1700377.9438669519,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 829842.2294697186,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 44467502.508772284,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 38975125.776211895,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 8971725.377336195,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 8423221.566450778,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "c4b2305f77f6a746b0f297c13fb29fc494514968",
          "message": "merge 5.7.x：main 同步——引用改指补漏",
          "timestamp": "2026-10-06T01:23:11+08:00",
          "tree_id": "4ec4c62657170eb96128fecfef5bc632aa55f112",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/c4b2305f77f6a746b0f297c13fb29fc494514968"
        },
        "date": 1791221462897,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 1723502912.3891425,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1146176483.736257,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 53673626.083653726,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 56443511.89136602,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 18749362.537631214,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 56394217.64058415,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 41108188.93226789,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1657792.1643415303,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 825500.1000517803,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 45127249.51964076,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 37621008.1277435,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 10265414.252259685,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 9828206.947521254,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "8d0c180ed557e18f50d4a1d03c6cfc0ad36c4c1e",
          "message": "merge 5.7.x：main 同步——align 通道枚举核对收口\n\n# Conflicts:\n#\ttny-benchmark/results/bench-20261005-quick.json",
          "timestamp": "2026-10-06T01:44:22+08:00",
          "tree_id": "a24d5aea362ec4631c49b3bd80dd4a13b04d34b5",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/8d0c180ed557e18f50d4a1d03c6cfc0ad36c4c1e"
        },
        "date": 1791222745748,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2186667606.243712,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1472797131.2603898,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 49828725.209489465,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 51781420.01142249,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 18379182.867003657,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 51838623.26724997,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 37077845.24510088,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 1678115.2017234694,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 811689.5583971356,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 43679992.80878779,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 38440083.78324684,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 8801273.333495192,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 8273929.369823121,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "718a089dac06ae64dee0d033c48e53e5df2f3659",
          "message": "merge 5.7.x：main 同步——凭据对与双来源归档入账",
          "timestamp": "2026-10-06T02:06:26+08:00",
          "tree_id": "b9f29e592f1fcde972b60c9037b0da71d70309ff",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/718a089dac06ae64dee0d033c48e53e5df2f3659"
        },
        "date": 1791224069632,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2110348339.1094086,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1271871000.4383442,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91998461.08660313,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204160255.81182984,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24318007.96465901,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204150422.4438794,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 110772843.9982795,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2427807.0712409713,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 903649.2414759693,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 109276508.69686897,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 86601655.10231075,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15792805.962125141,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14754113.043005953,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "c5e5bc06de1e9751c795c47fcd5f5d1931120568",
          "message": "merge main：吸收远端例行基准提交\n\n# Conflicts:\n#\ttny-benchmark/results/bench-20261005-quick.json",
          "timestamp": "2026-10-06T10:15:04+08:00",
          "tree_id": "0805c94cbc22d52c462679767a8b7dabfcef5b15",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/c5e5bc06de1e9751c795c47fcd5f5d1931120568"
        },
        "date": 1791253374454,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2666297493.7517138,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1603490490.2945404,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 121850459.02146097,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 231719521.14765564,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 28509423.114252746,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 231987381.0404548,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 131107727.26652214,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 3617689.232764891,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1168301.1321455524,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 125879935.74911363,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 112785338.01932588,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 18801934.675640874,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 17403572.51069077,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "dba04e300c1ea894588a95ed857be0bc008f7a29",
          "message": "merge 5.7.x：main 同步——合并变更归档入账\n\n# Conflicts:\n#\ttny-benchmark/results/bench-20261006-quick.json",
          "timestamp": "2026-10-06T11:11:10+08:00",
          "tree_id": "4854be47dc761d07524ae5fdae245b11a5e2e8d5",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/dba04e300c1ea894588a95ed857be0bc008f7a29"
        },
        "date": 1791256709372,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2975030596.018466,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1943113376.434534,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 72116285.58612365,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 75638146.7250863,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 20156424.842425417,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 75664779.42285031,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 51405652.68741188,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2069685.329083244,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1050653.111904943,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 71261095.45346105,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 61112494.00252219,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 12640022.831381347,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 11279079.632369665,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      }
    ],
    "Routine full (5.7.x)": [
      {
        "commit": {
          "author": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "committer": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "id": "e64188c49808be4361f5ef6ed6b14b3f61077d16",
          "message": "chore(bench): routine benchmark results [skip ci]",
          "timestamp": "2026-10-04T14:46:21Z",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/e64188c49808be4361f5ef6ed6b14b3f61077d16"
        },
        "date": 1791125184028,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 4209957191.346293,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 2778826235.4854283,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 81027234.90153793,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 103563988.6555017,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 34015981.48775201,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 103454502.22184023,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 61809428.9402474,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2712004.8340093265,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 1274675.046664828,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 972050.1432008095,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 114490.13613895423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1120680.0434913728,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 78493.81460048686,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1250768.2855405791,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 340338.9360931484,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1380560.8245270825,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 351315.2706350148,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1342192.7358793784,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 511331.751849999,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1552639.142859924,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 554110.3836585667,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1522589.5205985187,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 321778.86130095436,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1403919.9976008204,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 356434.66821176047,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1704745.1912205839,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 451084.76729525346,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1583917.1985897121,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 538058.3900497304,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1059073.916620513,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 441510.18261737947,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1031920.8963105452,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 441464.1175786862,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 87482342.8618702,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 79586542.64817978,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 13946475.125978012,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 13080867.491693277,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "committer": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "id": "e5b9a4ad326acb6c9aa0591425c5b022604a5965",
          "message": "chore(bench): routine benchmark results [skip ci]",
          "timestamp": "2026-10-04T16:40:25Z",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/e5b9a4ad326acb6c9aa0591425c5b022604a5965"
        },
        "date": 1791132027257,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2113747419.3986537,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1281107147.9000819,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91965886.83932832,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204027979.68546233,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24257820.461170144,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 203912874.5784788,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109385336.1756562,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2404421.795685931,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 915563.9423825594,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 723920.9609673969,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 119116.66062519574,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 802912.2338428074,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 121431.59920844769,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 873481.8439350821,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 205690.21990459092,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1005747.9807691643,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 216063.78367327945,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 950396.0498121055,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 325730.10964089195,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1100930.937451309,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 338470.9951581517,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1025784.2136220519,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 179997.1767943396,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 953235.5018756881,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 210011.81333340768,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1112611.338552604,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 248612.2047898778,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1027323.2444818892,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 298481.5751459801,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 783240.5552022004,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 238062.09530094717,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 782442.8041557707,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 236930.63352894812,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 106008651.10439236,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 92326123.76507358,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15432673.238964241,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14713832.28310295,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      }
    ],
    "Routine full (main)": [
      {
        "commit": {
          "author": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "committer": {
            "email": "yangkun311@gmail.com",
            "name": "Tunaiyi",
            "username": "Tunaiyi"
          },
          "distinct": true,
          "id": "5cc9e710925b0a5bfd0623b01d00189c4b92fad7",
          "message": "docs(openspec): upgrade-grgit-for-worktrees 归档——linked worktree 守卫与完整克隆纪律定案\n\n对 specs 账本的净影响：零——本册 skip_specs（环境守卫与错误体验，release-versioning\n七条与 gradle-build-style 八条需求均不受影响），主账本无写入。交付物：tny.git 的\ngitdir 位置判据守卫（worktree 构建从配置期误导性 NPE 变为含三要素的指向性错误，\nGitFlow 设 head 判空兜底）、docs 发布执行环境段（完整克隆纪律定案）、\nrevise-release-branch-flow 挂账①销案、JGit 升级路径证伪证据表与可重跑 watch 复验脚本。\n验证以 apply 期实测矩阵留档（正向触发、克隆与 detached 不误伤、主仓回归逐行一致），\n未走独立 verify——七项判据均有实测输出支撑；子模块布局未竟现场以 git 文档依据登记。\n\nCo-Authored-By: Claude Code <noreply@anthropic.com>",
          "timestamp": "2026-10-05T01:48:35+08:00",
          "tree_id": "84ff8eea1957043edc263cfb3a877bc71a6c48db",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/5cc9e710925b0a5bfd0623b01d00189c4b92fad7"
        },
        "date": 1791137578586,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2110371562.7785416,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1274649616.9928098,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91843935.47251353,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 203314071.03297856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23211306.032359116,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204053438.1011172,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 110144363.88461287,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2421815.5502616814,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 911768.0348030856,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 730488.1188173571,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 119531.12718918482,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 800647.5695962419,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 121731.82050031054,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 879199.5759120618,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 207279.77040731593,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1014537.6106102144,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 215316.7799526392,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 959035.2535730426,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 306913.874107198,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1112669.7568060614,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 343809.9202991406,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 997829.1024911361,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 179040.7731319578,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 939439.7812539665,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 202725.04649752085,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1125388.8943130858,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 251202.05086334544,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1021205.2575068964,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 295132.9012960164,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 783386.5658032915,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 238315.92048053863,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 781307.4911585681,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 238161.52384752463,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108699047.90731084,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95413891.86127262,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15702118.228312338,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14801662.152142424,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "committer": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "id": "ba0f9833d29dc161cf238978ddb575bcdcfea443",
          "message": "chore(bench): routine benchmark results [skip ci]",
          "timestamp": "2026-10-04T22:11:41Z",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/ba0f9833d29dc161cf238978ddb575bcdcfea443"
        },
        "date": 1791151903446,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2114580094.5777652,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1267448649.2706027,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 91759918.21330342,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204131728.20601797,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 24168835.52445695,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 203785664.354725,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109533191.77254131,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2494898.0958935935,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 915344.8646781385,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 704146.0674564957,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 118473.51719690145,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 815709.7868051529,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 121542.93291767637,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 866128.8371322202,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 207883.31526073595,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1015197.699079412,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 213748.73661489895,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 957585.182734274,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 309317.3833317978,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1144292.7546237297,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 325927.9301694036,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1042303.5905513422,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 175764.1643667777,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 952103.1699263944,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 204434.8042507063,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1086788.4501748704,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 241397.7480573413,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1029877.3067681579,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 295154.09957308153,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 784638.632051534,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 237702.41253999007,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 780533.5195999163,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 235013.6377196458,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 106317401.36089948,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95089335.2809002,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15509518.907444904,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14749480.11328502,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "committer": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "id": "179d21434a181c252d3a2248251db4d18f10a0e1",
          "message": "chore(bench): routine benchmark results [skip ci]",
          "timestamp": "2026-10-06T00:36:16Z",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/179d21434a181c252d3a2248251db4d18f10a0e1"
        },
        "date": 1791246978353,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2110423145.4460511,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1274290104.7753081,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92090142.14366385,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 203879221.79761142,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 23207159.73462423,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204138383.42949367,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 136577950.87621188,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2399982.7104027225,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 910534.7998436792,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 729893.1817415908,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 117833.18454095798,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 809908.9781576276,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 118390.12576749086,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 880908.2922600254,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 215294.57656529118,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1022165.0528622952,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 215892.97838159068,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 946739.048408179,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 306148.0091062281,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1136388.6771855715,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 320398.4873474158,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1022462.4489316633,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 179285.43592844444,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 957962.6645240554,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 203321.0310926603,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1083755.8300443103,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 249855.98314637708,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1032157.8308182035,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 298729.4743139072,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 786050.3038532932,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 236862.84211741568,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 773556.6015966284,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 235233.54305067472,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 108547993.90670864,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 96163519.44793567,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15701843.789643738,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14844058.501234526,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "committer": {
            "name": "bench-routine[bot]",
            "email": "bench-routine@users.noreply.github.com"
          },
          "id": "58983e6e15080000594b580a940003afe8900185",
          "message": "chore(bench): routine benchmark results [skip ci]",
          "timestamp": "2026-10-06T23:05:07Z",
          "url": "https://github.com/Tunaiyi/tny-framework/commit/58983e6e15080000594b580a940003afe8900185"
        },
        "date": 1791327910193,
        "tool": "jmh",
        "benches": [
          {
            "name": "com.tny.game.benchmark.net.devtest.SmokeBenchmark.noop",
            "value": 2108690714.71418,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"0\"} )",
            "value": 1281424857.313886,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.addMessage ( {\"capacity\":\"64\"} )",
            "value": 92137790.86239448,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"0\"} )",
            "value": 204050523.94075906,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.filteredRead ( {\"capacity\":\"64\"} )",
            "value": 22887755.26509417,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"0\"} )",
            "value": 204002548.2853799,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.MessageQueueBenchmark.snapshotRead ( {\"capacity\":\"64\"} )",
            "value": 109770462.37246725,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"false\"} )",
            "value": 2466034.106998728,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PacketCodecBenchmark.encodeThenDecode ( {\"verify\":\"true\"} )",
            "value": 903443.2273213059,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 698930.5780100028,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 118625.00172352421,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 814927.969639971,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc64_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 121572.33613815547,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 872457.9829619636,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 207011.52227498527,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1017114.6463501789,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 214248.00429506955,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 976230.830706566,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 309254.7909049246,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1089571.139827262,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xortile\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 339001.71542524645,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1034100.7678702159,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 181131.4685346813,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 950655.4855426155,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 208051.25495795958,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 1125156.0004987414,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 248157.12468614226,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 1030564.5738729599,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_crc32_xorprod\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 300526.01322844526,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"42\"} )",
            "value": 783760.7954899367,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"bench-key\",\"size\":\"994\"} )",
            "value": 236723.04067066853,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"42\"} )",
            "value": 788404.2301019465,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.PipelineCryptoMatrixBenchmark.encodeThenDecode ( {\"algo\":\"prod_siphash24_chacha\",\"benchKey\":\"0123456789abcdef\",\"size\":\"994\"} )",
            "value": 236984.36578021152,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"1000\"} )",
            "value": 103208917.67191817,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.pollMiss ( {\"inflight\":\"10000\"} )",
            "value": 95807499.67058149,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"1000\"} )",
            "value": 15922223.825279215,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          },
          {
            "name": "com.tny.game.benchmark.net.routine.RespondFutureBenchmark.putAndPoll ( {\"inflight\":\"10000\"} )",
            "value": 14815887.142659208,
            "unit": "ops/s",
            "extra": "iterations: 10\nforks: 2\nthreads: 1"
          }
        ]
      }
    ]
  }
}