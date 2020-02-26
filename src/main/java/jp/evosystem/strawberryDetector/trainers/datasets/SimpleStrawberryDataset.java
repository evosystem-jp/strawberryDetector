package jp.evosystem.strawberryDetector.trainers.datasets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.util.BufferedImageUtils;
import ai.djl.modality.cv.util.NDImageUtils.Flag;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.dataset.Record;
import ai.djl.translate.Pipeline;

/**
 * いちごのデータセット.
 *
 * @author evosystem
 */
public class SimpleStrawberryDataset extends RandomAccessDataset {

	/**
	 * 使用するクラスラベル.
	 */
	private static final int USE_CLASS_LABEL = 0;

	private Flag flag;

	private List<Path> imagePaths;

	private List<float[]> labels;

	protected SimpleStrawberryDataset(Builder builder) {
		super(builder);
		flag = builder.flag;
		imagePaths = new ArrayList<>();
		labels = new ArrayList<>();

		try {
			// データを準備
			prepareData(builder.usage);
		} catch (Exception e) {
			e.printStackTrace();

			// TODO
		}
	}

	/**
	 * Creates a new builder to build a {@link SimpleStrawberryDataset}.
	 *
	 * @return a new builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * データを準備.
	 *
	 * @param usage
	 * @throws IOException
	 */
	public void prepareData(Usage usage) throws IOException {
		Path usagePath;
		switch (usage) {
			case TRAIN:
				usagePath = Paths.get("Training/Strawberry");
				break;
			case TEST:
				usagePath = Paths.get("Test/Strawberry");
				break;
			case VALIDATION:
			default:
				throw new UnsupportedOperationException("Validation data not available.");
		}
		usagePath = Paths.get("Fruit-Images-Dataset").resolve(usagePath);

		// 全ての画像ファイルに対して実行
		for (File file : usagePath.toFile().listFiles()) {
			float[] labelArray = new float[5];
			String imgName = file.getName();
			// Class label
			labelArray[0] = USE_CLASS_LABEL;

			// Bounding box labels
			// Labels contain in format (Xmin, Ymin, Xmax, Ymax).
			// We need it in (Xmin, Ymax, Xmax, Ymin)
			labelArray[1] = 0.0f;
			labelArray[2] = 0.0f;
			labelArray[3] = 1.0f;
			labelArray[4] = 1.0f;
			imagePaths.add(usagePath.resolve(imgName));
			labels.add(labelArray);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Record get(NDManager manager, long index) throws IOException {
		int idx = Math.toIntExact(index);
		NDList d = new NDList(BufferedImageUtils.readFileToArray(manager, imagePaths.get(idx), flag));
		NDArray label = manager.create(labels.get(idx));
		NDList l = new NDList(label.reshape(new Shape(1).addAll(label.getShape())));
		return new Record(d, l);
	}

	/** {@inheritDoc} */
	@Override
	public long size() {
		return imagePaths.size();
	}

	/** A builder for a {@link SimpleStrawberryDataset}. */
	public static final class Builder extends BaseBuilder<Builder> {

		Usage usage;

		Flag flag;

		/** Constructs a new builder. */
		Builder() {
			usage = Usage.TRAIN;
			flag = Flag.COLOR;
			pipeline = new Pipeline(new ToTensor());
		}

		/** {@inheritDoc} */
		@Override
		public Builder self() {
			return this;
		}

		/**
		 * Sets the optional usage.
		 *
		 * @param usage
		 *            the usage
		 * @return this builder
		 */
		public Builder optUsage(Usage usage) {
			this.usage = usage;
			return self();
		}

		/**
		 * Sets the optional color mode flag.
		 *
		 * @param flag
		 *            the color mode flag
		 * @return this builder
		 */
		public Builder optFlag(Flag flag) {
			this.flag = flag;
			return self();
		}

		/**
		 * Builds the {@link SimpleStrawberryDataset}.
		 *
		 * @return the {@link SimpleStrawberryDataset}
		 */
		public SimpleStrawberryDataset build() {
			return new SimpleStrawberryDataset(this);
		}
	}
}
