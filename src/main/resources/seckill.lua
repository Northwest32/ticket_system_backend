local stockKey = KEYS[1]
local userKey = KEYS[2]
local userId = ARGV[1]
local qty     = tonumber(ARGV[2])
local stock   = tonumber(redis.call('GET', stockKey))
if not stock or stock < qty then return -1 end
local bought = tonumber(redis.call('HGET', userKey, userId) or '0')
if bought + qty > 3 then return -2 end
redis.call('DECRBY', stockKey, qty)
redis.call('HINCRBY', userKey, userId, qty)
return 1
