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
def daticalDBUsername = props['daticalDBUsername'];
def daticalDBPassword = props['daticalDBPassword'];
def daticalDBDriversDir = getAbsPath(props['daticalDBDriversDir']);
def daticalDBPipeline = props['daticalDBPipeline'];
def daticalDBProjectDir = getAbsPath(props['daticalDBProjectDir']);
def daticalImmutableProject = props['daticalImmutableProject'];
def daticalProjectName = props['daticalProjectName'];
def daticalDBAction = "deploy";
def daticalDBServer = props['daticalDBServer'];
def daticalDBContext = props['daticalDBContext'];
def daticalDBRollback = props['daticalDBRollback']
def daticalDBExportSQL = props['daticalDBExportSQL'];
def daticalDBExportRollbackSQL = props['daticalDBExportRollbackSQL'];
def daticalDBLabels = props['daticalDBLabels'];
def daticalServiceUsername = props['daticalServiceUsername'];
def daticalService = props['daticalService'];


if (daticalDBRollback == "false") {
    daticalDBAction = "deploy";
} else {
	daticalDBAction = "deploy-autoRollback";
}

// START building the CLI args.  Start with the pointer to hammer
def cmdArgs = [daticalDBCmd]; 

//Check for Datical Service Specific Properties nd BUild the Appropriate Command Line
if (daticalService && daticalServiceUsername) {
	cmdArgs << "--daticalServer=" + daticalService;
	cmdArgs << "--daticalUsername=" + daticalServiceUsername;
}

// Set the immutableProject flag if needed
if (daticalImmutableProject) {
	cmdArgs << "--immutableProject=" + daticalImmutableProject;
}

if (daticalProjectName){
	cmdArgs << "--projectKey=" + daticalProjectName;
}

// Add driver location and project directory
cmdArgs << '--drivers';
cmdArgs << daticalDBDriversDir;
cmdArgs << '--project';
cmdArgs << daticalDBProjectDir;

// Handle SQL Exports 
if (daticalDBExportSQL == "true") {
	cmdArgs << '--genSQL';
}	

if (daticalDBExportRollbackSQL == "true") {
	cmdArgs <<  '--genRollbackSQL';
} 

if (daticalDBUsername) {
	def usernameString = daticalDBContext + ":::" + daticalDBUsername;
	cmdArgs << "-un";
	cmdArgs << usernameString;
}

if (daticalDBPassword) {
	def passwordString = daticalDBContext + ":::" + daticalDBPassword;
	cmdArgs << "-pw";
	cmdArgs << passwordString;
}

if (daticalDBContext) {
	cmdArgs << "--context";
	cmdArgs << daticalDBContext;
}

def daticalDBDeployThreshold = props['daticalDBDeployThreshold'];
if (daticalDBDeployThreshold) {
	cmdArgs << "--deployThreshold";
	cmdArgs << daticalDBDeployThreshold;
}

if (daticalDBLabels) {
	cmdArgs << "--labels";
	cmdArgs << daticalDBLabels;
}

if (daticalDBPipeline) {
	cmdArgs << "--pipeline";
	cmdArgs << daticalDBPipeline;
}

cmdArgs << daticalDBAction;
cmdArgs << daticalDBServer;

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
