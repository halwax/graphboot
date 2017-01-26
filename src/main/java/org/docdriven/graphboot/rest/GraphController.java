package org.docdriven.graphboot.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {

	@RequestMapping("/open")
	public String open() {
		return "";
	}

	@RequestMapping(value = "/export")
	public ResponseEntity<String> export(@RequestParam("xml") String xml, @RequestParam("format") String format,
			@RequestParam("filename") String filename, @RequestParam(value = "bg", required = false) String bg,
			@RequestParam(value = "w", required = false) String w) throws UnsupportedEncodingException {
		if (xml != null && xml.startsWith("%3C")) {
			xml = URLDecoder.decode(xml, "UTF-8");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_XML_VALUE);
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(xml);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> save(@RequestParam("xml") String xml, @RequestParam("format") String format,
			@RequestParam("filename") String filename, @RequestParam(value = "bg", required = false) String bg,
			@RequestParam(value = "w", required = false) String w) throws UnsupportedEncodingException {

		if (xml != null && xml.startsWith("%3C")) {
			xml = URLDecoder.decode(xml, "UTF-8");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_XML_VALUE);
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(xml);
	}

}
