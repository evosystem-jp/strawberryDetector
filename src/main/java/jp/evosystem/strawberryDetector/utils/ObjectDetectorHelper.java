package jp.evosystem.strawberryDetector.utils;

import jp.evosystem.strawberryDetector.constants.Configurations;
import jp.evosystem.strawberryDetector.detectors.DjlOwnModelSingleShotObjectDetector;
import jp.evosystem.strawberryDetector.detectors.DjlZooModelSingleShotObjectDetector;
import jp.evosystem.strawberryDetector.detectors.JavacvCascadeClassifierObjectDetector;
import jp.evosystem.strawberryDetector.detectors.JavacvObjectFinderObjectDetector;
import jp.evosystem.strawberryDetector.detectors.JavacvYolo3ObjectDetector;
import jp.evosystem.strawberryDetector.detectors.ObjectDetector;

/**
 * 検出器ヘルパー.
 *
 * @author evosystem
 */
public class ObjectDetectorHelper {

	/**
	 * 検出器のインスタンスを取得.
	 *
	 * @return
	 */
	public static ObjectDetector getObjectDetector() {
		try {
			if (Configurations.USE_OBJECT_DETECTOR_CLASS == DjlZooModelSingleShotObjectDetector.class) {
				return new DjlZooModelSingleShotObjectDetector();
			} else if (Configurations.USE_OBJECT_DETECTOR_CLASS == DjlOwnModelSingleShotObjectDetector.class) {
				return new DjlOwnModelSingleShotObjectDetector();
			} else if (Configurations.USE_OBJECT_DETECTOR_CLASS == JavacvObjectFinderObjectDetector.class) {
				return new JavacvObjectFinderObjectDetector();
			} else if (Configurations.USE_OBJECT_DETECTOR_CLASS == JavacvCascadeClassifierObjectDetector.class) {
				return new JavacvCascadeClassifierObjectDetector();
			} else if (Configurations.USE_OBJECT_DETECTOR_CLASS == JavacvYolo3ObjectDetector.class) {
				return new JavacvYolo3ObjectDetector();
			}
			throw new RuntimeException("ObjectDetector not found.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}