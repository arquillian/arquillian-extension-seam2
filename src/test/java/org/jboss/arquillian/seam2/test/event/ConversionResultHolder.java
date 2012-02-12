/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.seam2.test.event;

import org.jboss.arquillian.seam2.test.simple.FluidOuncesConverter;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;

@Name("holder")
public class ConversionResultHolder
{
   private Double milliliters = Double.NaN;

   @In
   private FluidOuncesConverter converter;

   @Observer(Event.CONVERT_OZ_TO_ML)
   public void observe(Double oz)
   {
      milliliters = converter.convertToMillilitres(oz);
   }

   public Double getMilliliters()
   {
      return milliliters;
   }

}
