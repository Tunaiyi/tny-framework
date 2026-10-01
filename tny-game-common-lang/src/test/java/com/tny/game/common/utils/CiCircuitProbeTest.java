package com.tny.game.common.utils;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PROBE fix-ci-unit-flakes 2.1 —— 电路自证空弹（与任何产品行为无关，取证后即刻 revert）：
 * 本类必然失败，用于制造一次计划内的 unit 红，验证 ci-unit-diag 诊断电路
 * 能正确投递四件案卷（etcd 容器日志 / gradle 输出尾 / 失败用例 XML / 环境自述）。
 * 见到此红请勿"修复"——按 tasks 2.2 取卷验证后由 2.3 回滚。
 */
class CiCircuitProbeTest {

    @Test
    void circuitProbe() {
        fail("PROBE fix-ci-unit-flakes 2.1: 验证 unit 诊断电路投递路径");
    }
}
