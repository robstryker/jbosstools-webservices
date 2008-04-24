package org.jboos.tools.ws.creation.core.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.jboos.tools.ws.creation.core.data.ServiceModel;
import org.jboos.tools.ws.creation.core.utils.JBossWSCreationUtils;
import org.jboss.tools.ws.core.JbossWSCorePlugin;

public class WSProviderInvokeCommnad extends AbstractDataModelOperation{

	private ServiceModel model;
	
	
	public WSProviderInvokeCommnad(ServiceModel model){
		this.model = model;
	}
	
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		String runtimeLocation = JbossWSCorePlugin.getDefault().getPreferenceStore().getString("jbosswsruntimelocation");
		String commandLocation = runtimeLocation + Path.SEPARATOR + "bin";		
		String command =  "sh wsprovide.sh ";
		if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			command =  "cmd wsprovide.bat";		   
		}		
		String args = getCommandlineArgs();		
		command += " -k " + args + " " + model.getServiceClass();
		
		try {
			
			InputStreamReader ir = new InputStreamReader(Runtime.getRuntime().exec(command, null, new File(commandLocation)).getInputStream());
            LineNumberReader input = new LineNumberReader(ir);            
            String str = input.readLine();
            while(str != null){                
                System.out.println(str);
                str = input.readLine();
           }
            
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		refreshProject(model.getWebProjectName(), monitor);
		
		return Status.OK_STATUS;
	}
	
	private void refreshProject(String project, IProgressMonitor monitor){
		try {
			JBossWSCreationUtils.getProjectByName(project).refreshLocal(2, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getCommandlineArgs(){
		String commandLine;
		String project = model.getWebProjectName();
		String projectRoot = JBossWSCreationUtils.getProjectRoot(project).toOSString();
		commandLine = "-s " + projectRoot + Path.SEPARATOR + "src";
		
        if(model.isGenWSDL()){
        	commandLine += " -w "; 
        }
        commandLine += " -o " + projectRoot + Path.SEPARATOR + "WebContext" + Path.SEPARATOR + "wsdl";
		
		return commandLine;
		
	}
}