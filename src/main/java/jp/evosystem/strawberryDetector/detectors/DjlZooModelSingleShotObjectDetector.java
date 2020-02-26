package jp.evosystem.strawberryDetector.detectors;

import java.awt.image.BufferedImage;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.DetectedObjects;
import ai.djl.modality.cv.ImageVisualization;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

/**
 * DJLによる物体検出.
 *
 * @author evosystem
 */
public class DjlZooModelSingleShotObjectDetector extends ObjectDetector {

	/**
	 * Predictor.
	 */
	private static Predictor<BufferedImage, DetectedObjects> predictor;

	/**
	 * コンストラクタ.
	 *
	 * @throws Exception
	 */
	public DjlZooModelSingleShotObjectDetector() throws Exception {
		Criteria<BufferedImage, DetectedObjects> criteria = Criteria.builder()
				.optApplication(Application.CV.OBJECT_DETECTION)
				.setTypes(BufferedImage.class, DetectedObjects.class)
				.optFilter("size", "512")
				.optFilter("backbone", "resnet50")
				.optFilter("flavor", "v1")
				.optFilter("dataset", "voc")
				.optProgress(new ProgressBar())
				.build();

		// モデルを読み込み
		ZooModel<BufferedImage, DetectedObjects> model = ModelZoo.loadModel(criteria);

		// Predictorを作成
		predictor = model.newPredictor();
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