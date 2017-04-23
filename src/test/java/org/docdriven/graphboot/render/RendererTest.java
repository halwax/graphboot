package org.docdriven.graphboot.render;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.mxgraph.util.mxUtils;

public class RendererTest {

	@Test
	public void renderPdf() throws IOException, SAXException, ParserConfigurationException {
		
		Renderer renderer = new Renderer();
		
		String xml = new String(Files.readAllBytes(Paths.get("./src/test/resources/output.xml")));
		OutputStream out = Files.newOutputStream(Paths.get("./src/test/resources/output.pdf"));
		Color bg = mxUtils.parseColor("#C3D9FF");
		
		renderer.renderPdf("", 200, 200, bg, xml, out);
		
		out.flush();
		out.close();
	}
	
}
