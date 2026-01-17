CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL
);

CREATE TABLE products
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255)   NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT            NOT NULL
);

CREATE TABLE stock_history
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id    BIGINT      NOT NULL,
    change_amount INT         NOT NULL,
    operation     VARCHAR(20) NOT NULL,
    timestamp     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
