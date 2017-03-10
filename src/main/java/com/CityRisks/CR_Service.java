package com.CityRisks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import eu.cityrisks.module.ModuleTest;

public class CR_Service {

	public static void main(String[] args) throws ParseException {
	    ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
	    	//changed to potgresql settings
	        .setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
	        .setJdbcUsername("sa")
	        .setJdbcPassword("")
	        .setJdbcDriver("org.h2.Driver")
	    	.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
	    	//new for Postgresql
	        //.setJdbcUrl("jdbc:postgresql://localhost:5432/city_risks")
	        //.setJdbcUsername("postgres")
	        //.setJdbcPassword("postgres")
	        //.setJdbcDriver("org.postgresql.Driver")
	    	//.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
	    ProcessEngine processEngine = cfg.buildProcessEngine();
	    String pName = processEngine.getName();
	    String ver = ProcessEngine.VERSION;
	    System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");

	    RepositoryService repositoryService = processEngine.getRepositoryService();
	    Deployment deployment = repositoryService.createDeployment()
	        .addClasspathResource("CityRisks.bpmn").deploy();
	    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
	        .deploymentId(deployment.getId()).singleResult();
	    System.out.println(
	        "Found process definition [" 
	            + processDefinition.getName() + "] with id [" 
	            + processDefinition.getId() + "]");
	    //Java Delegate - goes to AutomatedDataDelegate
	    RuntimeService runtimeService = processEngine.getRuntimeService();
	    ProcessInstance processInstance = runtimeService
	        .startProcessInstanceByKey("CityRisks");
	    System.out.println("CityRisks process started with process instance id [" 
	        + processInstance.getProcessInstanceId()
	        + "] key [" + processInstance.getProcessDefinitionKey() + "]");
	    System.out.println("Please provide the Brand of the Device that will show the percentage of Users that are associated to it.");
	    System.out.println("Device Brand?");
	    //CityRisks//
	    //percentage of Users using the device...//
	    Scanner scanner = new Scanner(System.in);
	    String value = scanner.nextLine();
	    ModuleTest CRModule = new ModuleTest();
	    String deviceBrand = value;
	    float returned = CRModule.executeFooNativeQueryUserReturnByBrand(deviceBrand);
	    float AllUsers = CRModule.executeFooNativeQueryUser();
	    float Result = (returned * 100) / AllUsers;
	    System.out.println("\nThe Percentage of Users Using the Brand " + deviceBrand + " is: \n" + returned + " / " + AllUsers + " = " + Result + " % of All Users\n");
	    //CityRisks//
	    //Just import the AutomatedDataDelagate class in properties >> javaclass in main config of the service task.
	    scanner.close();
	    //TaskService taskService = processEngine.getTaskService();
	    //For the textboxes in the UserTask
	    //FormService formService = processEngine.getFormService();
	    System.out.println("History...");
	    //Show History - step by step the processes
	    HistoryService historyService = processEngine.getHistoryService();
	    HistoricActivityInstance endActivity = null;
        List<HistoricActivityInstance> activities = 
            historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstance.getId()).finished()
            .orderByHistoricActivityInstanceEndTime().asc()
            .list();
        for (HistoricActivityInstance activity : activities) {
          if (activity.getActivityType() == "startEvent") {
            System.out.println("BEGIN " + processDefinition.getName() 
                + " [" + processInstance.getProcessDefinitionKey()
                + "] " + activity.getStartTime());
          }
          if (activity.getActivityType() == "endEvent") {
            // Handle edge case where end step happens so fast that the end step
            // and previous step(s) are sorted the same. So, cache the end step 
            //and display it last to represent the logical sequence.
            endActivity = activity;
          } else {
            System.out.println("-- " + activity.getActivityName() 
                + " [" + activity.getActivityId() + "] "
                + activity.getDurationInMillis() + " ms");
          }
        }
        if (endActivity != null) {
          System.out.println("-- " + endActivity.getActivityName() 
                + " [" + endActivity.getActivityId() + "] "
                + endActivity.getDurationInMillis() + " ms");
          System.out.println("COMPLETE " + processDefinition.getName() + " ["
                + processInstance.getProcessDefinitionKey() + "] " 
                + endActivity.getEndTime());
        }
        //System.out.println(variables.get("cr_User").toString());
	    //Scanner scanner = new Scanner(System.in);
	    //String value = scanner.nextLine();
	    ////Get Form Types of the UserTask - Textboxes.
	    //while (processInstance != null && !processInstance.isEnded()) {
	    //  List<Task> tasks = taskService.createTaskQuery()
	    //      .taskCandidateGroup("managers").list();
	    //  System.out.println("Active outstanding tasks: [" + tasks.size() + "]");
	    //  for (int i = 0; i < tasks.size(); i++) {
	    //    Task task = tasks.get(i);
	    //    System.out.println("Processing Task [" + task.getName() + "]");
	    //    Map<String, Object> variables = new HashMap<String, Object>();
	    //    FormData formData = formService.getTaskFormData(task.getId());
	    //    for (FormProperty formProperty : formData.getFormProperties()) {
	    //      if (StringFormType.class.isInstance(formProperty.getType())) {
	    //        System.out.println(formProperty.getName() + "?");
	    //        String value = scanner.nextLine();
	    //        variables.put(formProperty.getId(), value);
	    //      } else if (LongFormType.class.isInstance(formProperty.getType())) {
	    //        System.out.println(formProperty.getName() + "? (Must be a whole number)");
	    //        Long value = Long.valueOf(scanner.nextLine());
	    //        variables.put(formProperty.getId(), value);
	    //      } else if (DateFormType.class.isInstance(formProperty.getType())) {
	    //        System.out.println(formProperty.getName() + "? (Must be a date m/d/yy)");
	    //        DateFormat dateFormat = new SimpleDateFormat("m/d/yy");
	    //        Date value = dateFormat.parse(scanner.nextLine());
	    //        variables.put(formProperty.getId(), value);
	    //      } else {
	    //        System.out.println("<form type not supported>");
	    //      }
	    //    }
	    //    taskService.complete(task.getId(), variables);
	    //    
	    //    HistoricActivityInstance endActivity = null;
	    //    List<HistoricActivityInstance> activities = 
	    //        historyService.createHistoricActivityInstanceQuery()
	    //        .processInstanceId(processInstance.getId()).finished()
	    //        .orderByHistoricActivityInstanceEndTime().asc()
	    //        .list();
	    //    for (HistoricActivityInstance activity : activities) {
	    //      if (activity.getActivityType() == "startEvent") {
	    //        System.out.println("BEGIN " + processDefinition.getName() 
	    //            + " [" + processInstance.getProcessDefinitionKey()
	    //            + "] " + activity.getStartTime());
	    //      }
	    //      if (activity.getActivityType() == "endEvent") {
	    //        // Handle edge case where end step happens so fast that the end step
	    //        // and previous step(s) are sorted the same. So, cache the end step 
	    //        //and display it last to represent the logical sequence.
	    //        endActivity = activity;
	    //      } else {
	    //        System.out.println("-- " + activity.getActivityName() 
	    //            + " [" + activity.getActivityId() + "] "
	    //            + activity.getDurationInMillis() + " ms");
	    //      }
	    //    }
	    //    if (endActivity != null) {
	    //      System.out.println("-- " + endActivity.getActivityName() 
	    //            + " [" + endActivity.getActivityId() + "] "
	    //            + endActivity.getDurationInMillis() + " ms");
	    //      System.out.println("COMPLETE " + processDefinition.getName() + " ["
	    //            + processInstance.getProcessDefinitionKey() + "] " 
	    //            + endActivity.getEndTime());
	    //      System.out.println(variables.get("cr_User").toString());
	    
