local storeKey = KEYS[1]
local messageId = ARGV[1]
local message = ARGV[2]
local time = ARGV[3]

if (redis.call('ZSCORE', storeKey + '.queue', messageId) ~= nil) then
    return 0;
end
if (redis.call('ZSCORE', storeKey + '.unack', messageId) ~= nil) then
    return 0;
end

redis.call('HSET', storeKey + '.messages', messageId, message)
redis.call('ZADD', storeKey + '.queue', time, messageId)
return 1;