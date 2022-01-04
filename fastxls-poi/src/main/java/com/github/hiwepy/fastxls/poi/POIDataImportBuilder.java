/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.github.hiwepy.fastxls.poi;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.hiwepy.fastxls.core.Constants;
import com.github.hiwepy.fastxls.core.builder.DataImportBuilder;
import com.github.hiwepy.fastxls.core.function.DefaultRowDtoMapperBiFunction;
import com.github.hiwepy.fastxls.core.function.DefaultRowDtoValidationBiFunction;
import com.github.hiwepy.fastxls.core.function.DefaultRowMapMapperBiFunction;
import com.github.hiwepy.fastxls.core.function.DefaultRowMapValidationFunction;
import com.github.hiwepy.fastxls.core.function.DefaultRowValidationFunction;
import com.github.hiwepy.fastxls.core.model.CellModel;
import com.github.hiwepy.fastxls.core.model.ColumnWrapper;
import com.github.hiwepy.fastxls.core.model.ConstraintViolationResult;
import com.github.hiwepy.fastxls.core.model.RowConstraintViolation;
import com.github.hiwepy.fastxls.core.model.RowMap;
import com.github.hiwepy.fastxls.core.model.RowMapper;
import com.github.hiwepy.fastxls.core.model.RowWrapper;
import com.github.hiwepy.fastxls.core.task.DataImportTask;
import com.github.hiwepy.fastxls.core.task.MappedDataImportTask;
import com.github.hiwepy.fastxls.core.utils.ExecutorUtils;
import com.google.common.collect.Lists;

