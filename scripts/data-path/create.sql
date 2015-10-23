USE [master]

IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'data_test') 
	BEGIN
		USE data_test

		IF IS_MEMBER ('db_owner') = 1
			PRINT 'Current user is a member of the db_owner role'
		ELSE IF IS_MEMBER ('db_owner') = 0
			PRINT 'Current user is NOT a member of the db_owner role'
		ELSE IF IS_MEMBER ('db_owner') IS NULL
			PRINT 'ERROR: Invalid group / role specified';

		IF (NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo' AND  TABLE_NAME = 'server_instance'))
		BEGIN
			CREATE TABLE server_instance 
			(
				[server_instance_ip] [varchar](255) NOT NULL,
				[server_instance_name] [varchar](255) NOT NULL,
			)
 
			INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('107.189.88.120', 'SRE_VDC1')
			INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('107.189.88.123', 'SRE_VDC2')
			INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('100.65.192.252', 'fan_test_instance')
			PRINT 'create successful, SUCCESS'
		END

	END
	ELSE 
		CREATE DATABASE data_test
		USE data_test
		IF (NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo' AND  TABLE_NAME = 'server_instance'))
		BEGIN
			CREATE TABLE server_instance 
			(
				[server_instance_ip] [varchar](255) NOT NULL,
				[server_instance_name] [varchar](255) NOT NULL,
			)
 
			INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('107.189.88.120', 'SRE_VDC1')
			INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('107.189.88.123', 'SRE_VDC2')
			INSERT  [dbo].[server_instance] ([server_instance_ip], [server_instance_name]) VALUES ('100.65.192.252', 'fan_test_instance')
			PRINT 'create successful, SUCCESS'
		END