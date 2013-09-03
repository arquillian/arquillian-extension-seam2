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
package org.jboss.arquillian.seam2.enhancement;

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;

import java.io.InputStream;

public class WebDescriptorEnhancer
{

   private WebArchiveExtractor webArchiveExtractor = new WebArchiveExtractor();

   public void enhance(final EnterpriseArchive archive)
   {
      enhance(webArchiveExtractor.findTestableWebArchive(archive));
   }

   public void enhance(final WebArchive archive)
   {
      final InputStream webAppDescriptor = webArchiveExtractor.getWebAppDescriptorAsStream(archive);
      if (webAppDescriptor == null)
      {
         addMinimalSeam2WebDescriptor(archive);
      }
      else
      {
         enhanceWithSeamListener(archive, webAppDescriptor);
      }
   }

   private void enhanceWithSeamListener(final WebArchive archive, final InputStream webAppDescriptorStream)
   {
      final WebAppDescriptor webAppDescriptor = Descriptors.importAs(WebAppDescriptor.class).fromStream(webAppDescriptorStream);
      webAppDescriptor.getOrCreateListener().listenerClass("org.jboss.seam.servlet.SeamListener");
      archive.delete("WEB-INF/web.xml");
      archive.setWebXML(new StringAsset(webAppDescriptor.exportAsString()));
   }

   private void addMinimalSeam2WebDescriptor(final WebArchive archive)
   {
      archive.setWebXML("minimal-seam2-web.xml");
   }
}
