BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "items" (
	"SKU"	BIGINT NOT NULL UNIQUE,
	"SN"	STRING UNIQUE,
	"PN"	STRING,
	"UPC"	STRING,
	"Grade"	STRING,
	"Location"	STRING,
	"Notes"	STRING,
	"User"	STRING,
	"DateTime"	DATETIME,
	"Images"	STRING,
	"OtherRecords"	STRING,
	PRIMARY KEY("SKU")
);
INSERT INTO "items" ("SKU","SN","PN","UPC","Grade","Location","Notes","User","DateTime","Images","OtherRecords") VALUES (23456667,'234DD4','lol',2345,'B',NULL,'lol','User 1',NULL,NULL,NULL),
 (2333667,'2666D4',23456,2345,'A','somewhere','lol','User 1','2016-12-21 01:01:00.0','somepath','someSKUs'),
 (234,'bb','tb',NULL,NULL,NULL,'newnotes','undefined user','2022-05-12 15:41:07.042',NULL,NULL),
 (123478,'fffffff',NULL,NULL,NULL,NULL,'vrvrtvrt','undefined user','2022-05-12 16:05:09.145',NULL,NULL),
 (666,666,NULL,NULL,NULL,NULL,'mmmmm','undefined user','2022-05-13 09:54:26.943',NULL,NULL),
 (6666,6666,NULL,NULL,NULL,NULL,NULL,'undefined user','2022-05-13 11:13:02.246',NULL,NULL),
 (4,4,4,4,4,4,4,'undefined user','2022-05-13 14:11:42.64',NULL,NULL),
 (23,'f','lol','f','f','f','olohh','undefined user','2022-05-13 15:00:20.587',NULL,NULL),
 (222222,456,'grgrg',NULL,NULL,NULL,'btbtbrgtrg','undefined user','2022-05-13 15:05:55.422',NULL,NULL),
 (555,5,NULL,NULL,NULL,NULL,5,'undefined user','2022-05-13 18:00:06.117',NULL,NULL),
 (1111,NULL,NULL,NULL,NULL,NULL,'lol','undefined user','2022-05-16 08:58:34.787',NULL,NULL),
 (44,NULL,NULL,NULL,NULL,NULL,'grtgtgrt','undefined user','2022-05-16 09:34:29.893',NULL,NULL),
 (34444,'v',NULL,NULL,NULL,NULL,'rrgrg','undefined user','2022-05-16 17:39:46.155',NULL,NULL),
 (3444,'fds',NULL,NULL,NULL,NULL,'gggg','undefined user','2022-05-16 17:41:59.259',NULL,NULL),
 (1,'fdf','dsdf',NULL,NULL,NULL,'ffrfefef','undefined user','2022-05-17 11:38:14.324',NULL,NULL),
 (15,NULL,'ntb',NULL,'bbrebtr',NULL,'trbtbdrtetbre','undefined user','2022-05-17 12:08:57.286',NULL,NULL),
 (16,44444444,'grvgr',NULL,NULL,NULL,'vgtr','undefined user','2022-05-17 12:10:18.055',NULL,NULL);
CREATE UNIQUE INDEX IF NOT EXISTS "SN_indx" ON "items" (
	"SN"
);
COMMIT;
