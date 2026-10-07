-- Copyright (c) 2020 Tunaiyi
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
local storeKey = KEYS[1]
local unacksNum = ARGV[2]

local queueKey = storeKey + '.queue';
local unackKey = storeKey + '.unack';
local messagesKey = storeKey + '.messages';

local unacks = {}
local unackScores = {}
local unackStartIdx = 3

for i = 0, unacksNum - 1 do
    unacks[i] = ARGV[unackStartIdx + (i * 2)]
    unackScores[i] = ARGV[unackStartIdx + (i * 2) + 1]
end

local added = 0
local removed = 0
for i = 0, unacksNum - 1 do
    local memVal = redis.call('hget', messagesKey, unacks[i])
    if (memVal) then
        redis.call('zadd', queueKey, unackScores[i], unacks[i])
        redis.call('zrem', unackKey, unacks[i])
        added = added + 1
    else
        redis.call('zrem', unackKey, unacks[i])
        removed = removed + 1
    end
end

return { added, removed }