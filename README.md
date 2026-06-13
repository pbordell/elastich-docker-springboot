# Elastich Docker SpringBoot

![Java](https://shields.io)
![Docker](https://shields.io)
![Elasticsearch](https://shields.io)

Este proyecto proporciona una infraestructura contenedorizada para el ecosistema **ELK Stack (Elasticsearch, Logstash y Kibana)**, completamente integrada con una aplicación **Spring Boot** encargada de realizar búsquedas y consumir datos de manera eficiente.

## 🚀 Características y Arquitectura

* **Stack ELK Completo:** Servicios independientes y preconfigurados para la ingesta, almacenamiento y visualización de datos.
* **Spring Boot Searcher:** Microservicio en Java diseñado para interactuar con el clúster de Elasticsearch.
* **Orquestación Centralizada:** Despliegue unificado y control de variables de entorno mediante Docker Compose.

## 🛠️ Requisitos Previos

Antes de levantar el entorno, asegúrate de tener instalado en tu sistema:
* [Docker Desktop](https://docker.com)
* [Docker Compose](https://docker.com)
* [Java Development Kit (JDK)](https://oracle.com) (versión 11 o superior recomendada para compilar el microservicio)

## 📦 Estructura del Repositorio

El repositorio está organizado en los siguientes módulos clave:
* `elasticsearch/`: Configuraciones de almacenamiento y nodos de búsqueda.
* `logstash/`: Canalizaciones y filtros para el procesamiento de logs e ingesta de datos.
* `kibana/`: Interfaz gráfica y paneles para visualización y analítica del stack.
* `spring-boot-searcher/`: Código fuente de la aplicación Java encargada de consumir Elasticsearch.
* `docker-compose.yml`: Archivo de orquestación de red y contenedores.
* `.env`: Definición centralizada de variables de entorno (puertos, versiones, credenciales).

## 🏁 Inicialización y Despliegue

Sigue estos pasos para poner en marcha toda la infraestructura:

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com
   cd elastich-docker-springboot
   ```

2. **Crear la red dedicada de Docker:**
   Garantiza la comunicación interna aislada entre todos los servicios del stack:
   ```bash
   docker network create elastich-network
   ```

3. **Levantar los contenedores:**
   Ejecuta el despliegue en segundo plano (-d) utilizando el archivo de orquestación:
   ```bash
   docker-compose up -d
   ```

4. **Verificar el estado de los servicios:**
   ```bash
   docker-compose ps
   ```

## 🔌 Puertos por Defecto

Una vez inicializado el entorno, podrás acceder a los servicios en las siguientes direcciones locales:
* **Elasticsearch API:** `http://localhost:9200`
* **Kibana UI:** `http://localhost:5601`
* **Spring Boot App:** `http://localhost:8080` (o el puerto configurado en el microservicio)

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo correspondiente para más detalles.
