package com.example.starter.handler;

import com.example.starter.model.callback.ResponeCallback;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.json.schema.*;

public class TransactionValidationRequestHandler {
  private final JsonSchema jsonSchemal;
  private ResponeCallback responeCallback = new ResponeCallback();
  
  public TransactionValidationRequestHandler(final JsonSchema jsonSchema){
    this.jsonSchemal = jsonSchema;
  }

  public void validate(RoutingContext context){
    JsonObject jsonObject = context.body().asJsonObject();

    OutputUnit outputUnit = Validator.create(jsonSchemal,
    new JsonSchemaOptions()
      .setDraft(Draft.DRAFT7)
      .setBaseUri("https://vertx.io")
      .setOutputFormat(OutputFormat.Basic)
    ).validate(jsonObject);

    if(outputUnit.getValid()){
      context.next();
    }
    else {
      String errorMsg = "Property does not match schema";
      try {
        outputUnit.checkValidity();
      } catch (JsonSchemaValidationException e) {
        throw new RuntimeException(e);
      }
      responeCallback.responseClient(context, 400, 1, errorMsg, null);
    }
  }
}
