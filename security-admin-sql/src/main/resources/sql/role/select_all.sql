select *
from sec.role r
where (:name is null or (trim(lower(name))) = (trim(lower(:name)))) and
(:description is null or (trim(lower(description))) = (trim(lower(:description))));