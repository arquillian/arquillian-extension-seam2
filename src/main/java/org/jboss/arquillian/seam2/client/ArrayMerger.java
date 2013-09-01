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
package org.jboss.arquillian.seam2.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ArrayMerger
{

   public  <T> T[] merge(final T[] first, final T[] second)
   {
      final List<T> merged = new ArrayList<T>(first.length + second.length);
      merged.addAll(Arrays.asList(first));
      merged.addAll(Arrays.asList(second));
      return merged.toArray(first);
   }

}
