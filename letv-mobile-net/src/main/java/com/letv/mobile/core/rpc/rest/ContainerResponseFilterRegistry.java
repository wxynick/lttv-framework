package com.letv.mobile.core.rpc.rest;
import java.util.Collections;
import java.util.List;

import com.letv.javax.ws.rs.container.ContainerResponseFilter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class ContainerResponseFilterRegistry extends JaxrsInterceptorRegistry<ContainerResponseFilter>
{
   protected LegacyPrecedence precedence;

   public ContainerResponseFilterRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory, ContainerResponseFilter.class);
      this.precedence = precedence;
   }


   @Override
   protected void sort(List<Match> matches)
   {
      Collections.sort(matches, new DescendingPrecedenceComparator());

   }

   public ContainerResponseFilterRegistry clone(ResteasyProviderFactory factory)
   {
      ContainerResponseFilterRegistry clone = new ContainerResponseFilterRegistry(factory, precedence);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
