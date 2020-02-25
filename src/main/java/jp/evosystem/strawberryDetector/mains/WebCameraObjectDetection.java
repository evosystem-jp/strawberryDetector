package jp.evosystem.strawberryDetector.mains;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import jp.evosystem.strawberryDetector.constants.Configurations;
import jp.evosystem.strawberryDetector.detectors.ObjectDetector;
import jp.evosystem.strawberryDetector.utils.ObjectDetectorHelper;

/**
 * Webカメラ画像から物体を検出.
 *
 * @author evosystem
 */
public class WebCameraObjectDetection {

	/**
	 * main.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// 検出器のインスタンスを取得
		ObjectDetector objectDetector = ObjectDetectorHelper.getObjectDetector();

		// Webカメラから映像取得
		try (FrameGrabber frameGrabber = FrameGrabber.createDefault(Configurations.TARGET_DEVICE_NUMBER)) {
			frameGrabber.start();

			// コンバーターを作成
			OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

			// 画面を作成
			CanvasFrame canvasFrame = new CanvasFrame("タイトル", CanvasFrame.getDefaultGamma() / frameGrabber.getGamma());

			// 取得した映像データ
			Mat grabbedImage;

			// 画面が表示中の間ループ
			while (canvasFrame.isVisible() && (grabbedImage = converter.convert(frameGrabber.grab())) != null) {
				// 画像処理
				objectDetector.processTargetImage(grabbedImage);

				// フレームを作成
				Frame frame = converter.convert(grabbedImage);

				// フレームを表示
				canvasFrame.showImage(frame);
			}

			// 画面を閉じる
			canvasFrame.dispose();
		}
	}
}