
## Características Generales

*   **Motor Gráfico:** Renderizado 2D optimizado mediante `Canvas` y `Paint`.
*   **Persistencia de Datos:** Almacenamiento local de la puntuación máxima.
*   **Sistema de Audio:** Reproducción de efectos de sonido sincronizados con eventos del juego.
*   **Control Gestual:** Detección de deslizamientos (swipes) para el control de la serpiente.

---

## Arquitectura y Funcionamiento

La aplicación sigue una estructura modular sencilla, dividiendo la responsabilidad entre la interfaz de usuario y la lógica del juego. A continuación se detalla el funcionamiento de cada componente:

### 1. MainActivity (Menú Principal)
Es el punto de entrada de la aplicación. Su responsabilidad principal es la gestión del estado global de la puntuación y la navegación.

*   **Gestión del Récord:** Utiliza `SharedPreferences` para almacenar y recuperar la puntuación máxima de forma persistente.
*   **Ciclo de Vida:** Implementa la lectura de datos en el método `onResume()`. Esto garantiza que la puntuación mostrada en la pantalla principal se actualice inmediatamente al regresar de una partida, sin necesidad de reiniciar la actividad.
*   **Navegación:** Inicia la actividad del juego (`GameActivity`) mediante un `Intent` explícito.

### 2. GameActivity (Contenedor)
Funciona como un contenedor para la vista del juego. Es una actividad mínima que carga el layout `activity_game.xml`, el cual contiene la instancia de `SnakeView`. Su función es proporcionar el contexto de pantalla completa necesario para la ejecución del juego.

### 3. SnakeView (Motor del Juego)
Esta clase es el núcleo del proyecto. Hereda de la clase `View` de Android y encapsula toda la lógica, el renderizado y el manejo de entradas del juego.

#### A. Inicialización
En el constructor y en el método `onSizeChanged()`, se configuran los recursos gráficos (`Paint`) y se calculan las dimensiones del tablero basándose en la resolución del dispositivo. Se inicializa también el `MediaPlayer` para los efectos de sonido.

#### B. Bucle de Juego (Game Loop)
Para lograr la animación, se implementa un bucle de juego utilizando un `Handler` y un `Runnable`.
*   El sistema ejecuta el método de actualización lógica y solicita un redibujado (`invalidate()`).
*   Posteriormente, programa la siguiente ejecución con un retraso de 200ms, estableciendo así la velocidad del juego.

#### C. Lógica de Actualización
El método `actualizar()` gestiona las reglas del juego en cada ciclo:
1.  **Movimiento:** Calcula la nueva posición de la cabeza de la serpiente según la dirección actual.
2.  **Detección de Colisiones:** Verifica si la nueva posición está fuera de los límites del tablero o si coincide con una coordenada ocupada por el cuerpo de la serpiente.
3.  **Mecánica de Alimentación:**
    *   Si la serpiente alcanza la comida, se incrementa la puntuación, se reproduce el sonido y la serpiente crece (no se elimina el último segmento).
    *   Si no come, la serpiente se desplaza manteniendo su longitud (se elimina el último segmento de la cola).

#### D. Renderizado
El método `onDraw(Canvas canvas)` se encarga de dibujar el estado actual del juego:
*   Dibuja una cuadrícula de fondo para referencia visual.
*   Renderiza la comida como un elemento circular.
*   Dibuja el cuerpo de la serpiente iterando sobre la lista de coordenadas y pintando rectángulos con bordes redondeados.

#### E. Control de Entrada
Se sobrescribe el método `onTouchEvent()` para detectar gestos táctiles:
*   Calcula el desplazamiento entre el punto inicial y final del toque.
*   Determina la dirección del deslizamiento (horizontal o vertical) comparando las diferencias en los ejes X e Y.
*   Aplica cambios de dirección validando que no sean opuestos al movimiento actual (para evitar colisiones inmediatas).

#### F. Gestión de Recursos
El método `onDetachedFromWindow()` asegura la liberación correcta de recursos, como el objeto `MediaPlayer` y la detención del bucle de juego, previniendo fugas de memoria al cerrar la actividad.

---

## Estructura del Proyecto

*   **`MainActivity.java`**: Lógica de la pantalla de inicio y puntuaciones.
*   **`GameActivity.java`**: Actividad contenedora del juego.
*   **`SnakeView.java`**: Clase personalizada que contiene toda la lógica y gráficos del juego.
*   **`res/layout/`**: Archivos XML que definen la interfaz de usuario.
*   **`res/raw/`**: Contiene los recursos de audio.

## Autor
Desarrollado por **Sergio Fernández Morales**.
