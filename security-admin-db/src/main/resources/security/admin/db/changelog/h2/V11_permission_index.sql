CREATE INDEX role_permission_permission_idx
ON sec.role_permission (permission_code);

CREATE INDEX permission_parent_idx
ON sec.permission (parent_code);