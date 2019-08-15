INSERT INTO sec.oauth_client_details (
                                     client_id,
                                     client_secret,
                                     authorized_grant_types,
                                     "scope",
                                     auto_approve_scope,
                                     access_token_validity_seconds,
                                     registered_redirect_uri)
       VALUES (
              'authCodeClient',
              'authCodeClientSecret',
              'authorization_code',
              'read,write',
              TRUE,
              3600,
              'http://localhost:9999/client, http://localhost:9999/client/login,
 http://localhost:8081/admin/login, http://localhost:8081/admin'),
('clientCredentialsClient',
'clientCredentialsClientSecret',
'client_credentials',
'read, write',
TRUE,
3600,
NULL) ;
