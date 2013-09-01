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
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.seam2.ReflectionHelper;
import org.jboss.arquillian.seam2.configuration.Seam2Configuration;
import org.jboss.arquillian.seam2.util.ArrayMerger;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.seam.annotations.In;
import org.jboss.seam.util.Strings;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

/**
 * Extends test archive by adding Seam 2 dependecies.
 *
 * @author <a href="mailto:michaelschuetz83@gmail.com">Michael Schuetz</a>
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 * @version $Revision: $
 */
public class Seam2ArchiveProcessor implements ApplicationArchiveProcessor
{
   private static final String MVN_ALTERNATE_SETTINGS = "mvn.alternate.settings";

   private static final String POM_XML = "pom.xml";

   private static final String ANNOTATION_CLASS_NAME = "org.jboss.seam.annotations.In";

   @Inject
   private Instance<Seam2Configuration> configurationInstance;

   @Override
   public void process(Archive<?> applicationArchive, TestClass testClass)
   {
      boolean shouldEnrichTestArchiveWithSeamLibraries = configurationInstance.get().isAutoPackage();
      if (hasSeamAnnotation(testClass) && shouldEnrichTestArchiveWithSeamLibraries)
      {
         appendSeamLibraries(applicationArchive);
      }
   }

   private void appendSeamLibraries(Archive<?> applicationArchive)
   {
      final File[] seamDependencies = resolveSeamDependencies();

      if (applicationArchive instanceof EnterpriseArchive)
      {
         final File[] jbossElDependencies = resolveArtifact(Seam2Configuration.JBOSS_EL_ARTIFACT, configurationInstance.get().getJbossElVersion());
         final EnterpriseArchive ear = (EnterpriseArchive) applicationArchive;
         ear.addAsModules(seamDependencies)
            .addAsLibraries(jbossElDependencies);
      }
      else if (applicationArchive instanceof WebArchive)
      {
         final WebArchive war = (WebArchive) applicationArchive;
         war.addAsLibraries(seamDependencies);
      }
      else
      {
         throw new RuntimeException("Unsupported archive format[" + applicationArchive.getClass().getSimpleName()
               + ", " + applicationArchive.getName() + "] for Seam 2 application. Please use WAR or EAR.");
      }
   }

   private boolean hasSeamAnnotation(TestClass testClass)
   {
      return ReflectionHelper.isClassPresent(ANNOTATION_CLASS_NAME) && ReflectionHelper.getFieldsWithAnnotation(testClass.getJavaClass(), In.class).size() > 0;
   }

   private File[] resolveSeamDependencies()
   {
      final File[] seamDependencies;
      if (!Strings.isEmpty(configurationInstance.get().getSeamVersion()))
      {
         seamDependencies = resolveArtifact(Seam2Configuration.SEAM_ARTIFACT + ":" + configurationInstance.get().getSeamVersion());
      }
      else
      {
         seamDependencies = resolveArtifact(Seam2Configuration.SEAM_ARTIFACT, Seam2Configuration.DEFAULT_SEAM_VERSION);
      }

      return new ArrayMerger().merge(seamDependencies, addAdditionalDependencies());
   }

   private File[] addAdditionalDependencies()
   {
      File[] additionalDependencies = new File[0];

      if (configurationInstance.get().getAdditionalLibraries() != null)
      {
         final String[] archives = configurationInstance.get().getAdditionalLibraries().split(",");
         for (String gav :  archives)
         {
            additionalDependencies = new ArrayMerger().merge(additionalDependencies, resolveArtifact(gav));
         }
      }
      return additionalDependencies;
   }

   /**
    * Resolves given artifact using POM metadata first. If not found, default version is fetched.
    *
    * @param artifact
    * @param defaultVersion
    * @return
    */
   private File[] resolveArtifact(final String artifact, final String defaultVersion)
   {
      File[] artifacts = null;
      try
      {
         artifacts = resolveArtifact(artifact);
      }
      catch (Exception e)
      {
         artifacts = resolveArtifact(artifact + ":" + defaultVersion);
      }
      return artifacts;
   }

   private File[] resolveArtifact(final String artifact)
   {
      MavenResolverSystem resolver;

      final String alternateMavenSettings = System.getProperty(MVN_ALTERNATE_SETTINGS);
      if (alternateMavenSettings == null)
      {
         resolver = Maven.resolver();
      }
      else
      {
         resolver = Maven.configureResolver().fromFile(alternateMavenSettings);
      }

      if (mavenIsUsed())
      {
         resolver.loadPomFromFile(POM_XML);
      }

      return resolver.resolve(artifact).withTransitivity().asFile();
   }

   private boolean mavenIsUsed()
   {
      return new File(POM_XML).exists();
   }
}
