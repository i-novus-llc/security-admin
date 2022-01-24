INSERT INTO sec.account(user_id, name, region_id, organization_id, department_id, user_level, external_system,
                        external_uid)
SELECT id,
       username,
       region_id,
       organization_id,
       department_id,
       user_level,
       ext_sys,
       ext_uid
FROM sec."user";

INSERT INTO sec.account_role(account_id, role_id)
SELECT id, role_id
FROM sec.account as acc
         join sec.user_role as ur on (acc.user_id = ur.user_id);