local storeKey = KEYS[1]
local messageId = ARGV[1]

local removedUnack = redis.call('ZREM', storeKey + '.unack', messageId)
if (removedUnack >= 1) then
    redis.call('HDEL', storeKey + '.messages', messageId)
    return true
end
return false