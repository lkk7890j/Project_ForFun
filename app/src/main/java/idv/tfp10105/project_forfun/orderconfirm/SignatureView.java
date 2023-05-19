package idv.tfp10105.project_forfun.orderconfirm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SignatureView extends View implements View.OnTouchListener {

    private List<List<Path>> signaturePaths = new ArrayList<>();
    private List<Path> activeSignaturePaths;
    private Paint signaturePaint;
    private Paint baselinePaint;

    private int baselinePaddingHorizontal = 0;
    private int baselinePaddingBottom = 0;
    private int baselineXMark = 0;
    private int baselineXMarkOffsetVertical = 0;

    private int[] lastTouchEvent;

    public SignatureView(Context context) {
        super(context);

        initDefaultValues();
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initDefaultValues();
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDefaultValues();
    }

    protected void initDefaultValues() {
        setOnTouchListener(this);

        signaturePaint = new Paint();
        signaturePaint.setAntiAlias(true);
        signaturePaint.setColor(Color.BLUE);
        signaturePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        signaturePaint.setStrokeWidth(15f);
        signaturePaint.setStrokeCap(Paint.Cap.ROUND);

        baselinePaint = new Paint();
        baselinePaint.setAntiAlias(true);
        baselinePaint.setColor(Color.BLACK);
        baselinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        baselinePaint.setStrokeWidth(1f);
        baselinePaint.setStrokeCap(Paint.Cap.ROUND);
    }


    @Override
    public boolean onTouch(View view, MotionEvent e) {
        int[] event = new int[]{(int) e.getX(), (int) e.getY()};

        if (e.getAction() == MotionEvent.ACTION_UP) {
            lastTouchEvent = null;

            signaturePaths.add(activeSignaturePaths);
            activeSignaturePaths = new ArrayList<>();
        } else if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(activeSignaturePaths == null || activeSignaturePaths.size() < 1))
                signaturePaths.add(activeSignaturePaths);

            activeSignaturePaths = new ArrayList<>();
        } else {
            Path path = new Path();
            path.moveTo(lastTouchEvent[0], lastTouchEvent[1]);
            path.lineTo(event[0], event[1]);

            activeSignaturePaths.add(path);
        }

        lastTouchEvent = event;

        postInvalidate();

        return true;
    }

    public void undoLastSignaturePath() {
        if (0 < signaturePaths.size()) {
            signaturePaths.remove(signaturePaths.size() - 1);

            postInvalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawIndicators(canvas);
        drawSignaturePaths(canvas);
    }

    protected void drawIndicators(Canvas canvas) {
        canvas.drawLine(baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                canvas.getWidth() - baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                baselinePaint);

        drawXMark(canvas);
    }

    protected void drawXMark(Canvas canvas) {
        int radius = baselineXMark / 2;
        int cX = baselinePaddingHorizontal + radius;
        int cY = canvas.getHeight() - baselinePaddingBottom - radius - baselineXMarkOffsetVertical;

        canvas.save();
        canvas.rotate(-45, cX, cY);
        canvas.drawLine(cX - radius, cY, cX + radius, cY, baselinePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(45, cX, cY);
        canvas.drawLine(cX - radius, cY, cX + radius, cY, baselinePaint);
        canvas.restore();
    }

    protected void drawSignaturePaths(Canvas canvas) {
        for (List<Path> l : signaturePaths) {
            for (Path p : l) {
                canvas.drawPath(p, signaturePaint);
            }
        }

        if (activeSignaturePaths != null) {
            for (Path p : activeSignaturePaths) {
                canvas.drawPath(p, signaturePaint);
            }
        }
    }

    public Bitmap getContentDataURI() {
        setDrawingCacheEnabled(true);

        Bitmap bitmap = getDrawingCache();
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() - 1);
        bitmap.recycle();

        return cropBitmap;

    }

}