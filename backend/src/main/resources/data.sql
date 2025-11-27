-- 建表（DDL） + 种子数据（DML）合并版本。仅用于演示/开发。生产建议使用版本化迁移（Flyway）。

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    openid
    VARCHAR
(
    128
) UNIQUE,
    nickname VARCHAR
(
    128
),
    avatar VARCHAR
(
    512
),
    phone VARCHAR
(
    32
),
    role VARCHAR
(
    32
) DEFAULT 'member',
    level INT DEFAULT 1,
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS trainers
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    name
    VARCHAR
(
    128
) NOT NULL,
    bio TEXT,
    avatar VARCHAR
(
    512
),
    rate_per_hour INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS classes
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    title
    VARCHAR
(
    255
) NOT NULL,
    description TEXT,
    trainer_id BIGINT,
    start_time DATETIME,
    end_time DATETIME,
    capacity INT,
    price INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_class_trainer FOREIGN KEY
(
    trainer_id
) REFERENCES trainers
(
    id
) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bookings
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL,
    class_id
    BIGINT
    NOT
    NULL,
    status
    VARCHAR
(
    32
) DEFAULT 'booked',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
) ON DELETE CASCADE,
    CONSTRAINT fk_booking_class FOREIGN KEY
(
    class_id
) REFERENCES classes
(
    id
)
  ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS coupons
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    code
    VARCHAR
(
    64
) UNIQUE,
    title VARCHAR
(
    255
),
    type VARCHAR
(
    32
), -- amount | percent
    value INT, -- 分 或 折扣百分比(比如 10 表示 9折)
    min_spend INT DEFAULT 0,
    total INT,
    claimed INT DEFAULT 0,
    valid_from DATETIME,
    valid_to DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_coupons
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL,
    coupon_id
    BIGINT
    NOT
    NULL,
    status
    VARCHAR
(
    32
) DEFAULT 'unused',
    claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at DATETIME,
    CONSTRAINT fk_uc_user FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
) ON DELETE CASCADE,
    CONSTRAINT fk_uc_coupon FOREIGN KEY
(
    coupon_id
) REFERENCES coupons
(
    id
)
  ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orders
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    order_no
    VARCHAR
(
    64
) UNIQUE,
    user_id BIGINT NOT NULL,
    subject VARCHAR
(
    255
),
    amount INT NOT NULL,
    status VARCHAR
(
    32
) DEFAULT 'unpaid',
    coupon_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME,
    extra JSON,
    CONSTRAINT fk_order_user FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
) ON DELETE CASCADE,
    CONSTRAINT fk_order_coupon_user FOREIGN KEY
(
    coupon_user_id
) REFERENCES user_coupons
(
    id
)
  ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 种子数据
INSERT INTO trainers (name, bio, avatar, rate_per_hour)
VALUES ('Alex', '力量与体能教练', '', 30000),
       ('Bella', '普拉提与瑜伽', '', 28000),
       ('Chris', '减脂与营养', '', 26000);

INSERT INTO classes (title, description, trainer_id, start_time, end_time, capacity, price)
VALUES ('燃脂团课', '高强度间歇训练', 1, DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 2 HOUR), 20, 9900),
       ('瑜伽舒展', '放松与柔韧', 2, DATE_ADD(NOW(), INTERVAL 3 HOUR), DATE_ADD(NOW(), INTERVAL 4 HOUR), 15, 7900),
       ('力量基础', '杠铃基础动作', 1, DATE_ADD(NOW(), INTERVAL 5 HOUR), DATE_ADD(NOW(), INTERVAL 6 HOUR), 12, 9900);

INSERT INTO coupons (code, title, type, value, min_spend, total, claimed, valid_from, valid_to)
VALUES ('WELCOME20', '新人立减20', 'amount', 2000, 5000, 1000, 0, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
       ('SAVE10', '课程9折', 'percent', 10, 0, 1000, 0, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY));