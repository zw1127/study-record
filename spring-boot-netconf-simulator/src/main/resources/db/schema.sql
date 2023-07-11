CREATE SCHEMA IF NOT EXISTS simulator;

CREATE TABLE IF NOT EXISTS simulator.simulator_config
(
    id         int          NOT NULL AUTO_INCREMENT,
    device_id  varchar(255) NOT NULL,
    node_name  varchar(255) NOT NULL,
    node_value clob         NOT NULL,
    PRIMARY KEY (id),
    constraint uk_configinfo unique (device_id, node_name)
);