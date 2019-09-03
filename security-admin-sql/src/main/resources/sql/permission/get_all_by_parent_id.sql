select  p.name, p.code, p.parent_code
from sec.permission p where parent_code = :parent_code;