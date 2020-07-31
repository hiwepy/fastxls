package com.github.hiwepy.fastxls.jexcel.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import com.github.hiwepy.fastxls.core.property.ExportProperties;

public class JXLCellFormatCacheManager {

	protected static Logger LOG = LoggerFactory.getLogger(JXLCellFormatCacheManager.class);
	protected static ConcurrentMap<String, JXLCellFormatKey> COMPLIED_CELLFORMATKEY = new ConcurrentHashMap<String, JXLCellFormatKey>();
	protected static JXLFontCacheManager fontCacheManager = JXLFontCacheManager.getInstance();
	protected static LoadingCache<JXLCellFormatKey,WritableCellFormat> CELL_FORMAT_CACHE;
	
	private volatile static JXLCellFormatCacheManager singleton;

	public static JXLCellFormatCacheManager getInstance(ExportProperties properties) {
		if (singleton == null) {
			synchronized (JXLCellFormatCacheManager.class) {
				if (singleton == null) {
					singleton = new JXLCellFormatCacheManager(properties);
				}
			}
		}
		return singleton;
	}
	
	private JXLCellFormatCacheManager(ExportProperties properties){
		  
		// 字体名称对象缓存，减少对象创建的消耗
		// CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
		CELL_FORMAT_CACHE = CacheBuilder.newBuilder()
	        // 设置并发级别，并发级别是指可以同时写缓存的线程数
	        .concurrencyLevel(properties.getConcurrencyLevel())
	        // 设置缓存被创建或值被替换后多少分钟后过期
	        .expireAfterWrite(properties.getExpireAfterWrite(), TimeUnit.MINUTES)
	        // 设置缓存最后一次访问多少分钟后过期
	        .expireAfterAccess(properties.getExpireAfterAccess(), TimeUnit.MINUTES)
	        // 设置缓存容器的初始容量
	        .initialCapacity(properties.getInitialCapacity())
	        // 设置缓存最大容量限制，超过限制之后就会按照LRU最近虽少使用算法来移除缓存项
	        .maximumSize(properties.getMaximumSize())
	        // 
	        .maximumWeight(properties.getMaximumWeight())
	        // 设置要统计缓存的命中率
	        .recordStats()
	        // 设置缓存的移除通知
	        .removalListener(new RemovalListener<JXLCellFormatKey, WritableCellFormat>() {
	            @Override
	            public void onRemoval(RemovalNotification<JXLCellFormatKey, WritableCellFormat> notification) {
	            	LOG.info(notification.getKey().toString() + " was removed, cause is " + notification.getCause());
	            }
	        })
	        // build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
			.build(new CacheLoader<JXLCellFormatKey,WritableCellFormat>() {
				@Override
				public WritableCellFormat load(JXLCellFormatKey formatKey) throws Exception {
					//获取字体
					WritableFont font = fontCacheManager.getFont(formatKey.getName(), formatKey.getSize(), formatKey.isBold());
					//解析参数中的样式值
					Alignment alignment = formatKey.getAlignment();
					VerticalAlignment valignment = formatKey.getValignment();
					Colour background = formatKey.getBackground();
					Border border = formatKey.getBorder();
					BorderLineStyle borderLine = formatKey.getBorderLine();
					//创建CellFormat
					WritableCellFormat cellFormat = new WritableCellFormat(font);
					cellFormat.setAlignment(alignment);
					cellFormat.setVerticalAlignment(valignment);
					cellFormat.setBackground(background);
					cellFormat.setBorder(border, borderLine);
					//输出日志
					LOG.info("Create CellFormat [  FontName: {0}, Size: {1}, Bold: {2}, Alignment: {3}, valignment: {4}, background: {5}, border: {6} - {7} ] " , 
							new Object[]{ formatKey.getName(), formatKey.getSize(), formatKey.isBold() , 
							alignment.getDescription() , valignment.getDescription() , 
							background.getDescription(), border.getDescription() , borderLine.getDescription()});
					//返回CellFormat
					return cellFormat;
				}
			});
	}
	
	public JXLCellFormatKey getCellFormatKey(String name, int size, boolean bold,
			Alignment alignment, VerticalAlignment valignment,
			Colour background, Border border, BorderLineStyle borderLine) {
		if (StringUtils.isNotEmpty(name)) {
			String format =  StringUtils.join(new Object[]{ name , size , bold , alignment.getDescription() , 
					valignment.getDescription() , background.getDescription(), borderLine.getDescription()}, "-");
			JXLCellFormatKey ret = COMPLIED_CELLFORMATKEY.get(format);
			if (ret != null) {
				return ret;
			}
			ret = new JXLCellFormatKey(name, size, bold, alignment, valignment, background, border, borderLine);
			JXLCellFormatKey existing = COMPLIED_CELLFORMATKEY.putIfAbsent(format, ret);
			if (existing != null) {
				ret = existing;
			}
			return ret;
		}
		return null;
	}
	 
	
	public WritableCellFormat getCellFormat(String name, int size, boolean bold,
			Alignment alignment, VerticalAlignment valignment,
			Colour background, Border border, BorderLineStyle borderLine){
		try {
			JXLCellFormatKey cellFormatKey = getCellFormatKey(name, size, bold,alignment, valignment, background, border, borderLine);
			return CELL_FORMAT_CACHE.get(cellFormatKey);
		} catch (ExecutionException e) {
			return null;
		}
	}
	
}
