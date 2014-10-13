package com.letv.mobile.core.rpc.rest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import com.letv.mobile.core.rpc.rest.spi.ConstructorInjector;
import com.letv.mobile.core.rpc.rest.spi.PropertyInjector;
import com.letv.mobile.core.rpc.rest.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public interface InjectorFactory
{
   ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory factory);
   PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory factory);
   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory);
   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory factory);

//   ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory);
//
//   MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory);
//
//   PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory);
//
//   ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory);
}
