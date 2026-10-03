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

package com.tny.game.actor.stage;

/**
 * 当fragment完成(无论成功或失败)时阶段完成
 * Created by Kun Yang on 16/1/22.
 */
class ThenDoneStage<R> extends DefaultStage<R> {

    public ThenDoneStage(Object name, Fragment<?, R> fragment) {
        super(name, fragment);
    }

    @Override
    public boolean doCheck(Fragment prev) {
        return prev.isDone();
    }

}
