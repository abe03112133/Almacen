# Sistema de Gestión de Almacén - Procesadora

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

Sistema web de gestión integral de almacén de productos congelados desarrollado con Spring Boot. Implementa control de inventario en tiempo real, sistema PEPS automatizado, generación de pick lists y trazabilidad completa de tarimas.

## Tabla de Contenidos

- [Descripcion del Proyecto](#descripcion-del-proyecto)
- [Caracteristicas Principales](#caracteristicas-principales)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Requisitos Previos](#requisitos-previos)
- [Instalacion](#instalacion)
- [Configuracion](#configuracion)
- [Uso del Sistema](#uso-del-sistema)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Roles de Usuario](#roles-de-usuario)
- [Funcionalidades por Rol](#funcionalidades-por-rol)
- [API Endpoints](#api-endpoints)
- [Documentacion](#documentacion)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)

## Descripcion del Proyecto

### Contexto
**Procesadora** es una empresa dedicada a la producción y distribución de productos alimenticios congelados con 60 colaboradores. Actualmente, el almacén opera con procesos manuales basados en Excel, generando:

- ❌ Inconsistencias en el inventario físico vs registros
- ❌ Desconocimiento en tiempo real de disponibilidad de productos
- ❌ Pérdida de tiempo en captura y consolidación de información
- ❌ Dificultad para aplicar correctamente el método PEPS
- ❌ Falta de control sobre productos en cuarentena o dañados
- ❌ Mermas por tarimas caducadas no surtidas a tiempo

### Solucion
Sistema web que automatiza el ciclo completo de vida de las tarimas, desde su creación hasta su salida del almacén, garantizando:

- ✅ Trazabilidad completa de cada tarima
- ✅ Control de inventario en tiempo real
- ✅ Aplicación automática del método PEPS
- ✅ Reducción del 80% en tiempo de captura manual
- ✅ Precisión de inventario > 95%
- ✅ Gestión eficiente de excepciones (cuarentenas, daños, bloqueos)


## Caracteristicas Principales

### Gestión de Tarimas
- Creación automatizada con folio único (formato: TAR-YYYYMMDD-XXXX)
- Generación e impresión de etiquetas con código de barras
- Registro de datos: producto, lote, cantidad, fechas de producción/caducidad
- Estados: Normal, Cuarentena, Dañada, Bloqueada, Surtida

### Control de Inventario
- Dashboard en tiempo real con totalizadores
- Consulta de inventario actualizado automáticamente
- Búsqueda avanzada con múltiples filtros
- Historial completo de movimientos por tarima
- Kardex de entradas y salidas

### Sistema PEPS Automático
- Algoritmo de priorización inteligente:
  1. Fecha de producción (más antigua primero)
  2. Proximidad a fecha de caducidad
  3. Ubicación física (optimización de ruta)
- Exclusión automática de tarimas en cuarentena/dañadas/bloqueadas

### Pick Lists Inteligentes
- Generación automática basada en PEPS
- Selección óptima de tarimas para surtir
- Interfaz optimizada para tablets
- Validación por escaneo de código de barras
- Seguimiento de progreso en tiempo real

### Gestión de Ubicaciones
- Mapa visual del almacén con código de colores
- Asignación de tarimas a racks mediante escaneo
- Control de ocupación por ubicación
- Liberación automática al completar salidas

### Reportes Operativos
- Inventario actual (Excel/PDF)
- Movimientos (Kardex completo)
- Por producto, fechas, turno, lote
- Reportes semanales y mensuales
- Cuarentenas y mermas

### Seguridad
- Autenticación con Spring Security
- Control de acceso basado en roles (RBAC)
- Contraseñas encriptadas con BCrypt
- Auditoría completa de operaciones críticas
- Sesiones con timeout de 30 minutos

## Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.x**
  - Spring Web (MVC)
  - Spring Data JPA
  - Spring Security
- **Hibernate ORM**
- **MySQL 8.0+**

### Frontend
- **Thymeleaf**
- **Bootstrap 5**
- **JavaScript**
- **HTML5 & CSS3**

### Herramientas Adicionales
- **Maven** (Gestión de dependencias)
- **Lombok** (Reducción de código boilerplate)
- **Apache POI** (Exportación a Excel)
- **iText** (Generación de PDFs)

### Infraestructura
- **Git & GitHub** (Control de versiones)
- **Apache Tomcat** (Servidor embebido)
- **MySQL Server** (Base de datos)

## Requisitos Previos

Antes de instalar el sistema, asegúrate de tener:

- **Java JDK 17 o superior** ([Descargar aquí](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.x+** ([Descargar aquí](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Descargar aquí](https://dev.mysql.com/downloads/))
- **Git** ([Descargar aquí](https://git-scm.com/downloads))
- **IDE recomendado**: IntelliJ IDEA

### Hardware Recomendado
- **RAM:** 4 GB mínimo, 8 GB recomendado
- **Espacio en disco:** 500 MB para la aplicación + espacio para BD
- **Red:** Conectividad LAN para acceso multiusuario

### Dispositivos Compatibles
- Lectores de código de barras
- Impresoras de red
- Tablets para operadores (Android/iOS con navegador moderno)

## Instalacion

### 1. Clonar el Repositorio
git clone https://github.com/abe03112133/Almacen.git
cd procesadora

### 2. Configurar Base de Datos
# Conectarse a MySQL
mysql -u root -p ******

# Crear base de datos
CREATE DATABASE almacen_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Crear usuario (opcional pero recomendado)
CREATE USER 'ppa'@'localhost' IDENTIFIED BY 'password_seguro';
GRANT ALL PRIVILEGES ON almacen_db.* TO 'ppa'@'localhost';
FLUSH PRIVILEGES;
EXIT;

### 3. Configurar Variables de Entorno

Crea un archivo `application.properties` en `src/main/resources/`:
# Configuración de Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3310/almacen_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=almacen_user
spring.datasource.password=password_seguro
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Configuración del Servidor
server.port=8089
server.servlet.context-path=/

# Configuración de Sesión
server.servlet.session.timeout=30m

# Configuración de Thymeleaf
spring.thymeleaf.cache=false

# Configuración de Archivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Configuración de Logging
logging.level.com.procesadora.almacen=DEBUG
logging.level.org.springframework.security=DEBUG

### 4. Compilar y Ejecutar
# Compilar el proyecto
./mvnw clean install

# Ejecutar la aplicación
./mvnw spring-boot:run

O si prefieres generar el JAR:
./mvnw clean package
java -jar target/procesadora-1.0.0.jar

### 5. Acceder al Sistema

Abre tu navegador en: **http://localhost:8089/dashboard**

## Configuracion

### Usuarios por Defecto

Después de la primera ejecución, el sistema crea automáticamente los siguientes usuarios de prueba:

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `admin123` | ADMIN |

⚠️ **IMPORTANTE:** Cambia estas contraseñas en produccion.

### Configuración de Impresora de Red

En application.properties:
properties
# Configuración de Impresora
impresora.red.ip=192.168.1.100
impresora.red.nombre=HP_LaserJet_Almacen
impresora.red.puerto=9100

## Uso del Sistema

### Flujo Básico de Operación

#### 1️ **Creación de Tarimas** (Montacarguista/Supervisor Producción)

Login → Empaque →

├─ Seleccionar Estado de Producto

├─ escanear etiqueta de cajas

├─ Cantidad de cajas (default: 25)

├─ Fecha de producción (automatico)

└─ Se imprime hoja de preTarima

#### 2️ **Asignación a Ubicación** (Montacarguista)
Login → Empaque

├─ Escanear código de pre-tarima

├─ Verificar datos

├─ Escanear código de ubicación (rack)

└─ Confirmar → Tarima asignada

#### 3️ **Generación de Pick List** (Supervisor de Materiales)

Login → Surtido → Nuevo surtido

├─ Seleccionar Producto

├─ Cantidad de tarimas por producto requeridas

├─ Destino

└─ Generar → Sistema aplica PEPS automáticamente

#### 4️ **Ejecución de Surtido** (Surtidor)

Login → Mis Pick Lists → Seleccionar Pick List

├─ Ver lista de tarimas a surtir (en orden PEPS)

├─ Para cada tarima:

│ └─ Validar tarima surtida

└─ Finalizar Pick List → Inventario actualizado

#### 5️ **Consulta de Inventario** (Todos los roles)

Login → Inventario

├─ Ver tarimas disponibles

├─ Filtrar por producto/estado/ubicación

├─ Ver detalles de tarima

└─ Exportar

## Estructura del Proyecto
procesadora/

├── src/

│ ├── main/

│ │ ├── java/com/procesadora/almacen/

│ │ │ ├── config/ # Configuraciones (Security, etc.)

│ │ │ ├── controller/ # Controladores MVC

│ │ │ ├── dto/ # Data Transfer Objects

│ │ │ ├── entity/ # Entidades JPA

│ │ │ ├── repository/ # Repositorios Spring Data

│ │ │ ├── service/ # Lógica de negocio

│ │ │ │ ├── impl/ # Implementaciones

│ │ │ │ └── interfaces/ # Interfaces de servicios

│ │ │ ├── util/ # Utilidades (generadores, helpers)

│ │ │ └── AlmacenApplication.java

│ │ └── resources/

│ │ ├── static/ # CSS, JS, imágenes

│ │ ├── templates/ # Plantillas Thymeleaf

│ │ ├── application.properties

│ │ ├── schema.sql # Script de creación de BD

│ │ └── data.sql # Datos iniciales

│ ├── manual-usuario.pdf

│ ├── manual-tecnico.pdf

│ ├── diagramas/

│ └── api/ ├── .gitignore

├── pom.xml

├── README.md

└── LICENSE


## Roles de Usuario

### 1. Administrador (ADMIN)
**Acceso completo al sistema**
- Gestión de usuarios y roles
- Configuración de catálogos (productos, ubicaciones)
- Consulta de auditoría y logs
- Configuración del sistema

### 2. Montacarguista (MONTACARGUISTA)
**Operaciones de almacenamiento**
- Crear tarimas
- Imprimir etiquetas
- Asignar tarimas a ubicaciones
- Reportar tarimas dañadas/bloqueadas
- Consultar inventario

### 3. Surtidor (SURTIDOR)
**Operaciones de despacho**
- Ver pick lists asignadas
- Ejecutar surtido con validación por escaneo
- Registrar salidas
- Reportar incidencias
- Consultar inventario

### 4. Supervisor de Materiales (SUPERVISOR_MATERIALES)
**Gestión de pedidos e inventario**
- Todo lo anterior +
- Generar pick lists
- Gestionar cuarentenas
- Aprobar bajas de tarimas dañadas
- Generar reportes operativos

### 5. Supervisor de Producción (SUPERVISOR_PRODUCCION)
**Gestión de producción**
- Crear tarimas
- Generar reportes de producción
- Consultar inventario
- Análisis de eficiencia

### 6. Gerencia/Dirección (GERENCIA)
**Visualización ejecutiva**
- Dashboard con KPIs
- Todos los reportes
- Consulta de inventario
- Análisis de tendencias

## Funcionalidades por Rol

| Funcionalidad | Admin | Montac. | Surtidor | Sup.Mat | Sup.Prod | Gerencia |
|--------------|:-----:|:-------:|:--------:|:-------:|:--------:|:--------:|
| Gestionar usuarios | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Gestionar productos | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Gestionar ubicaciones | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Crear tarimas | ✅ | ✅ | ❌ | ✅ | ✅ | ❌ |
| Imprimir etiquetas | ✅ | ✅ | ❌ | ✅ | ✅ | ❌ |
| Asignar ubicaciones | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ |
| Generar pick lists | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ |
| Ejecutar surtido | ✅ | ❌ | ✅ | ✅ | ❌ | ❌ |
| Gestionar cuarentenas | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ |
| Reportar daños | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| Consultar inventario | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Generar reportes | ✅ | ❌ | ❌ | ✅ | ✅ | ✅ |
| Ver logs/auditoría | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

## API Endpoints

### Autenticación
POST   /login              # Login de usuario
POST   /logout             # Logout de usuario

### Tarimas
GET    /tarimas            # Listar tarimas
POST   /tarimas            # Crear tarima
GET    /tarimas/{id}       # Ver detalle
PUT    /tarimas/{id}       # Actualizar tarima
DELETE /tarimas/{id}       # Eliminar (soft delete)
GET    /tarimas/{id}/etiqueta  # Generar PDF de etiqueta
GET    /tarimas/buscar     # Búsqueda avanzada

### Inventario
GET    /inventario         # Consultar inventario actual
GET    /inventario/export  # Exportar a Excel/PDF
GET    /inventario/kardex  # Ver kardex de movimientos

### Pick Lists
GET    /picklists          # Listar pick lists
POST   /picklists          # Crear pick list
GET    /picklists/{id}     # Ver detalle
PUT    /picklists/{id}/ejecutar  # Marcar en ejecución
PUT    /picklists/{id}/completar # Completar pick list

### Ubicaciones
GET    /ubicaciones        # Listar ubicaciones
POST   /ubicaciones        # Crear ubicación
GET    /ubicaciones/mapa   # Ver mapa del almacén

### Productos
GET    /productos          # Listar productos
POST   /productos          # Crear producto
PUT    /productos/{id}     # Actualizar producto

### Reportes
GET    /reportes/inventario      # Reporte de inventario
GET    /reportes/movimientos     # Reporte de movimientos
GET    /reportes/producto/{id}   # Reporte por producto
GET    /reportes/cuarentenas     # Reporte de cuarentenas

## Documentacion

### Documentación Disponible

-  **Manual de Usuario** (PDF)
  - Manual de Montacarguista
  - Manual de Surtidor
  - Manual de Supervisores
  - Manual de Administrador

-  **Manual Técnico** (PDF)
  - Arquitectura del sistema
  - Modelo de datos (Diagrama ER)
  - Guía de instalación
  - Guía de desarrollo

-  **Diagramas**
  - Diagrama de clases
  - Diagrama de secuencia
  - Diagrama de flujo de procesos


## Contribuciones

Este es un proyecto académico desarrollado para **Tecmilenio: Taller de productividad basada en Herramientas Tecnologicas**. 

### Cómo Contribuir

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Codigo de Conducta

- Usa nombres descriptivos para variables y funciones
- Documenta funciones complejas
- Escribe tests para nuevas funcionalidades
- Sigue las convenciones de Java y Spring Boot

## Licencia

Este proyecto está bajo la Licencia MIT
MIT License

Copyright (c) 2025 [Tu Nombre]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE

## Agradecimientos

- A **Procesadora** por permitir el desarrollo de este proyecto
- A los colaboradores del almacén por sus valiosos insights
- A **Tecmilenio** por el apoyo académico
- A la comunidad de Spring Boot por la excelente documentación

---

## Estadisticas del Proyecto

- **Líneas de código:** ~15,000
- **Tiempo de desarrollo:** 8 semanas (2 meses)
- **Funcionalidades implementadas:** 30+
- **Roles de usuario:** 6
- **Tests:** 50+ casos de prueba
- **Cobertura:** ~70%

---

## Roadmap - Fase 2 (Futuro)
Funcionalidades planeadas para futuras versiones:

- [ ] Dashboard interactivo con gráficos en tiempo real
- [ ] Visualización 2D/3D del almacén
- [ ] Notificaciones push y por email
- [ ] App móvil nativa (Android/iOS)
- [ ] Integración con sistema ERP
- [ ] Modo offline para tablets
- [ ] Reportes con Machine Learning (predicción de demanda)
- [ ] Sistema de gestión de proveedores
