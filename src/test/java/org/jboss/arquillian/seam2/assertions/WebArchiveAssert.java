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

import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;
import org.jboss.arquillian.seam2.enhancement.WebArchiveExtractor;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;

import java.io.InputStream;

public class WebArchiveAssert extends GenericAssert<WebArchiveAssert, WebArchive>
{

   protected WebArchiveAssert(Class<WebArchiveAssert> selfType, WebArchive webArchive)
   {
      super(selfType, webArchive);
   }

   public static WebArchiveAssert assertThat(WebArchive archive)
   {
      return new WebArchiveAssert(WebArchiveAssert.class, archive);
   }

   public WebDescriptorAssert containsWebDescriptor()
   {
      final InputStream webAppDescriptor = new WebArchiveExtractor().getWebAppDescriptorAsStream(this.actual);
      Assertions.assertThat(webAppDescriptor).isNotNull();
      return WebDescriptorAssert.assertThat(Descriptors.importAs(WebAppDescriptor.class).fromStream(webAppDescriptor));
   }

}
