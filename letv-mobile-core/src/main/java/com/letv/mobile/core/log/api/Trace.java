/*
 * $Id$
 * 
 * Copyright (C) Hygensoft Inc. All rights reserved.
 * 
 * Created on Sep 18, 2003
 */

package com.letv.mobile.core.log.api;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.letv.mobile.core.log.spi.ILoggerFactory;
import com.letv.mobile.core.log.spi.Log;



/**
Insert type's desciption here
@author   Lin
Aug 6, 2002
Revised History
 */
public class Trace 
{
	private final Log logger;
	private Log delegate;
	private static Map<String, Trace> registeredLoggers = new ConcurrentHashMap<String, Trace>();
	private static Map<String, Log> registeredTaps = new ConcurrentHashMap<String, Log>();

	/**
	 * @param message
	 */
	public void debug(String message) {
		log(SeverityLevel.DEBUG,message);
	}

	/**
	 * @param message
	 * @param e
	 */
	public void debug(String message, Throwable e) {
		log(SeverityLevel.DEBUG,message, e);
	}

	/**
	 * @param message
	 */
	public void error(String message) {
		log(SeverityLevel.ERROR,message);
	}

	/**
	 * @param message
	 * @param e
	 */
	public void error(String message, Throwable e) {
		log(SeverityLevel.ERROR,message, e);
	}

	/**
	 * @param message
	 */
	public void fatal(String message) {
		log(SeverityLevel.FATAL,message);
	}

	/**
	 * @param message
	 * @param e
	 */
	public void fatal(String message, Throwable e) {
		log(SeverityLevel.FATAL,message, e);
	}

	/**
	 * @param message
	 */
	public void info(String message) {
		log(SeverityLevel.INFO,message);
	}

	/**
	 * @param message
	 * @param e
	 */
	public void info(String message, Throwable e) {
		log(SeverityLevel.INFO,message, e);
	}

	/**
	 * @return
	 */
	public boolean isDebugEnabled() {
		return this.isEnabled(SeverityLevel.DEBUG);
	}

	public boolean isTraceEnabled() {
		return this.isEnabled(SeverityLevel.TRACE);
	}
	/**
	 * @return
	 */
	public boolean isInfoEnabled() {
		return this.isEnabled(SeverityLevel.INFO);
	}

	/**
	 * @param message
	 */
	public void warn(String message) {
		log(SeverityLevel.WARN,message);
	}

	/**
	 * @param message
	 * @param arg1
	 */
	public void warn(String message, Throwable e) {
		if(logger.isEnabled(SeverityLevel.DEBUG)){
			logger.log(SeverityLevel.WARN,message, e);
		}else if(e != null){
			logger.log(SeverityLevel.WARN,new StringBuilder(message).append(",Exception :").append(e.getClass().getName()).append(", message :").append(e.getLocalizedMessage()).toString());
		}else{
			logger.log(SeverityLevel.WARN,message);
		}
		if(delegate != null){
			delegate.log(SeverityLevel.WARN,message, e);
		}
	}

	/**
	 * @param arg0
	 */
	Trace(String category) {
		this.logger = ILoggerFactory.DefaultFactory.getLoggerFactory().createLogger(category);
	}

	/**
   @param cls
   @return Trace
   @roseuid 3E51E0880109
	 */
	public static Trace register(Class<?> cls) 
	{
		return register(cls.getName());
	}

	public static Trace getTrace(Class<?> cls) {
		return register(cls.getName());
	}

	public static Trace getTrace(String name) {
		return register(name);
	}
	
	public static Trace getLogger(String name){
		return register(name);
	}
	
	public static Trace getLogger(Class<?> cls){
		return register(cls);
	}

	/**
   @param catagory
   @return Trace
   @roseuid 3E51E0880132
	 */
	public static Trace register(String catagory) 
	{
		Trace tr = registeredLoggers.get(catagory);
		if(tr == null){
			tr = new Trace(catagory);
			registeredLoggers.put(catagory, tr);
			for (String cate : registeredTaps.keySet()) {
				if(catagory.startsWith(cate)){
					tr.delegate = registeredTaps.get(cate);
					break;
				}
			}
		}
		return tr;
	}

