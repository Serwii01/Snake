# 🐍 Snake Game - Neon Edition

¡Bienvenido a mi versión del mítico Snake! Este proyecto es el clásico juego de la serpiente hecho para Android usando Java puro. Nada de motores pesados como Unity; aquí todo está hecho a mano y con código limpio.

## ¿Qué tiene de especial?
*   **Look Neon:** Un diseño oscuro con colores chillones que queda genial.
*   **100% Código Propio:** El motor del juego está hecho desde cero.
*   **Guarda tu récord:** Si haces una puntuación increíble, se queda guardada aunque cierres la app.
*   **Sonido:** Tiene un efecto de sonido cuando la serpiente come.

---

## ⚙️ ¿Cómo funciona esto por dentro?

La app es sencilla: tiene dos pantallas y una clase "mágica" que hace todo el trabajo duro. Vamos paso a paso:

### 1. El Menú (`MainActivity`)
Es la pantalla que ves al abrir la app. Aquí no hay mucho misterio, pero hace dos cosas importantes:
*   **Muestra el Récord:** Usa una cosa llamada `SharedPreferences` (que es como una libreta interna de la app) para leer la puntuación máxima guardada.
*   **El truco del `onResume`:** Usamos este método para que, si juegas una partida y vuelves al menú, el récord se actualice al instante. Si no lo hiciéramos ahí, verías el número viejo hasta que reiniciaras la app.

### 2. La Caja del Juego (`GameActivity`)
Esta actividad es solo un contenedor vacío. Imagínatelo como un marco de un cuadro. Su único trabajo es decirle a Android: "Oye, carga aquí dentro la vista del juego (`SnakeView`)". No tiene lógica, solo sostiene el juego.

### 3. El Corazón del Juego (`SnakeView`)
Aquí es donde está toda la "chicha". Es una **Vista Personalizada** (`Custom View`), lo que significa que en vez de usar botones o textos normales, nosotros le decimos a Android píxel a píxel qué dibujar.

#### A. Preparando el terreno
Cuando arranca, preparamos los "pinceles" (`Paint`) con los colores neón y cargamos el sonido en memoria. También calculamos cuánto mide la pantalla (`onSizeChanged`) para saber cuántos cuadraditos caben en el tablero.

#### B. El Bucle Infinito (Game Loop)
Un juego necesita moverse todo el rato. Para esto usamos un truco con un `Handler` (un temporizador).
1.  **Ejecuta:** Mueve la serpiente y comprueba cosas.
2.  **Pinta:** Dibuja todo de nuevo.
3.  **Espera:** Se pausa 200 milisegundos.
4.  **Repite:** Vuelve al paso 1.
Esto crea la ilusión de movimiento a 5 fotogramas por segundo. ¡Simple pero efectivo!

#### C. La Lógica (`actualizar`)
Cada vez que el bucle "piensa", hace esto:
1.  **Calcula la cabeza:** Mira hacia dónde vas y calcula la siguiente casilla.
2.  **¿Choque?**: Si la casilla está fuera de la pantalla o toca tu propio cuerpo -> ¡Game Over!
3.  **¿Comida?**:
    *   Si la cabeza toca la comida: Sumamos punto, suena el audio y la serpiente crece (simplemente no borramos la cola).
    *   Si no come: La serpiente se mueve "borrando" el último trozo de la cola para mantener su tamaño.

#### D. El Pintor (`onDraw`)
Este método es el artista. Android le da un lienzo en blanco (`Canvas`) y nosotros pintamos:
*   Primero la cuadrícula flojita de fondo.
*   Luego la bolita de comida.
*   Y al final, recorremos toda la lista de puntos de la serpiente y dibujamos rectángulos redondeados para que quede suave.

#### E. Los Dedos (`onTouchEvent`)
Para controlarla, detectamos cuando pones el dedo y cuando lo levantas.
*   Calculamos la diferencia: ¿Has movido el dedo más en horizontal o en vertical?
*   Dependiendo de eso, cambiamos la dirección.
*   *Nota:* El código impide que hagas un giro de 180º (ej. ir hacia abajo si vas hacia arriba) para que no te choques contigo mismo por error.

#### F. Limpieza
Cuando cierras el juego, nos aseguramos de liberar el sonido y parar el bucle para que el móvil no se quede gastando batería a lo tonto (`onDetachedFromWindow`).

---

## Estructura
*   `MainActivity.java`: El menú.
*   `SnakeView.java`: Donde ocurre la magia (lógica + gráficos).
*   `GameActivity.java`: El envoltorio del juego.
*   `ehhsound.mp3`: El sonido que hace al comer.

## Autor
Creado por **Sergio Fernández Morales**.
