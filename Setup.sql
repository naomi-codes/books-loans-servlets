-- SQL to (re)build the MySQL tables for the
--   CSCU9YD library assignment
-- and to populate with some initial data

DROP TABLE Loan ;
DROP TABLE Borrower ;
DROP TABLE Book ;

CREATE TABLE Borrower
  (BorrowerNo  INT PRIMARY KEY,
   Name        VARCHAR(20),
   Department  VARCHAR(20)) ;

CREATE TABLE Book
  (BookNo      INT PRIMARY KEY,
   Author      VARCHAR(20),
   Title       VARCHAR(40)) ;

CREATE TABLE Loan
  (BorrowerNo  INT REFERENCES Borrower,
   BookNo      INT REFERENCES Book,
   Date_out    DATE,
   Dispatched  CHAR(1) DEFAULT 'N',
   Date_back   DATE) ;

INSERT INTO Book VALUES (320,'Austen','Northanger Abbey') ;
INSERT INTO Book VALUES (321,'Austen','Northanger Abbey') ;
INSERT INTO Book VALUES (543,'Grass','The Tin Drum') ;
INSERT INTO Book VALUES (654,'Mitchell','Gone with the wind') ;
INSERT INTO Book VALUES (765,'Proulx','The Shipping News') ;
INSERT INTO Book VALUES (986,'Powell','Books Do Furnish A Room') ;
INSERT INTO Book VALUES (987,'Dickens','Barnaby Rudge') ;

INSERT INTO Borrower VALUES (123,'Smith','CS and M') ;
INSERT INTO Borrower VALUES (456,'Jones','Marketing') ;
INSERT INTO Borrower VALUES (789,'Robinson','Film and Media') ;
INSERT INTO Borrower VALUES (890,'Wilson','Film and Media') ;

-- start here to reset loans to initial state
DELETE FROM Loan ;
INSERT INTO Loan VALUES (789,321,'2019-09-21','Y','2019-09-24') ;
INSERT INTO Loan VALUES (123,986,'2019-10-12','Y','2019-10-20') ;
INSERT INTO Loan (BorrowerNo, BookNo, Date_out, Dispatched) 
                 VALUES (789,543,'2019-10-01','Y') ;
INSERT INTO Loan (BorrowerNo, BookNo, Date_out, Dispatched) 
                 VALUES (123,654,'2019-09-25','N') ;

COMMIT ;
