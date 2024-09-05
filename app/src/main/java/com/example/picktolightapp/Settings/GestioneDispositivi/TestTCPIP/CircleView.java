package com.example.picktolightapp.Settings.GestioneDispositivi.TestTCPIP;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
    private Paint paint1;
    private Paint paint2;



    // Costruttore per l'inflating da XML
    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Inizializza i colori dei pennelli
    private void init() {
        paint1 = new Paint();
        int darkerRed = Color.rgb(80, 0, 0);
        int darkerGreen = Color.rgb(0, 80, 0);

        paint1.setColor(darkerRed); // Colore del primo cerchio
        paint2 = new Paint();
        paint2.setColor(darkerGreen); // Colore del secondo cerchio
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int radius = 20;
        int gap = 65;

        int x1 = (width / 2) - radius - (gap / 2);
        int y1 = height / 2;


        int x2 = (width / 2) + radius + (gap / 2); // Posizionato un po' a destra del centro
        int y2 = height / 2; // Centrato verticalmente

        // Disegna il primo cerchio
        canvas.drawCircle(x1, y1, radius, paint1);

        // Disegna il secondo cerchio
        canvas.drawCircle(x2, y2, radius, paint2);
    }

    public void setLXColor(int color){
        paint1.setColor(color);
        invalidate();
    }


    public void setDXColor(int color){
        paint2.setColor(color);
        invalidate();
    }

    public void setCircleColors(int color1, int color2) {
        paint1.setColor(color1);
        paint2.setColor(color2);
        invalidate(); // Ridisegna la vista
    }
}
