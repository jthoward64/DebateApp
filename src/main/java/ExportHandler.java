package main.java;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import main.java.controls.FlowEditor;
import main.java.controls.MinimalHTMLEditor;
import org.apache.commons.io.output.StringBuilderWriter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.w3c.tidy.Tidy;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExportHandler {
	private final FlowEditor editor;

	public ExportHandler(FlowEditor editor) {
		this.editor = editor;
	}

	/**
	 * @param type      image file type to save as
	 *                  accepts: png, jpg, gif, tif
	 * @param directory parent directory to store images
	 */
	public void saveToImages(String type, File directory) {
		//Validate image type
		if (!("png jpg gif tif").contains(type.toLowerCase()))
			throw new IllegalArgumentException("Accepted file types are \"png\", \"jpg\", \"gif\", or \"tif\"");

		List<MinimalHTMLEditor> editors = editor.getOrderedEditors();
		List<VBox> columns = editors.stream().map((MinimalHTMLEditor e) -> new VBox(e.getEditorLabel(), e.getWebView()))
						.collect(Collectors.toList());

		Dialog<ButtonType> previewDialog = generatePreviewDialog(columns);
		previewDialog.setOnCloseRequest(event -> {
			if (previewDialog.getResult().equals(ButtonType.OK)) {
				BufferedImage[] bufferedImages = generateBufferedImages(editors, columns);

				//Save each file
				for (int i = 0; i < bufferedImages.length; i++) {
					try {
						ImageIO.write(bufferedImages[i], type,
										new File(directory.getPath() + File.separatorChar + editor.debateEventProperty()
														.getValue().getName() + "_" + editor.getOrderedSpeeches()
														.get(i) + '.' + type));
					} catch (IOException e) {
						AppUtils.showExceptionDialog(e);
					}
				}

				//Log completion
				Logger.getLogger("DebateApp").info("Exported as a group of " + type + " images to " + directory.getAbsolutePath());
			} else
				Logger.getLogger("DebateApp").info(type + " export cancelled");
		});
	}

	/**
	 * @param file Where to save the generated TIFF image
	 */
	public void saveToTiff(File file) {
		List<MinimalHTMLEditor> editors = editor.getOrderedEditors();
		List<VBox> columns = editors.stream().map((MinimalHTMLEditor e) -> new VBox(e.getEditorLabel(), e.getWebView()))
						.collect(Collectors.toList());

		Dialog<ButtonType> previewDialog = generatePreviewDialog(columns);
		previewDialog.setOnCloseRequest(event -> {
			if (previewDialog.getResult().equals(ButtonType.OK)) {
				BufferedImage[] bufferedImages = generateBufferedImages(editors, columns);

				try {
				// Get an instance of the TIFF writer
				ImageWriter writer = ImageIO.getImageWritersByFormatName("TIFF").next();

				// Get an output stream for the output file and link it to the writer
				ImageOutputStream output = ImageIO.createImageOutputStream(file);
				writer.setOutput(output);

				// Set compression level of the writer
				ImageWriteParam params = writer.getDefaultWriteParam();
				params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				params.setCompressionType(
								"LZW"); // Compression: None, PackBits, ZLib, Deflate, LZW, JPEG and CCITT variants allowed

					// Prepares the writer
					writer.prepareWriteSequence(null);

					// Sends the individual panes to the writer
					for (BufferedImage image : bufferedImages) {
						writer.writeToSequence(new IIOImage(image, null, null), params);
					}

					//Finish off the writer and output stream
					writer.endWriteSequence();
					writer.dispose();
					output.close();
				} catch (IOException e) {
					AppUtils.showExceptionDialog(e);
				}

				// Log completion
				Logger.getLogger("DebateApp").info("Exported as a paginated TIFF to " + file.getAbsolutePath());
			} else
				Logger.getLogger("DebateApp").info("TIFF export cancelled");
		});
	}

	private Dialog<ButtonType> generatePreviewDialog(List<VBox> columns) {
		HBox previewRoot = new HBox();
		previewRoot.getChildren().addAll(columns);

		Dialog<ButtonType> previewDialog = new Dialog<>();
		previewDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
		Pane transparentLayer = new Pane();
		transparentLayer.setOpacity(0);
		previewDialog.getDialogPane().setContent(new StackPane(previewRoot, transparentLayer));
		previewDialog.initOwner(DebateAppMain.instance.mainScene.getWindow());
		previewDialog.initModality(Modality.APPLICATION_MODAL);
		previewDialog.setX(15);
		previewDialog.setY(15);
		previewDialog.setTitle("Preview");
		previewDialog.show();
		return previewDialog;
	}

	/**
	 * @param file Where to save the generated TIFF image
	 */
	public void saveToBigPNG(File file) {
		List<MinimalHTMLEditor> editors = editor.getOrderedEditors();
		List<VBox> columns = editors.stream().map((MinimalHTMLEditor e) -> new VBox(e.getEditorLabel(), e.getWebView()))
						.collect(Collectors.toList());

		Dialog<ButtonType> previewDialog = generatePreviewDialog(columns);
		previewDialog.setOnCloseRequest(event -> {
			if (previewDialog.getResult().equals(ButtonType.OK)) {
				BufferedImage[] bufferedImages = generateBufferedImages(editors, columns);

				//Sets width to the sum of all the panes and the height to the greatest of any individual pane
				int height = 0;
				int width = 0;
				for (BufferedImage image : bufferedImages) {
					height = Math.max(height, image.getHeight());
					width += image.getWidth();
				}

				// Draws all the pane snapshots onto one large image
				BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				int x = 0;
				int y = 0;
				for (BufferedImage image : bufferedImages) {
					result.getGraphics().drawImage(image, x, y, null);
					x += image.getWidth();
				}

				// Saves the combined image
				try {
					ImageIO.write(result, "png", file);
				} catch (IOException e) {
					AppUtils.showExceptionDialog(e);
				}

				// Log completion
				Logger.getLogger("DebateApp").info("Exported as a combined PNG to " + file.getAbsolutePath());
			} else
				Logger.getLogger("DebateApp").info("PNG export cancelled");
		});
	}

	private BufferedImage[] generateBufferedImages(List<MinimalHTMLEditor> editors, List<VBox> columns) {
		WritableImage[] writableImages = new WritableImage[editors.size()];
		BufferedImage[] bufferedImages = new BufferedImage[editors.size()];
		for (int i = 0; i < editors.size(); i++) {
			writableImages[i] = columns.get(i).snapshot(null,
							new WritableImage((int) columns.get(i).getWidth(), (int) columns.get(i).getHeight()));
			bufferedImages[i] = SwingFXUtils.fromFXImage(writableImages[i],
							new BufferedImage((int) writableImages[i].getWidth(), (int) writableImages[i].getHeight(),
											BufferedImage.TYPE_INT_RGB));
		}
		return bufferedImages;
	}

	public void saveToDOCX(File file) throws IOException, Docx4JException {
		List<MinimalHTMLEditor> editors = editor.getOrderedEditors();
		String[] htmlTexts = new String[editors.size()];

		for (int i = 0; i < editors.size(); i++) {
			htmlTexts[i] = "<h1>" + editors.get(i).getEditorLabel().getText() + "</h1>" + editors.get(i).getHtmlText();
		}

		String fullHtmlText = String.join("\n", htmlTexts);

		Tidy tidy = new Tidy();
		tidy.setXHTML(true);

		Reader targetReader = new StringReader(fullHtmlText);
		StringBuilderWriter writer = new StringBuilderWriter();

		tidy.parse(targetReader, writer);
		String xhtmlText = writer.toString();

		targetReader.close();
		writer.close();

		// To docx, with content controls
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

		XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

		wordMLPackage.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(xhtmlText, null));

		wordMLPackage.save(file);

		Logger.getLogger("DebateApp").info("Exported as docx to " + file.getAbsolutePath());
	}
}