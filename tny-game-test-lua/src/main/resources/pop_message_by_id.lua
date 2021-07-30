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
