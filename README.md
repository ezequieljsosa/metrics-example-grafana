# Javalin Micrometer Example

Ejemplo de proyecto con un simple recolector de métricas de Micrometer configurado para una app en Javalin.

El ejemplo muestra una config mínima para tener:
1) Métricas de la JVM y otros recuros de la instancia local
2) Métricas custom, configurables desde la app y en relación al dominio específico u otro motivo
3) Tags comunes a las métricas para identificar la instancia/servicio/etc particular
4) Exposición del endpoint de métricas utlizando la API propia de Micrometer

## Ejecutar y testear el comportamiento localmente

Para levantar el servicio de ejemplo:

```bash
mvn clean package exec:java
```

Para llamar al servicio con la ruta de métricas:

```bash
curl localhost:7070/metrics -H "Authorization: Bearer token" # cambiar el token por el valor configurado
```

## Integración con Grafana Cloud

Esta sección es un resumen de la configuración [que pueden encontrar aquí, ver para más detalle](https://grafana.com/docs/grafana-cloud/monitor-infrastructure/integrations/integration-reference/integration-metrics-endpoint/).

1) Entrar a Grafana Cloud, crear un usuario y una cloud propia
2) Ir a _Connections_ en la barra izquierda y buscar _Metrics Endpoint_ en la barra de búsqueda para agregar nuevas conexiones

![Crear nuevo scrape job](docs/1.png)

3) Click en _Add new scrape job_ e ir a la configuración del trabajo
4) Configurar el trabajoo de scraping:
    - Nombre del trabajo con el valor que quieran
    - _Scrape job URL_ que debe apuntar al endpoint de métricas de su servicio (ej: https://miservicio.com/metrics)
    - _Type of Authentication Credentials_ en _Bearer_
    - _Bearer Token_ con el valor configurado del token en su app (en este ejemplo por defecto está en `token`)

![Configuración del scraping job](docs/2.png)

5) Click en _Test Connection_, _Save Scrape Job_ e _Install_
6) Crear el dashboard

Listo, en el nuevo dashboard creado deberían ver las métricas reportadas por el job de scraping. Pueden configurar este dashboard y customizarlo como prefieran.

![Dashboard con métricas](docs/3.png)
