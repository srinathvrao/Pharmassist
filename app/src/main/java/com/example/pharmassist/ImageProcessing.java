package com.example.pharmassist;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Yo7A on 5/9/2017.
 */

public abstract class ImageProcessing {


    private static long decodeYUV420SPtoRedBlueGreenSum(Bitmap bmap, int width, int height, int type) {
        if (bmap == null) return 0;

        final int frameSize = width * height;

        long sum=0;
        long sumr = 0;
        long sumg = 0;
        long sumb = 0;
//        for (int j = 0, yp = 0; j < height; j++) {
//            int uvp = frameSize + ((j >> 1) * width), u = 0, v = 0;
//            for (int i = 0; i < width; i++, yp++) {
//
//                int y = (0xff & yuv420sp[yp]) - 16;
//
//                Log.v("heartbeat","in sum " +uvp+" "+y+" "+yuv420sp[yp]+" "+yuv420sp.length);
//                if (y < 0) y = 0;
//                if ((i & 1) == 0) {
//                    v = (0xff & yuv420sp[uvp++]) - 128;
//                    u = (0xff & yuv420sp[uvp++]) - 128;
//                }
//                int y1192 = 1192 * y;
//                int r = (y1192 + 1634 * v);
//                int g = (y1192 - 833 * v - 400 * u);
//                int b = (y1192 + 2066 * u);
//
//                if (r < 0) r = 0;
//                else if (r > 262143) r = 262143;
//                if (g < 0) g = 0;
//                else if (g > 262143) g = 262143;
//                if (b < 0) b = 0;
//                else if (b > 262143) b = 262143;
//
//                int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
//                int red = (pixel >> 8) & 0xff;
//                int green = (pixel >> 4) & 0xff;
//                int blue = pixel&0xff;
//                sumr += red;
//                sumg +=green;
//                sumb +=blue;
//            }
//        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int colour = bmap.getPixel(i,j);
                int red = Color.red(colour);
                int green = (int) Color.green(colour);
                int blue = Color.blue(colour);
                sumr += red;
                sumb += blue;
                sumg += green;
            }
        }
        Log.v("heartbeat",sumg+" - sumgreen ");
        Log.v("heartbeat",sumb+" - sumblue ");
        Log.v("heartbeat",sumr+" - sumred ");


        switch(type){
            case (1): sum =sumr;//*((long) 0.7);
                break;
            case (2): sum =sumb;//*((long) 0.55);
                break;
            case (3): sum = sumg;//*((long)0.65);
                break;
        }
        return sum;
    }

    /**
     * Given a byte array representing a yuv420sp image, determine the average
     * amount of red in the image. Note: returns 0 if the byte array is NULL.
     *
     * @param bmap
     *            Byte array representing a yuv420sp image
     * @param width
     *            Width of the image.
     * @param height
     *            Height of the image.
     * @return int representing the average amount of red in the image.
     */
    public static double decodeYUV420SPtoRedBlueGreenAvg(Bitmap bmap, int height, int width, int type) {
        if (bmap == null) {
            Log.v("is bitmap null","yes it is");

            return 0;}
        final int frameSize = width * height;
        Log.v("heartbeat","in rgbavg "+width+" "+height);
        long sum = decodeYUV420SPtoRedBlueGreenSum(bmap, width, height, type);
        long mean = (sum / frameSize);

        return mean;
    }
}


