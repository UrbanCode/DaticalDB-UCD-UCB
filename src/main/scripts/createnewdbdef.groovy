import com.urbancode.air.CommandHelper;

def inputPropsFile = new File(args[0])
def outputPropsFile = new File(args[1])

def props = new Properties()
try {
	props.load(new FileInputStream(inputPropsFile))
}
catch (IOException e) {
	throw new RuntimeException(e)
}

def cwd = new File('.');
def cmdHelper = new CommandHelper(cwd);



//--------------------------------------------------------------------------------------------------
def getAbsPath(def file) {
	def tempFile = null;
	if (file != null && file != "") {
		File temporaryFile = new File(file);
		tempFile = temporaryFile.getAbsolutePath();
	}
	return tempFile;
}
//path properties
def daticalDBCmd = getAbsPath(props['daticalDBCmd']);
def daticalDBDriversDir = getAbsPath(props['daticalDBDriversDir']);
def daticalDBProjectDir = getAbsPath(props['daticalDBProjectDir']);
def daticalDBAction = "newDBDef";

def daticalDBVendor = props['daticalDBVendor'];
def daticalDBRef = "";

if (daticalDBVendor == "Oracle") {
	daticalDBRef = "OracleDbDef";
} else if (daticalDBVendor == "MSSQL") {
	daticalDBRef = "SqlServerDbDef";
} else if (daticalDBVendor == "MySQL") {
	daticalDBRef = "MysqlDbDef";
} else if (daticalDBVendor == "PostgreSQL") {
	daticalDBRef = "PostgresqlDbDef";
} else if (daticalDBVendor == "DB2"){
	daticalDBRef = "Db2DbDef";
} else {
	// throw an error
	// return new ActionResult(false, "Invalid value for Datical DB Vendor: " + daticalDBVendor.toString());
}

def daticalDBDatabaseName = props['daticalDBDatabaseName'];
if (daticalDBVendor == ("MySQL") || daticalDBVendor == "PostgreSQL" || daticalDBVendor == "DB2" || daticalDBVendor == "MSSQL") {
	if (daticalDBDatabaseName == "" || daticalDBDatabaseName == null) {
		// throw an error
		// return new ActionResult(false, "For Database Vendor " + daticalDBVendor.toString() + ", Datical DB Database Name is required.");
	}
}

def daticalDBServiceName = props['daticalDBServiceName'];
def daticalDBSID = props['daticalDBSID'];
if (daticalDBVendor.equals("Oracle")) {
	if (daticalDBSID == "" || daticalDBSID == null) {
		if (daticalDBServiceName == "" || daticalDBServiceName == null) {
			// throw error
			//return new ActionResult(false, "For Database Vendor " + daticalDBVendor.toString() + ", SID or Service Name is required.");
		}
	}
}

def cmdArgs = [];


cmdArgs << daticalDBCmd;
cmdArgs << "--project";
cmdArgs << daticalDBProjectDir;
cmdArgs << "newDbDef";
cmdArgs << "DbDefClass";
cmdArgs << daticalDBRef;
cmdArgs << "name";
cmdArgs << props['daticalDBStepName'];
cmdArgs << "username";
cmdArgs << props['daticalDBUsername'];
cmdArgs << "password";
cmdArgs << props['daticalDBPassword'];
cmdArgs << "hostname";
cmdArgs << props['daticalDBHost'];
cmdArgs << "port";
cmdArgs << props['daticalDBPort'];

if (daticalDBDatabaseName) {
	if (daticalDBVendor == "MSSQL") {
		cmdArgs << "databaseName";
	} else {
		cmdArgs << "database";
	}
	cmdArgs << daticalDBDatabaseName;
}

if (daticalDBSID) {
	cmdArgs << "sid";
	cmdArgs << daticalDBSID;
}

if (daticalDBServiceName) {
	cmdArgs << "serviceName";
	cmdArgs << daticalDBServiceName;
}

def daticalDBIsIntegratedSecurity = props['daticalDBIsIntegratedSecurity'];
if (daticalDBIsIntegratedSecurity) {
	if (daticalDBIsIntegratedSecurity != null || daticalDBIsIntegratedSecurity != "") {
		cmdArgs << "isIntegratedSecurity";
		cmdArgs << daticalDBIsIntegratedSecurity.toString().toLowerCase();
	}
}

def daticalDBApplicationName = props['daticalDBApplicationName'];
if (daticalDBApplicationName) {
	cmdArgs << "applicationname";
	cmdArgs << daticalDBApplicationName;
}

def daticalDBContext = props['daticalDBContext'];
if (daticalDBContext) {
	cmdArgs << "context";
	cmdArgs << daticalDBContext;
}

def daticalDBDefaultCatalogName = props['daticalDBDefaultCatalogName'];
if (daticalDBDefaultCatalogName) {
	cmdArgs << "defaultCatalogName";
	cmdArgs << daticalDBDefaultCatalogName;
}

def daticalDBDefaultSchemaName = props['daticalDBDefaultSchemaName'];
if (daticalDBDefaultSchemaName) {
	cmdArgs << "defaultSchemaName";
	cmdArgs << daticalDBDefaultSchemaName;
}

def daticalDBInstanceName = props['daticalDBInstanceName'];
if (daticalDBInstanceName) {
	cmdArgs << "instancename";
	cmdArgs << daticalDBInstanceName;
}

def daticalDBvm = props['daticalDBvm'];
if (daticalDBvm) {
	cmdArgs << "--vm";
	cmdArgs << daticalDBvm;
}
def daticalDBvmargs = props['daticalDBvmargs'];
if (daticalDBvmargs) {
	cmdArgs << "--vmargs";
	String[] myArray = daticalDBvmargs.split();
	for ( x in myArray ) {
		cmdArgs << x;
	}
	//cmdArgs << daticalDBvmargs;
}

int exitCode = cmdHelper.runCommand("Executing Datical DB", cmdArgs);

System.exit(exitCode);
