insert into market.stock(id, symbol, name, market, tag) values
    (1,'AAPL','Apple Inc.','US','미국'),
    (2,'MSFT','Microsoft Corporation','US','미국'),
    (3,'NVDA','NVIDIA Corporation','US','미국')
    on conflict do nothing;

select setval(pg_get_serial_sequence('market.stock','id'), (select max(id) from market.stock));
