CREATE INDEX client_role_client_idx
ON sec.client_role USING btree(client_client_id);

CREATE INDEX client_role_role_idx
ON sec.client_role USING btree(role_id);