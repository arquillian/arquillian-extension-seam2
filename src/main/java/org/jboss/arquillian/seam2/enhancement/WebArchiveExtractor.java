/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class WebArchiveExtractor
{

   private static final String WAR_PATTERN = ".*\\.war";

   /**
    * Returns open stream of web.xml found in the archive, but
    * only if single file have been found.
    *
    * @param archive
    * @return Input stream of web.xml found or null if zero or multiple found in the archive.
    */
   public InputStream getWebAppDescriptorAsStream(final Archive<?> archive)
   {
      final Archive<?> testable = findTestableWebArchive(archive);
      final Collection<Node> values = collectWebXml(testable);
      if (values.size() == 1)
      {
         return values.iterator().next().getAsset().openStream();
      }

      return null;
   }

   /**
    * Inspects archive in order to find nested testable archive, assuming
    * @param archive
    * @return testable archive or null if nothing found
    */
   public WebArchive findTestableWebArchive(final Archive<?> archive)
   {
      final Map<ArchivePath, Node> nestedArchives = archive.getContent(Filters.include(WAR_PATTERN));
      if (!nestedArchives.isEmpty())
      {
         for (ArchivePath path : nestedArchives.keySet())
         {
            try
            {
               final WebArchive webArchive = archive.getAsType(WebArchive.class, path);
               boolean onlyOneWebArchiveNested = webArchive != null && nestedArchives.size() == 1;
               if (Testable.isArchiveToTest(webArchive) || onlyOneWebArchiveNested)
               {
                  return webArchive;
               }
            }
            catch (IllegalArgumentException e)
            {
               // no-op, Nested archive is not a ShrinkWrap archive.
            }
         }
      }

      if (archive instanceof WebArchive)
      {
         return (WebArchive) archive;
      }

      return null;
   }

   /**
    * Recursively scans archive content (including sub archives) for web.xml descriptors.
    *
    * @param archive
    * @return
    */
   private Collection<Node> collectWebXml(final Archive<?> archive)
   {
      final Collection<Node> nodes = new LinkedList<Node>(getWebAppDescriptors(archive));
      for (Node node : collectSubArchives(archive))
      {
         if (node.getAsset() instanceof ArchiveAsset)
         {
            final ArchiveAsset archiveAsset = (ArchiveAsset) node.getAsset();
            nodes.addAll(collectWebXml(archiveAsset.getArchive()));
         }
      }
      return nodes;
   }

   private Collection<Node> getWebAppDescriptors(final Archive<?> archive)
   {
      return archive.getContent(Filters.include(".*web.xml")).values();
   }

   private Collection<Node> collectSubArchives(final Archive<?> archive)
   {
      return archive.getContent(Filters.include(WAR_PATTERN)).values();
   }
}

