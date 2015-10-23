USE [master]

IF NOT EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'data_test') 
	PRINT 'Can Not Find database, Failure AH501! '
ELSE
	USE data_test
	IF (EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo' AND  TABLE_NAME = 'server_instance'))
	BEGIN
		SELECT * FROM data_test.dbo.server_instance
		PRINT 'read table successful, SUCESS '
	END
	ELSE
		PRINT 'Can not Find tables, Failure AH502! '