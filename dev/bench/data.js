window.BENCHMARK_DATA = {
  "lastUpdate": 1790873745866,
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
      }
    ]
  }
}