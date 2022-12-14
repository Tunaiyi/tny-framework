/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command;

import com.tny.game.net.message.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public interface Certificate<UID> extends Messager, Serializable {

    /**
     * @return 获取凭证号
     */
    long getId();

    /**
     * @return 用户 ID
     */
    UID getUserId();

    /**
     * @return 是否已认证
     */
    default boolean isAuthenticated() {
        return getStatus().isAuthenticated();
    }

    /**
     * @return 用户组
     */
    default String getUserGroup() {
        return this.getMessagerType().getGroup();
    }

    /**
     * @return 鉴定时间
     */
    Optional<Instant> getAuthenticateAt();

    /**
     * @return 创建时间
     */
    Instant getCreateAt();

    /**
     * @return 获取授权状态
     */
    CertificateStatus getStatus();

    /**
     * 当前认证是否比 other 新
     *
     * @param other 对比认证
     * @return 比 other 新返回 true, 否则返回 false
     */
    default boolean isNewerThan(Certificate<UID> other) {
        if (this.isAuthenticated() && !other.isAuthenticated()) {
            return true;
        }
        if (!this.isAuthenticated() && other.isAuthenticated()) {
            return true;
        }
        Optional<Instant> thisInstant = this.getAuthenticateAt();
        Optional<Instant> otherInstant = this.getAuthenticateAt();
        if (thisInstant.isPresent() && otherInstant.isPresent()) {
            return thisInstant.get().isAfter(otherInstant.get());
        }
        return true;
    }

    /**
     * 当前认证是否比 other 旧
     *
     * @param other 对比认证
     * @return 比 other 旧返回 true, 否则返回 false
     */
    default boolean isOlderThan(Certificate<UID> other) {
        if (!this.isAuthenticated() && other.isAuthenticated()) {
            return true;
        }
        if (this.isAuthenticated() && !other.isAuthenticated()) {
            return true;
        }
        Optional<Instant> thisInstant = this.getAuthenticateAt();
        Optional<Instant> otherInstant = this.getAuthenticateAt();
        if (thisInstant.isPresent() && otherInstant.isPresent()) {
            return thisInstant.get().isBefore(otherInstant.get());
        }
        return false;
    }

    /**
     * 当前凭证与指定的凭证 other 是否属于同用户(userType user)
     *
     * @param other 指定凭证
     * @return true 同用户, false 不同用户
     */
    default boolean isSameUser(Certificate<UID> other) {
        if (this == other) {
            return true;
        }
        return Objects.equals(getUserId(), other.getUserId()) &&
                Objects.equals(getMessagerType(), other.getMessagerType());
    }

    /**
     * 当前凭证与指定的凭证 other 是否属于同一凭证()
     *
     * @param other 指定凭证
     * @return true 同用户, false 不同用户
     */
    default boolean isSameCertificate(Certificate<UID> other) {
        if (this == other) {
            return true;
        }
        return getId() == other.getId() &&
                Objects.equals(getUserId(), other.getUserId()) &&
                Objects.equals(getMessagerType(), other.getMessagerType());
    }

}
