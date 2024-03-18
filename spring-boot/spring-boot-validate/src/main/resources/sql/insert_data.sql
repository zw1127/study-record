insert into validation_rule (rule_type, pojo_name, field_name, validation_rule)
values ('type1', 'User', 'userNo', 'Size(min=1,max=100)'),
       ('type2', 'User', 'userNo', 'Size(min=200,max=300)'),
       ('type1', 'User', 'age', 'Size(min=6,max=12)'),
       ('type2', 'User', 'age', 'Size(min=18,max=22)'),
       ('type1', 'User', 'password', 'Pattern(regexp="^(?=.[0-9])(?=.[!@#$%^&*()])(?=\S+$).{8,}$")');