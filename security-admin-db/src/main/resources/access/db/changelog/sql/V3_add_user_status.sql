ALTER TABLE sec.user
  ADD status character varying;

UPDATE sec.user set status = 'REGISTERED'