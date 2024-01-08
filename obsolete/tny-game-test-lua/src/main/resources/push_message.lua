local storeKey = KEYS[1]
local messageId = ARGV[1]
local message = ARGV[2]
local time = ARGV[3]
redis.call('HSET', storeKey + '.messages', messageId, message)
redis.call('ZADD', storeKey + '.queue', time, messageId)
return 1