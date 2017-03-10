package com.CityRisks;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import eu.cityrisks.module.ModuleTest;

public class CR_Users {

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
	    System.out.println("Please provide the Model of the Device that will show the percentage of Users that are associated to it.");
	    System.out.println("Device Model?");
	    //CityRisks//
	    //percentage of Users using the device...//
	    Scanner scanner = new Scanner(System.in);
	    String value = scanner.nextLine();
	    ModuleTest CRModule = new ModuleTest();
	    String deviceModel = value;
	    float returned = CRModule.executeFooNativeQueryUserReturnByModel(deviceModel);
	    float AllUsers = CRModule.executeFooNativeQueryUser();
	    float Result = (returned * 100) / AllUsers;
	    System.out.println("\nThe Percentage of Users Using the Model " + deviceModel + " is: \n" + returned + " / " + AllUsers + " = " + Result + " % of All Users\n");
	    //CityRisks//
	    //Just import the AutomatedDataDelagate class in properties >> javaclass in main config of the service task.
	    scanner.close();
	    System.out.println("History...");
	    //Show History - step by step - the processes
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
            // and display it last to represent the logical sequence.				
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
	    //Save data to a textfile called CityRisks.txt inside the project
        FileWriter writer;
        Date now = new Date();
		try {
			writer = new FileWriter("CityRisksResults.txt", true);
			writer.write("The Percentage of Users Using the Model " + deviceModel + " is: " + returned + " / " + AllUsers + " = " + Result + " % of All Users ("+ now +")\n");
			writer.write(System.getProperty("line.separator"));
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		

	  }
	}