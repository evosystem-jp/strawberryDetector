package jp.evosystem.strawberryDetector.detectors;

import static org.bytedeco.opencv.global.opencv_core.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.opencv.global.opencv_dnn;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Rect2d;
import org.bytedeco.opencv.opencv_core.Rect2dVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_core.StringVector;
import org.bytedeco.opencv.opencv_dnn.Net;

/**
 * Yolo3による物体検出.
 *
 * @see <a href="https://github.com/bytedeco/javacv/issues/1249">sample for
 *      yolo3(darknet)</a>
 * @author evosystem
 */
public class JavacvYolo3ObjectDetector extends ObjectDetector {

	@Override
	public void processTargetImage(Mat img) {
		// setting blob, size can be:320/416/608
		// opencv blob setting can check here
		// https://github.com/opencv/opencv/tree/master/samples/dnn#object-detection
		Mat blob = opencv_dnn.blobFromImage(img, 1.0 / 255, new Size(416, 416), new Scalar(), true, false, CV_32F);
		// Mat blob = opencv_dnn.blobFromImage(img, 1.0, new Size(608, 608), new
		// Scalar(), true, false,CV_8U);

		// load model and config, if you got error: "separator_index <
		// line.size()", check your cfg file, must be something wrong.
		String model = "yolo/yolov3.weights"; // Download
												// and
												// load
												// only
												// wights
												// for
												// YOLO
												// ,
												// this
												// is
												// obtained
												// from
												// official
												// YOLO
												// site//
		String cfg = "yolo/yolov3.cfg";// Download
										// and
										// load
										// cfg
										// file
										// for
										// YOLO
										// ,
										// can
										// be
										// obtained
										// from
										// official
										// site//

		Net net = opencv_dnn.readNetFromDarknet(cfg, model);
		// set preferable
		net.setPreferableBackend(0);
		/*
		 * 0:DNN_BACKEND_DEFAULT 1:DNN_BACKEND_HALIDE
		 * 2:DNN_BACKEND_INFERENCE_ENGINE 3:DNN_BACKEND_OPENCV
		 */
		net.setPreferableTarget(0);
		/*
		 * 0:DNN_TARGET_CPU 1:DNN_TARGET_OPENCL 2:DNN_TARGET_OPENCL_FP16
		 * 3:DNN_TARGET_MYRIAD 4:DNN_TARGET_FPGA
		 */

		// input data
		net.setInput(blob);

		// get output layer name
		StringVector outNames = net.getUnconnectedOutLayersNames();
		// create mats for output layer
		// MatVector outs = outNames.Select(_ => new Mat()).ToArray();

		MatVector outs = new MatVector();
		for (int i = 0; i < outNames.size(); i++) {
			outs.put(new Mat());
		}

		// forward model
		net.forward(outs, outNames);

		// get result from all output
		float threshold = 0.6f; // for confidence
		float nmsThreshold = 0.5f; // threshold for nms
		GetResult(outs, img, threshold, nmsThreshold, true);
	}

	/**
	 * GetResult.
	 *
	 * @param output
	 * @param image
	 * @param threshold
	 * @param nmsThreshold
	 * @param nms
	 */
	private void GetResult(MatVector output, Mat image, float threshold, float nmsThreshold, boolean nms) {
		nms = true;
		// for nms
		ArrayList<Integer> classIds = new ArrayList<>();
		ArrayList<Float> confidences = new ArrayList<>();
		ArrayList<Float> probabilities = new ArrayList<>();
		ArrayList<Rect2d> rect2ds = new ArrayList<>();
		// Rect2dVector boxes = new Rect2dVector();
		try {
			int w = image.cols();
			int h = image.rows();
			/*
			 * YOLO3 COCO trainval output 0 1 : center 2 3 : w/h 4 : confidence
			 * 5 ~ 84 : class probability
			 */
			int prefix = 5; // skip 0~4
			/**/
			for (int k = 0; k < output.size(); k++) {
				Mat prob = output.get(k);
				final FloatRawIndexer probIdx = prob.createIndexer();
				for (int i = 0; i < probIdx.rows(); i++) {
					float confidence = probIdx.get(i, 4);
					if (confidence > threshold) {
						// get classes probability
						DoublePointer minVal = new DoublePointer();
						DoublePointer maxVal = new DoublePointer();
						Point min = new Point();
						Point max = new Point();
						minMaxLoc(prob.rows(i).colRange(prefix, prob.cols()), minVal, maxVal, min, max, null);
						int classes = max.x();
						float probability = probIdx.get(i, classes + prefix);

						// if (probability > threshold) //more accuracy, you can
						// cancel it
						// {
						// get center and width/height
						float centerX = probIdx.get(i, 0) * w;
						float centerY = probIdx.get(i, 1) * h;
						float width = probIdx.get(i, 2) * w;
						float height = probIdx.get(i, 3) * h;

						if (!nms) {
							// draw result (if don't use NMSBoxes)
							continue;
						}

						// put data to list for NMSBoxes
						classIds.add(classes);
						confidences.add(confidence);
						probabilities.add(probability);
						rect2ds.add(new Rect2d(centerX, centerY, width, height));
						// }
					}
				}
			}

			if (!nms)
				return;

			// using non-maximum suppression to reduce overlapping low
			// confidence box
			IntPointer indices = new IntPointer(confidences.size());
			Rect2dVector boxes = new Rect2dVector();
			for (int i = 0; i < rect2ds.size(); i++) {
				boxes.push_back(rect2ds.get(i));
			}

			FloatPointer con = new FloatPointer(confidences.size());
			float[] cons = new float[confidences.size()];
			for (int i = 0; i < confidences.size(); i++) {
				cons[i] = confidences.get(i);
			}
			con.put(cons);

			opencv_dnn.NMSBoxes(boxes, con, threshold, nmsThreshold, indices); // 只会修改前2个参数，后面不动？

			List<String> list = new ArrayList<String>();
			FileInputStream fis = new FileInputStream("yolo/coco.names");
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			String[] Labels = list.toArray(new String[list.size()]);
			br.close();
			isr.close();
			fis.close();
			// Console.WriteLine($"NMSBoxes drop {confidences.Count -
			// indices.Length} overlapping result.");

			for (int m = 0; m < indices.limit(); m++) {
				int i = indices.get(m);
				System.out.println(i);
				Rect2d box = boxes.get(i);
				String res = "name=" + Labels[classIds.get(i)] + " classIds=" + classIds.get(i) + " confidences="
						+ confidences.get(i) + " probabilities=" + probabilities.get(i);
				res += " box.x=" + box.x() + " box.y=" + box.y() + " box.width=" + box.width() + " box.height="
						+ box.height();
				System.out.println(res);

				Rect rect = new Rect((int) box.x(), (int) box.y(), (int) box.width(), (int) box.height());
				opencv_imgproc.rectangle(image, rect, Scalar.GREEN);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("GetResult error:" + e.getMessage());
		}

	}
}