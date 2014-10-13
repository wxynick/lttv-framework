package com.letv.mobile.core.rpc.rest;

import com.letv.javax.ws.rs.core.Response;

public class ClientContext
{
   private ClientInvocation invocation;
   private Response clientResponse;
   private EntityExtractorFactory extractorFactory;

   public ClientContext(ClientInvocation invocation, Response clientResponse, EntityExtractorFactory extractorFactory)
   {
      this.invocation = invocation;
      this.clientResponse = clientResponse;
      this.extractorFactory = extractorFactory;
   }

   public ClientInvocation getInvocation()
   {
      return invocation;
   }

   public Response getClientResponse()
   {
      return clientResponse;
   }

   public EntityExtractorFactory getExtractorFactory()
   {
      return extractorFactory;
   }
}