	public static void tap(String catagory,Log tr) 
	{
		if((tr == null)||(catagory == null)||(catagory.trim().length() == 0)){
			throw new IllegalArgumentException("tr and category cannot be NULL !");
		}
		registeredTaps.put(catagory, tr);
		for (String cate : registeredLoggers.keySet()) {
			if(cate.startsWith(catagory)){
				registeredLoggers.get(cate).delegate = tr;
			}
		}
	}

	public static void unTap(String catagory,Log log) 
	{
		registeredTaps.remove(catagory);
		for (String cate : registeredLoggers.keySet()) {
			if(cate.startsWith(catagory)){
				Trace tr = registeredLoggers.get(cate);
				if(tr.delegate == log){
					tr.delegate = null;
				}
			}
		}
	}

	public static void unTapAll(Log log) 
	{
		for (String name : registeredLoggers.keySet()) {
			unTap(name, log);
		}
	}

	public static void tapAll(Log log){
		for (String name : registeredLoggers.keySet()) {
			tap(name, log);
		}
	}

	public static Iterator<String> allTracers() {
		return registeredLoggers.keySet().iterator();
	}

	public static void tap(Class<?> cls,Log tr) 
	{
		tap(cls.getName(), tr);
	}

	public static void unTap(Class<?> cls,Log tr) 
	{
		unTap(cls.getName(), tr);
	}


