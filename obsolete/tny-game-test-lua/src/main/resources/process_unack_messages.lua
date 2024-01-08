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