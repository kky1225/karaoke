CREATE TABLE TJ_POPULAR_SONG (
    no VARCHAR(10) NOT NULL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    singer VARCHAR(200) NOT NULL,
    lyrics VARCHAR(200) NOT NULL,
    music VARCHAR(200) NOT NULL,
    regdate DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TJ_POPULAR_SONG (
    no VARCHAR(10) NOT NULL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    singer VARCHAR(200) NOT NULL,
    lyrics VARCHAR(200) NOT NULL,
    music VARCHAR(200) NOT NULL,
    regdate DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TJ_POPULAR_SONG (
    ranking INT(100) NOT NULL,
    no VARCHAR(10) NOT NULL,
    title VARCHAR(200) NOT NULL,
    singer VARCHAR(200) NOT NULL,
    category VARCHAR(10) NOT NULL,
    year VARCHAR(4) NOT NULL,
    month VARCHAR(2) NOT NULL,
    regdate DATETIME DEFAULT CURRENT_TIMESTAMP
);