package jp.evosystem.strawberryDetector.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.DisplayMode;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.bytedeco.javacv.CanvasFrame;

import jp.evosystem.strawberryDetector.models.ProcessImageParameter;

/**
 * 拡張CanvasFrame.
 *
 * @author evosystem
 */
public class ExtendedCanvasFrame extends CanvasFrame {

	/**
	 * 色相の下限.
	 */
	private JSlider minHueSlider;

	/**
	 * 彩度の下限.
	 */
	private JSlider minSaturationSlider;

	/**
	 * 明度の下限.
	 */
	private JSlider minValueSlider;

	/**
	 * 色相の上限.
	 */
	private JSlider maxHueSlider;

	/**
	 * 彩度の上限.
	 */
	private JSlider maxSaturationSlider;

	/**
	 * 明度の上限.
	 */
	private JSlider maxValueSlider;

	/**
	 * コンストラクタ.
	 *
	 * @param title
	 * @param gamma
	 */
	public ExtendedCanvasFrame(String title, double gamma) {
		super(title, gamma);
	}

	@Override
	protected void initCanvas(boolean fullScreen, DisplayMode displayMode, double gamma) {
		super.initCanvas(fullScreen, displayMode, gamma);

		// スライダーを作成
		minHueSlider = new JSlider(0, 180, 0);
		minHueSlider
				.setLabelTable(minHueSlider.createStandardLabels(minHueSlider.getMaximum() / 10));
		minHueSlider.setPaintLabels(true);

		// スライダーを作成
		minSaturationSlider = new JSlider(0, 255, 0);
		minSaturationSlider
				.setLabelTable(minSaturationSlider.createStandardLabels(minSaturationSlider.getMaximum() / 10));
		minSaturationSlider.setPaintLabels(true);

		// スライダーを作成
		minValueSlider = new JSlider(0, 255, 0);
		minValueSlider.setLabelTable(minValueSlider.createStandardLabels(minValueSlider.getMaximum() / 10));
		minValueSlider.setPaintLabels(true);

		// スライダーを作成
		maxHueSlider = new JSlider(0, 180, 180);
		maxHueSlider
				.setLabelTable(maxHueSlider.createStandardLabels(maxHueSlider.getMaximum() / 10));
		maxHueSlider.setPaintLabels(true);

		// スライダーを作成
		maxSaturationSlider = new JSlider(0, 255, 255);
		maxSaturationSlider
				.setLabelTable(maxSaturationSlider.createStandardLabels(maxSaturationSlider.getMaximum() / 10));
		maxSaturationSlider.setPaintLabels(true);

		// スライダーを作成
		maxValueSlider = new JSlider(0, 255, 255);
		maxValueSlider
				.setLabelTable(maxValueSlider.createStandardLabels(maxValueSlider.getMaximum() / 10));
		maxValueSlider.setPaintLabels(true);

		// コンポーネントを配置
		Container contentPane = this.getContentPane();

		// 上から下へレイアウトするパネルを作成
		JPanel pageAxisPanel = new JPanel();
		pageAxisPanel.setLayout(new BoxLayout(pageAxisPanel, BoxLayout.PAGE_AXIS));

		// 左から右へレイアウトするパネルを作成
		JPanel minHueLineAxisPanel = new JPanel();
		minHueLineAxisPanel.setLayout(new BoxLayout(minHueLineAxisPanel, BoxLayout.LINE_AXIS));
		pageAxisPanel.add(minHueLineAxisPanel);

		// コンポーネントを追加
		minHueLineAxisPanel.add(new JLabel("minHue"));
		minHueLineAxisPanel.add(minHueSlider);

		// 左から右へレイアウトするパネルを作成
		JPanel minSaturationLineAxisPanel = new JPanel();
		minSaturationLineAxisPanel.setLayout(new BoxLayout(minSaturationLineAxisPanel, BoxLayout.LINE_AXIS));
		pageAxisPanel.add(minSaturationLineAxisPanel);

		// コンポーネントを追加
		minSaturationLineAxisPanel.add(new JLabel("minSaturation"));
		minSaturationLineAxisPanel.add(minSaturationSlider);

		// 左から右へレイアウトするパネルを作成
		JPanel minValueLineAxisPanel = new JPanel();
		minValueLineAxisPanel.setLayout(new BoxLayout(minValueLineAxisPanel, BoxLayout.LINE_AXIS));
		pageAxisPanel.add(minValueLineAxisPanel);

		// コンポーネントを追加
		minValueLineAxisPanel.add(new JLabel("minValue"));
		minValueLineAxisPanel.add(minValueSlider);

		// 左から右へレイアウトするパネルを作成
		JPanel maxHueLineAxisPanel = new JPanel();
		maxHueLineAxisPanel.setLayout(new BoxLayout(maxHueLineAxisPanel, BoxLayout.LINE_AXIS));
		pageAxisPanel.add(maxHueLineAxisPanel);

		// コンポーネントを追加
		maxHueLineAxisPanel.add(new JLabel("maxHue"));
		maxHueLineAxisPanel.add(maxHueSlider);

		// 左から右へレイアウトするパネルを作成
		JPanel maxSaturationLineAxisPanel = new JPanel();
		maxSaturationLineAxisPanel.setLayout(new BoxLayout(maxSaturationLineAxisPanel, BoxLayout.LINE_AXIS));
		pageAxisPanel.add(maxSaturationLineAxisPanel);

		// コンポーネントを追加
		maxSaturationLineAxisPanel.add(new JLabel("maxSaturation"));
		maxSaturationLineAxisPanel.add(maxSaturationSlider);

		// 左から右へレイアウトするパネルを作成
		JPanel maxValueLineAxisPanel = new JPanel();
		maxValueLineAxisPanel.setLayout(new BoxLayout(maxValueLineAxisPanel, BoxLayout.LINE_AXIS));
		pageAxisPanel.add(maxValueLineAxisPanel);

		// コンポーネントを追加
		maxValueLineAxisPanel.add(new JLabel("maxValue"));
		maxValueLineAxisPanel.add(maxValueSlider);

		contentPane.add(pageAxisPanel, BorderLayout.NORTH);
	}

	/**
	 * 画像処理用のパラメータを取得.
	 *
	 * @return
	 */
	public ProcessImageParameter getCurrentParameter() {
		ProcessImageParameter parameter = new ProcessImageParameter();
		parameter.minHue = minHueSlider.getValue();
		parameter.minSaturation = minSaturationSlider.getValue();
		parameter.minValue = minValueSlider.getValue();
		parameter.maxHue = maxHueSlider.getValue();
		parameter.maxSaturation = maxSaturationSlider.getValue();
		parameter.maxValue = maxValueSlider.getValue();
		return parameter;
	}
}