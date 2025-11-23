create table if not exists random_records
(
    id            bigserial primary key,
    date          date        not null,
    latin         varchar(10) not null,
    cyrillic      varchar(10) not null,
    even_int      int         not null,
    decimal_value numeric(20, 8)
);
