package jp.evosystem.strawberryDetector.detectors;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

import org.bytedeco.javacv.ObjectFinder;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.CvArr;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import jp.evosystem.strawberryDetector.constants.Configurations;

/**
 * ObjectFinderによる物体検出.
 *
 * @see <a href="http://javacv.ru/javacv-objectfinder/">Javacv ObjectFinder.
 *      Поиск шаблона</a>
 * @author evosystem
 */
public class JavacvObjectFinderObjectDetector extends ObjectDetector {

	/**
	 * 検出器.
	 */
	private static ObjectFinder objectFinder;

	/**
	 * コンストラクタ.
	 */
	public JavacvObjectFinderObjectDetector() {
		// 対象の画像ファイルを読み込み
		IplImage targetImage = opencv_imgcodecs.cvLoadImage(Configurations.JAVACV_OBJECT_FINDER_OBJECT_IMAGE_FILE_PATH);

		// 検出器を作成
		ObjectFinder.Settings settings = new ObjectFinder.Settings();
		settings.setObjectImage(targetImage);
		settings.setUseFLANN(true);
		settings.setDescriptorChannels(3);
		settings.setRansacReprojThreshold(4);
		objectFinder = new ObjectFinder(settings);
	}

	/**
	 * 画像処理.
	 *
	 * @see org.bytedeco.opencv.global.opencv_core#cvIplImage(Mat)
	 * @see org.bytedeco.opencv.global.opencv_core#cvarrToMat(CvArr)
	 * @param targetImageMat
	 */
	@Override
	public void processTargetImage(Mat targetImageMat) {
		// 検出器で検出
		long start = System.currentTimeMillis();
		double[] dst_corners = objectFinder.find(opencv_core.cvIplImage(targetImageMat));
		System.out.println("Finding time = " + (System.currentTimeMillis() - start) + " ms");

		// 全ての検出結果に対して実行
		if (dst_corners != null) {
			System.out.println(dst_corners.length);
			for (int i = 0; i < 4; i++) {
				int j = (i + 1) % 4;
				int x1 = (int) Math.round(dst_corners[2 * i]);
				int y1 = (int) Math.round(dst_corners[2 * i + 1]);
				int x2 = (int) Math.round(dst_corners[2 * j]);
				int y2 = (int) Math.round(dst_corners[2 * j + 1]);
				line(targetImageMat, new Point(x1, y1), new Point(x2, y2), Scalar.RED, 3, 8, 0);
			}
		}
	}
}