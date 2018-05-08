select p.id, p.name, p.code, p.parent_id
from sec.permission p where id = :id;