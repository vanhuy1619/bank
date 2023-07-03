package com.example.starter;

import com.example.starter.api.constant.PropertiesConfig;
import com.example.starter.utils.ConfigUtils;
import com.example.starter.utils.StackTraceUtil;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.tracing.zipkin.HttpSenderOptions;
import io.vertx.tracing.zipkin.ZipkinTracingOptions;

public class Main {
  public static void main(String []args)
  {
    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    new UptimeMetrics().bindTo(registry);

    Vertx vertx = Vertx.vertx(new VertxOptions()
      .setWorkerPoolSize(500)
      .setMetricsOptions(
        new MicrometerMetricsOptions()
          .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
          .setJvmMetricsEnabled(true)
          .setMicrometerRegistry(registry)
          .setEnabled(true)
      )
      .setTracingOptions(new ZipkinTracingOptions()
        .setServiceName(ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.SERVICE_NAME))
        .setSenderOptions(new HttpSenderOptions()
          .setSenderEndpoint(ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.ZIPKIN_ENDPOINT))
          .setSsl(true))
      ));

    vertx.deployVerticle(MainVerticle.class.getName())
      .onFailure(throwable -> {
        System.out.println(StackTraceUtil.getStackTrace(throwable));
        System.exit(-1);
      });
  }
}
