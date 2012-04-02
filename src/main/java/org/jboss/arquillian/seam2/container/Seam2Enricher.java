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
package org.jboss.arquillian.seam2.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.jboss.arquillian.seam2.ReflectionHelper;
import org.jboss.arquillian.seam2.util.Strings;
import org.jboss.arquillian.test.spi.TestEnricher;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

/**
 * Test Enricher injecting Seam 2 components to the test class fields.
 *
 * @author <a href="mailto:michaelschuetz83@gmail.com">Michael Schuetz</a>
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 * @version $Revision: $
 */
public class Seam2Enricher implements TestEnricher
{
   private static final String ANNOTATION_CLASS_NAME = "org.jboss.seam.annotations.In";

   @Override
   public void enrich(Object testCase)
   {
      if (ReflectionHelper.isClassPresent(ANNOTATION_CLASS_NAME))
      {
         enrichFields(testCase);
      }
   }

   private void enrichFields(Object testCase)
   {
      if (Contexts.getApplicationContext() == null)
      {
         Lifecycle.beginCall();
      }

      final List<Field> seamComponents = ReflectionHelper.getFieldsWithAnnotation(testCase.getClass(), In.class);
      for (Field seamComponent : seamComponents)
      {
         try
         {
            if (!seamComponent.isAccessible())
            {
               seamComponent.setAccessible(true);
            }
            Object component = resolveSeamComponent(seamComponent);
            seamComponent.set(testCase, component);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not inject seam component on field " + seamComponent, e);
         }
      }
   }

   @Override
   public Object[] resolve(Method method)
   {
      return new Object[method.getParameterTypes().length];
   }

   // Private methods

   private Object resolveSeamComponent(Field seamComponent)
   {
      final In in = seamComponent.getAnnotation(In.class);
      String name = in.value();
      if (Strings.isEmpty(name))
      {
         name = seamComponent.getName();
      }
      return getInstance(name);
   }

   private Object getInstance(String componentName)
   {
      return Component.getInstance(componentName);
   }
}
