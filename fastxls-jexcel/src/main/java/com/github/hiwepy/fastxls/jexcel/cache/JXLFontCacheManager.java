 package com.github.hiwepy.fastxls.jexcel.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;

 /**
  *  获得字体和样式根据类
  */
public class JXLFontCacheManager {
	
	protected static Logger LOG = LoggerFactory.getLogger(JXLFontCacheManager.class);
	protected static ConcurrentMap<String, JXLFontKey> COMPLIED_FONTKEY = new ConcurrentHashMap<String, JXLFontKey>();
	protected static JXLFontNameCacheManager fontNameCacheManager = JXLFontNameCacheManager.getInstance("");
	protected static ConcurrentMap<JXLFontKey,WritableFont> COMPLIED_FONTS = new ConcurrentHashMap<JXLFontKey,WritableFont>();
	 
	private volatile static JXLFontCacheManager singleton;

	public static JXLFontCacheManager getInstance() {
		if (singleton == null) {
			synchronized (JXLFontCacheManager.class) {
				if (singleton == null) {
					singleton = new JXLFontCacheManager();
				}
			}
		}
		return singleton;
	}
	
	private JXLFontCacheManager(){
		
	}
	
	public JXLFontKey getFontKey(String fontName,int size,boolean bold) {
		if (StringUtils.isNotEmpty(fontName)) {
			String format =  StringUtils.join(new Object[]{ fontName , size , bold }, "-");
			JXLFontKey ret = COMPLIED_FONTKEY.get(format);
			if (ret != null) {
				return ret;
			}
			ret = new JXLFontKey(fontName,size,bold);
			JXLFontKey existing = COMPLIED_FONTKEY.putIfAbsent(format, ret);
			if (existing != null) {
				ret = existing;
			}
			return ret;
		}
		return null;
	}
	
	public WritableFont getFont(String name,int size,boolean bold){
		JXLFontKey fontKey = getFontKey(name,size,bold);
		WritableFont ret = COMPLIED_FONTS.get(fontKey);
		if (ret != null) {
			return ret;
		}
		try {
			FontName fontName = fontNameCacheManager.getFontName(fontKey.getName());
			ret = new WritableFont(fontName, fontKey.getSize(), (fontKey.isBold() ? WritableFont.BOLD : WritableFont.NO_BOLD));
		} finally {
			LOG.info("Create WritableFont [ {0} ] " , fontKey.getName());
		}
		WritableFont existing = COMPLIED_FONTS.putIfAbsent(fontKey, ret);
		if (existing != null) {
			ret = existing;
		}
		return ret;
	}
	
	public void destroy(String fontName,int size,boolean bold) {
		if(fontName != null){
			//清除fontName对应的字体缓存
			JXLFontKey fontKey = getFontKey(fontName,size,bold);
			COMPLIED_FONTS.remove(fontKey);
			String format =  StringUtils.join(new Object[]{ fontName , size , bold }, "-");
			COMPLIED_FONTKEY.remove(format);
		}
	}
 
}

