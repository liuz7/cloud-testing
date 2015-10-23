USE [master]

IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'data_test') 
USE data_test

IF IS_MEMBER ('db_owner') = 1
   PRINT 'Current user is a member of the db_owner role'
ELSE IF IS_MEMBER ('db_owner') = 0
   PRINT 'Current user is NOT a member of the db_owner role'
ELSE IF IS_MEMBER ('db_owner') IS NULL
   PRINT 'ERROR: Invalid group / role specified';

IF ( EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo' AND  TABLE_NAME = 'server_instance'))
BEGIN

INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('120.100.100.0', 'new test1')
INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('188.189.88.888', 'new test2')
INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('100.111.11.11', 'update')
UPDATE  [dbo].[server_instance] 
	SET [server_instance_ip] = '99.99.99.99', 
	    [server_instance_name] = 'fan_updated_instance'
	WHERE [server_instance_name] = 'update'   
	PRINT 'create successful, SUCCESS '
END
