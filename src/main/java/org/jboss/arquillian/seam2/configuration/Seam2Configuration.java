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
package org.jboss.arquillian.seam2.configuration;

import java.io.Serializable;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class Seam2Configuration implements Serializable
{

   private static final long serialVersionUID = 400180473206524250L;

   public static final String SEAM_ARTIFACT = "org.jboss.seam:jboss-seam";

   public static final String JBOSS_EL_ARTIFACT = "org.jboss.el:jboss-el";

   public static final String DEFAULT_SEAM_VERSION = "2.3.1.Final";

   private String seamVersion = "";

   private String jbossElVersion = "1.0_02.CR6"; 

   private boolean autoPackage = true;

   // Accessors

   public String getSeamVersion()
   {
      return seamVersion;
   }

   public void setSeamVersion(String seamVersion)
   {
      this.seamVersion = seamVersion;
   }

   public String getJbossElVersion()
   {
      return jbossElVersion;
   }

   public void setJbossElVersion(String jbossElVersion)
   {
      this.jbossElVersion = jbossElVersion;
   }

   public boolean isAutoPackage()
   {
      return autoPackage;
   }

   public void setAutoPackage(boolean autoPackage)
   {
      this.autoPackage = autoPackage;
   }

}
