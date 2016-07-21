package com.newteo.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joephon on 16/7/21.
 */
public class CustomView extends View{

    private int mPanelWidth;
    private float mLineHeight;
    private final int MAX_LINE = 10;
    private final int MAX_COUNT_IN_LINE = 5;
    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private float mRatioPieceOfLineHeight = 3 * 1.0f / 4;
    private boolean mIsWhite = true;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";
    private static final String INSTANCE_IS_WHITE = "instance_is_white";
    public String result;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init () {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        }
        else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
        int pieceWidth = (int) (mLineHeight * mRatioPieceOfLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIsGameOver) return false;

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);

            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArray.add(p);
            }
            else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private Point getValidPoint(int x, int y) {

        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    private void drawBoard(Canvas canvas) {
        int width = mPanelWidth;
        float height = mLineHeight;

        for (int i = 0; i < MAX_LINE; i ++) {
            int startX = (int) height / 2;
            int endX = (int) (width - height / 2);
            int y = (int) ((0.5 + i) * height);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }

    }

    private void drawPieces(Canvas canvas) {

        for (int i = 0, n = mWhiteArray.size(); i < n; i ++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - mRatioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - mRatioPieceOfLineHeight) / 2) * mLineHeight, null);
        }

        for (int i = 0, n = mBlackArray.size(); i < n; i ++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - mRatioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - mRatioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    private void checkGameOver() {

        boolean whiteWin =  checkFiveInLine(mWhiteArray);
        boolean blackWin =  checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin) {
            mIsGameOver =  true;
            mIsWhiteWinner = whiteWin;

            String text =  mIsWhiteWinner ? "黑棋弱爆了,执白棋者胜!" : "白棋弱爆了,执黑棋者胜!";
            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
            result = mIsWhiteWinner ? "上回鹿死白手" : "上回鹿死黑手";
        }

    }


    private boolean checkFiveInLine(List<Point> points) {

        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;
        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x - i, y))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;

        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x + i, y))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x, y - i))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;

        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x, y + i))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x - i, y + i))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;

        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x + i, y - i))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x - i, y - i))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;

        for (int i = 1; i < MAX_COUNT_IN_LINE; i ++) {
            if(points.contains(new Point(x + i, y + i))) {
                count++;
            }
            else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    public void reStart() {
        mIsGameOver = false;
        mIsWhite = true;
        mIsWhiteWinner = false;
        mWhiteArray.clear();
        mBlackArray.clear();
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        bundle.putBoolean(INSTANCE_IS_WHITE, mIsWhite);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver =  bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            mIsWhite = bundle.getBoolean(INSTANCE_IS_WHITE);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
