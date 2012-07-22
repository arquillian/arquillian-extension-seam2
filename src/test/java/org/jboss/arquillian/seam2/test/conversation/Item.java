package org.jboss.arquillian.seam2.test.conversation;

import java.math.BigDecimal;

public class Item
{

   private final String name;

   private final BigDecimal price;

   public Item(String name, BigDecimal price)
   {
      this.name = name;
      this.price = price;
   }

   // Public methods

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((price == null) ? 0 : price.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!(obj instanceof Item))
      {
         return false;
      }

      final Item other = (Item) obj;

      if (name == null && other.getName() != null)
      {
            return false;
      }
      else if (!name.equals(other.getName()))
      {
         return false;
      }

      if (price == null && other.getPrice() != null)
      {
            return false;
      }
      else if (!price.equals(other.getPrice()))
      {
         return false;
      }

      return true;
   }

   // Accessor methods

   public String getName()
   {
      return name;
   }

   public BigDecimal getPrice()
   {
      return price;
   }

}
