# DC schema
 
# --- !Ups


CREATE TABLE PROJECT (
    ID integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME varchar(255) NOT NULL
);


CREATE TABLE TASK (
    ID integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
    COLOR varchar(255) NOT NULL,
    STATUS varchar(255) NOT NULL,
    PROJECT integer NOT NULL,
    FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID)
);


 
# --- !Downs

DROP TABLE TASK;
DROP TABLE PROJECT;
