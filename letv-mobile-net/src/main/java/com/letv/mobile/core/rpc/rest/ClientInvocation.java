package com.letv.mobile.core.rpc.rest;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

import com.letv.javax.ws.rs.BadRequestException;
import com.letv.javax.ws.rs.ClientErrorException;
import com.letv.javax.ws.rs.InternalServerErrorException;
import com.letv.javax.ws.rs.NotAcceptableException;
import com.letv.javax.ws.rs.NotAllowedException;
import com.letv.javax.ws.rs.NotAuthorizedException;
import com.letv.javax.ws.rs.NotFoundException;
import com.letv.javax.ws.rs.NotSupportedException;
import com.letv.javax.ws.rs.ProcessingException;
import com.letv.javax.ws.rs.RedirectionException;
import com.letv.javax.ws.rs.ServerErrorException;
import com.letv.javax.ws.rs.ServiceUnavailableException;
import com.letv.javax.ws.rs.WebApplicationException;
import com.letv.javax.ws.rs.client.ClientRequestFilter;
import com.letv.javax.ws.rs.client.ClientResponseFilter;
import com.letv.javax.ws.rs.client.Entity;
import com.letv.javax.ws.rs.client.Invocation;
import com.letv.javax.ws.rs.client.ResponseProcessingException;
import com.letv.javax.ws.rs.core.Configuration;
import com.letv.javax.ws.rs.core.GenericEntity;
import com.letv.javax.ws.rs.core.GenericType;
import com.letv.javax.ws.rs.core.Response;
import com.letv.javax.ws.rs.core.Variant;
import com.letv.javax.ws.rs.ext.MessageBodyWriter;
import com.letv.javax.ws.rs.ext.Providers;
import com.letv.javax.ws.rs.ext.WriterInterceptor;
import com.letv.mobile.core.async.api.IAsyncCallback;
import com.letv.mobile.core.async.api.ICancellable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class ClientInvocation implements Invocation
{
	protected ResteasyClient client;
	protected ClientRequestHeaders headers;
	protected String method;
	protected Object entity;
	protected Type entityGenericType;
	protected Class<?> entityClass;
	protected Annotation[] entityAnnotations;
	protected ClientConfiguration configuration;
	protected URI uri;

	// todo need a better solution for this.  Apache Http Client 4 does not let you obtain the OutputStream before executing
	// this request. is problematic for obtaining and setting
	// the output stream.  It also does not let you modify the request headers before the output stream is available
	// Since MessageBodyWriter allows you to modify headers, you're s
	protected DelegatingOutputStream delegatingOutputStream = new DelegatingOutputStream();
	protected OutputStream entityStream = delegatingOutputStream;

	public ClientInvocation(ResteasyClient client, URI uri, ClientRequestHeaders headers, ClientConfiguration parent)
	{
		this.uri = uri;
		this.client = client;
		this.configuration = parent; //new ClientConfiguration(parent);
		this.headers = headers;
	}

	/**
	 * Extracts result from response throwing an appropriate exception if not a successful response.
	 *
	 * @param responseType
	 * @param response
	 * @param annotations
	 * @param <T>
	 * @return
	 */
	public static <T> T extractResult(GenericType<T> responseType, Response response, Annotation[] annotations)
	{
		int status = response.getStatus();
		if (status >= 200 && status < 300)
		{
			try
			{
				if (response.getMediaType() == null)
				{
					return null;
				}
				else
				{
					return response.readEntity(responseType, annotations);
				}
			}
			catch (WebApplicationException wae)
			{
				throw wae;
			}
			catch (Throwable throwable)
			{
				throw new ResponseProcessingException(response, throwable);
			}
			finally
			{
				if (response.getMediaType() == null) response.close();
			}
		}
		try
		{
			if (status >= 300 && status < 400) throw new RedirectionException(response);

			handleErrorStatus(response);
			return null;
		}
		finally
		{
			// close if no content
			if (response.getMediaType() == null) response.close();
		}

	}

	/**
	 * Throw an exception.  Expecting a status of 400 or greater.
	 *
	 * @param response
	 * @param <T>
	 * @return
	 */
	public static void handleErrorStatus(Response response)
	{
		final int status = response.getStatus();
		switch (status)
		{
		case 400:
			throw new BadRequestException(response);
		case 401:
			throw new NotAuthorizedException(response);
		case 404:
			throw new NotFoundException(response);
		case 405:
			throw new NotAllowedException(response);
		case 406:
			throw new NotAcceptableException(response);
		case 415:
			throw new NotSupportedException(response);
		case 500:
			throw new InternalServerErrorException(response);
		case 503:
			throw new ServiceUnavailableException(response);
		default:
			break;
		}

		if (status >= 400 && status < 500) throw new ClientErrorException(response);
		if (status >= 500) throw new ServerErrorException(response);


		throw new WebApplicationException(response);
	}

	public ClientConfiguration getClientConfiguration()
	{
		return configuration;
	}

	public ResteasyClient getClient()
	{
		return client;
	}

	public DelegatingOutputStream getDelegatingOutputStream()
	{
		return delegatingOutputStream;
	}

	public void setDelegatingOutputStream(DelegatingOutputStream delegatingOutputStream)
	{
		this.delegatingOutputStream = delegatingOutputStream;
	}

	public OutputStream getEntityStream()
	{
		return entityStream;
	}

	public void setEntityStream(OutputStream entityStream)
	{
		this.entityStream = entityStream;
	}

	public URI getUri()
	{
		return uri;
	}

	public void setUri(URI uri)
	{
		this.uri = uri;
	}

	public Annotation[] getEntityAnnotations()
	{
		return entityAnnotations;
	}

	public void setEntityAnnotations(Annotation[] entityAnnotations)
	{
		this.entityAnnotations = entityAnnotations;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public void setHeaders(ClientRequestHeaders headers)
	{
		this.headers = headers;
	}

	public Map<String, Object> getMutableProperties()
	{
		return configuration.getMutableProperties();
	}

	public Object getEntity()
	{
		return entity;
	}

	public Type getEntityGenericType()
	{
		return entityGenericType;
	}

	public Class<?> getEntityClass()
	{
		return entityClass;
	}

	public ClientRequestHeaders getHeaders()
	{
		return headers;
	}

	public void setEntity(Entity entity)
	{
		if (entity == null)
		{
			this.entity = null;
			this.entityAnnotations = null;
			this.entityClass = null;
			this.entityGenericType = null;
		}
		else
		{
			Object ent = entity.getEntity();
			setEntityObject(ent);
			this.entityAnnotations = entity.getAnnotations();
			Variant v = entity.getVariant();
			headers.setMediaType(v.getMediaType());
			headers.setLanguage(v.getLanguage());
			headers.header("Content-Encoding", v.getEncoding());
		}

	}

	public void setEntityObject(Object ent)
	{
		if (ent instanceof GenericEntity)
		{
			@SuppressWarnings("unchecked")
			GenericEntity<Object> genericEntity = (GenericEntity<Object>) ent;
			entityClass = genericEntity.getRawType();
			entityGenericType = genericEntity.getType();
			this.entity = genericEntity.getEntity();
		}
		else
		{
			this.entity = ent;
			this.entityClass = ent.getClass();
			this.entityGenericType = ent.getClass();
		}
	}

	public void writeRequestBody(OutputStream outputStream) throws IOException
	{
		if (entity == null)
		{
			return;
		}

		MessageBodyWriter writer = getWriter();
		WriterInterceptor[] interceptors = getWriterInterceptors();
		if (interceptors == null || interceptors.length == 0)
		{
			writer.writeTo(entity, entityClass, entityGenericType, entityAnnotations, headers.getMediaType(), headers.getHeaders(), outputStream);
		}
		else
		{
			AbstractWriterInterceptorContext ctx = new ClientWriterInterceptorContext(interceptors, writer, entity, entityClass, entityGenericType, entityAnnotations, headers.getMediaType(), headers.getHeaders(), outputStream, getMutableProperties());
			ctx.proceed();
		}
	}

	public MessageBodyWriter getWriter()
	{
		MessageBodyWriter writer = configuration
				.getMessageBodyWriter(entityClass, entityGenericType,
						entityAnnotations, this.getHeaders().getMediaType());
		if (writer == null)
		{
			throw new ProcessingException("could not find writer for content-type "
					+ this.getHeaders().getMediaType() + " type: " + entityClass.getName());
		}
		return writer;
	}


	public WriterInterceptor[] getWriterInterceptors()
	{
		return configuration.getWriterInterceptors(null, null);
	}

	public ClientRequestFilter[] getRequestFilters()
	{
		return configuration.getRequestFilters(null, null);
	}

	public ClientResponseFilter[] getResponseFilters()
	{
		return configuration.getResponseFilters(null, null);
	}

	// Invocation methods


	public Configuration getConfiguration()
	{
		return configuration;
	}

	@Override
	public void invoke(final IAsyncCallback<Response> callback)
	{
		Providers current = ResteasyProviderFactory.getContextData(Providers.class);
		ResteasyProviderFactory.pushContext(Providers.class, configuration);
		try
		{
			final ClientRequestContextImpl requestContext = new ClientRequestContextImpl(this);
			final ClientRequestFilter[] requestFilters = getRequestFilters();
			ClientResponse aborted = null;
			if (requestFilters != null && requestFilters.length > 0)
			{
				for (ClientRequestFilter filter : requestFilters)
				{
					try
					{
						filter.filter(requestContext);
						if (requestContext.getAbortedWithResponse() != null)
						{
							aborted = new AbortedResponse(configuration, requestContext.getAbortedWithResponse());
							break;
						}
					}
					catch (ProcessingException e)
					{
						throw e;
					}
					catch (WebApplicationException e)
					{
						throw e;
					}
					catch (Throwable e)
					{
						throw new ProcessingException(e);
					}
				}
			}
			// spec requires that aborted response go through filter/interceptor chains.
			ClientResponse response = aborted;
			IAsyncCallback<ClientResponse> httpCallback = new IAsyncCallback<ClientResponse>() {

				@Override
				public void success(ClientResponse result) {
					Providers current = ResteasyProviderFactory.getContextData(Providers.class);
					ResteasyProviderFactory.pushContext(Providers.class, configuration);
					try {
						result.setProperties(configuration.getMutableProperties());

						ClientResponseFilter[] responseFilters = getResponseFilters();
						Throwable ex = null;
						if (requestFilters != null && requestFilters.length > 0)
						{
							ClientResponseContextImpl responseContext = new ClientResponseContextImpl(result);
							for (ClientResponseFilter filter : responseFilters)
							{
								try
								{
									filter.filter(requestContext, responseContext);
								}
								catch (ResponseProcessingException e)
								{
									ex = e;
									break;
								}
								catch (Throwable e)
								{
									ex = new ResponseProcessingException(result, e);
									break;
								}
							}
						}
						if(ex != null){
							callback.failed(ex);
						}else{
							callback.success(result);
						}
					}finally {
						ResteasyProviderFactory.popContextData(Providers.class);
						if (current != null) ResteasyProviderFactory.pushContext(Providers.class, current);
					}
				}

				@Override
				public void failed(Throwable cause) {
					callback.failed(cause);

				}

				@Override
				public void setCancellable(ICancellable cancellable) {
					callback.setCancellable(cancellable);
				}

				@Override
				public void cancelled() {
					callback.cancelled();
				}
			};
			if (response != null) {
				httpCallback.success(response);
			}else{
				client.httpEngine().invoke(this,httpCallback);
			}
		}
		finally
		{
			ResteasyProviderFactory.popContextData(Providers.class);
			if (current != null) ResteasyProviderFactory.pushContext(Providers.class, current);
		}
	}

	@Override
	public <T> void invoke(final Class<T> responseType,final IAsyncCallback<T> callback)
	{
		invoke(new IAsyncCallback<Response>() {

			@Override
			public void success(Response result) {
				callback.success(extractResult(new GenericType<T>(responseType), result, null));
			}

			@Override
			public void failed(Throwable cause) {
				callback.failed(cause);
			}

			@Override
			public void setCancellable(ICancellable cancellable) {
				callback.setCancellable(cancellable);
			}

			@Override
			public void cancelled() {
				callback.cancelled();
			}
		});
	}

	@Override
	public <T> void invoke(final GenericType<T> responseType,final IAsyncCallback<T> callback)
	{
		invoke(new IAsyncCallback<Response>() {

			@Override
			public void success(Response result) {
				callback.success(extractResult(responseType, result, null));
			}

			@Override
			public void failed(Throwable cause) {
				callback.failed(cause);
			}

			@Override
			public void setCancellable(ICancellable cancellable) {
				callback.setCancellable(cancellable);
			}

			@Override
			public void cancelled() {
				callback.cancelled();
			}
		});
	}


	@Override
	public Invocation property(String name, Object value)
	{
		configuration.property(name, value);
		return this;
	}

}
