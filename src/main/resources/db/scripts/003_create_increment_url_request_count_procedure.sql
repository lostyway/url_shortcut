create or replace procedure increment_url_request_count_by_code(
    p_code text
)
    language plpgsql
as
$$
begin
    update urls
    set request_count = request_count + 1
    where code = p_code;
end;
$$;