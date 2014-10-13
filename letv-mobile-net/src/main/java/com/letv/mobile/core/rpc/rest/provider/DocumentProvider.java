package com.letv.mobile.core.rpc.rest.provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.letv.javax.ws.rs.Consumes;
import com.letv.javax.ws.rs.Produces;
import com.letv.javax.ws.rs.WebApplicationException;
import com.letv.javax.ws.rs.core.MediaType;
import com.letv.javax.ws.rs.core.MultivaluedMap;
import com.letv.javax.ws.rs.ext.Provider;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.rpc.rest.ReaderException;
import com.letv.mobile.core.rpc.rest.WriterException;

/**
 * Provider that reads and writes org.w3c.dom.Document
 *
 * @author <a href="sduskis@gmail.com>Solomon Duskis</a>
 * @version $Revision$
 */
@Provider
@Produces({"text/*+xml", "application/*+xml"})
@Consumes({"text/*+xml", "application/*+xml"})
public class DocumentProvider extends AbstractEntityProvider<Document>
{
   private static final Trace logger = Trace.register(DocumentProvider.class);
   
   private final TransformerFactory transformerFactory;
   private final DocumentBuilderFactory documentBuilder;
   private boolean expandEntityReferences = true;

   public DocumentProvider()
   {
      this.documentBuilder = DocumentBuilderFactory.newInstance();
      this.transformerFactory = TransformerFactory.newInstance();
      try
      {
//         String s = config.getParameter(ResteasyContextParameters.RESTEASY_EXPAND_ENTITY_REFERENCES);
         expandEntityReferences = true;
      }
      catch (Exception e)
      {
         logger.debug("Unable to retrieve config: expandEntityReferences defaults to true");
      }
   }

   public boolean isReadable(Class<?> clazz, Type type,
                             Annotation[] annotation, MediaType mediaType)
   {
      return Document.class.isAssignableFrom(clazz);
   }

   public Document readFrom(Class<Document> clazz, Type type,
                            Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, String> headers, InputStream input)
           throws IOException, WebApplicationException
   {
      try
      {
         documentBuilder.setExpandEntityReferences(expandEntityReferences);
         return documentBuilder.newDocumentBuilder().parse(input);
      }
      catch (Exception e)
      {
         throw new ReaderException(e);
      }
   }

   public boolean isWriteable(Class<?> clazz, Type type,
                              Annotation[] annotation, MediaType mediaType)
   {
      return Document.class.isAssignableFrom(clazz);
   }

   public void writeTo(Document document, Class<?> clazz, Type type,
                       Annotation[] annotation, MediaType mediaType,
                       MultivaluedMap<String, Object> headers, OutputStream output)
           throws IOException, WebApplicationException
   {
      try
      {
         DOMSource source = new DOMSource(document);
         StreamResult result = new StreamResult(output);
         transformerFactory.newTransformer().transform(source, result);
      }
      catch (TransformerException te)
      {
         throw new WriterException(te);
      }
   }
}
