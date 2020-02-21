package jp.evosystem.strawberryDetector.constants;

/**
 * 環境設定.
 *
 * @author evosystem
 */
public interface Configurations {

	/**
	 * 検出器ファイルのパス.
	 */
	String CLASSIFIER_FILE_PATH = "Haar-Cascade-Classifiers/strawberry_classifier.xml";

	/**
	 * <code>ObjectFinder</code>用の物体画像のファイルのパス.
	 */
	String OBJECT_FINDEROBJECT_IMAGE_FILE_PATH = "Fruit-Images-Dataset/Training/Strawberry/0_100.jpg";

	/**
	 * 対象の画像ファイルのパス.
	 */
	String TARGET_IMAGE_FILE_PATH = "images/example_02.jpg";

	/**
	 * 対象の動画ファイルのパス.
	 */
	String TARGET_VIDEO_FILE_PATH = "videos/example.mp4";

	/**
	 * 使用するWebカメラのデバイス番号.
	 */
	int TARGET_DEVICE_NUMBER = 0;
}