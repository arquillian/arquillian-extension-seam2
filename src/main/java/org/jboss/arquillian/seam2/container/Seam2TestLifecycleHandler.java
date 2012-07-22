package org.jboss.arquillian.seam2.container;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;

/**
 * Hooks around test execution to start and destroy Seam Context.
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class Seam2TestLifecycleHandler
{

   public void initializeContext(@Observes Before beforeTest)
   {
      if (Contexts.getApplicationContext() == null)
      {
         Lifecycle.beginCall();
      }
      Manager.instance().initializeTemporaryConversation();
   }

   public void destroyContext(@Observes After afterTest)
   {
      Lifecycle.endCall();
   }

}
