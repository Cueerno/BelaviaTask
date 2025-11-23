create or replace function calc_sum_and_median()
    returns table
            (
                even_number_sum bigint,
                median          numeric
            )
    language plpgsql
as
$$
begin
    return query
        select sum(even_int)                                                         even_number_sum,
               percentile_cont(0.5) within group ( order by decimal_value )::numeric median
        from random_records;
end;
$$;