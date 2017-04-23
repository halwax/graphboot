package org.docdriven.graphboot.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.canvas.mxICanvas2D;
import com.mxgraph.reader.mxSaxOutputHandler;
import com.mxgraph.util.mxUtils;

@Service
public class Renderer {
	
	/**
	 * 
	 */
	private transient SAXParserFactory parserFactory = SAXParserFactory.newInstance();

	/**
	 * Cache for all images.
	 */
	protected transient Hashtable<String, Image> imageCache = new Hashtable<String, Image>();
	
	/**
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * 
	 */
	public void renderImage(String url, String format, int w, int h, Color bg, String xml, OutputStream out)
			throws IOException, SAXException, ParserConfigurationException
	{
		BufferedImage image = mxUtils.createBufferedImage(w, h, bg);

		if (image != null)
		{
			Graphics2D g2 = image.createGraphics();
			mxUtils.setAntiAlias(g2, true, true);
			renderXml(xml, createCanvas(url, g2));

			ImageIO.write(image, format, out);
		}
	}

	/**
	 * Creates and returns the canvas for rendering.
	 * @throws IOException 
	 * @throws DocumentException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public void renderPdf(String url, int w, int h, Color bg, String xml, OutputStream out)
			throws IOException, SAXException, ParserConfigurationException
	{
		// Fixes PDF offset
		w += 1;
		h += 1;

		PDDocument document = new PDDocument();
		PDPage pdPage = new PDPage();			
		document.addPage(pdPage);
		
		PDPageContentStream content = new PDPageContentStream(document, pdPage);
		
		BufferedImage bi = new
			    BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);		
		mxGraphicsCanvas2D gc = createCanvas(url, bi.createGraphics());

		// Fixes PDF offset
		gc.translate(1, 1);
		renderXml(xml, gc);
		
		gc.getGraphics().dispose();
		
		bi.flush();
		
		PDImageXObject imageXObject = LosslessFactory.createFromImage(document, bi);
		content.drawImage(imageXObject, 0, 0);
		
		content.close();
		
		document.save(out);
		document.close();
	}

	/**
	 * Renders the XML to the given canvas.
	 */
	public void renderXml(String xml, mxICanvas2D canvas) throws SAXException, ParserConfigurationException, IOException
	{
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		reader.setContentHandler(new mxSaxOutputHandler(canvas));
		reader.parse(new InputSource(new StringReader(xml)));
	}

	/**
	 * Creates a graphics canvas with an image cache.
	 */
	protected mxGraphicsCanvas2D createCanvas(String url, Graphics2D g2)
	{
		// Caches custom images for the time of the request
		final Hashtable<String, Image> shortCache = new Hashtable<String, Image>();
		final String domain = url.isEmpty() ? url : url.substring(0, url.lastIndexOf("/"));

		mxGraphicsCanvas2D g2c = new mxGraphicsCanvas2D(g2)
		{
			public Image loadImage(String src)
			{
				// Uses local image cache by default
				Hashtable<String, Image> cache = shortCache;

				// Uses global image cache for local images
				if (src.startsWith(domain))
				{
					cache = imageCache;
				}

				Image image = cache.get(src);

				if (image == null)
				{
					image = super.loadImage(src);

					if (image != null)
					{
						cache.put(src, image);
					}
					else
					{
						cache.put(src, Constants.EMPTY_IMAGE);
					}
				}
				else if (image == Constants.EMPTY_IMAGE)
				{
					image = null;
				}

				return image;
			}
		};

		return g2c;
	}

}
