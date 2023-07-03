package com.example.starter.handler;

import com.example.starter.model.callback.ResponeCallback;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.json.schema.*;

public class TransactionValidationRequestHandler {

  private final JsonSchema jsonSchema;
  private ResponeCallback responeCallback = new ResponeCallback();
  public TransactionValidationRequestHandler(final JsonSchema jsonSchema){
    this.jsonSchema = jsonSchema;
  }

  public void validate(RoutingContext rc) {
    JsonObject jsonBody = rc.body().asJsonObject();

    OutputUnit resultVal = Validator.create(jsonSchema,
      new JsonSchemaOptions()
        .setDraft(Draft.DRAFT7)
        .setBaseUri("https://vertx.io")
        .setOutputFormat(OutputFormat.Basic)
    ).validate(jsonBody);

    if(resultVal.getValid()){
      rc.next();
    } else {
      String errorMsg = "Property does not match schema";
      try {
        resultVal.checkValidity();
      } catch (JsonSchemaValidationException ex){
        errorMsg = ex.getCause().getMessage() + "::" + ex.getMessage();
      }

      responeCallback.responseClient(rc, 400, 1, errorMsg, null);
    }
  }
}
