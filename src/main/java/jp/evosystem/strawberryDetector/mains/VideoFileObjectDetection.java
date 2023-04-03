package jp.evosystem.strawberryDetector.mains;

import java.io.File;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import jp.evosystem.strawberryDetector.constants.Configurations;
import jp.evosystem.strawberryDetector.detectors.ObjectDetector;
import jp.evosystem.strawberryDetector.utils.ObjectDetectorHelper;

/**
 * 動画内の物体を検出.
 *
 * @author evosystem
 */
public class VideoFileObjectDetection {

	/**
	 * main.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// 検出器のインスタンスを取得
		ObjectDetector objectDetector = ObjectDetectorHelper.getObjectDetector();

		// 対象の動画ファイル
		File targetVideofile = new File(Configurations.TARGET_VIDEO_FILE_PATH);

		// Webカメラから映像取得
		try (FrameGrabber frameGrabber = new FFmpegFrameGrabber(targetVideofile)) {
			frameGrabber.start();

			// 動画ファイルのフレームレートを取得
			double frameRate = frameGrabber.getFrameRate();

			// レコーダーを作成
			try (FrameRecorder recorder = FrameRecorder.createDefault("target/VideoFileObjectDetection.mp4",
					frameGrabber.getImageWidth(),
					frameGrabber.getImageHeight())) {
				// 録画を開始
				if (Configurations.ENABLE_RECORDING) {
					recorder.start();
				}

				// コンバーターを作成
				OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

				// 画面を作成
				CanvasFrame canvasFrame = new CanvasFrame("タイトル",
						CanvasFrame.getDefaultGamma() / frameGrabber.getGamma());

				// 取得した映像データ
				Mat grabbedImage;

				// 画面が表示中の間ループ
				while (canvasFrame.isVisible() && (frameGrabber.getFrameNumber() < frameGrabber.getLengthInFrames())) {
					try {
						// 動画のフレームを取得
						grabbedImage = converter.convert(frameGrabber.grab());

						// 動画のフレームが存在する場合のみ処理を実行
						if (grabbedImage != null) {
							// 画像処理
							objectDetector.processTargetImage(grabbedImage);

							// フレームを作成
							Frame frame = converter.convert(grabbedImage);

							// フレームを表示
							canvasFrame.showImage(frame);

							// フレームを録画
							if (Configurations.ENABLE_RECORDING) {
								recorder.record(frame);
							}

							// フレームレートに応じて適切にウエイトを行う
							Thread.sleep((int) (1000 / frameRate));
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// 画面を閉じる
				canvasFrame.dispose();
			}
		}
	}
}