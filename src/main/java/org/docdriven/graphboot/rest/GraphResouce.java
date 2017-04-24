package org.docdriven.graphboot.rest;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.docdriven.graphboot.render.Constants;
import org.docdriven.graphboot.render.Constants.RenderType;
import org.docdriven.graphboot.render.Renderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.mxgraph.util.mxUtils;

@RestController
public class GraphResouce {

	@Autowired
	private Renderer renderer;

	@RequestMapping("/open")
	public String open() {
		return "";
	}

	@RequestMapping(value = "/export")
	public ResponseEntity<byte[]> export(@RequestParam("xml") String xml, @RequestParam("format") String format,
			@RequestParam("filename") String filename, @RequestParam(value = "bg", required = false) String bgColorStr,
			@RequestParam(value = "w", required = false) Integer w,
			@RequestParam(value = "h", required = false) Integer h, RequestEntity<String> requestEntity)
			throws UnsupportedEncodingException {

		return handleRequest(xml, format, filename, bgColorStr, w, h, requestEntity);
	}

	private ResponseEntity<byte[]> handleRequest(String xml, String format, String filename, String bgColorStr,
			Integer w, Integer h, RequestEntity<String> requestEntity) throws UnsupportedEncodingException {

		if (xml != null && xml.startsWith("%3C")) {
			xml = URLDecoder.decode(xml, "UTF-8");
		}

		BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);
		handleFileName(filename, responseBuilder);
		ResponseEntity<byte[]> response = responseBuilder.build();

		switch (toRenderType(format)) {
		case IMAGE:
			response = renderImage(requestEntity.getUrl(), format, w, h, bgColorStr, xml, responseBuilder);
			break;
		case PDF:
			response = renderPdf(requestEntity.getUrl(), format, w, h, bgColorStr, xml, responseBuilder);
			break;
		case SVG:
			response = renderXml(xml, responseBuilder, Constants.MEDIATYPE_IMAGE_SVG);
			break;
		case XML:
		default:
			response = renderXml(xml, responseBuilder, MediaType.APPLICATION_XML);
			break;
		}

		return response;
	}

	private void handleFileName(String filename, BodyBuilder responseBuilder) {
		if (filename != null) {
			responseBuilder.contentType(Constants.MEDIATYPE_APPLICATION_UNKOWN);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("Content-Disposition",
					"attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + filename);
			responseBuilder.headers(httpHeaders);
		}
	}

	private RenderType toRenderType(String format) {
		final String srcFormat = format != null ? format : RenderType.XML.name().toLowerCase();
		RenderType renderType = Stream.of(RenderType.values())
				.filter((value) -> value.getFormats().contains(srcFormat.toLowerCase())).findFirst()
				.orElse(RenderType.XML);
		return renderType;
	}

	private ResponseEntity<byte[]> renderXml(String xml, BodyBuilder responseBuilder, MediaType mediaType) {
		byte[] byteArray = xml.getBytes();
		responseBuilder.contentLength(byteArray.length);
		responseBuilder.contentType(mediaType);
		return responseBuilder.body(byteArray);
	}

	private ResponseEntity<byte[]> renderPdf(URI uri, String format, int w, int h, String bgColorStr, String xml,
			BodyBuilder responseBuilder) {
		Color bg = toBgColor(bgColorStr, format);
		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			renderer.renderPdf(uri.toString(), w, h, bg, xml, out);
			byte[] byteArray = out.toByteArray();
			responseBuilder.contentLength(byteArray.length);

			responseBuilder.contentType(MediaType.APPLICATION_PDF);

			return responseBuilder.body(byteArray);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private ResponseEntity<byte[]> renderImage(URI uri, String format, int w, int h, String bgColorStr, String xml,
			BodyBuilder responseBuilder) {

		MediaType mediaType = Constants.MEDIATYPE_APPLICATION_UNKOWN;
		format = format != null ? format : "";
		switch (format.toLowerCase()) {
		case Constants.IMAGE_FORMAT_PNG:
			mediaType = MediaType.IMAGE_PNG;
			break;
		case Constants.IMAGE_FORMAT_JPEG:
		case Constants.IMAGE_FORMAT_JPG:
			mediaType = MediaType.IMAGE_JPEG;
			break;
		case Constants.IMAGE_FORMAT_GIF:
			mediaType = MediaType.IMAGE_GIF;
			break;

		}

		Color bgColor = toBgColor(bgColorStr, format);

		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			renderer.renderImage(uri.toString(), format, w, h, bgColor, xml, out);

			byte[] byteArray = out.toByteArray();

			responseBuilder.cacheControl(CacheControl.noCache());
			responseBuilder.contentLength(byteArray.length);
			responseBuilder.contentType(mediaType);

			return responseBuilder.body(byteArray);

		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}


	private Color toBgColor(String bgColorStr, String format) {
		Color bgColor = (bgColorStr != null) ? mxUtils.parseColor(bgColorStr) : null;
		// Allows transparent backgrounds only for PNG
		if (bgColor == null && !format.equals(Constants.IMAGE_FORMAT_PNG)) {
			bgColor = Color.WHITE;
		}
		return bgColor;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<byte[]> save(@RequestParam("xml") String xml,
			@RequestParam(name = "format", required = false) String format, @RequestParam("filename") String filename,
			@RequestParam(value = "bg", required = false) String bgColorStr,
			@RequestParam(value = "w", required = false) Integer w,
			@RequestParam(value = "h", required = false) Integer h, RequestEntity<String> requestEntity)
			throws UnsupportedEncodingException {

		return handleRequest(xml, format, filename, bgColorStr, w, h, requestEntity);
	}

}
