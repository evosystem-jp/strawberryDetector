package jp.evosystem.strawberryDetector.models;

/**
 * 画像処理用のパラメータ.
 *
 * @author evosystem
 */
public class ProcessImageParameter {

	/**
	 * 色相の下限.
	 */
	public int minHue;

	/**
	 * 彩度の下限.
	 */
	public int minSaturation;

	/**
	 * 明度の下限.
	 */
	public int minValue;

	/**
	 * 色相の上限.
	 */
	public int maxHue;

	/**
	 * 彩度の上限.
	 */
	public int maxSaturation;

	/**
	 * 明度の上限.
	 */
	public int maxValue;

	/**
	 * デフォルトのパラメータを取得.
	 *
	 * @return
	 */
	public static ProcessImageParameter getDefaultParameter() {
		ProcessImageParameter parameter = new ProcessImageParameter();
		parameter.minHue = 0;
		parameter.minSaturation = 0;
		parameter.minValue = 0;
		parameter.maxHue = 180;
		parameter.maxSaturation = 255;
		parameter.maxValue = 255;
		return parameter;
	}
}