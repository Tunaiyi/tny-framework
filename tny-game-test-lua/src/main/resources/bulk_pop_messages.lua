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