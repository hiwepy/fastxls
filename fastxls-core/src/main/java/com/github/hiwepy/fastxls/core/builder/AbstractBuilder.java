/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.core.builder;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.Builder;

import com.github.hiwepy.fastxls.core.FilenameSuffix;
import com.github.hiwepy.fastxls.core.Suffix;
import com.github.hiwepy.fastxls.core.property.ExportProperties;
import com.github.hiwepy.fastxls.core.utils.Assert;

/**
 * @author <a href="https://github.com/vindell">wandl</a>
 */
public abstract class AbstractBuilder<W> implements Builder<W> {

	/**
	 * 是否已经初始化
	 */
	private volatile boolean initialized = false;
	/**
	 * xls 模板文件uuid
	 */
	private String uuid = UUID.randomUUID().toString();
	/**
	 * 生成的Excel文件后缀;决定了使用什么对象去构建wookbook：默认xls;支持xls,xlsx
	 */
	private Suffix suffix = Suffix.XLS;
	/**
	 * 导出xls文件时,每个Sheet最大允许有多少行，超过工作簿最大65536时候则以65536为最大值
	 */
	private int maxRow = 10000;
	/*
	 * xls文件读取密码
	 */
	private String password;
	/**
	 * xls 临时文件存储路径 ，默认 ：tmpDir
	 */
	private File tmpdir = SystemUtils.getUserDir();
	/**
	 * xls 文件存储时，文件名后的前缀字符串 【前缀字符串-文件名.xls】 ，默认 ：空
	 */
	private String namePrefix = "";
	/**
	 * xls 文件存储时，文件名后的后缀字符串 【文件名-后缀字符串.xls】生成方式 ，可选【Date,UUID】，默认 ：UUID
	 */
	private FilenameSuffix nameSuffix = FilenameSuffix.UUID;
	
	public AbstractBuilder<W> suffix(Suffix suffix){
		Assert.notNull(suffix, " suffix must not be null ");
		this.suffix = suffix;
		return this;
	}
	
	public AbstractBuilder<W> maxRow(int maxRow){
		Assert.isTrue(maxRow > 0 , "The value must be greater than zero");
		this.maxRow = maxRow;
		return this;
	}

	public AbstractBuilder<W> password(String password){
		this.password = password;
		return this;
	}

    public AbstractBuilder<W> uuid(String uuid){
    	Assert.notNull(uuid, " uuid must not be null ");
		this.uuid = uuid;
		return this;
	}
	
	public AbstractBuilder<W> tmpdir(File tmpdir){
		Assert.isTrue(tmpdir != null && tmpdir.exists(), " tmpdir must not be null ");
		this.tmpdir = tmpdir;
		return this;
	}
	
	public AbstractBuilder<W> namePrefix(String namePrefix){
		this.namePrefix = namePrefix;
		return this;
	}
	
	public AbstractBuilder<W> nameSuffix(FilenameSuffix nameSuffix){
		this.nameSuffix = nameSuffix;
		return this;
	}
	
	public AbstractBuilder<W> properties(ExportProperties properties){
		Assert.notNull(properties, " properties must not be null ");
		this.suffix = properties.getSuffix();
		this.maxRow = properties.getMaxRow();
		this.password = properties.getPassword();
		this.tmpdir = new File(properties.getTmpdir());
		this.namePrefix = properties.getNamePrefix();
		this.nameSuffix = properties.getNameSuffix();
		return this;
	}
	
    /**
     * Initialize the object.
     */
    public void init() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    internalInit();
                    this.initialized = true;
                }
            }
        }
    }

    /**
     * Internal initialization of the object.
     */
    protected abstract void internalInit() ;

    @Override
    public W build() {
    	init();
    	return null;
    }
    
    public String getUuid() {
		return uuid;
	}
    
    protected Suffix getSuffix() {
		return suffix;
	}

	protected int getMaxRow() {
		return maxRow;
	}

	protected String getPassword() {
		return password;
	}
	
	protected File getTmpdir() {
		return tmpdir;
	}

	protected String getNamePrefix() {
		return namePrefix;
	}

	protected FilenameSuffix getNameSuffix() {
		return nameSuffix;
	}
	
}
