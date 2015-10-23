#function Invoke-MyTest 
#{ 
#    [CmdletBinding()] 
    param( 
    [Parameter(Position=0, Mandatory=$true)] [string]$ServerInstance, 
    [Parameter(Position=1, Mandatory=$false)] [string]$Database, 
    [Parameter(Position=2, Mandatory=$false)] [string]$Query, 
    [Parameter(Position=2, Mandatory=$false)] [string]$tableName,    
    [Parameter(Position=3, Mandatory=$false)] [string]$Username, 
    [Parameter(Position=4, Mandatory=$false)] [string]$Password, 
    [Parameter(Position=5, Mandatory=$false)] [Int32]$QueryTimeout=600, 
    [Parameter(Position=6, Mandatory=$false)] [Int32]$ConnectionTimeout=15, 
    [Parameter(Position=7, Mandatory=$false)] [ValidateScript({test-path $_})] [string]$InputFile, 
    [Parameter(Position=8, Mandatory=$false)] [ValidateSet("DataSet", "DataTable", "DataRow")] [string]$As="DataRow" 

    ) 

    if ($InputFile) 
    { 
        $filePath = $(resolve-path $InputFile).path 
        $Query =  [System.IO.File]::ReadAllText("$filePath") 
    } 

    $conn=new-object System.Data.SqlClient.SQLConnection 

    if ($Username) 
    { $ConnectionString = "Server=$ServerInstance;Database=$Database;User ID=$Username;Password=$Password;Trusted_Connection=False;Connect Timeout=$ConnectionTimeout" } 
    else 
    { $ConnectionString = "Server=$ServerInstance;Database=$Database;Integrated Security=True;Connect Timeout=$ConnectionTimeout" } 

    $conn.ConnectionString=$ConnectionString 

    #Following EventHandler is used for PRINT and RAISERROR T-SQL statements. Executed when -Verbose parameter specified by caller 

    if ($PSBoundParameters.Verbose) 
    { 
        $conn.FireInfoMessageEventOnUserErrors=$true 
        $handler = [System.Data.SqlClient.SqlInfoMessageEventHandler] {Write-Verbose "$($_)"} 
        $conn.add_InfoMessage($handler) 
    } 

    $conn.Open() 


    # Table 
    if (!$tableName) 
    {
        $tableName = "data_test"
    }

    $path = $MyInvocation.MyCommand.Definition
    $path = Split-Path -Parent $MyInvocation.MyCommand.Definition

    switch ($Query) 
    { 
        'Create' { 
            $InputFile = "$path\create.sql"
            $filePath = $(resolve-path $InputFile).path 
            $Query =  [System.IO.File]::ReadAllText("$filePath") 
            $Result = "create_result.txt"
            #{ return "Data Path Test - Creating test Passed!" }
        }
        'Read'   { 
            $InputFile = "$path\read.sql"
            $filePath = $(resolve-path $InputFile).path 
            $Query =  [System.IO.File]::ReadAllText("$filePath") 
            $Result = "read_result.txt"
        }  
        'Update' { 
            $InputFile = "$path\update.sql"
            $filePath = $(resolve-path $InputFile).path 
            $Query =  [System.IO.File]::ReadAllText("$filePath") 
            $Result = "update_result.txt"
        }
        'Delete'  { 
            $InputFile = "$path\delete.sql"
            $filePath = $(resolve-path $InputFile).path 
            $Query =  [System.IO.File]::ReadAllText("$filePath")
            $Result = "delete_result.txt"
        }
        
    }

    $cmd=new-object system.Data.SqlClient.SqlCommand($Query,$conn) 
    $cmd.CommandTimeout=$QueryTimeout 
    $ds=New-Object system.Data.DataSet 
    $da=New-Object system.Data.SqlClient.SqlDataAdapter($cmd) 

    [void]$da.fill($ds) 
    $conn.Close() 

    switch ($As) 
    { 
        'DataSet'   { Write-Output ($ds) } 
        'DataTable' { Write-Output ($ds.Tables) } 
        'DataRow'   { Write-Output ($ds.Tables[0]) } 
    } 

     @($ds) | Out-File "$path\$Result"
    $res = (Get-Content $path\$Result | Select-String "HasErrors" ).ToString().Split(":")[1]

    if ($res -match 'False') 
    {
    	Write-Host "Data Path Test Successful!"
    } 
    else 
    {
    	Write-Host "Data Path Test Failed !"
    }


#}