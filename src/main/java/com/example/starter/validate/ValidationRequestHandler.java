package com.example.starter.validate;

import io.vertx.json.schema.JsonSchema;

public class ValidationRequestHandler {
  private JsonSchema jsonSchema;

  public ValidationRequestHandler(JsonSchema jsonSchema){
    this.jsonSchema = jsonSchema;
  }


}
