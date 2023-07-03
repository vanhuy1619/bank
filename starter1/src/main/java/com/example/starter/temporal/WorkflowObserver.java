package com.example.starter.temporal;

import com.example.starter.api.constant.PropertiesConfig;
import com.example.starter.utils.ConfigUtils;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.vertx.core.Vertx;

public class WorkflowObserver {

  private WorkflowClient client;
  private WorkerFactory factory;
  private Vertx vertx;

  public void setVertx(Vertx vertx){
    this.vertx = vertx;
  }

  private WorkflowObserver() {
//    onStart();
  }

  private static class BillPughSingleTon{
    private static final WorkflowObserver INSTANCE = new WorkflowObserver();
  }

  public static WorkflowObserver getInstance(){
    return BillPughSingleTon.INSTANCE;
  }

  String taskQueue = ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.TEMPORAL_QUEUE_NAME);
  String temporalNameSpace = ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.TEMPORAL_QUEUE_NAME);

  public void onStart(){
    WorkflowServiceStubs serviceStubs = WorkflowServiceStubs.newInstance(WorkflowServiceStubsOptions.newBuilder()
      .setTarget("11.11.11")
      .build());

    client = WorkflowClient.newInstance(serviceStubs, WorkflowClientOptions.newBuilder()
      .setNamespace(temporalNameSpace)
      .build());
    factory = WorkerFactory.newInstance(client);
    Worker worker = factory.newWorker(taskQueue);

    worker.registerWorkflowImplementationTypes(); //.class
    worker.registerWorkflowImplementationTypes();//.class
  }
}
