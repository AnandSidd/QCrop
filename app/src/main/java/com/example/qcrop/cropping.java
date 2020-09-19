package com.example.qcrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
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

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 80));
        Mat dilate = new Mat();
        Mat drawrect = new Mat();
        drawrect = mat;

        Imgproc.dilate(thresh, dilate, kernel, new Point(-1, -1), 2);
        Bitmap bmpdilate;
//        bmpdilate = Bitmap.createBitmap(dilate.cols(), dilate.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(dilate, bmpdilate);
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(dilate, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Iterator<MatOfPoint> each;
        each = contours.iterator();
        int qnowidth = 0;
//        Utils.matToBitmap(dilate, recta);
        List<Integer> qheight = new ArrayList<>();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            double area = Imgproc.contourArea(contour);
            Log.i("CONTAREA", String.valueOf(area));
            Rect rect;
            if (Imgproc.contourArea(contour) < 60000 && Imgproc.contourArea(contour)>25000) {
                rect = Imgproc.boundingRect(contour);
                Log.i("Rect", rect.toString());
                //Imgproc.rectangle(drawrect, rect, new Scalar(255, 0, 0, 255), 8);
                qnowidth = rect.x + rect.width;
                qheight.add(rect.y);
            }
        }

        if (qheight.size() <= 1) {
            Rect roi = new Rect(new Point(qnowidth, qheight.get(0)), new Point(matcopy.width(), matcopy.height()));
            Mat cropped = new Mat(matcopy, roi);
            Mat gray1 = new Mat();
            Imgproc.cvtColor(cropped, gray1, Imgproc.COLOR_BGR2GRAY);

            Mat thresh1 = new Mat();
            Imgproc.threshold(gray1, thresh1, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

            Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 40));
            Mat dilate1 = new Mat();
            Imgproc.dilate(thresh1, dilate1, kernel1, new Point(-1, -1), 2);
            ArrayList<MatOfPoint> contours1 = new ArrayList<MatOfPoint>();
            Imgproc.findContours(dilate1, contours1, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            Iterator<MatOfPoint> each1;
            each1 = contours1.iterator();
            qheight.clear();
            while (each1.hasNext()) {
                MatOfPoint contour1 = each1.next();
                if (Imgproc.contourArea(contour1) > 10000 && Imgproc.contourArea(contour1) < 20000) {
                    Rect rect1 = Imgproc.boundingRect(contour1);
                    qheight.add(rect1.y);
                }
            }
        }
        Log.i("Height", qheight.toString());
        Bitmap recta = null;
        String ques_num = null;
        ///home/anandshivansh/QCrop/app/src/main/assets/tessdata/eng.traineddata
        //app/src/main/assets/tessdata/eng.traineddata
        for (int i = 0; i < qheight.size(); i++) {
            if (i == 0) {
                Rect roi = new Rect(new Point(0, qheight.get(0)), new Point(mat.width(), mat.height()));
//                Rect roi = new Rect(0, qheight.get(0), matcopy.width(), matcopy.height());
                Imgproc.rectangle(drawrect, new Point(0, qheight.get(0)), new Point(mat.width(), mat.height()), new Scalar(255, 0, 0, 255), 8);

                if (roi.x >= 0 && roi.y >= 0 && roi.width <= mat.cols() && roi.height <= mat.rows()) {
                    //Log.i("Cropping entered", "hjkbkjsgb");
                    Mat cropped = new Mat(mat, roi);
//                    Size s = new Size(roi.width * 4, roi.height * 4);
//                    Mat resized = new Mat();
//                    Imgproc.resize(cropped, resized, s);
//                    Bitmap bitmap = Bitmap.createBitmap(resized.cols(), resized.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(resized, bitmap);
//                    String text = doOCR(bitmap);
//                    //Log.i("OCR Text", text);
//                    char firstword = text.charAt(0);
//                    //Log.i("First Text", String.valueOf(firstword));
//                    if (firstword>=48 && firstword<=57){
//                        String[] words = text.split(" ");
//                        ques_num = words[0];
//                        if (TextUtils.isDigitsOnly(ques_num)){
//                            ques_num = words[0];
//                        }else{
//                            ques_num = ques_num.replaceAll("[^a-zA-Z0-9]", "");
//                        }
//                    }
//                    else if(firstword == 'Q'){
//                        String[] words = text.split(" ");
//
//                        if(words[0].equals("Q.")){
//                            ques_num = words[1];
//                            if(TextUtils.isDigitsOnly(ques_num)){
//                                ques_num = words[1];
//                                Log.i("Words", words[1]);
//                            }else {
//                                ques_num = words[1].replace("l", "1");
//                            }
//                        }else{
//                            ques_num = words[0].replace("Q.", "");
//                        }
//                    }
//                    else {
//                        ques_num = "0";
//                    }
                }

                List<Integer> questionrect1 = new ArrayList<>();
                questionrect1.add(0, qnowidth);
                questionrect1.add(1, qheight.get(0));
                questionrect1.add(2, matcopy.width());
                questionrect1.add(3, matcopy.height());
//                if (ques_num != null) {
//                    questionrect1.add(4, Integer.parseInt(ques_num));
//                }else {
//                    questionrect1.add(4, 0);
//                }
                responses.add(questionrect1);
            } else if (i != 0) {
                Bitmap bitmap;
                Rect roi = new Rect(new Point(0, qheight.get(i)), new Point(mat.width(), qheight.get(i - 1)));
                Imgproc.rectangle(drawrect, new Point(0, qheight.get(i)), new Point(mat.width(), qheight.get(i - 1)), new Scalar(255, 0, 0, 255), 8);
                if (roi.x >= 0 && roi.y >= 0 && roi.width <= mat.cols() && roi.height <= mat.rows()) {
                    // Log.i("Cropping entered", "looop2");
                    Mat cropped = new Mat(mat, roi);
//                    Size s = new Size(roi.width * 4 , roi.height * 4);
//                    Mat resized = new Mat();
//                    Imgproc.resize(cropped, resized, s);
//                    bitmap = Bitmap.createBitmap(resized.cols(), resized.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(resized, bitmap);
//                    String text = doOCR(bitmap);
//                    //Log.i("OCR Text",text);
//                    char firstword = text.charAt(0);
//                    if (firstword>=48 && firstword<=57){
//                        String[] words = text.split(" ");
//                        ques_num = words[0];
//                        if (TextUtils.isDigitsOnly(ques_num)){
//                            ques_num = words[0];
//                        }else{
//                            ques_num = ques_num.replaceAll("[^a-zA-Z0-9]", "");
//                        }
//                    }
//                    else if(firstword == 'Q'){
//                        String[] words = text.split(" ");
//                        if(words[0].equals("Q.")){
//                            ques_num = words[1];
//                            if(TextUtils.isDigitsOnly(ques_num)){
//                                ques_num = words[1];
//                                //Log.i("Words", words[1]);
//                            }else {
//                                ques_num = words[1].replace("l", "1");
//                            }
//                        }else{
//                            ques_num = words[0].replace("Q.", "");
//                        }
//                    }
//                    else {
//                        ques_num = "0";
//                    }
                }


                List<Integer> questionrect2 = new ArrayList<>();
                questionrect2.add(0, qnowidth);
                questionrect2.add(1, qheight.get(i));
                questionrect2.add(2, matcopy.width());
                questionrect2.add(3, qheight.get(i-1));
//                if (ques_num != null) {
//                    questionrect2.add(4, Integer.parseInt(ques_num));
//                }else {
//                    questionrect2.add(4, 0);
//                }
                responses.add(questionrect2);
            }
        }
        return responses;
//        bmpdilate = Bitmap.createBitmap(dilate.cols(), dilate.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(drawrect, bmpdilate);
//        return bmpdilate;
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

        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10));
        Mat dilate = new Mat();
        Mat drawrect = new Mat();
        drawrect = mat;
        Bitmap bmpdilate = null;

        Imgproc.dilate(thresh, dilate, kernel, new Point(-1, -1), 2);


        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(dilate, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Iterator<MatOfPoint> each;
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            Rect rect = Imgproc.boundingRect(contour);
            if(rect.height>mat.height()*0.75 && rect.height<mat.height()) {
                //Imgproc.rectangle(drawrect, rect, new Scalar(255, 0, 0, 255), 5);
                centreline = true;
            }
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