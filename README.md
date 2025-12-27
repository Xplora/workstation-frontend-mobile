# TripMatch Mobile - Android App 
##  Descripción

Aplicación móvil nativa para la plataforma **TripMatch / Xplora**, desarrollada en Android (Kotlin). Esta aplicación permite a los usuarios interactuar con agencias de turismo, buscar destinos y gestionar sus matches de viaje directamente desde su dispositivo móvil.

El proyecto está construido utilizando los estándares modernos de desarrollo Android, con una interfaz declarativa basada en Jetpack Compose y una arquitectura robusta para la persistencia de datos y comunicación en red.

##  Tech Stack & Librerías

El proyecto utiliza las siguientes tecnologías y dependencias clave:

* **Lenguaje:** Kotlin (JVM Target 11)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Networking:**
    * [Retrofit](https://square.github.io/retrofit/) + Gson (Consumo de API REST)
    * OkHttp Logging Interceptor (Depuración de red)
* **Persistencia de Datos:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite abstracto)
* **Carga de Imágenes:**
    * [Coil](https://coil-kt.github.io/coil/)
    * Landscapist Glide
* **Distribución & Analytics:**
    * Firebase App Distribution (CI/CD para QA)
    * Firebase Analytics

##  Configuración del Proyecto

### Requisitos del Sistema
* **Android Studio:** Koala / Ladybug o superior (Recomendado).
* **JDK:** Version 11 o superior.
* **Min SDK:** 29 (Android 10).
* **Target SDK:** 36 (Android 15).

### Instalación

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/Xplora/workstation-frontend-mobile.git](https://github.com/Xplora/workstation-frontend-mobile.git)
    ```

2.  **Configurar Google Services:**
    * Descarga el archivo `google-services.json` desde la consola de Firebase.
    * Colócalo en la carpeta `/app` del proyecto.

3.  **Sincronizar Gradle:**
    Abre el proyecto en Android Studio y presiona "Sync Project with Gradle Files".

##  Compilación y Ejecución

### Modo Debug
Para ejecutar en un emulador o dispositivo físico conectado:
```bash
./gradlew installDebug
