 package com.github.hiwepy.fastxls.jexcel.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;
import com.github.hiwepy.fastxls.core.utils.StringUtils;

/**
 * 字体名称对象缓存管理
 */
public class JXLFontNameCacheManager {
	
	protected static Logger LOG = LoggerFactory.getLogger(JXLFontNameCacheManager.class);
	protected static ConcurrentMap<String, FontName> COMPLIED_FONTNAME = new ConcurrentHashMap<String, FontName>();
 
	private volatile static JXLFontNameCacheManager singleton;

	public static JXLFontNameCacheManager getInstance(String initFontNames) {
		if (singleton == null) {
			synchronized (JXLFontNameCacheManager.class) {
				if (singleton == null) {
					singleton = new JXLFontNameCacheManager(initFontNames);
				}
			}
		}
		return singleton;
	}
	
	private JXLFontNameCacheManager(String initFontNames){
		if (StringUtils.isNotEmpty(initFontNames)) {
			String[] fontNames = StringUtils.tokenizeToStringArray(initFontNames);
			for (String fontName : fontNames) {
				COMPLIED_FONTNAME.putIfAbsent(fontName, WritableFont.createFont(fontName));
				LOG.debug("Create FontName [ {0} ] " , fontName);
			}
		}
	}
	
	public FontName getFontName(String fontName){
		if (StringUtils.isNotEmpty(fontName)) {
			FontName ret = COMPLIED_FONTNAME.get(fontName);
 			if (ret != null) {
 				return ret;
 			} 
 			ret = WritableFont.createFont(fontName);
 			LOG.debug("Create FontName [ {0} ] " , fontName);
 			FontName existing = COMPLIED_FONTNAME.putIfAbsent(fontName, ret);
 			if (existing != null) {
 				ret = existing;
 			}
 			return ret;
 		}
 		return null;
	}

	public void destroy(String fontName) {
		if(fontName != null){
			//清除fontName对应的字体缓存
			COMPLIED_FONTNAME.remove(fontName);
		}
	}
	
}

