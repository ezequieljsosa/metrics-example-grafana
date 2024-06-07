package ar.edu.utn.dds.k3003;

import io.javalin.Javalin;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class Main {
  // valor del auth token para validar la request de métricas
  // *IMPORTANTE*: para uso personal, cambiar para tomarlo del ambiente y no
  // persistirlo en código
  private static final String TOKEN = "token";

  public static void main(final String... args) {
    System.out.println("starting up the server");

    // crea un registro para nuestras métricas basadas en Prometheus
    final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    // agregar aquí cualquier tag que aplique a todas las métrivas de la app
    // (e.g. EC2 region, stack, instance id, server group)
    registry.config().commonTags("app", "metrics-sample");

    // agregamos a nuestro reigstro de métricas todo lo relacionado a infra/tech
    // de la instancia y JVM
    try (var jvmGcMetrics = new JvmGcMetrics();
         var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
      jvmGcMetrics.bindTo(registry);
      jvmHeapPressureMetrics.bindTo(registry);
    }
    new JvmMemoryMetrics().bindTo(registry);
    new ProcessorMetrics().bindTo(registry);
    new FileDescriptorMetrics().bindTo(registry);

    // agregamos métricas custom de nuestro dominio
    Gauge.builder("myapp_random", () -> (int)(Math.random() * 1000))
        .description("Random number from My-Application.")
        .strongReference(true)
        .register(registry);

    // seteamos el registro dentro de la config de Micrometer
    final var micrometerPlugin =
        new MicrometerPlugin(config -> config.registry = registry);

    // registramos el plugin de Micrometer dentro de la config de la app de
    // Javalin
    Javalin.create(config -> { config.registerPlugin(micrometerPlugin); })
        .get("/", ctx -> ctx.result("Hello World"))
        // agregamos una ruta para hacer polling de las métrivas con el
        // handler que toma llos resultadose scrapear la registry de
        // métricas
        .get("/metrics",
             ctx -> {
               // chequear el header de authorization y chequear el token bearer
               // configurado
               var auth = ctx.header("Authorization");

               if (auth != null && auth.intern() == "Bearer " + TOKEN) {
                 ctx.contentType("text/plain; version=0.0.4")
                     .result(registry.scrape());
               } else {
                 // si el token no es el apropiado, devolver error,
                 // desautorizado
                 // este paso es necesario para que Grafana online
                 // permita el acceso
                 ctx.status(401).json("unauthorized access");
               }
             })
        .start(7070);
  }
}
