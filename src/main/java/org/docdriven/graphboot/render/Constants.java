package org.docdriven.graphboot.render;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;

public class Constants
{
	public static final MediaType MEDIATYPE_APPLICATION_UNKOWN = MediaType.parseMediaType("application/x-unknown");
	
	public static final MediaType MEDIATYPE_IMAGE_SVG = MediaType.valueOf("image/svg+xml");
	
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
	
	public static final String IMAGE_FORMAT_GIF = "gif";

	public static final String IMAGE_FORMAT_JPG = "jpg";

	public static final String IMAGE_FORMAT_JPEG = "jpeg";

	public static final String IMAGE_FORMAT_PNG = "png";
	
	public enum RenderType {
		PDF("pdf"),
		SVG("svg"),
		IMAGE(IMAGE_FORMAT_GIF,IMAGE_FORMAT_JPG,IMAGE_FORMAT_PNG, IMAGE_FORMAT_JPEG),
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
