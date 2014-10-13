/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// Contibutors: "Luke Blanshard" <Luke@quiq.com>
//              "Mark DONSZELMANN" <Mark.Donszelmann@cern.ch>
//               Anders Kristensen <akristensen@dynamicsoft.com>

package org.apache.log4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;

import com.letv.mobile.core.util.StringUtils;

/**
   Allows the configuration of log4j from an external file.  See
   <b>{@link #doConfigure(String, LoggerRepository)}</b> for the
   expected format.

   <p>It is sometimes useful to see how log4j is reading configuration
   files. You can enable log4j internal logging by defining the
   <b>log4j.debug</b> variable.

   <P>As of log4j version 0.8.5, at class initialization time class,
   the file <b>log4j.properties</b> will be searched from the search
   path used to load classes. If the file can be found, then it will
   be fed to the {@link PropertyConfigurator#configure(java.net.URL)}
   method.

   <p>The <code>PropertyConfigurator</code> does not handle the
   advanced configuration features supported by the {@link
   org.apache.log4j.xml.DOMConfigurator DOMConfigurator} such as
   support custom {@link org.apache.log4j.spi.ErrorHandler ErrorHandlers},
   nested appenders such as the {@link org.apache.log4j.AsyncAppender
   AsyncAppender}, etc.

   <p>All option <em>values</em> admit variable substitution. The
   syntax of variable substitution is similar to that of Unix
   shells. The string between an opening <b>&quot;${&quot;</b> and
   closing <b>&quot;}&quot;</b> is interpreted as a key. The value of
   the substituted variable can be defined as a system property or in
   the configuration file itself. The value of the key is first
   searched in the system properties, and if not found there, it is
   then searched in the configuration file being parsed.  The
   corresponding value replaces the ${variableName} sequence. For
   example, if <code>java.home</code> system property is set to
   <code>/home/xyz</code>, then every occurrence of the sequence
   <code>${java.home}</code> will be interpreted as
   <code>/home/xyz</code>.


   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen
   @since 0.8.1 */
public class PropertyConfigurator implements Configurator {

	/**
     Used internally to keep track of configured appenders.
	 */
	protected Hashtable registry = new Hashtable(11);  
	private LoggerRepository repository;
	protected LoggerFactory loggerFactory = new DefaultCategoryFactory();

	static final String      CATEGORY_PREFIX = "log4j.category.";
	static final String      LOGGER_PREFIX   = "log4j.logger.";
	static final String       FACTORY_PREFIX = "log4j.factory";
	static final String    ADDITIVITY_PREFIX = "log4j.additivity.";
	static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
	static final String ROOT_LOGGER_PREFIX   = "log4j.rootLogger";
	static final String      APPENDER_PREFIX = "log4j.appender.";
	static final String      RENDERER_PREFIX = "log4j.renderer.";
	static final String      THRESHOLD_PREFIX = "log4j.threshold";
	private static final String      THROWABLE_RENDERER_PREFIX = "log4j.throwableRenderer";
	private static final String LOGGER_REF	= "logger-ref";
	private static final String ROOT_REF		= "root-ref";
	private static final String APPENDER_REF_TAG 	= "appender-ref";  


	/** Key for specifying the {@link org.apache.log4j.spi.LoggerFactory
      LoggerFactory}.  Currently set to "<code>log4j.loggerFactory</code>".  */
	public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";

	/**
	 * If property set to true, then hierarchy will be reset before configuration.
	 */
	private static final String RESET_KEY = "log4j.reset";

	static final private String INTERNAL_ROOT_NAME = "root";


	public
	void doConfigure(String configFileName, LoggerRepository hierarchy) {
		Properties props = new Properties();
		FileInputStream istream = null;
		try {
			istream = new FileInputStream(configFileName);
			props.load(istream);
			istream.close();
		}
		catch (Exception e) {
			if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			LogLog.error("Could not read configuration file ["+configFileName+"].", e);
			LogLog.error("Ignoring configuration file [" + configFileName+"].");
			return;
		} finally {
			if(istream != null) {
				try {
					istream.close();
				} catch(InterruptedIOException ignore) {
					Thread.currentThread().interrupt();
				} catch(Throwable ignore) {
				}

			}
		}
		// If we reach here, then the config file is alright.
		doConfigure(props, hierarchy);
	}

	/**
	 */
	static
	public
	void configure(String configFilename) {
		new PropertyConfigurator().doConfigure(configFilename,
				LogManager.getLoggerRepository());
	}


