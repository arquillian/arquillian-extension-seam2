package org.jboss.arquillian.seam2.test.conversation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Name;

@Name("shoppingCart")
@Conversational
public class ShoppingCart
{

   private final Map<Item, Integer> basket = new HashMap<Item, Integer>();

   @Begin(join = true)
   public void add(Item item)
   {
      add(item, 1);
   }

   @Begin(join = true)
   public void add(Item item, Integer amount)
   {
      Integer currentAmount = basket.get(item);
      if (currentAmount == null)
      {
         currentAmount = Integer.valueOf(0);
      }
      basket.put(item, currentAmount + amount);
   }

   @End
   public BigDecimal checkout()
   {
      BigDecimal result = BigDecimal.ZERO;
      for (Item item : basket.keySet())
      {
         result = result.add(item.getPrice().multiply(BigDecimal.valueOf(basket.get(item))));
      }

      return result;
   }

}
