/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.seam2.client;

import java.io.File;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.seam2.ReflectionHelper;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.seam.annotations.In;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * Extends test archive by adding Seam 2 dependecies.
 *
 * @author <a href="mailto:michaelschuetz83@gmail.com">Michael Schuetz</a>
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 */
public class Seam2ArchiveProcessor implements ApplicationArchiveProcessor
{
   private static final String ANNOTATION_CLASS_NAME = "org.jboss.seam.annotations.In";

   private static final String SEAM_ARTIFACT = "org.jboss.seam:jboss-seam:2.2.2.Final";

   private static final String JBOSS_EL_ARTIFACT = "org.jboss.el:jboss-el:1.0_02.CR5";

   @Override
   public void process(Archive<?> applicationArchive, TestClass testClass)
   {
      if(hasSeamAnnotation(testClass))
      {
         appendLibrary(applicationArchive);
      }
   }

   private void appendLibrary(Archive<?> applicationArchive)
   {

      final MavenDependencyResolver mvnResolver = DependencyResolvers.use(MavenDependencyResolver.class)
                                                                     .loadMetadataFromPom("pom.xml");
      final File[] seamArtifacts = mvnResolver.artifact(SEAM_ARTIFACT)
                                              .resolveAsFiles();

      final File[] elArtifacts = mvnResolver.artifact(JBOSS_EL_ARTIFACT)
                                            .resolveAsFiles();

      if (applicationArchive instanceof EnterpriseArchive)
      {
         final EnterpriseArchive ear = (EnterpriseArchive) applicationArchive;

         ear.addAsModules(seamArtifacts)
            .addAsLibraries(elArtifacts);

      }
      else if (applicationArchive instanceof WebArchive)
      {
         final WebArchive war = (WebArchive) applicationArchive;
         war.addAsLibraries(seamArtifacts)
            .addAsLibraries(elArtifacts);
      }
      else
      {
         throw new RuntimeException("Unsupported archive format for Seam 2 application. Please use WAR or EAR.");
      }

   }

   private boolean hasSeamAnnotation(TestClass testClass)
   {
      return ReflectionHelper.isClassPresent(ANNOTATION_CLASS_NAME) && ReflectionHelper.getFieldsWithAnnotation(testClass.getJavaClass(), In.class).size() > 0;
   }
}
