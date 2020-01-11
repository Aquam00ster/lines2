package lines1;

import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;

public class DisplayImage
{
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public static BufferedImage Mat2BufferedImage(Mat m)
    {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1)
        {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;
    }

    public static void displayImage(Image img)
    {   
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());        
        frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);     
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static final int MAX_LOW_THRESHOLD = 100;
    private static final int RATIO = 3;
    private static final int KERNEL_SIZE = 3;
    private static final Size BLUR_SIZE = new Size(3,3);

    static Mat processImage(Mat original)
    {   
        Mat filtered = new Mat();
        Mat blurred = new Mat();
        Mat hsv = new Mat();

        // blur image
        Imgproc.blur(original, blurred, BLUR_SIZE);

        //convert image to HSV color space and find green
        Imgproc.cvtColor(blurred, hsv, Imgproc.COLOR_BGR2HSV);
        Scalar minGreen = new Scalar(30, 100, 50);
        Scalar maxGreen = new Scalar(90, 255, 255);
        Core.inRange(hsv, minGreen, maxGreen, filtered);

        //find contours (non approximated)
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        System.out.println("contours count = " + contours.size());
        for (int i = 0; i < contours.size(); i++) {
            //approximate contours
            Point[] c = contours.get(i).toArray();
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(c),approx,3,true);
            c = approx.toArray();

            System.out.println("len =" + c.length);
            for (int j = 0; j < c.length; j++)
            {
                System.out.println("x = " + c[j].x + " y = " + c[j].y);
                // TODO save to object
            }
        }

        //visualise results
        Mat display = original;
        // Mat display = new Mat();
        // Imgproc.cvtColor(filtered, display, Imgproc.COLOR_GRAY2BGR);
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(display, contours, i, new Scalar(0, 0, 255), -1);
        }
        return display;
    }

    public static void main(String[] args)
    {
        Mat m;
        if (args.length == 0)
        {
            VideoCapture capture = new VideoCapture(1);
            m = new Mat();
            if (!capture.read(m))
            {
                System.out.println("Error");
                return;
            }
        }
        else{
            m = Imgcodecs.imread(args[0]);
        }
        m = processImage(m);
        BufferedImage bi = Mat2BufferedImage(m);
        displayImage(bi);
    }
}