	/**
	 * @param sourceClass
	 * @param sourceMethod
	 */
	public void entering(String sourceClass, String sourceMethod) {
		if(isTraceEnabled()){
			trace(new StringBuffer(">>>").append(sourceClass).append('.').append(sourceMethod).append("( )").toString());
		}
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 * @param param1
	 */
	public void entering(
			String sourceClass,
			String sourceMethod,
			Object param1) {
		if(isTraceEnabled()){
			trace(new StringBuffer(">>>").append(sourceClass).append('.').append(sourceMethod).append("( )\n\t Parameters :[").append(param1).append(']').toString());
		}
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 * @param params
	 */
	public void entering(
			String sourceClass,
			String sourceMethod,
			Object[] params) {
		if(isTraceEnabled()){
			StringBuffer buf = new StringBuffer(">>>").append(sourceClass).append('.').append(sourceMethod).append("( )\n\t Parameters: [ ");
			for(int i=0 ; (params != null)&&(i < params.length) ;i++){
				if(i > 0)
					buf.append(",\n\t\t");
				buf.append(params[i]);
			}
			trace(buf.append(" ]").toString());
		}
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 */
	public void exiting(String sourceClass, String sourceMethod) {
		if(isTraceEnabled()){
			trace(new StringBuffer("<<<").append(sourceClass).append('.').append(sourceMethod).append("( )").toString());
		}
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 * @param result
	 */
	public void exiting(
			String sourceClass,
			String sourceMethod,
			Object result) {
		if(isTraceEnabled()){
			trace(new StringBuffer("<<<").append(sourceClass).append('.').append(sourceMethod).append("( )\n\t Result:[").append(result).append(']').toString());
		}
	}

	/**
	 * @param msg
	 */
	public void trace(String msg) {
		log(SeverityLevel.TRACE,msg);
	}

	/**
	 * @param msg
	 */
	public void trace(String msg,Throwable t) {
		log(SeverityLevel.TRACE,msg,t);
	}


	/**
	 * @param msg
	 */
	public void fine(String msg) {
		log(SeverityLevel.TRACE,msg);
	}

	/**
	 * @param msg
	 */
	public void finer(String msg) {
		log(SeverityLevel.TRACE,msg);
	}

	private Throwable getThrowable(Object... args) {
		if((args == null)||(args.length == 0)){
			return null;
		}
		Object obj = args[args.length-1];
		if(obj instanceof Throwable) {
			return (Throwable)obj;
		}
		return null;
	}

	public void fatal(String message, Object... args) {
		Throwable t = getThrowable(args);
		if(t != null){
			int len = args.length-1;
			Object[] oldArgs = args;
			if(len > 0){
				args = new Object[len];
				for(int i=0 ; i < len ; i++){
					args[i] = oldArgs[i];
				}
			}else{
				args = null;
			}
		}
		log(SeverityLevel.FATAL,message,args,t);
	}

	public void error(String message, Object... args) {
		Throwable t = getThrowable(args);
		if(t != null){
			int len = args.length-1;
			Object[] oldArgs = args;
			if(len > 0){
				args = new Object[len];
				for(int i=0 ; i < len ; i++){
					args[i] = oldArgs[i];
				}
			}else{
				args = null;
			}
		}
		log(SeverityLevel.ERROR,message,args,t);
	}

	public void warn(String message, Object... args) {
		Throwable t = getThrowable(args);
		if(t != null){
			int len = args.length-1;
			Object[] oldArgs = args;
			if(len > 0){
				args = new Object[len];
				for(int i=0 ; i < len ; i++){
					args[i] = oldArgs[i];
				}
			}else{
				args = null;
			}
		}
		log(SeverityLevel.WARN,message,args,t);
	}

	public void debug(String message, Object... args) {		
		if(!isDebugEnabled()){
			return;
		}
		Throwable t = getThrowable(args);
		if(t != null){
			int len = args.length-1;
			Object[] oldArgs = args;
			if(len > 0){
				args = new Object[len];
				for(int i=0 ; i < len ; i++){
					args[i] = oldArgs[i];
				}
			}else{
				args = null;
			}
		}
		log(SeverityLevel.DEBUG,message,args,t);
	}

	public void info(String message, Object... args) {
		if(!isInfoEnabled()){
			return;
		}
		Throwable t = getThrowable(args);
		if(t != null){
			int len = args.length-1;
			Object[] oldArgs = args;
			if(len > 0){
				args = new Object[len];
				for(int i=0 ; i < len ; i++){
					args[i] = oldArgs[i];
				}
			}else{
				args = null;
			}
		}
		log(SeverityLevel.INFO,message,args,t);
	}

	public void trace(String message, Object... args) {
		if(!isTraceEnabled()){
			return;
		}
		Throwable t = getThrowable(args);
		if(t != null){
			int len = args.length-1;
			Object[] oldArgs = args;
			if(len > 0){
				args = new Object[len];
				for(int i=0 ; i < len ; i++){
					args[i] = oldArgs[i];
				}
			}else{
				args = null;
			}
		}
		log(SeverityLevel.TRACE,message,args,t);
	}

	/**
	 * @param level
	 * @return
	 * @see com.letv.mobile.core.log.spi.Log#isEnabled(com.wxxr.mobile.core.log.api.SeverityLevel)
	 */
	public boolean isEnabled(SeverityLevel level) {
		return logger.isEnabled(level)||((delegate != null)&&delegate.isEnabled(level));
	}

	/**
	 * @param level
	 * @param message
	 * @see com.letv.mobile.core.log.spi.Log#log(com.wxxr.mobile.core.log.api.SeverityLevel, java.lang.Object)
	 */
	public void log(SeverityLevel level, String message) {
		if(logger.isEnabled(level)) {
			logger.log(level, message);
		}
		if((delegate != null)&&delegate.isEnabled(level)){
			delegate.log(level, message);
		}
	}

	/**
	 * @param level
	 * @param message
	 * @param t
	 * @see com.letv.mobile.core.log.spi.Log#log(com.wxxr.mobile.core.log.api.SeverityLevel, java.lang.Object, java.lang.Throwable)
	 */
	public void log(SeverityLevel level, String message, Throwable t) {
		if(logger.isEnabled(level)) {
			logger.log(level, message, t);
		}
		if((delegate != null)&&delegate.isEnabled(level)){
			delegate.log(level, message,t);
		}
	}

	/**
	 * @param level
	 * @param message
	 * @param args
	 * @param t
	 * @see com.letv.mobile.core.log.spi.Log#log(com.wxxr.mobile.core.log.api.SeverityLevel, java.lang.String, java.lang.Object[], java.lang.Throwable)
	 */
	public void log(SeverityLevel level, String message, Object[] args,
			Throwable t) {
		if(logger.isEnabled(level)) {
			logger.log(level, message, args, t);
		}
		if((delegate != null)&&delegate.isEnabled(level)){
			delegate.log(level, message, args, t);
		}
	}


}
