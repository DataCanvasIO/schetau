drop table if exists schetau_task;
drop table if exists schetau_signal_relation;
drop table if exists schetau_signal;
drop table if exists schetau_plan_job;
drop table if exists schetau_plan;
drop table if exists schetau_job;
drop table if exists schetau_activity;
drop table if exists schetau_node;

create table schetau_node (
    node_id char(36) primary key,
    host_address char(15),
    host_name varchar(64)
);

create table schetau_activity (
    seq_no long primary key,
    node_id char(36) not null,
    last_updating_time long not null,
    foreign key (node_id) references schetau_node(node_id) on delete cascade
);

create table schetau_job (
    job_id long primary key auto_increment,
    job_name varchar(64) not null,
    description clob,
    job_type varchar(32) not null,
    execution_info clob
);

create table schetau_plan (
    plan_id long primary key auto_increment,
    plan_name varchar(64) not null,
    first_run_time long not null,
    run_interval long not null default 0,
    expire_time long not null default -1,
    wait_timeout long not null default -1,
    signal_definition clob,
    run_times int not null default 0,
    next_run_time long not null
);

create table schetau_plan_job (
    plan_id long not null,
    job_id long not null,
    primary key (plan_id, job_id),
    foreign key (plan_id) references schetau_plan(plan_id) on delete cascade,
    foreign key (job_id) references schetau_job(job_id) on delete cascade
);

create table schetau_signal (
    signal_id long primary key auto_increment,
    signature char(64) not null unique,
    count_down int not null
);

create table schetau_signal_relation (
    parent_id long not null,
    child_id long not null,
    primary key (parent_id, child_id),
    foreign key (parent_id) references schetau_signal(signal_id) on delete cascade,
    foreign key (child_id) references schetau_signal(signal_id) on delete cascade
);

create table schetau_task (
    task_id long primary key auto_increment,
    plan_id long not null,
    job_id long not null,
    create_time long not null,
    run_times int not null,
    wait_timeout long,
    signal_id long,
    task_status char(32) not null,
    run_time long not null default 0,
    result clob,
    foreign key (plan_id) references schetau_plan(plan_id) on delete cascade,
    foreign key (job_id) references schetau_job(job_id) on delete cascade,
    foreign key (signal_id) references schetau_signal(signal_id) on delete cascade
);
