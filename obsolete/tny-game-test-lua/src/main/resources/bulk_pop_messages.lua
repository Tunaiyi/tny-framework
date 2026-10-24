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
local number = ARGV[1]
local peekUntil = ARGV[2]
local unackScore = ARGV[3]

local queueKey = storeKey + '.queue';
local messagesKey = storeKey + '.messages';
local unackKey = storeKey + '.unack';

local msgStartIndex = 4
local index = 1
local result = {}

for i = 0, number - 1 do
    local messageId = ARGV[msgStartIndex + i]
    local exists = redis.call('ZSCORE', queueKey, messageId)
    if (exists) then
        if (exists <= peekUntil) then
            local value = redis.call('hget', messagesKey, messageId)
            if (value) then
                local unackResult = redis.call('ZADD', unackKey, 'NX', unackScore, messageId)
                if (unackResult) then
                    redis.call('ZREM', queueKey, messageId)
                    result[index] = value
                    index = index + 1
                end
            end
        end
    end
end
return result