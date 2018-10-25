select u.*, null as ids, null as names
from sec.user u
where u.username = :username