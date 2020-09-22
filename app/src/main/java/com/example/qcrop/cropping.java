package com.example.qcrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class cropping {

    public cropping() {
    }

    public List<List<Integer>> extractquestion(Bitmap bmp) {


        List<List<Integer>> responses = new ArrayList<>();

        Mat mat = new Mat();
        Utils.bitmapToMat(bmp, mat);

        Mat matcopy = new Mat();
        Utils.bitmapToMat(bmp, matcopy);

        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);


        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 100));
        Mat dilate = new Mat();
        Mat drawrect = mat.clone();

        Imgproc.dilate(thresh, dilate, kernel, new Point(-1, -1), 2);

        Mat dilate2 = dilate.rowRange(dilate.rows()/5, dilate.rows());

        Mat columnss = new Mat();
        Core.reduce(dilate2, columnss, 0, Core.REDUCE_AVG);
        int flag = -1;
        double crop1=0.0, crop2=0.0;
        for(int i=0;i<columnss.cols();i++){
            double[] pixelval = columnss.get(0, i);
            //Log.i("Column pixels", String.valueOf(pixelval[0]));
            if(pixelval[0] > 0 && flag<0){
                crop1 = i;
                flag++;
            }
            if(pixelval[0]< 5 && flag==0){
                crop2 = i;
                Log.i("Column pixels", String.valueOf(crop2));
                flag++;
            }
            if (flag==1){
                break;
            }
        }
        Imgproc.rectangle(dilate, new Point(crop1, 0), new Point(crop2, mat.height()), new Scalar(255, 0, 0, 255), 8);
        Bitmap bmpdilate;

        Rect roi1 = new Rect(new Point(crop1, 0), new Point(crop2, matcopy.height()));
        Mat cropped1 = new Mat(matcopy, roi1);
        Mat cropcopy = cropped1.clone();
        Mat grayq = new Mat();
        Imgproc.cvtColor(cropped1, grayq, Imgproc.COLOR_BGR2GRAY);
        Mat thresh2 = new Mat();
        Imgproc.threshold(grayq, thresh2, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        Mat kernelq = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
        Imgproc.dilate(thresh2, cropped1, kernelq, new Point(-1, -1), 2);
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(cropped1, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Iterator<MatOfPoint> each;
        each = contours.iterator();
        int qnowidth = 0;
        List<Integer> qheight = new ArrayList<>();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            Rect rect;
            if (Imgproc.contourArea(contour) > 0) {
                rect = Imgproc.boundingRect(contour);
                //Log.i("Rect", rect.toString());
                Imgproc.rectangle(cropcopy, rect, new Scalar(255, 0, 0, 255), 8);
                qnowidth = rect.x + rect.width;
                qheight.add(rect.y);
            }
        }
        bmpdilate = Bitmap.createBitmap(cropped1.cols(), cropped1.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cropcopy, bmpdilate);

        for (int i = 0; i < qheight.size(); i++) {
            if (i == 0) {
                List<Integer> questionrect1 = new ArrayList<>();
                questionrect1.add(0, qnowidth);
                questionrect1.add(1, qheight.get(0));
                questionrect1.add(2, matcopy.width());
                questionrect1.add(3, matcopy.height());
                responses.add(questionrect1);
            } else if (i != 0) {
                List<Integer> questionrect2 = new ArrayList<>();
                questionrect2.add(0, qnowidth);
                questionrect2.add(1, qheight.get(i));
                questionrect2.add(2, matcopy.width());
                questionrect2.add(3, qheight.get(i-1));
                responses.add(questionrect2);
            }
        }
        return responses;
    }
    public List<List<Integer>> autocrop(Bitmap bitmap) throws Exception {
        List<List<Integer>> rectanglecoord = new ArrayList<>();
        Mat mat = new Mat();
        boolean centreline = false;
        Utils.bitmapToMat(bitmap, mat);

        Mat matcopy = new Mat();
        Utils.bitmapToMat(bitmap, matcopy);

        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        Mat thresh2 = new Mat();
        Imgproc.threshold(gray, thresh2, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Mat columns = new Mat();
        Core.reduce(thresh2, columns, 0, Core.REDUCE_AVG);
        Log.i("Columns", Arrays.toString(columns.get(0, bitmap.getWidth()/2)));
        double[] centrepixelval = columns.get(0, bitmap.getWidth()/2);

        if(centrepixelval[0] > 250){
            centreline = true;
        }

        if(centreline){
            Bitmap half1 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth()/2, bitmap.getHeight());
            Bitmap half2 = Bitmap.createBitmap(bitmap,bitmap.getWidth()/2 + 30,0,bitmap.getWidth()/2 - 30, bitmap.getHeight());
            List<List<Integer>> response1 = extractquestion(half1);
            List<List<Integer>> response2 = extractquestion(half2);
            for (int i=0;i<response2.size();i++){
                List<Integer> innerlist;
                innerlist = response2.get(i);
                int x1= innerlist.get(0);
                x1 += ((bitmap.getWidth() / 2) + 30);
                innerlist.remove(0);
                innerlist.add(0, x1);
                int x2 = innerlist.get(2);
                x2 = bitmap.getWidth();
                innerlist.remove(2);
                innerlist.add(2, x2);
            }
            rectanglecoord.addAll(response2);
            rectanglecoord.addAll(response1);

        }else {
            rectanglecoord = extractquestion(bitmap);
        }

    Log.i("centreline", String.valueOf(centreline));
    return rectanglecoord;
    }
    public String doOCR(Context context, final Bitmap bitmap) {
        final TesseractOCR mTessOCR = new TesseractOCR(context, "eng");
        return mTessOCR.getOCRResult(bitmap);
    }
    public Dictionary<String, String> answerkey(Context context, Bitmap bmp){
        Mat mat = new Mat();
        Utils.bitmapToMat(bmp, mat);
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        Bitmap threshimg = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(thresh, threshimg);
        String text = doOCR(context, threshimg);
        Log.i("ANS", text);
        String[] lines = text.split("\n");
        Log.i("lines", String.valueOf(lines));
        List<String> quesnumber = new ArrayList<>();
        List<String> answer = new ArrayList<>();
        int count = 0;
        for(int i=0;i<lines.length;i++) {
            if (lines[i].contains("Answer")) {
                count = i;
            }
        }
        for(int x=count+1;x<lines.length;x++){
            String[] words1 = lines[x].split(" ");
            for(int j=0;j<words1.length;j++){
                if(j%2==0){
                    quesnumber.add(words1[j]);
                }else {
                    answer.add(words1[j]);
                }
            }
        }
        Log.i("Question", String.valueOf(quesnumber));
        Log.i("Answers", String.valueOf(answer));
        Dictionary<String, String> responseanswer = new Hashtable<>();
        for(int i=0;i<quesnumber.size();i++){
            responseanswer.put(quesnumber.get(i), answer.get(i));
        }
        return responseanswer;
    }
}