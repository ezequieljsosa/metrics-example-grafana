# Javalin Micrometer Example

Ejemplo de proyecto con un simple recolector de métricas de Micrometer configurado para una app en Javalin.

El ejemplo muestra una config mínima para tener:
1) Métricas de la JVM y otros recuros de la instancia local
2) Métricas custom, configurables desde la app y en relación al dominio específico u otro motivo
3) Tags comunes a las métricas para identificar la instancia/servicio/etc particular
4) Exposición del endpoint de métricas utlizando la API propia de Micrometer