	/**
Reads configuration options from an InputStream.

@since 1.2.17
	 */
	public
	static
	void configure(InputStream inputStream) {
		new PropertyConfigurator().doConfigure(inputStream,
				LogManager.getLoggerRepository());
	}


	/**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, LoggerRepository)} for the expected format.
	 */
	static
	public
	void configure(Properties properties) {
		new PropertyConfigurator().doConfigure(properties,
				LogManager.getLoggerRepository());
	}

	/**
     Like {@link #configureAndWatch(String, long)} except that the
     default delay as defined by {@link FileWatchdog#DEFAULT_DELAY} is
     used.

     @param configFilename A file in key=value format.

	 */
	static
	public
	void configureAndWatch(String configFilename) {
		configureAndWatch(configFilename, FileWatchdog.DEFAULT_DELAY);
	}


	/**
     Read the configuration file <code>configFilename</code> if it
     exists. Moreover, a thread will be created that will periodically
     check if <code>configFilename</code> has been created or
     modified. The period is determined by the <code>delay</code>
     argument. If a change or file creation is detected, then
     <code>configFilename</code> is read to configure log4j.

      @param configFilename A file in key=value format.
      @param delay The delay in milliseconds to wait between each check.
	 */
	static
	public
	void configureAndWatch(String configFilename, long delay) {
		PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
		pdog.setDelay(delay);
		pdog.start();
	}


