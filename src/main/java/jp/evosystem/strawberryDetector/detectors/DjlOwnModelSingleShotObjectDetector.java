package jp.evosystem.strawberryDetector.detectors;

import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.DetectedObjects;
import ai.djl.modality.cv.ImageVisualization;
import ai.djl.modality.cv.SingleShotDetectionTranslator;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.nn.Block;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import jp.evosystem.strawberryDetector.trainers.TrainStrawberry;

/**
 * DJLによる物体検出(自分で学習したモデルを使用).
 *
 * @author evosystem
 */
public class DjlOwnModelSingleShotObjectDetector extends ObjectDetector {

	/**
	 * Predictor.
	 */
	private static Predictor<BufferedImage, DetectedObjects> predictor;

	/**
	 * コンストラクタ.
	 *
	 * @throws Exception
	 */
	public DjlOwnModelSingleShotObjectDetector() throws Exception {
		Model model = Model.newInstance();
		float detectionThreshold = 0.6f;
		// load parameters back to original training block
		model.setBlock(TrainStrawberry.getSsdTrainBlock());
		model.load(Paths.get("build/model"), "ssd");
		// append prediction logic at end of training block with parameter
		// loaded
		Block ssdTrain = model.getBlock();
		model.setBlock(TrainStrawberry.getSsdPredictBlock(ssdTrain));

		Pipeline pipeline = new Pipeline(new ToTensor());
		List<String> classes = new ArrayList<>();
		classes.add("strawberry");
		SingleShotDetectionTranslator translator = SingleShotDetectionTranslator.builder()
				.setPipeline(pipeline)
				.setClasses(classes)
				.optThreshold(detectionThreshold)
				.build();

		// Predictorを作成
		predictor = model.newPredictor(translator);
	}

	@Override
	public void processTargetImage(BufferedImage targetImage) {
		try {
			// 物体を検出
			DetectedObjects detection = predictor.predict(targetImage);
			System.out.println(detection);

			// 検出結果の矩形を描画
			ImageVisualization.drawBoundingBoxes(targetImage, detection);
		} catch (TranslateException e) {
			e.printStackTrace();
		}
	}
}