CREATE TABLE execution_order (
    id BIGSERIAL PRIMARY KEY,
    os_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    queued_at TIMESTAMP WITH TIME ZONE,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE execution_item (
    id BIGSERIAL PRIMARY KEY,
    execution_order_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    applied_price NUMERIC(19, 2) NOT NULL,
    status VARCHAR(40) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_execution_item_order
        FOREIGN KEY (execution_order_id) REFERENCES execution_order(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_execution_order_status_queued
    ON execution_order(status, queued_at);

CREATE INDEX idx_execution_item_order
    ON execution_item(execution_order_id);
