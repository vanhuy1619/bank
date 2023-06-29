package com.example.starter.schema;

import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.common.dsl.Schemas;

public class TransactionSchemaBuider {
  public static JsonSchema build()
  {
    ObjectSchemaBuilder objectSchemaBuilder = Schemas.objectSchema()
      .requiredProperty("sender", Schemas.objectSchema()
        .requiredProperty("from", Schemas.stringSchema()) //name people's transfer
        .requiredProperty("money_transfer", Schemas.stringSchema())
        .requiredProperty("bear_cost", Schemas.stringSchema()))
      .requiredProperty("receiver", Schemas.objectSchema()
        .requiredProperty("to_idcard", Schemas.stringSchema()));

    JsonSchema jsonSchema = JsonSchema.of(objectSchemaBuilder.toJson());
    return jsonSchema;
  }
}
