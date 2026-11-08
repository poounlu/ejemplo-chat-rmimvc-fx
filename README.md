# Chat RMI en red con JavaFX y Serialización — POO UNLu

Ejemplo de referencia pedagógica para la **Clase 12** de Programación Orientada a Objetos (UNLu): cómo evolucionar un sistema de mensajería interactivo implementado con **JavaFX + MVC + Observer** a una arquitectura distribuida **cliente-servidor con RMI**, utilizando la biblioteca oficial `libreria-rmimvc` de la asignatura e integrando persistencia multisesión compartida mediante **Serialización**.

Este repositorio muestra la evolución incremental del sistema a lo largo de 8 pasos registrados en *tags Git* (`paso-0` a `paso-7`).

---

## Características de la Aplicación

1. **Arquitectura Cliente-Servidor Desacoplada:** El servidor aloja la única instancia del modelo `Chat` y la publica mediante RMI. Cada usuario ejecuta su propia instancia cliente con interfaz gráfica JavaFX (`AppCliente` / `LanzadorCliente`).
2. **Observer Remoto Transparente:** La vista y el controlador (`IControladorRemoto`) se suscriben a los cambios del modelo remoto (`IObservableRemoto`). El servidor invoca `actualizar(IObservableRemoto, Object)` en cada cliente de forma asíncrona.
3. **Sincronización con el Hilo de UI:** Las notificaciones entrantes de RMI se ejecutan en hilos de red (RMI TCP Connection); `VistaGrafica` utiliza `Platform.runLater` para delegar la actualización del árbol visual al *JavaFX Application Thread*.
4. **Regla del Anfitrión (Host) y Pausa de Sala:** El primer usuario en ingresar a la sala es asignado automáticamente como **Anfitrión**. Si el anfitrión se desconecta o cierra su ventana, la sala entra en estado de **pausa** (`isSalaActiva() == false`), bloqueando el envío de mensajes en todos los clientes conectados hasta que el anfitrión vuelva a conectarse con su nombre original.
5. **Persistencia Multisesión Compartida con Serialización:** Cualquier participante puede hacer clic en `"💾 Guardar chat"`. La sesión (`SesionChat`, conteniendo el anfitrión y el historial de mensajes) se serializa en el disco del servidor mediante `ObjectOutputStream`. Al reiniciar el servidor, la sesión se restaura automáticamente si el archivo existe.

---

## Requisitos

- **JDK 17 o superior** (recomendado: **JDK 21**).
- **IntelliJ IDEA** (recomendado) o cualquier entorno compatible con Maven y JavaFX.
- La biblioteca `LibreriaRMIMVC.jar` ya está incluida en la carpeta `lib/` y declarada en `pom.xml` con scope `system`. No requiere configuración adicional.

---

## Cómo Correrlo

### Desde IntelliJ IDEA

El proyecto incluye configuraciones de ejecución listas para usar en `.run/`:

1. **Ejecutar el Servidor:** Seleccionar la configuración `Servidor` y presionar ▶ (Run). Elegir la IP sugerida y el puerto `8888`. Se abrirá una ventana de estado indicando que el servidor está corriendo.
2. **Ejecutar Clientes:** Seleccionar la configuración `Cliente` y presionar ▶ (Run). La configuración tiene habilitada la opción `singleton="false"` (*Allow multiple instances*), permitiendo abrir 2 o más ventanas de clientes en simultáneo. Ingresar el nombre de usuario y participar del chat.

> **Nota:** Se utilizan las clases `LanzadorServidor` y `LanzadorCliente` (que no extienden de `Application`) como punto de entrada para evitar incompatibilidades de carga del módulo JavaFX en ejecución estándar.

### Desde Línea de Comandos

```bash
# Compilar y ejecutar pruebas unitarias
./mvnw clean test

# Iniciar servidor
./mvnw javafx:run -Djavafx.mainClass=ar.edu.unlu.chatmvc.servidor.LanzadorServidor

# Iniciar cliente
./mvnw javafx:run -Djavafx.mainClass=ar.edu.unlu.chatmvc.cliente.LanzadorCliente
```

