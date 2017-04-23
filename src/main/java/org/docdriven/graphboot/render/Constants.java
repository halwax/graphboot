package org.docdriven.graphboot.render;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class Constants
{
	/**
	 * Contains an empty image.
	 */
	public static BufferedImage EMPTY_IMAGE;

	/**
	 * Initializes the empty image.
	 */
	static
	{
		try
		{
			EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		}
		catch (Exception e)
		{
			// ignore
		}
	}

	/**
	 * Maximum size (in bytes) for request payloads. Default is 10485760 (10MB).
	 */
	public static final int MAX_REQUEST_SIZE = 10485760;

	/**
	 * Maximum area for exports. Default is 10000x10000px.
	 */
	public static final int MAX_AREA = 10000 * 10000;
	
	public enum RenderType {
		PDF("pdf"),
		SVG("svg"),
		IMAGE("png","jpg","gif"),
		XML("xml");
		
		private List<String> formats;

		private RenderType(String ... formats) {
			this.formats = Arrays.asList(formats);
		}
		
		public List<String> getFormats() {
			return formats;
		}
	}

}
