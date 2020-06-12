package main.java;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import main.java.controls.FlowEditor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ExportHandler {
	private final FlowEditor                   editor;

	public ExportHandler(FlowEditor editor) {
		this.editor = editor;
	}

	private BufferedImage[] getBufferedImages() {
		var editors = editor.getOrderedEditors();
		var writableImages = new WritableImage[editors.size()];
		var bufferedImages = new BufferedImage[editors.size()];

		int page = editor.getCurrentPage();
		editor.getChildren().setAll(editor.getFlowEditorPages());
		for(int i = 0; i<editors.size(); i++) {
			writableImages[i] = editors.get(i).snapshot();
			bufferedImages[i] = SwingFXUtils.fromFXImage(writableImages[i],
							new BufferedImage((int) writableImages[i].getWidth(), (int) writableImages[i].getHeight(),
											BufferedImage.TYPE_INT_RGB));
		}
		editor.setPage(page);

		return bufferedImages;
	}

	/**
	 * @param type      image file type to save as
	 *                  accepts: png, jpg, gif, tif
	 * @param directory parent directory to store images
	 */
	public void saveToImages(String type, File directory) throws IOException {
		BufferedImage[] bufferedImages = getBufferedImages();

		for(int i = 0; i<bufferedImages.length; i++) {
			ImageIO.write(bufferedImages[i], type,
							new File(directory.getPath() + File.separatorChar + editor.debateEventProperty().getValue()
											.getName() + "_" + editor.getOrderedSpeeches().get(i) + '.' + type));
		}

		AppUtils.logger.info("Exported as " + type + " to " + directory.getAbsolutePath());
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public void saveToTiff(File file) throws IOException {
		BufferedImage[] bufferedImages= getBufferedImages();

		// Obtain a TIFF writer
		ImageWriter writer = ImageIO.getImageWritersByFormatName("TIFF").next();

		ImageOutputStream output = ImageIO.createImageOutputStream(file);
		writer.setOutput(output);

		ImageWriteParam params = writer.getDefaultWriteParam();
		params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

		// Compression: None, PackBits, ZLib, Deflate, LZW, JPEG and CCITT variants allowed
		// (different plugins may use a different set of compression type names)
		params.setCompressionType("LZW");

		writer.prepareWriteSequence(null);

		for(BufferedImage image : bufferedImages) {
			writer.writeToSequence(new IIOImage(image, null, null), params);
		}

		// We're done
		writer.endWriteSequence();

		writer.dispose();

		AppUtils.logger.info("Exported as TIFF to " + file.getAbsolutePath());
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public void saveToBigPNG(File file) throws IOException {
		BufferedImage[] bufferedImages= getBufferedImages();

		int height = 0;
		int width = 0;
		for(BufferedImage image : bufferedImages) {
			height = Math.max(height, image.getHeight());
			width += image.getWidth();
		}

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int x = 0;
		int y = 0;
		for(BufferedImage image : bufferedImages) {
			result.getGraphics().drawImage(image, x, y, null);
			x += image.getWidth();
		}

		ImageIO.write(result, "png", file);

		AppUtils.logger.info("Exported as big PNG to " + file.getAbsolutePath());
	}

	public void saveToDOCX(File file) {
		var editors = editor.getOrderedEditors();
		var htmlTexts = new String[editors.size()];

		for(int i = 0; i<editors.size(); i++) {
			htmlTexts[i] = editors.get(i).getHtmlText();
		}
		System.out.println("This is supposed to save " + Arrays.toString(htmlTexts) + " as a docx");//TODO implement

		AppUtils.logger.info("Exported as docx to " + file.getAbsolutePath());
	}
}