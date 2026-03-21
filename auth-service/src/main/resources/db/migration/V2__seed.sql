insert into usr.app_user(id, email, name)
values (1, 'demo@stocklens.local', 'demo')
    on conflict do nothing;

select setval(pg_get_serial_sequence('usr.app_user','id'), (select max(id) from usr.app_user));
