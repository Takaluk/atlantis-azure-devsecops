alter table usr.app_user
    add column if not exists password_hash varchar(255);
