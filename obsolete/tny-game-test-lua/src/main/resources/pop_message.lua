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
local messageId = ARGV[1]
local unackTime = ARGV[2]

if (redis.call('ZADD', storeKey + '.unack', "NX", messageId) <= 0) then
    return nil;
end

if (redis.call('ZREM', storeKey + '.queue', messageId) <= 0) then
    return nil;
end

return redis.call('HGET', storeKey + '.messages', messageId)