	    	  //CityRisks Module
	          //processInstance = startProcessInstanceByMessage(String messageName);
	          
	          //ModuleTest asd = new ModuleTest();
	          //variables.get("cr_User").toString()
	          //String returned = asd.executeFooNativeQueryUserReturn("J11");
	          
	          //variables.put("users", returned); // variable name mapping
	          FileWriter writer;
			//	System.getProperty("line.separator");
	        //  //File desktop = new File(System.getProperty("user.home"), "Desktop");
			//	//String userHomeFolder = System.getProperty("user.home");
			//	File textFile = new File(System.getProperty("user.home"), "NewTxtCityRisks.txt");
			//	try {
			//		BufferedWriter out = new BufferedWriter(new FileWriter(textFile, true));
			//		out.write((int) returned);
			//		out.close();
			//	} catch (IOException e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}
	          
	        //Save data to a textfile called CityRisks.txt inside the project
	          Date now = new Date();
					try {
						writer = new FileWriter("CityRisksResults.txt", true);
						writer.write("The Percentage of Users Using the Brand " + deviceBrand + " is: " + returned + " / " + AllUsers + " = " + Result + " % of All Users ("+ now +")\n");
						writer.write(System.getProperty("line.separator"));
						writer.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}		
					//Runtime run = Runtime.getRuntime();
					
						//try {
							//run.exec("C:\\cityriskstxt2");
						//} catch (IOException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
						//}
					//
	    //    }
	    //  }
	    //  // Re-query the process instance, making sure the latest state is available
	    //  processInstance = runtimeService.createProcessInstanceQuery()
	    //      .processInstanceId(processInstance.getId()).singleResult();
	    //}
	    //scanner.close();
	  }
	}