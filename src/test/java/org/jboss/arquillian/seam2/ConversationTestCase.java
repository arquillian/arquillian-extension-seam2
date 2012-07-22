package org.jboss.arquillian.seam2;

import static org.fest.assertions.Assertions.*;

import java.math.BigDecimal;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.seam2.test.conversation.Item;
import org.jboss.arquillian.seam2.test.conversation.ShoppingCart;
import org.jboss.seam.annotations.In;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConversationTestCase
{

   @Deployment
   public static Archive<?> createDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
                       .addPackage(Item.class.getPackage())
                       .addPackages(true, "org.fest")
                       .addPackages(true, "org.dom4j") // Required for JBoss AS 4.2.3.GA
                       .addAsResource(EmptyAsset.INSTANCE, "seam.properties")
                       .setWebXML("web.xml");
   }

   @In
   ShoppingCart shoppingCart;

   @Test
   public void should_add_items_within_conversation() throws Exception
   {
      // given
      final Item laptop = new Item("Lappy", BigDecimal.TEN);
      final Item bike = new Item("Bike", BigDecimal.valueOf(12.0));

      // when
      shoppingCart.add(bike);
      shoppingCart.add(laptop, 10);
      BigDecimal finalPrice = shoppingCart.checkout();

      // then
      assertThat(finalPrice).isEqualByComparingTo(BigDecimal.valueOf(112L));
   }

}
