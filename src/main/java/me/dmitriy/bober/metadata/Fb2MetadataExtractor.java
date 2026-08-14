package me.dmitriy.bober.metadata;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.springframework.util.CollectionUtils.firstElement;


@Component
public class Fb2MetadataExtractor implements BookMetadataExtractor{

    private static final String XLINK_NS = "http://www.w3.org/1999/xlink";

    @Override
    public boolean supports(String extension) {
        return "fb2".equalsIgnoreCase(extension);
    }

    @Override
    public ExtractedMetadata extract(Path filepath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = factory.newDocumentBuilder().parse(filepath.toFile());

            Element titleInfo = firstElement(doc.getDocumentElement(), "description", "title-info");
            Element publishInfo = firstElement(doc.getDocumentElement(), "description", "publish-info");

            String title = textOf(titleInfo, "book-title");
            String author = extractAuthorName(titleInfo);
            String publisher = textOf(publishInfo, "publisher");
            byte[] cover = extractCover(doc, titleInfo);

            return new ExtractedMetadata(title, author, publisher, cover);
        } catch (Exception e) {
            throw new IllegalStateException("Fb2 file parsing failed", e);
        }
    }

    private Element firstElement(Element root, String ... path) {
        Element current = root;
        for(String tag : path) {
            current = firstChildByLocalName(current, tag);
            if(current == null) return null;
        }
        return current;
    }

    private Element firstChildByLocalName(Element parent, String localName) {
        if(parent == null) return null;
        NodeList children = parent.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if(node instanceof Element el && localName.equals(el.getLocalName())) {
                return el;
            }
        }
        return null;
    }

    private String textOf(Element parent, String localName) {
        Element el = firstChildByLocalName(parent, localName);
        return el == null ? null : el.getTextContent().trim();
    }

    private String extractAuthorName(Element titleInfo) {
        Element authorEl = firstChildByLocalName(titleInfo, "author");
        if(authorEl == null) return null;
        String first = textOf(authorEl, "first-name");
        String last = textOf(authorEl, "last-name");
        String joined = Stream.of(first, last)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
        return joined.isBlank() ? null : joined;
    }

    private byte[] extractCover(Document doc, Element titleInfo) {
        Element coverpage =  firstChildByLocalName(titleInfo, "coverpage");
        Element image = firstChildByLocalName(coverpage, "image");
        if(image == null) return null;

        String href = image.getAttributeNS(XLINK_NS, "href");
        if(href == null || !href.startsWith("#")) return null;
        String binaryId = href.substring(1);

        NodeList binaries = doc.getElementsByTagNameNS("*", "binary");
        for(int i = 0; i < binaries.getLength(); i++) {
            Element binary  = (Element) binaries.item(i);
            if(binaryId.equals(binary.getAttribute("id"))) {
                try {
                    return Base64.getMimeDecoder().decode(binary.getTextContent().trim());
                } catch (IllegalArgumentException badBase64) {
                    return null;
                }
            }
        }
        return null;
    }
}
