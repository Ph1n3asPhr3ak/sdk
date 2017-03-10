package com.CityRisks;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class AutomatedDataDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    Date now = new Date();
    //Shows setting a process variable. In this case, the variable autoWelcomeTime with the current time.
    //--Right now it doesn't find the "autowelcometime" variable because we don' t use the scripttask which is defined by the javascript--
    execution.setVariable("autoWelcomeTime", now);
    System.out.println(now);
    //Shows retrieving a process variable.
    //From User Task getVariable form the Form - textbox
    //System.out.println("Faux call to backend for [" 
    //+ execution.getVariable("fullName") + "] with CityRisks User" + execution.getVariable("cr_User"));
  }

}