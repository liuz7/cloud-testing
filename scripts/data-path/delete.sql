USE [master]

IF NOT EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'data_test') 
PRINT 'Database does not exists!'
ELSE
USE data_test
IF (EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo' AND  TABLE_NAME = 'server_instance'))
	BEGIN
		DROP TABLE dbo.server_instance
		PRINT 'table drop successful, SUCCESS '
	END
ELSE
	PRINT 'server_instance table does not existed!'