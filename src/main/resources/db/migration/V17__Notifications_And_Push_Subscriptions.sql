ALTER TABLE user_preference
    ADD COLUMN notify_connection_requests BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN notify_shared_expenses BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN notify_financial_digest BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE notifications(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body VARCHAR(500) NOT NULL,
    url VARCHAR(255) NOT NULL,
    resource_type VARCHAR(100) DEFAULT NULL,
    resource_id INT DEFAULT NULL,
    dedupe_key VARCHAR(255) DEFAULT NULL,
    read_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX uk_notifications_dedupe_key
    ON notifications(dedupe_key);

CREATE INDEX idx_notifications_user_created_at
    ON notifications(user_id, created_at);

CREATE INDEX idx_notifications_user_read_at
    ON notifications(user_id, read_at);

CREATE INDEX idx_notifications_created_at
    ON notifications(created_at);

CREATE TABLE push_subscriptions(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    p256dh VARCHAR(500) NOT NULL,
    auth VARCHAR(255) NOT NULL,
    expiration_time DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX uk_push_subscriptions_endpoint
    ON push_subscriptions(endpoint);

CREATE INDEX idx_push_subscriptions_user_id
    ON push_subscriptions(user_id);
