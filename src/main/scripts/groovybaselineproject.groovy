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
def daticalDBSkipForce = props['daticalDBSkipForce']
def daticalDBSync = props['daticalDBSync']
def daticalDBZipProject = props['daticalDBZipProject'];
def daticalDBvm = props['daticalDBvm'];
def daticalDBvmargs = props['daticalDBvmargs'];

def daticalDBHammerPath = new File(daticalDBCmd).getParent();
def daticalDBProjectBaselineScript = 'file:/' + daticalDBHammerPath + File.separator + "scripts" + File.separator + 'project_baseline.groovy';

def cmdArgs = [daticalDBCmd, 'groovy', daticalDBProjectBaselineScript];

if (daticalDBSkipForce != "unselected") {
	cmdArgs << daticalDBSkipForce
}

if (daticalDBZipProject == "true") {
	cmdArgs << "zip";
}

if (daticalDBSync == "true") {
	cmdArgs << "noSync";
}


if (daticalDBvm) {
	cmdArgs << "--vm";
	cmdArgs << daticalDBvm;
}

if (daticalDBvmargs) {
	cmdArgs << "--vmargs";
	String[] myArray = daticalDBvmargs.split();
	for ( x in myArray ) {
		cmdArgs << x;
	}
}

int exitCode = cmdHelper.runCommand("Executing Datical DB", cmdArgs);

System.exit(exitCode);
