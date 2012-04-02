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

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.CachedAuxilliaryArchiveAppender;
import org.jboss.arquillian.seam2.ReflectionHelper;
import org.jboss.arquillian.seam2.container.Seam2RemoteExtension;
import org.jboss.arquillian.seam2.util.Strings;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 * @version $Revision: $
 */
public class Seam2ArchiveAppender extends CachedAuxilliaryArchiveAppender
{

   @Override
   protected Archive<?> buildArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "arquillian-seam2.jar")
                       .addClass(ReflectionHelper.class)
                       .addPackages(true, Seam2RemoteExtension.class.getPackage(),
                                          Strings.class.getPackage())
                       .addAsResource(EmptyAsset.INSTANCE, "seam.properties")
                       .addAsServiceProvider(RemoteLoadableExtension.class, Seam2RemoteExtension.class);
   }

}
