package team2773.find_targets;

import org.opencv.core.Mat;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;

class FindShapesPipeline implements VisionPipeline {
    public Mat m;

    public FindShapesPipeline() {
        NetworkTableInstance.getDefault().startClient("rpi4", 2345);
    }

    @Override
    public void process(Mat image) {
        FindShapes.Result result = FindShapes.processImage(image, true);
        m = result.display;
        // System.out.println(result.contours.length);
        if (result.target != null) {
            double x = result.target.center.x;
            double xc = m.cols() / 2;
            double alpha = 30 * (x - xc) / xc;
            System.out.println("t=" + result.target.center.x + "," + result.target.center.y);
            System.out.println(alpha + " Degrees");
            NetworkTableInstance.getDefault().getEntry("/angle").forceSetDouble(-alpha);
        }
    }
}
