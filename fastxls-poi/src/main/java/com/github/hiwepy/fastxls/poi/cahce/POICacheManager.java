package com.github.hiwepy.fastxls.poi.cahce;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class POICacheManager {

	private static LoadingCache<String, byte[]> loadingCache;

	static {
		loadingCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).maximumSize(50)
				.build(new CacheLoader<String, byte[]>() {
					@Override
					public byte[] load(String filePath) throws Exception {
						return FileUtils.readFileToByteArray(new File(filePath));
					}
				});
	}
	
	public static InputStream getFile(String filePath) {
		try {
			//复杂数据,防止操作原数据
			byte[] result = Arrays.copyOf(loadingCache.get(filePath), loadingCache.get(filePath).length);
			return new ByteArrayInputStream(result);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