public class POIDataImportBuilder<T extends CellModel, M extends RowMap> 
	extends DataImportBuilder<Workbook, Sheet, Row, Cell , T, M>  {

	private BiFunction<RowWrapper<Row> , Integer, RowMapper<Row, RowMap>> rowmapMapper;
	private BiFunction<RowMapper<Row, RowMap>, Integer, Set<ConstraintViolation<RowMap>>> rowmapValidation;
	private BiFunction<RowWrapper<Row>, Integer, RowMapper<Row, M>> rowDtoMapper;
	private BiFunction<RowMapper<Row, M>, Integer, Set<ConstraintViolation<M>>> rowDtoValidation;
	private BiFunction<RowWrapper<Row>, Integer, Set<ConstraintViolation<M>>> rowValidation;
	
	
	public POIDataImportBuilder() {
		this(new POIWorkbookReader());
	}

	public POIDataImportBuilder(POIWorkbookReader workbookReader) {
		super(workbookReader);
	}

	public POIDataImportBuilder(BiFunction<ColumnWrapper<Cell>, String, String> transformer) {
		this(new POIWorkbookReader(), transformer);
	}

	public POIDataImportBuilder(POIWorkbookReader workbookReader, BiFunction<ColumnWrapper<Cell>, String, String> transformer) {
		super(workbookReader);
		this.transform(transformer);
	}
	
	@Override
	protected void internalInit() {
		super.internalInit();
		// Excel 文件处理
		rowmapMapper = new DefaultRowMapMapperBiFunction<>(workbookReader, this.getTransformer());
		rowmapValidation = new DefaultRowMapValidationFunction<>();
		rowDtoMapper = new DefaultRowDtoMapperBiFunction<>(workbookReader, this.getTransformer());
		rowDtoValidation = new DefaultRowDtoValidationBiFunction<>(getValidator());
		rowValidation = new DefaultRowValidationFunction<>(workbookReader, this.getTransformer());
	}
	

	@Override
	public ConstraintViolationResult<M> input(File file, 
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiConsumer<RowWrapper<Row>, Integer> consumer) throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(file);
		return this.input(sheets, header, consumer);
	}

	@Override
	public ConstraintViolationResult<M> input(InputStream input,
			BiFunction<Sheet, Integer, List<Map<String, String>>> header, 
			BiConsumer<RowWrapper<Row>, Integer> consumer)
			throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(input);
		return this.input(sheets, header, consumer);
	}

	@Override
	public ConstraintViolationResult<M> input(Sheet[] sheets,
			BiFunction<Sheet, Integer, List<Map<String, String>>> header, 
			BiConsumer<RowWrapper<Row>, Integer> consumer)
			throws Exception {
		ConstraintViolationResult<M> result = new ConstraintViolationResult<M>();
		// 计算总行数
		BigDecimal count = BigDecimal.ONE.max(BigDecimal.valueOf(Stream.of(sheets).mapToInt(sheet -> sheet.getLastRowNum()).sum()));
		int index = 0;
		// 是否使用多线程进行构建
		if(this.isThreadPool()) {
			// 计算每个sheet需要多少个线程进行处理,并累加计算总共需要线程数
			int threadNum = Math.max(1, Stream.of(sheets).mapToInt(sheet -> ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize())).sum());
			// 固定大小的创建线程池
			ExecutorService cachedThreadPool = Executors.newFixedThreadPool(threadNum);
			// 创建Future集合
			List<Future<List<RowConstraintViolation<M>>>> futureTaskList = Lists.newArrayList();
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				index += sheet.getFirstRowNum();
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				// 计算当前Sheet数据需要的线程数
				int threadNumOfSheet = ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize());
				// 打印日志
				LOG.info(Constants.THREAD_INFO, sheet.getSheetName(), sheet.getLastRowNum(), this.getMaxThreadTaskSize() , threadNumOfSheet);
				// 起始位
				int offset = 0;
				// 循环构建线程
				for (int threadIndex = 0; threadIndex <= threadNumOfSheet; threadIndex++) {
					// 线程名称
					String taskName = String.format(Constants.THREAD_TASKNAME, sheet.getSheetName(), this.getThreadNamePrefix(), threadIndex);
					// 向线程池增加一个线程任务
					futureTaskList.add(cachedThreadPool.submit(new DataImportTask< Workbook, Sheet, Row, Cell, M>( 
							taskName, sheet, offset, this.getMaxThreadTaskSize(), this.getFirstDataRowOfSheet(), 
							columns, this.isIgnoreException(), this.getWorkbookReader(), getRowValidation(), consumer)));
					// 计算下一个开始位置
					offset += this.getMaxThreadTaskSize();
				}
				sheetNum += 1;
			}
			// 等待所有任务完成，关闭线程池
			ExecutorUtils.awaitShutdown(cachedThreadPool, 10 , TimeUnit.SECONDS);
			// 等待返回结果，用future.get()获取结果。
			for(Future<List<RowConstraintViolation<M>>> future : futureTaskList){
		        try {
		        	result.addConstraintViolations(future.get());
		        } catch (ExecutionException | InterruptedException e) {
		            e.printStackTrace();
		        }
			}
		} else {
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				index += sheet.getFirstRowNum();
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				// 循环行数据
				for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
					// 跳过非数据行
					if(rowNum < this.getFirstDataRowOfSheet()) {
						continue;
					}
					// 行数据
					Row row = sheet.getRow(rowNum);
					// 行对象包装
					RowWrapper<Row> wrapper = new RowWrapper<Row>(row, columns, rowNum, this.getFirstDataRowOfSheet());
					// 执行验证
					Set<ConstraintViolation<M>> constraintViolations = getRowValidation().apply(wrapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<M>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(wrapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(index).divide(count, 2, BigDecimal.ROUND_HALF_UP ).floatValue());
					index ++;
				}
				sheetNum += 1;
			}
		}
		// 操作完成
		getFinish().accept(this.getUuid(), 100f);
		return result;
	}
	
	@Override
	public ConstraintViolationResult<RowMap> input2(File file,
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<Row, RowMap>, Integer> consumer) throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(file);
		return this.input2(sheets, header, consumer);
	}
	 
	@Override
	public ConstraintViolationResult<RowMap> input2(InputStream input, 
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<Row, RowMap>, Integer> consumer) throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(input);
		return this.input2(sheets, header, consumer);
	}

	@Override
	public ConstraintViolationResult<RowMap> input2(Sheet[] sheets,
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<Row, RowMap>, Integer> consumer) throws Exception {
		ConstraintViolationResult<RowMap> result = new ConstraintViolationResult<RowMap>();
		// 计算总行数
		BigDecimal count = BigDecimal.ONE.max(BigDecimal.valueOf(Stream.of(sheets).mapToInt(sheet -> sheet.getLastRowNum()).sum()));
		int index = 0;
		// 是否使用多线程进行构建
		if(this.isThreadPool()) {
			// 计算每个sheet需要多少个线程进行处理,并累加计算总共需要线程数
			int threadNum = Math.max(1, Stream.of(sheets).mapToInt(sheet -> ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize())).sum());
			// 固定大小的创建线程池
			ExecutorService cachedThreadPool = Executors.newFixedThreadPool(threadNum);
			// 创建Future集合
			List<Future<List<RowConstraintViolation<RowMap>>>> futureTaskList = Lists.newArrayList();
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				index += sheet.getFirstRowNum();
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				// 计算当前Sheet数据需要的线程数
				int threadNumOfSheet = ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize());
				// 打印日志
				LOG.info(Constants.THREAD_INFO, sheet.getSheetName(), sheet.getLastRowNum(), this.getMaxThreadTaskSize() , threadNumOfSheet);
				// 起始位
				int offset = 0;
				// 循环构建线程
				for (int threadIndex = 0; threadIndex <= threadNumOfSheet; threadIndex++) {
					// 线程名称
					String taskName = String.format(Constants.THREAD_TASKNAME, sheet.getSheetName(), this.getThreadNamePrefix(), threadIndex);
					// 向线程池增加一个线程任务
					futureTaskList.add(cachedThreadPool.submit(new MappedDataImportTask< Workbook, Sheet, Row, Cell, RowMap>( 
							taskName, sheet, offset, this.getMaxThreadTaskSize(), this.getFirstDataRowOfSheet(), 
							columns, this.isIgnoreException(), this.getWorkbookReader(), getRowmapMapper(), getRowmapValidation(), consumer)));
					// 计算下一个开始位置
					offset += this.getMaxThreadTaskSize();
				}
				sheetNum += 1;
			}
			// 等待所有任务完成，关闭线程池
			ExecutorUtils.awaitShutdown(cachedThreadPool, 10 , TimeUnit.SECONDS);
			// 等待返回结果，用future.get()获取结果。
			for(Future<List<RowConstraintViolation<RowMap>>> future : futureTaskList){
		        try {
		        	result.addConstraintViolations(future.get());
		        } catch (ExecutionException | InterruptedException e) {
		            e.printStackTrace();
		        }
			}
		} else {
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				index += sheet.getFirstRowNum();
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				// 循环行数据
				for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
					// 跳过非数据行
					if(rowNum < this.getFirstDataRowOfSheet()) {
						continue;
					}
					// 行数据
					Row row = sheet.getRow(rowNum);
					// 行对象包装
					RowWrapper<Row> wrapper = new RowWrapper<Row>(row, columns, rowNum, this.getFirstDataRowOfSheet());
					// 进行行数据转换
					RowMapper<Row, RowMap> mapper = getRowmapMapper().apply(wrapper, rowNum);
					// 执行验证
					Set<ConstraintViolation<RowMap>> constraintViolations = getRowmapValidation().apply(mapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<RowMap>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(mapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(index).divide(count, 2, BigDecimal.ROUND_HALF_UP).floatValue());
					index ++;
				}
				sheetNum += 1;
			}
		}
		// 操作完成
		getFinish().accept(this.getUuid(), 100f);
		return result;
	}
 

	@Override
	public ConstraintViolationResult<M> input3(File file, 
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<Row, M>, Integer> consumer) throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(file);
		return this.input3(sheets, header, consumer);
	}

	@Override
	public ConstraintViolationResult<M> input3(InputStream input,
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<Row, M>, Integer> consumer) throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(input);
		return this.input3(sheets, header, consumer);
	}

	@Override
	public ConstraintViolationResult<M> input3(Sheet[] sheets,
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiConsumer<RowMapper<Row, M>, Integer> consumer) throws Exception {
		ConstraintViolationResult<M> result = new ConstraintViolationResult<M>();
		// 计算总行数
		BigDecimal count = BigDecimal.ONE.max(BigDecimal.valueOf(Stream.of(sheets).mapToInt(sheet -> sheet.getLastRowNum()).sum()));
		int index = 0;
		// 是否使用多线程进行构建
		if(this.isThreadPool()) {
			// 计算每个sheet需要多少个线程进行处理,并累加计算总共需要线程数
			int threadNum = Math.max(1, Stream.of(sheets).mapToInt(sheet -> ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize())).sum());
			// 固定大小的创建线程池
			ExecutorService cachedThreadPool = Executors.newFixedThreadPool(threadNum);
			// 创建Future集合
			List<Future<List<RowConstraintViolation<M>>>> futureTaskList = Lists.newArrayList();
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				index += sheet.getFirstRowNum();
				// 计算当前Sheet数据需要的线程数
				int threadNumOfSheet = ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize());
				// 打印日志
				LOG.info(Constants.THREAD_INFO, sheet.getSheetName(), sheet.getLastRowNum(), this.getMaxThreadTaskSize() , threadNumOfSheet);
				// 起始位
				int offset = 0;
				// 循环构建线程
				for (int threadIndex = 0; threadIndex <= threadNumOfSheet; threadIndex++) {
					// 线程名称
					String taskName = String.format(Constants.THREAD_TASKNAME, sheet.getSheetName(), this.getThreadNamePrefix(), threadIndex);
					// 向线程池增加一个线程任务
					cachedThreadPool.submit(new MappedDataImportTask<Workbook, Sheet, Row, Cell, M>( taskName, sheet, offset, this.getMaxThreadTaskSize(), this.getFirstDataRowOfSheet(), 
							columns, this.isIgnoreException(), this.getWorkbookReader(), getRowDtoMapper(), getRowDtoValidation(), consumer));
					// 计算下一个开始位置
					offset += this.getMaxThreadTaskSize();
				}
				sheetNum += 1;
			}
			// 等待所有任务完成，关闭线程池
			ExecutorUtils.awaitShutdown(cachedThreadPool, 10 , TimeUnit.SECONDS);
			// 等待返回结果，用future.get()获取结果。
			for(Future<List<RowConstraintViolation<M>>> future : futureTaskList){
		        try {
		        	result.addConstraintViolations(future.get());
		        } catch (ExecutionException | InterruptedException e) {
		            e.printStackTrace();
		        }
			}
		} else {
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				index += sheet.getFirstRowNum();
				for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
					// 跳过非数据行
					if(rowNum < this.getFirstDataRowOfSheet()) {
						continue;
					}
					// 行数据
					Row row = sheet.getRow(rowNum);
					// 行对象包装
					RowWrapper<Row> wrapper = new RowWrapper<Row>(row, columns, rowNum, this.getFirstDataRowOfSheet());
					// 行数据转换
					RowMapper<Row, M> mapper = getRowDtoMapper().apply(wrapper, rowNum);
					// 执行验证
					Set<ConstraintViolation<M>> constraintViolations = getRowDtoValidation().apply(mapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<M>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(mapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(index).divide(count, 2, BigDecimal.ROUND_HALF_UP).floatValue());
					index ++;
				}
				sheetNum += 1;
			}
		}
		// 操作完成
		getFinish().accept(this.getUuid(), 100f);		
		return result;
	}
	
	@Override
	public ConstraintViolationResult<M> input(File file, 
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiFunction<RowWrapper<Row>, Integer, RowMapper<Row,M>> rowMapper,
			BiFunction<RowMapper<Row, M>, Integer, Set<ConstraintViolation<M>>> validation,
			BiConsumer<RowMapper<Row,M>, Integer> consumer)
			throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(file);
		return this.input(sheets, header, rowMapper, validation, consumer);
	}


	@Override
	public ConstraintViolationResult<M> input(InputStream input, 
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiFunction<RowWrapper<Row>, Integer, RowMapper<Row,M>> rowMapper,
			BiFunction<RowMapper<Row, M>, Integer, Set<ConstraintViolation<M>>> validation,
			BiConsumer<RowMapper<Row,M>, Integer> consumer)
			throws Exception {
		Sheet[] sheets = getWorkbookReader().getSheets(input);
		return this.input(sheets, header, rowMapper, validation, consumer);
	}

	@Override
	public ConstraintViolationResult<M> input(Sheet[] sheets,
			BiFunction<Sheet, Integer, List<Map<String, String>>> header,
			BiFunction<RowWrapper<Row>, Integer, RowMapper<Row, M>> rowMapper,
			BiFunction<RowMapper<Row, M>, Integer, Set<ConstraintViolation<M>>> validation,
			BiConsumer<RowMapper<Row, M>, Integer> consumer) throws Exception {
		ConstraintViolationResult<M> result = new ConstraintViolationResult<M>();
		// 计算总行数
		BigDecimal count = BigDecimal.ONE.max(BigDecimal.valueOf(Stream.of(sheets).mapToInt(sheet -> sheet.getLastRowNum()).sum()));
		int index = 0;
		// 是否使用多线程进行构建
		if(this.isThreadPool()) {
			// 计算每个sheet需要多少个线程进行处理,并累加计算总共需要线程数
			int threadNum = Math.max(1, Stream.of(sheets).mapToInt(sheet -> ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize())).sum());
			// 固定大小的创建线程池
			ExecutorService cachedThreadPool = Executors.newFixedThreadPool(threadNum);
			// 创建Future集合
			List<Future<List<RowConstraintViolation<M>>>> futureTaskList = Lists.newArrayList();
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				index += sheet.getFirstRowNum();
				// 计算当前Sheet数据需要的线程数
				int threadNumOfSheet = ExecutorUtils.getThreadCount(sheet.getLastRowNum(), this.getMaxThreadTaskSize());
				// 打印日志
				LOG.info(Constants.THREAD_INFO, sheet.getSheetName(), sheet.getLastRowNum(), this.getMaxThreadTaskSize() , threadNumOfSheet);
				// 起始位
				int offset = 0;
				// 循环构建线程
				for (int threadIndex = 0; threadIndex <= threadNumOfSheet; threadIndex++) {
					// 线程名称
					String taskName = String.format(Constants.THREAD_TASKNAME, sheet.getSheetName(), this.getThreadNamePrefix(), threadIndex);
					// 向线程池增加一个线程任务
					futureTaskList.add(cachedThreadPool.submit(new MappedDataImportTask<Workbook, Sheet, Row, Cell, M>( taskName, sheet, offset, this.getMaxThreadTaskSize(), this.getFirstDataRowOfSheet(), 
							columns, this.isIgnoreException(), this.getWorkbookReader(), rowMapper, validation, consumer)));
					// 计算下一个开始位置
					offset += this.getMaxThreadTaskSize();
				}
				sheetNum += 1;
			}
			// 等待所有任务完成，关闭线程池
			ExecutorUtils.awaitShutdown(cachedThreadPool, 10 , TimeUnit.SECONDS);
			// 等待返回结果，用future.get()获取结果。
			for(Future<List<RowConstraintViolation<M>>> future : futureTaskList){
		        try {
		        	result.addConstraintViolations(future.get());
		        } catch (ExecutionException | InterruptedException e) {
		            e.printStackTrace();
		        }
			}
		} else {
			// 循环Sheet对象
			int sheetNum = 0;
			for (Sheet sheet : sheets) {
				// 根据sheet获取列信息
				List<Map<String, String>> columns = header.apply(sheet, sheetNum);
				index += sheet.getFirstRowNum();
				for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
					// 跳过非数据行
					if(rowNum < this.getFirstDataRowOfSheet()) {
						continue;
					}
					// 行数据
					Row row = sheet.getRow(rowNum);
					// 行对象包装
					RowWrapper<Row> wrapper = new RowWrapper<Row>(row, columns, rowNum, this.getFirstDataRowOfSheet());
					// 行数据转换
					RowMapper<Row, M> mapper = rowMapper.apply(wrapper, rowNum);
					// 判断是否表头行
					if(mapper.isTitle()) {
						continue;
					}
					// 执行验证
					Set<ConstraintViolation<M>> constraintViolations = validation.apply(mapper, rowNum);
					if(CollectionUtils.isNotEmpty(constraintViolations)) {
						if( !isIgnoreException()) {
							throw new ConstraintViolationException(String.format("第%s行数据验证失败", rowNum + 1), constraintViolations);
						} else {
							result.addConstraintViolation(new RowConstraintViolation<M>(rowNum, constraintViolations));
						}
					}
					// 调用消费函数 
					consumer.accept(mapper, rowNum);
					// 记录完成进度
					getProcess().accept(this.getUuid(), BigDecimal.valueOf(index).divide(count, 2, BigDecimal.ROUND_HALF_UP).floatValue());
					index ++;
				}
				sheetNum += 1;
			}
		}
		// 操作完成
		getFinish().accept(this.getUuid(), 100f);		
		return result;
	}

	public BiFunction<RowWrapper<Row>, Integer, RowMapper<Row, RowMap>> getRowmapMapper() {
		return rowmapMapper;
	}

	public void setRowmapMapper(BiFunction<RowWrapper<Row>, Integer, RowMapper<Row, RowMap>> rowmapMapper) {
		this.rowmapMapper = rowmapMapper;
	}

	public BiFunction<RowMapper<Row, RowMap>, Integer, Set<ConstraintViolation<RowMap>>> getRowmapValidation() {
		return rowmapValidation;
	}

	public void setRowmapValidation(
			BiFunction<RowMapper<Row, RowMap>, Integer, Set<ConstraintViolation<RowMap>>> rowmapValidation) {
		this.rowmapValidation = rowmapValidation;
	}

	public BiFunction<RowWrapper<Row>, Integer, RowMapper<Row, M>> getRowDtoMapper() {
		return rowDtoMapper;
	}

	public void setRowDtoMapper(BiFunction<RowWrapper<Row>, Integer, RowMapper<Row, M>> rowDtoMapper) {
		this.rowDtoMapper = rowDtoMapper;
	}

	public BiFunction<RowMapper<Row, M>, Integer, Set<ConstraintViolation<M>>> getRowDtoValidation() {
		return rowDtoValidation;
	}

	public void setRowDtoValidation(BiFunction<RowMapper<Row, M>, Integer, Set<ConstraintViolation<M>>> rowDtoValidation) {
		this.rowDtoValidation = rowDtoValidation;
	}

	public BiFunction<RowWrapper<Row>, Integer, Set<ConstraintViolation<M>>> getRowValidation() {
		return rowValidation;
	}

	public void setRowValidation(BiFunction<RowWrapper<Row>, Integer, Set<ConstraintViolation<M>>> rowValidation) {
		this.rowValidation = rowValidation;
	}
	
}
