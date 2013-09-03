/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.seam2.assertions;

import junit.framework.Assert;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

public class WebDescriptorAssert extends GenericAssert<WebDescriptorAssert, WebAppDescriptor>
{

   protected WebDescriptorAssert(Class<WebDescriptorAssert> selfType, WebAppDescriptor webAppDescriptor)
   {
      super(selfType, webAppDescriptor);
   }

   public static WebDescriptorAssert assertThat(WebAppDescriptor descriptor)
   {
      return new WebDescriptorAssert(WebDescriptorAssert.class, descriptor);
   }

   public WebDescriptorAssert identicalTo(String exampleWebXml)
   {
      XMLUnit.setIgnoreWhitespace(true);
      XMLUnit.setIgnoreComments(true);
      XMLUnit.setNormalizeWhitespace(true);
      try
      {
         Diff diff = new Diff(actual.exportAsString(), asString(exampleWebXml));
         Assertions.assertThat(diff.identical()).isTrue();
      } catch (SAXException e)
      {
         throw new AssertionError(e);
      } catch (IOException e)
      {
         throw new AssertionError(e);
      }
      return this;
   }

   public WebDescriptorAssert containsListener(String listenerClass)
   {
      assertPresenceUsingXPath(actual.exportAsString(), "/web-app/listener/listener-class", listenerClass);
      return this;
   }

   private void assertPresenceUsingXPath(String xml, String expression, String... expectedValues) {

      if (expectedValues.length == 0)
      {
         throw new IllegalArgumentException("Expected values not specified!");
      }

      final Document doc = create(xml, false);

      final NodeList nodes = extractMatchingNodes(doc, expression);

      if (nodes.getLength() == 0)
      {
         Assert.fail("XPath expressions " + expression + " doesn't match with given XML");
      }

      // If not looking for an attribute, count found Node matches
      if (!expression.contains("@"))
      {
         Assert.assertEquals("ExpectedValue count should match found Node count", expectedValues.length,
               nodes.getLength());
      }

      for (int i = 0; i < nodes.getLength(); i++)
      {
         Node node = nodes.item(i);
         Assert.assertEquals("XPath content should match expected value", expectedValues[i], node.getTextContent());
      }
   }

   private Document create(String xml, boolean namespaceAware) {
      try
      {
         final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
         documentBuilderFactory.setNamespaceAware(namespaceAware);
         return documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException(ioe);
      }
      catch (final SAXException se)
      {
         throw new RuntimeException(se);
      }
      catch (final ParserConfigurationException pce)
      {
         throw new RuntimeException(pce);
      }
   }

   private NodeList extractMatchingNodes(final Document doc, String xpathExpression) {
      final XPathExpression xPathExpression;
      try
      {
         xPathExpression = XPathFactory.newInstance().newXPath().compile(xpathExpression);
      }
      catch (final XPathExpressionException xee)
      {
         throw new RuntimeException(xee);
      }

      final NodeList nodes;
      try
      {
         nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
      }
      catch (final XPathExpressionException xee)
      {
         throw new RuntimeException(xee);
      }
      return nodes;
   }

   private String asString(String resource)
   {
      return new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)).useDelimiter("\\A").next();
   }

}