	/**
     Read configuration options from <code>properties</code>.

     See {@link #doConfigure(String, LoggerRepository)} for the expected format.
	 */
	public
	void doConfigure(Properties properties, LoggerRepository hierarchy) {
		repository = hierarchy;
		String value = properties.getProperty(LogLog.DEBUG_KEY);
		if(value == null) {
			value = properties.getProperty("log4j.configDebug");
			if(value != null)
				LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
		}

		if(value != null) {
			LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
		}

		//
		//   if log4j.reset=true then
		//        reset hierarchy
		String reset = properties.getProperty(RESET_KEY);
		if (reset != null && OptionConverter.toBoolean(reset, false)) {
			hierarchy.resetConfiguration();
		}

		String thresholdStr = OptionConverter.findAndSubst(THRESHOLD_PREFIX,
				properties);
		if(thresholdStr != null) {
			hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr,
					(Level) Level.ALL));
			LogLog.debug("Hierarchy threshold set to ["+hierarchy.getThreshold()+"].");
		}

		configureRootCategory(properties, hierarchy);
		configureLoggerFactory(properties);
		parseCatsAndRenderers(properties, hierarchy);

		LogLog.debug("Finished configuring.");
		// We don't want to hold references to appenders preventing their
		// garbage collection.
		registry.clear();
	}

	/**
	 * Read configuration options from url <code>configURL</code>.
	 * 
	 * @since 1.2.17
	 */
	public void doConfigure(InputStream inputStream, LoggerRepository hierarchy) {
		Properties props = new Properties();
		try {
			props.load(inputStream);
		} catch (IOException e) {
			if (e instanceof InterruptedIOException) {
				Thread.currentThread().interrupt();
			}
			LogLog.error("Could not read configuration file from InputStream [" + inputStream
					+ "].", e);
			LogLog.error("Ignoring configuration InputStream [" + inputStream +"].");
			return;
		}
		this.doConfigure(props, hierarchy);
	}


	// --------------------------------------------------------------------------
	// Internal stuff
	// --------------------------------------------------------------------------

	/**
     Check the provided <code>Properties</code> object for a
     {@link org.apache.log4j.spi.LoggerFactory LoggerFactory}
     entry specified by {@link #LOGGER_FACTORY_KEY}.  If such an entry
     exists, an attempt is made to create an instance using the default
     constructor.  This instance is used for subsequent Category creations
     within this configurator.

     @see #parseCatsAndRenderers
	 */
	protected void configureLoggerFactory(Properties props) {
		String factoryClassName = OptionConverter.findAndSubst(LOGGER_FACTORY_KEY,
				props);
		if(factoryClassName != null) {
			LogLog.debug("Setting category factory to ["+factoryClassName+"].");
			loggerFactory = (LoggerFactory)
					OptionConverter.instantiateByClassName(factoryClassName,
							LoggerFactory.class,
							loggerFactory);
			PropertySetter.setProperties(loggerFactory, props, FACTORY_PREFIX + ".");
		}
	}

	/*
  void configureOptionHandler(OptionHandler oh, String prefix,
			      Properties props) {
    String[] options = oh.getOptionStrings();
    if(options == null)
      return;

    String value;
    for(int i = 0; i < options.length; i++) {
      value =  OptionConverter.findAndSubst(prefix + options[i], props);
      LogLog.debug(
         "Option " + options[i] + "=[" + (value == null? "N/A" : value)+"].");
      // Some option handlers assume that null value are not passed to them.
      // So don't remove this check
      if(value != null) {
	oh.setOption(options[i], value);
      }
    }
    oh.activateOptions();
  }
	 */


	void configureRootCategory(Properties props, LoggerRepository hierarchy) {
		String effectiveFrefix = ROOT_LOGGER_PREFIX;
		String value = OptionConverter.findAndSubst(ROOT_LOGGER_PREFIX, props);

		if(value == null) {
			value = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, props);
			effectiveFrefix = ROOT_CATEGORY_PREFIX;
		}

		if(value == null)
			LogLog.debug("Could not find root logger information. Is this OK?");
		else {
			Logger root = hierarchy.getRootLogger();
			synchronized(root) {
				parseCategory(props, root, effectiveFrefix, INTERNAL_ROOT_NAME, value);
			}
		}
	}


	/**
     Parse non-root elements, such non-root categories and renderers.
	 */
	protected
	void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy) {
		Enumeration enumeration = props.propertyNames();
		while(enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			if(key.startsWith(CATEGORY_PREFIX) || key.startsWith(LOGGER_PREFIX)) {
				String loggerName = null;
				if(key.startsWith(CATEGORY_PREFIX)) {
					loggerName = key.substring(CATEGORY_PREFIX.length());
				} else if(key.startsWith(LOGGER_PREFIX)) {
					loggerName = key.substring(LOGGER_PREFIX.length());
				}
				String value =  OptionConverter.findAndSubst(key, props);
				Logger logger = hierarchy.getLogger(loggerName, loggerFactory);
				synchronized(logger) {
					parseCategory(props, logger, key, loggerName, value);
					parseAdditivityForLogger(props, logger, loggerName);
				}
			} else if(key.startsWith(RENDERER_PREFIX)) {
				String renderedClass = key.substring(RENDERER_PREFIX.length());
				String renderingClass = OptionConverter.findAndSubst(key, props);
				if(hierarchy instanceof RendererSupport) {
					RendererMap.addRenderer((RendererSupport) hierarchy, renderedClass,
							renderingClass);
				}
			} else if (key.equals(THROWABLE_RENDERER_PREFIX)) {
				if (hierarchy instanceof ThrowableRendererSupport) {
					ThrowableRenderer tr = (ThrowableRenderer)
							OptionConverter.instantiateByKey(props,
									THROWABLE_RENDERER_PREFIX,
									org.apache.log4j.spi.ThrowableRenderer.class,
									null);
					if(tr == null) {
						LogLog.error(
								"Could not instantiate throwableRenderer.");
					} else {
						PropertySetter setter = new PropertySetter(tr);
						setter.setProperties(props, THROWABLE_RENDERER_PREFIX + ".");
						((ThrowableRendererSupport) hierarchy).setThrowableRenderer(tr);

					}
				}
			}
		}
	}

	/**
     Parse the additivity option for a non-root category.
	 */
	void parseAdditivityForLogger(Properties props, Logger cat,
			String loggerName) {
		String value = OptionConverter.findAndSubst(ADDITIVITY_PREFIX + loggerName,
				props);
		LogLog.debug("Handling "+ADDITIVITY_PREFIX + loggerName+"=["+value+"]");
		// touch additivity only if necessary
		if((value != null) && (!value.equals(""))) {
			boolean additivity = OptionConverter.toBoolean(value, true);
			LogLog.debug("Setting additivity for \""+loggerName+"\" to "+
					additivity);
			cat.setAdditivity(additivity);
		}
	}

	/**
     This method must work for the root category as well.
	 */
	void parseCategory(Properties props, Logger logger, String optionKey,
			String loggerName, String value) {

		LogLog.debug("Parsing for [" +loggerName +"] with value=[" + value+"].");
		// We must skip over ',' but not white space
		String[] st = StringUtils.split(value, ",");
		int idx = 0;
		//    StringTokenizer st = new StringTokenizer(value, ",");

		// If value is not in the form ", appender.." or "", then we should set
		// the level of the loggeregory.

		if(!(value.startsWith(",") || value.equals(""))) {

			// just to be on the safe side...
			if((st == null)||(st.length == 0))
				return;

			String levelStr = st[idx++];
			LogLog.debug("Level token is [" + levelStr + "].");

			// If the level value is inherited, set category level value to
			// null. We also check that the user has not specified inherited for the
			// root category.
			if(INHERITED.equalsIgnoreCase(levelStr) || 
					NULL.equalsIgnoreCase(levelStr)) {
				if(loggerName.equals(INTERNAL_ROOT_NAME)) {
					LogLog.warn("The root logger cannot be set to null.");
				} else {
					logger.setLevel(null);
				}
			} else {
				logger.setLevel(OptionConverter.toLevel(levelStr, (Level) Level.DEBUG));
			}
			LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
		}

		// Begin by removing all existing appenders.
		logger.removeAllAppenders();

		Appender appender;
		String appenderName;
		while(idx < st.length) {
			appenderName = st[idx++].trim();
			if(appenderName == null || appenderName.equals(","))
				continue;
			LogLog.debug("Parsing appender named \"" + appenderName +"\".");
			appender = parseAppender(props, appenderName);
			if(appender != null) {
				logger.addAppender(appender);
			}
		}
	}

	Appender parseAppender(Properties props, String appenderName) {
		Appender appender = registryGet(appenderName);
		if((appender != null)) {
			LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
			return appender;
		}
		// Appender was not previously initialized.
		String prefix = APPENDER_PREFIX + appenderName;
		String layoutPrefix = prefix + ".layout";

		appender = (Appender) OptionConverter.instantiateByKey(props, prefix,
				org.apache.log4j.Appender.class,
				null);
		if(appender == null) {
			LogLog.error(
					"Could not instantiate appender named \"" + appenderName+"\".");
			return null;
		}
		appender.setName(appenderName);

		if(appender instanceof OptionHandler) {
			if(appender.requiresLayout()) {
				Layout layout = (Layout) OptionConverter.instantiateByKey(props,
						layoutPrefix,
						Layout.class,
						null);
				if(layout != null) {
					appender.setLayout(layout);
					LogLog.debug("Parsing layout options for \"" + appenderName +"\".");
					//configureOptionHandler(layout, layoutPrefix + ".", props);
					PropertySetter.setProperties(layout, props, layoutPrefix + ".");
					LogLog.debug("End of parsing for \"" + appenderName +"\".");
				}
			}
			final String errorHandlerPrefix = prefix + ".errorhandler";
			String errorHandlerClass = OptionConverter.findAndSubst(errorHandlerPrefix, props);
			if (errorHandlerClass != null) {
				ErrorHandler eh = (ErrorHandler) OptionConverter.instantiateByKey(props,
						errorHandlerPrefix,
						ErrorHandler.class,
						null);
				if (eh != null) {
					appender.setErrorHandler(eh);
					LogLog.debug("Parsing errorhandler options for \"" + appenderName +"\".");
					parseErrorHandler(eh, errorHandlerPrefix, props, repository);
					final Properties edited = new Properties();
					final String[] keys = new String[] { 
							errorHandlerPrefix + "." + ROOT_REF,
							errorHandlerPrefix + "." + LOGGER_REF,
							errorHandlerPrefix + "." + APPENDER_REF_TAG
					};
					for(Iterator iter = props.entrySet().iterator();iter.hasNext();) {
						Map.Entry entry = (Map.Entry) iter.next();
						int i = 0;
						for(; i < keys.length; i++) {
							if(keys[i].equals(entry.getKey())) break;
						}
						if (i == keys.length) {
							edited.put(entry.getKey(), entry.getValue());
						}
					}
					PropertySetter.setProperties(eh, edited, errorHandlerPrefix + ".");
					LogLog.debug("End of errorhandler parsing for \"" + appenderName +"\".");
				}

			}
			//configureOptionHandler((OptionHandler) appender, prefix + ".", props);
			PropertySetter.setProperties(appender, props, prefix + ".");
			LogLog.debug("Parsed \"" + appenderName +"\" options.");
		}
		parseAppenderFilters(props, appenderName, appender);
		registryPut(appender);
		return appender;
	}

	private void parseErrorHandler(
			final ErrorHandler eh,
			final String errorHandlerPrefix,
			final Properties props, 
			final LoggerRepository hierarchy) {
		boolean rootRef = OptionConverter.toBoolean(
				OptionConverter.findAndSubst(errorHandlerPrefix + ROOT_REF, props), false);
		if (rootRef) {
			eh.setLogger(hierarchy.getRootLogger());
		}
		String loggerName = OptionConverter.findAndSubst(errorHandlerPrefix + LOGGER_REF , props);
		if (loggerName != null) {
			Logger logger = (loggerFactory == null) ? hierarchy.getLogger(loggerName)
					: hierarchy.getLogger(loggerName, loggerFactory);
			eh.setLogger(logger);
		}
		String appenderName = OptionConverter.findAndSubst(errorHandlerPrefix + APPENDER_REF_TAG, props);
		if (appenderName != null) {
			Appender backup = parseAppender(props, appenderName);
			if (backup != null) {
				eh.setBackupAppender(backup);
			}
		}
	}


	void parseAppenderFilters(Properties props, String appenderName, Appender appender) {
		// extract filters and filter options from props into a hashtable mapping
		// the property name defining the filter class to a list of pre-parsed
		// name-value pairs associated to that filter
		final String filterPrefix = APPENDER_PREFIX + appenderName + ".filter.";
		int fIdx = filterPrefix.length();
		Hashtable filters = new Hashtable();
		Enumeration e = props.keys();
		String name = "";
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (key.startsWith(filterPrefix)) {
				int dotIdx = key.indexOf('.', fIdx);
				String filterKey = key;
				if (dotIdx != -1) {
					filterKey = key.substring(0, dotIdx);
					name = key.substring(dotIdx+1);
				}
				Vector filterOpts = (Vector) filters.get(filterKey);
				if (filterOpts == null) {
					filterOpts = new Vector();
					filters.put(filterKey, filterOpts);
				}
				if (dotIdx != -1) {
					String value = OptionConverter.findAndSubst(key, props);
					filterOpts.add(new NameValue(name, value));
				}
			}
		}

		// sort filters by IDs, insantiate filters, set filter options,
		// add filters to the appender
		Enumeration g = new SortedKeyEnumeration(filters);
		while (g.hasMoreElements()) {
			String key = (String) g.nextElement();
			String clazz = props.getProperty(key);
			if (clazz != null) {
				LogLog.debug("Filter key: ["+key+"] class: ["+props.getProperty(key) +"] props: "+filters.get(key));
				Filter filter = (Filter) OptionConverter.instantiateByClassName(clazz, Filter.class, null);
				if (filter != null) {
					PropertySetter propSetter = new PropertySetter(filter);
					Vector v = (Vector)filters.get(key);
					Enumeration filterProps = v.elements();
					while (filterProps.hasMoreElements()) {
						NameValue kv = (NameValue)filterProps.nextElement();
						propSetter.setProperty(kv.key, kv.value);
					}
					propSetter.activate();
					LogLog.debug("Adding filter of type ["+filter.getClass()
							+"] to appender named ["+appender.getName()+"].");
					appender.addFilter(filter);
				}
			} else {
				LogLog.warn("Missing class definition for filter: ["+key+"]");
			}
		}
	}


	void  registryPut(Appender appender) {
		registry.put(appender.getName(), appender);
	}

	Appender registryGet(String name) {
		return (Appender) registry.get(name);
	}
}

class PropertyWatchdog extends FileWatchdog {

	PropertyWatchdog(String filename) {
		super(filename);
	}

	/**
     Call {@link PropertyConfigurator#configure(String)} with the
     <code>filename</code> to reconfigure log4j. */
	public
	void doOnChange() {
		new PropertyConfigurator().doConfigure(filename,
				LogManager.getLoggerRepository());
	}
}

class NameValue {
	String key, value;
	public NameValue(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String toString() {
		return key + "=" + value;
	}
}

class SortedKeyEnumeration implements Enumeration {

	private Enumeration e;

	public SortedKeyEnumeration(Hashtable ht) {
		Enumeration f = ht.keys();
		Vector keys = new Vector(ht.size());
		for (int i, last = 0; f.hasMoreElements(); ++last) {
			String key = (String) f.nextElement();
			for (i = 0; i < last; ++i) {
				String s = (String) keys.get(i);
				if (key.compareTo(s) <= 0) break;
			}
			keys.add(i, key);
		}
		e = keys.elements();
	}

	public boolean hasMoreElements() {
		return e.hasMoreElements();
	}

	public Object nextElement() {
		return e.nextElement();
	}
}
