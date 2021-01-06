insert into schetau_node select * from csvread('classpath:/db/data/node.csv');

insert into schetau_activity select * from csvread('classpath:/db/data/activity.csv');

insert into schetau_job select * from csvread('classpath:/db/data/job.csv');

insert into schetau_plan select * from csvread('classpath:/db/data/plan.csv');

insert into schetau_plan_job select * from csvread('classpath:/db/data/plan_job.csv');

insert into schetau_signal select * from csvread('classpath:/db/data/signal.csv');

insert into schetau_signal_relation select * from csvread('classpath:/db/data/signal_relation.csv');

insert into schetau_task select * from csvread('classpath:/db/data/task.csv');
