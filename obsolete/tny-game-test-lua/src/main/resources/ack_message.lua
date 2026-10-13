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

local removedUnack = redis.call('ZREM', storeKey + '.unack', messageId)
if (removedUnack >= 1) then
    redis.call('HDEL', storeKey + '.messages', messageId)
    return true
end
return false