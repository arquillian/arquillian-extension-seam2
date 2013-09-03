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
package org.jboss.arquillian.seam2;


import junit.framework.Assert;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;
import org.jboss.arquillian.seam2.assertions.WebArchiveAssert;
import org.jboss.arquillian.seam2.enhancement.WebArchiveExtractor;
import org.jboss.arquillian.seam2.enhancement.WebDescriptorEnhancer;
import org.jboss.arquillian.seam2.test.simple.FluidOuncesConverter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;
import org.junit.Test;
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
import java.io.InputStream;
import java.util.Scanner;

public class WebDescriptorEnhancerTest
{

   private final WebDescriptorEnhancer webDescriptorEnhancer = new WebDescriptorEnhancer();

   @Test
   public void should_add_minimal_web_descriptor_for_seam_based_application() throws Exception
   {
      // given
      WebArchive webArchiveWithoutWebDescriptor = createArchiveWithoutWebDescriptor();

      // when
      webDescriptorEnhancer.enhance(webArchiveWithoutWebDescriptor);

      // then
      WebArchiveAssert.assertThat(webArchiveWithoutWebDescriptor).containsWebDescriptor()
                                                                 .identicalTo("minimal-seam2-web.xml");
   }

   @Test
   public void should_add_seam_listener_to_web_descriptor_for_seam_based_application() throws Exception
   {
      // given
      WebArchive webArchiveWithWebDescriptor = createArchiveWithWebDescriptor();

      // when
      webDescriptorEnhancer.enhance(webArchiveWithWebDescriptor);

      // then
      WebArchiveAssert.assertThat(webArchiveWithWebDescriptor).containsWebDescriptor()
                                                              .containsListener("org.jboss.seam.servlet.SeamListener");
   }

   @Test
   public void should_add_seam_listener_to_web_descriptor_for_seam_based_application_when_packaged_as_ear() throws Exception
   {
      // given
      final WebArchive webArchive = createArchiveWithWebDescriptor();
      EnterpriseArchive archive = wrapInEar(webArchive);

      // when
      webDescriptorEnhancer.enhance(archive);

      // then
      WebArchiveAssert.assertThat(webArchive)
                      .containsWebDescriptor()
                      .containsListener("org.jboss.seam.servlet.SeamListener");
   }

   private WebArchive createArchiveWithoutWebDescriptor()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
                       .addClass(FluidOuncesConverter.class)
                       .addAsResource(EmptyAsset.INSTANCE, "seam.properties");
   }

   private WebArchive createArchiveWithWebDescriptor()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
            .addClass(FluidOuncesConverter.class)
            .addAsResource(EmptyAsset.INSTANCE, "seam.properties")
            .setWebXML("non-seam2-web.xml");
   }

   private EnterpriseArchive wrapInEar(WebArchive archive)
   {
      return ShrinkWrap.create(EnterpriseArchive.class, "test-ear.ear").addAsModule(archive);
   }


}
