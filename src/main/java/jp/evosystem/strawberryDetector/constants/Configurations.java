package jp.evosystem.strawberryDetector.constants;

import jp.evosystem.strawberryDetector.detectors.DjlZooModelSingleShotObjectDetector;
import jp.evosystem.strawberryDetector.detectors.ObjectDetector;

/**
 * 環境設定.
 *
 * @author evosystem
 */
public interface Configurations {

	/**
	 * デバッグモードを有効化するかどうか.
	 */
	boolean ENABLE_DEBUG_MODE = false;

	/**
	 * 使用する検出器のクラス.
	 */
	Class<? extends ObjectDetector> USE_OBJECT_DETECTOR_CLASS = DjlZooModelSingleShotObjectDetector.class;

	/**
	 * <code>CascadeClassifier</code>用の検出器ファイルのパス.
	 */
	String JAVACV_CASCADE_CLASSIFIER_FILE_PATH = "Haar-Cascade-Classifiers/strawberry_classifier.xml";

	/**
	 * <code>ObjectFinder</code>用の物体画像のファイルのパス.
	 */
	String JAVACV_OBJECT_FINDER_OBJECT_IMAGE_FILE_PATH = "Fruit-Images-Dataset/Training/Strawberry/0_100.jpg";

	/**
	 * 処理結果をファイルに出力するかどうか.
	 */
	boolean ENABLE_RECORDING = true;

	/**
	 * 対象の画像ファイルのパス.
	 */
	String TARGET_IMAGE_FILE_PATH = "images/example.jpg";

	/**
	 * 対象の動画ファイルのパス.
	 */
	String TARGET_VIDEO_FILE_PATH = "videos/example.mp4";

	/**
	 * 使用するWebカメラのデバイス番号.
	 */
	int TARGET_DEVICE_NUMBER = 0;
}