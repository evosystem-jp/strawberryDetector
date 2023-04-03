package jp.evosystem.strawberryDetector.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.RotatedRect;
import org.bytedeco.opencv.opencv_core.Scalar;

import jp.evosystem.strawberryDetector.components.ExtendedCanvasFrame;
import jp.evosystem.strawberryDetector.constants.Configurations;
import jp.evosystem.strawberryDetector.models.ProcessImageParameter;
import jp.evosystem.strawberryDetector.utils.MathHelper;

/**
 * 画像内の物体を検出テスト.
 *
 * @author evosystem
 */
public class ImageFileExample1 {

	/**
	 * main.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// 対象の画像ファイル
		File targetImagefile = new File(Configurations.TARGET_IMAGE_FILE_PATH);

		// 対象の画像ファイルを読み込み
		Mat targetImageMat = opencv_imgcodecs.imread(targetImagefile.getAbsolutePath());

		// コンバーターを作成
		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

		// 画面を作成
		ExtendedCanvasFrame canvasFrame = new ExtendedCanvasFrame("タイトル", 1.0);

		// 画面が表示中の間ループ
		while (canvasFrame.isVisible()) {
			// 元画像のコピーを作成
			Mat targetImageMatClone = targetImageMat.clone();

			// 画像処理
			processTargetImage(targetImageMatClone, canvasFrame.getCurrentParameter());

			// フレームを作成
			Frame frame = converter.convert(targetImageMatClone);

			// フレームを表示
			canvasFrame.showImage(frame);
		}

		// 画面を閉じる
		canvasFrame.dispose();
	}

	/**
	 * 画像処理.
	 *
	 * @param targetImageMat
	 * @param processImageParameter
	 */
	private static void processTargetImage(Mat targetImageMat, ProcessImageParameter processImageParameter) {
		// ガンマ補正
		double gamma = 2.5;
		Mat lut = new Mat(1, 256, opencv_core.CV_8U);
		UByteIndexer lutIndexer = lut.createIndexer();
		for (int i = 0; i < 256; i++) {
			double lutValue = Math.pow((i / 255.0), (1.0 / gamma)) * 255;
			lutIndexer.put(i, (int) lutValue);
		}
		Mat targetImageMatLut = new Mat();
		opencv_core.LUT(targetImageMat, lut, targetImageMatLut);

		// HSV画像を作成
		Mat targetImageMatHsv = new Mat();
		opencv_imgproc.cvtColor(targetImageMatLut, targetImageMatHsv, opencv_imgproc.COLOR_BGR2HSV);

		// HSVでの色抽出(マスク)
		// 可変長引数のオーバーロード対策のため配列として引数を渡す
		Mat hsvLower = new Mat(new int[] { processImageParameter.minHue, processImageParameter.minSaturation,
				processImageParameter.minValue });
		Mat hsvUpper = new Mat(new int[] { processImageParameter.maxHue, processImageParameter.maxSaturation,
				processImageParameter.maxValue });
		Mat targetImageMatInRange = new Mat();
		opencv_core.inRange(targetImageMatHsv, hsvLower, hsvUpper, targetImageMatInRange);

		// マスク部分のみを抽出
		Mat targetImageMatBitwiseAnd = new Mat();
		opencv_core.bitwise_and(targetImageMat, targetImageMat, targetImageMatBitwiseAnd, targetImageMatInRange);

		// エッジ抽出
		Mat targetImageMatCanny = new Mat();
		opencv_imgproc.Canny(targetImageMatBitwiseAnd, targetImageMatCanny, 50, 100);
		Mat targetImageMatClose = new Mat();
		opencv_imgproc.morphologyEx(targetImageMatCanny, targetImageMatClose, opencv_imgproc.MORPH_CLOSE, new Mat());

		// 輪郭を検出
		MatVector targetImageContours = new MatVector();
		Mat targetImageHierarchy = new Mat();
		opencv_imgproc.findContours(targetImageMatClose, targetImageContours, targetImageHierarchy,
				opencv_imgproc.RETR_EXTERNAL,
				opencv_imgproc.CHAIN_APPROX_SIMPLE);

		// 全ての輪郭を描画
		opencv_imgproc.drawContours(targetImageMat, targetImageContours, -1, Scalar.GREEN, 3, 0, new Mat(), 1,
				new Point(0, 0));

		// 全ての輪郭に対して実行
		for (Mat contour : targetImageContours.get()) {
			// 輪郭に対する外接矩形を取得
			RotatedRect box = opencv_imgproc.minAreaRect(contour);

			// 外接矩形の4点の座標を取得(Mat型)
			Mat points = new Mat();
			opencv_imgproc.boxPoints(box, points);

			// 外接矩形の4点の座標(Point型リスト)
			// tl, tr, br, blの順
			List<Point> pointList = new ArrayList<>();

			// 外接矩形の4辺を描画
			// drawContoursが動かないため4辺をそれぞれ線で描画
			FloatRawIndexer rawIndexer = points.createIndexer();
			for (int i = 0; i < points.rows(); i++) {
				Point point1 = new Point((int) rawIndexer.get(i, 0), (int) rawIndexer.get(i, 1));
				Point point2 = new Point((int) rawIndexer.get(((i + 1) % 4), 0),
						(int) rawIndexer.get(((i + 1) % 4), 1));
				opencv_imgproc.line(targetImageMat, point1, point2, Scalar.BLUE);
				pointList.add(point1);
			}

			// 4点の座標を並び替え
			pointList = MathHelper.orderPoints2(pointList);

			// 4点の座標に円を描画
			for (Point point : pointList) {
				opencv_imgproc.circle(targetImageMat, point, 5, Scalar.RED, -1, opencv_imgproc.FILLED, 0);
			}

			// 外接矩形の4点の座標をそれぞれ変数に代入
			Point tl = pointList.get(0);
			Point tr = pointList.get(1);
			Point br = pointList.get(2);
			Point bl = pointList.get(3);

			// 中間点を計算
			Point tltr = MathHelper.midPoint(tl, tr);
			Point blbr = MathHelper.midPoint(bl, br);
			Point tlbl = MathHelper.midPoint(tl, bl);
			Point trbr = MathHelper.midPoint(tr, br);

			// 中間点に円を描画
			opencv_imgproc.circle(targetImageMat, tltr, 3, Scalar.BLUE, -1, opencv_imgproc.FILLED, 0);
			opencv_imgproc.circle(targetImageMat, blbr, 3, Scalar.BLUE, -1, opencv_imgproc.FILLED, 0);
			opencv_imgproc.circle(targetImageMat, tlbl, 3, Scalar.BLUE, -1, opencv_imgproc.FILLED, 0);
			opencv_imgproc.circle(targetImageMat, trbr, 3, Scalar.BLUE, -1, opencv_imgproc.FILLED, 0);

			// 中間点同士を結ぶ直線を描画
			opencv_imgproc.line(targetImageMat, tltr, blbr, Scalar.BLUE);
			opencv_imgproc.line(targetImageMat, tlbl, trbr, Scalar.BLUE);

			// 面積を取得
			double contourArea = opencv_imgproc.contourArea(contour);

			// 文字を描画
			opencv_imgproc.putText(targetImageMat, String.format("%.2f", contourArea),
					new Point((tltr.x() - 15), (tltr.y() - 10)),
					opencv_imgproc.FONT_HERSHEY_SIMPLEX, 0.5, Scalar.BLACK);
		}
	}
}