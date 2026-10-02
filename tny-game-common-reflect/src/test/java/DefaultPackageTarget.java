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

/**
 * 默认包（无包名）fixture——供「无包名类请求显式失败（边界路径）」场景经 Class.forName
 * 触达 WrapperProxyFactory 的无包名分支（匿名类分支已由既有钉桩覆盖，勿混用）。
 */
public class DefaultPackageTarget {

    public int value() {
        return 1;
    }
}
