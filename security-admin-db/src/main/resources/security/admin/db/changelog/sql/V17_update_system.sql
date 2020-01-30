ALTER TABLE sec.system ADD COLUMN show_on_interface boolean;
COMMENT ON COLUMN sec.system.show_on_interface IS 'Отображение систем в едином интерфейсе';