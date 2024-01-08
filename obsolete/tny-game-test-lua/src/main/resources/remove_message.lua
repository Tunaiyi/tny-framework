local storeKey = KEYS[1]
local messageId = ARGV[1]
local message = ARGV[2]
local time = ARGV[3]

if (redis.call('ZREM', storeKey + '.unack', messageId) <= 0) then
    return false
end
redis.call('HDEL', storeKey + '.messages', messageId)
return 0