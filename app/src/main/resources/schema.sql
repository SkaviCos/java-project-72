DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGINT generated by default as identity not null,
    name VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW(),
    constraint pk_url primary key (id)
);
