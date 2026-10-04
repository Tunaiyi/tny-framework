window.BENCHMARK_DATA = {
  "lastUpdate": 1791073894579,
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
      }
    ]
  }
}