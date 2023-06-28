package com.example.starter.schema;

import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.common.dsl.Schemas;

public class TransactionSchemaBuider {
  public static JsonSchema build()
  {
    ObjectSchemaBuilder objectSchemaBuilder = Schemas.objectSchema()
      .requiredProperty("trace", Schemas.objectSchema()
        .requiredProperty("from", Schemas.stringSchema()) //name people's transfer
        .requiredProperty("money", Schemas.stringSchema())
        .requiredProperty("cost", Schemas.stringSchema())
        .requiredProperty("status", Schemas.stringSchema())
        .requiredProperty("to_date", Schemas.stringSchema())
        .requiredProperty("bear_cost", Schemas.stringSchema()))
      .requiredProperty("receive", Schemas.objectSchema()
        .requiredProperty("to", Schemas.stringSchema()));

    JsonSchema jsonSchema = JsonSchema.of(objectSchemaBuilder.toJson());
    return jsonSchema;
  }
}
