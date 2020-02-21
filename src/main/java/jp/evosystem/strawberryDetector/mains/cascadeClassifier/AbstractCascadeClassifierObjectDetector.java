package jp.evosystem.strawberryDetector.mains.cascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import jp.evosystem.strawberryDetector.constants.Configurations;

/**
 * 画像から物体を検出.
 *
 * @author evosystem
 */
public abstract class AbstractCascadeClassifierObjectDetector {

	/**
	 * 検出器.
	 */
	private static CascadeClassifier cascadeClassifier;

	static {
		// 検出器を作成
		cascadeClassifier = new CascadeClassifier(Configurations.CLASSIFIER_FILE_PATH);
	}

	/**
	 * 画像処理.
	 *
	 * @param targetImageMat
	 */
	protected static void processTargetImage(Mat targetImageMat) {
		// 検出結果
		RectVector rectVector = new RectVector();

		// 検出器で検出
		cascadeClassifier.detectMultiScale(targetImageMat, rectVector);

		// 全ての検出結果に対して実行
		for (Rect rect : rectVector.get()) {
			// 矩形を描画
			rectangle(targetImageMat, rect, Scalar.RED, 2, opencv_imgproc.CV_AA, 0);
		}
	}
}