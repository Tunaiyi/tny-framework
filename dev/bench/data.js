window.BENCHMARK_DATA = {
  "lastUpdate": 1790875258871,
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
      }
    ]
  }
}