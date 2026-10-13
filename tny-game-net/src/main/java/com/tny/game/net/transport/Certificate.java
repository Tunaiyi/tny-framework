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
package com.tny.game.net.transport;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public interface Certificate extends ConnectIdentity, Serializable {

    long ANONYMITY_CONTACT_ID = -1L;
    long ANONYMITY_IDENTIFY = -1L;
    long ANONYMITY_ID = -1L;

    /**
     * @return 获取凭证号
     */
    long getId();

    /**
     * @return 是否已认证
     */
    default boolean isAuthenticated() {
        return getStatus().isAuthenticated();
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
    default boolean isNewerThan(Certificate other) {
        if (this.isAuthenticated() != other.isAuthenticated()) {
            // 已认证即较新；未认证方不得同时判定为更新（两方向互斥）
            return this.isAuthenticated();
        }
        Optional<Instant> thisInstant = this.getAuthenticateAt();
        Optional<Instant> otherInstant = other.getAuthenticateAt();
        if (thisInstant.isPresent() && otherInstant.isPresent()) {
            return thisInstant.get().isAfter(otherInstant.get());
        }
        // 时刻不可比：新旧无从判定，两方向都保守为否（等值时刻亦落此语义）
        return false;
    }

    /**
     * 当前认证是否比 other 旧
     *
     * @param other 对比认证
     * @return 比 other 旧返回 true, 否则返回 false
     */
    default boolean isOlderThan(Certificate other) {
        if (this.isAuthenticated() != other.isAuthenticated()) {
            // 未认证即较旧；与 isNewerThan 方向严格互斥
            return !this.isAuthenticated();
        }
        Optional<Instant> thisInstant = this.getAuthenticateAt();
        Optional<Instant> otherInstant = other.getAuthenticateAt();
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
    default boolean isSameUser(Certificate other) {
        if (this == other) {
            return true;
        }
        return Objects.equals(getContactId(), other.getContactId()) &&
               Objects.equals(getContactType(), other.getContactType());
    }

    /**
     * 当前凭证与指定的凭证 other 是否属于同一凭证()
     *
     * @param other 指定凭证
     * @return true 同用户, false 不同用户
     */
    default boolean isSameCertificate(Certificate other) {
        if (this == other) {
            return true;
        }
        return getId() == other.getId() &&
               Objects.equals(getIdentify(), other.getIdentify()) &&
               Objects.equals(getContactId(), other.getContactId()) &&
               Objects.equals(getContactType(), other.getContactType());
    }

}
