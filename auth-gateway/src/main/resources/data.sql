INSERT INTO sec.client (
                                     client_id,
                                     client_secret,
                                     authorized_grant_types,
                                     access_token_validity_seconds,
                                     refresh_token_validity_seconds,
                                     registered_redirect_uri,
                                     logout_url)
       VALUES (
              'authCodeClient',
              'authCodeClientSecret',
              'authorization_code',
              3600,3600,
              'http://localhost:9999/client, http://localhost:9999/client/login,
 http://localhost:8081/admin/login, http://localhost:8081/admin', 'logout URL HERE'),
('clientCredentialsClient',
'clientCredentialsClientSecret',
'client_credentials',
3600,3600,
NULL,'logout URL HERE') ;
