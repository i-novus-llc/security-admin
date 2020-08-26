ALTER TABLE sec."user" ADD COLUMN last_action_date timestamp default (now() at time zone 'utc');
CREATE INDEX last_action_date_idx on sec."user"(last_action_date);