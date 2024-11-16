package com.example.mipapalotedigital.models;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.mipapalotedigital.R;

public class CircleProgressBar extends View {

    // Atributos de la barra de progreso
    private float strokeWidth = 4; // Ancho del trazo de la barra
    private float progress = 0; // Progreso de la barra (de 0 a 100)
    private int min = 0; // Valor mínimo del progreso
    private int max = 100; // Valor máximo del progreso
    private int color = Color.DKGRAY; // Color de la barra de progreso

    // Componentes para el dibujo de la barra
    private RectF rectF; // Rectángulo para definir los límites de la barra
    private Paint backgroundPaint; // Pintura para el fondo de la barra (progreso restante)
    private Paint foregroundPaint; // Pintura para el primer plano de la barra (progreso completado)

    // Constructor que inicializa la vista
    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs); // Inicializa los atributos y objetos Paint
    }

    // Inicializa los atributos y los objetos Paint
    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF(); // Inicializa el rectángulo que define la barra

        // Obtiene los atributos personalizados de la vista
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleProgressBar,
                0, 0);

        try {
            // Lee los valores de los atributos
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress);
            color = typedArray.getColor(R.styleable.CircleProgressBar_progressbarColor, color);
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min);
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max);
        } finally {
            typedArray.recycle(); // Libera los recursos de TypedArray
        }

        // Configura la pintura del fondo (barra restante)
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(adjustAlpha(color)); // Ajusta la opacidad para el fondo
        backgroundPaint.setStyle(Paint.Style.STROKE); // Solo trazo
        backgroundPaint.setStrokeWidth(strokeWidth); // Establece el ancho del trazo

        // Configura la pintura del primer plano (barra de progreso completado)
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color); // Color del progreso
        foregroundPaint.setStyle(Paint.Style.STROKE); // Solo trazo
        foregroundPaint.setStrokeWidth(strokeWidth); // Establece el ancho del trazo
    }

    // Metodo auxiliar para ajustar la transparencia (alfa) del color
    private int adjustAlpha(int color) {
        int alpha = Math.round(Color.alpha(color) * (float) 0.3); // Ajusta el valor alfa
        int red = Color.red(color); // Extrae el valor rojo
        int green = Color.green(color); // Extrae el valor verde
        int blue = Color.blue(color); // Extrae el valor azul
        return Color.argb(alpha, red, green, blue); // Devuelve el color con la opacidad ajustada
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calcula las dimensiones de la vista basándose en las restricciones de medida
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height); // El tamaño mínimo de la vista
        setMeasuredDimension(min, min); // Establece las dimensiones finales
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2); // Ajusta el rectángulo de la barra
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Dibuja el fondo de la barra (el progreso restante)
        canvas.drawOval(rectF, backgroundPaint);

        // Dibuja el primer plano de la barra (el progreso completado)
        float angle = 360 * progress / max; // Calcula el ángulo basado en el progreso
        // Ángulo de inicio del progreso (comienza desde arriba)
        int startAngle = -90;
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint); // Dibuja el arco de progreso
    }

    // Metodo setter para actualizar el progreso
    public void setProgress(float progress) {
        this.progress = progress; // Actualiza el valor del progreso
        invalidate(); // Redibuja la vista con el nuevo progreso
    }

    // Metodo getter para obtener el progreso actual
    public float getProgress() {
        return progress; // Devuelve el progreso actual
    }
}
