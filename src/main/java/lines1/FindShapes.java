package lines1;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class FindShapes
{
    static class Result {
        public Mat display;
        public Point[][] contours;
    }
    private static final Size BLUR_SIZE = new Size(3,3);

    public static Result processImage(Mat original)
    {   
        Result result = new Result();

        Mat filtered = new Mat();
        Mat blurred = new Mat();
        Mat hsv = new Mat();

        // blur image
        Imgproc.blur(original, blurred, BLUR_SIZE);

        //convert image to HSV color space and find green
        Imgproc.cvtColor(blurred, hsv, Imgproc.COLOR_BGR2HSV);
        Scalar minGreen = new Scalar(30, 80, 50);
        Scalar maxGreen = new Scalar(90, 255, 255);
        Core.inRange(hsv, minGreen, maxGreen, filtered);

        //find contours (non approximated)
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        //System.out.println("contours count = " + contours.size());
        result.contours = new Point[contours.size()][];
        for (int i = 0; i < contours.size(); i++) {
            //approximate contours
            Point[] c = contours.get(i).toArray();
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(c),approx,3,true);
            c = approx.toArray();

            /*System.out.println("len =" + c.length);
            for (int j = 0; j < c.length; j++)
            {
                System.out.println("x = " + c[j].x + " y = " + c[j].y);
           
            }*/
            result.contours[i] = c;
        }

        //visualise results
        Mat display = original;
        // Mat display = new Mat();
        // Imgproc.cvtColor(filtered, display, Imgproc.COLOR_GRAY2BGR);
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(display, contours, i, new Scalar(0, 0, 255), -1);
        }
        result.display = display;
        return result;
    }
}