---

## Progresión Paso a Paso (`tags`)

Podés navegar la evolución conceptual del proyecto haciendo `git checkout <tag>` en cualquier momento (y regresar al estado final con `git checkout main`):

| Tag | Hito Pedagógico | Cambios Principales |
|:---|:---|:---|
| [`paso-0`](#) | **Punto de partida: Chat MVC local** | Chat funcional con JavaFX + MVC + Observer local en memoria (`observer/`), 2 ventanas locales en una misma JVM. |
| [`paso-1`](#) | **Incorporación de LibreriaRMIMVC** | Agregado de `lib/LibreriaRMIMVC.jar` y configuración de dependencia `system` en `pom.xml`. |
| [`paso-2`](#) | **Modelo Remoto y Serialización** | Eliminación del paquete local `observer`. `IChat` extiende `IObservableRemoto`, `Chat` extiende `ObservableRemoto` y las entidades (`Usuario`, `Mensaje`) implementan `Serializable`. |
| [`paso-3`](#) | **Controlador Remoto** | `Controlador` implementa `IControladorRemoto`, recibe el stub remoto en `setModeloRemoto` y captura `RemoteException` hacia la vista. |
| [`paso-4`](#) | **Hilos de Red vs Hilo de UI** | `VistaGrafica` envuelve las actualizaciones visuales en `Platform.runLater` para evitar `IllegalStateException` de JavaFX. |
| [`paso-5`](#) | **Separación Servidor / Cliente** | Creación de `AppServidor`/`LanzadorServidor`, `AppCliente`/`LanzadorCliente`, utilidad `Puertos` y perfiles `.run/` de IntelliJ. |
| [`paso-6`](#) | **Desconexión Limpia y Anfitrión** | Cierre de ventana conectado a `cerrar(controlador, id)`. Regla del anfitrión que pausa la sala si sale y la reactiva al volver. |
| [`paso-7`](#) | **Persistencia Multisesión con Serialización** | Clase `SesionChat`, clase utilitaria `PersistenciaChat` (`ObjectOutputStream`/`ObjectInputStream`), botón "💾 Guardar chat" y carga automática al iniciar. |

---

## Estructura del Proyecto

```
ejemplo-chat-rmimvc-fx/
├── .run/
│   ├── Cliente.run.xml
│   └── Servidor.run.xml
├── lib/
│   └── LibreriaRMIMVC.jar
├── src/
│   ├── main/
│   │   ├── java/ar/edu/unlu/chatmvc/
│   │   │   ├── cliente/
│   │   │   │   ├── AppCliente.java
│   │   │   │   ├── LanzadorCliente.java
│   │   │   │   └── Puertos.java
│   │   │   ├── controlador/
│   │   │   │   └── Controlador.java
│   │   │   ├── modelo/
│   │   │   │   ├── Chat.java
│   │   │   │   ├── Eventos.java
│   │   │   │   ├── IChat.java
│   │   │   │   ├── IMensaje.java
│   │   │   │   ├── IUsuario.java
│   │   │   │   ├── Mensaje.java
│   │   │   │   ├── PersistenciaChat.java
│   │   │   │   ├── SesionChat.java
│   │   │   │   └── Usuario.java
│   │   │   ├── servidor/
│   │   │   │   ├── AppServidor.java
│   │   │   │   └── LanzadorServidor.java
│   │   │   └── vista/
│   │   │       ├── ChatController.java
│   │   │       ├── IVista.java
│   │   │       ├── LoginController.java
│   │   │       └── VistaGrafica.java
│   │   └── resources/ar/edu/unlu/chatmvc/vista/
│   │       ├── chat.fxml
│   │       └── login.fxml
│   └── test/java/ar/edu/unlu/chatmvc/modelo/
│       └── ChatTest.java
├── pom.xml
└── README.md
```
