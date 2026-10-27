# SmartNotes
Proyecto del Grupo 4 de Desarollo de aplicaciones 

Sistema web transaccional para la gestión y estandarización de notas de corrección (Universidad Fidélitas, SC-403).

## Requisitos
- Java 21
- Maven
- MySQL 8

## Cómo ejecutar el proyecto
1. Abrir MySQL y ejecutar el script `Script_SmartNotes.sql` (crea la base de datos `smartnotes`, las tablas y los datos de prueba).
2. En `src/main/resources/application.properties` cambiar `spring.datasource.password` por la clave local de MySQL.
3. Ejecutar la clase `SmartNotesApplication` (o `mvn spring-boot:run`).
4. Abrir http://localhost:8080 e iniciar sesión.

## Usuarios de prueba
Todos usan la misma contraseña de demostración definida en el script SQL.

| Usuario  | Perfil     |
|----------|------------|
| admin    | ADMIN      |
| ljimenez | LIDER      |
| cmendoza | SUPERVISOR |
| asolano  | QC         |
| daraya   | RELEASE    |

## Estructura (MVC)
- `domain`: entidades JPA (Rol, Usuario, Categoria, TeamMember, Plantilla, Variable, PlantillaVariable, NotaCorreccion).
- `repository`: acceso a datos con Spring Data JPA.
- `service`: lógica de negocio (incluye `UsuarioDetailsService` para el inicio de sesión).
- `controller`: controladores MVC.
- `resources/templates`: vistas Thymeleaf con Bootstrap (`general/fragmentos.html` tiene el head, encabezado y footer compartidos).
- `resources/messages*.properties`: textos en español e inglés
