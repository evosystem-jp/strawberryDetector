package jp.evosystem.strawberryDetector.detectors;

import java.awt.image.BufferedImage;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 * 物体検出器.
 *
 * @author evosystem
 */
public abstract class ObjectDetector {

	/**
	 * 画像処理.
	 *
	 * @param targetImageMat
	 */
	public void processTargetImage(Mat targetImageMat) {
		// BufferedImageへ変換
		BufferedImage bufferedImage = Java2DFrameUtils.toBufferedImage(targetImageMat);

		// 画像処理
		processTargetImage(bufferedImage);

		// BufferedImageへの描画内容を引数のMatへ反映
		opencv_core.copyTo(Java2DFrameUtils.toMat(bufferedImage), targetImageMat, new Mat());
	}

	/**
	 * 画像処理.
	 *
	 * @param targetImage
	 */
	public void processTargetImage(BufferedImage targetImage) {
		// Matへ変換
		Mat mat = Java2DFrameUtils.toMat(targetImage);

		// 画像処理
		processTargetImage(mat);

		// TODO Matへの描画内容を引数のBufferedImageへ反映
	}

	/**
	 * 画像処理.
	 *
	 * @param targetImage
	 */
	public void processTargetImage(IplImage targetImage) {
		// FIXME
		processTargetImage(Java2DFrameUtils.toMat(targetImage));
	}
}