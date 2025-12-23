package com.shop.bookshop.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.bookshop.dao.CategoryMapper;
import com.shop.bookshop.pojo.Category;
import com.shop.bookshop.service.CategoryService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;

@RestController
@RequestMapping("category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	@Resource
	private CategoryMapper categoryMapper;
	@GetMapping("/searchcode")
	public ResultVO categorySearchByCode(String categoryCode)
	{
		
		Category categories = categoryService.selectByByCategoryCode(categoryCode);
		if(categories!=null)
		return new ResultVO(ResultCode.SUCCESS,categories);
		else
			return new ResultVO(ResultCode.RECORD_NOT_FOUND,null);
	}
	@PostMapping("/delete")
	public ResultVO categoryDelete(String categoryCode)
	{
		int categories = categoryService.deleteByByCategoryCode(categoryCode);
		return new ResultVO(ResultCode.SUCCESS,null);
	}

	//添加验证
	@PostMapping("/insert")
	public ResultVO categoryInsert(@Valid Category record)
	{
		
		int categories = categoryService.insert(record);
		return new ResultVO(ResultCode.SUCCESS,null);
	}

	//添加验证
	@PostMapping("/update")
	public  ResultVO categoryUpdate(@Valid Category record)
	{
		int categories = categoryService.updateByCategoryCode(record);
		return new ResultVO(ResultCode.SUCCESS,null);
	}

	//添加分页
	@GetMapping("/searchall")
	public  ResultVO categorySearchAll(Integer page,Integer limit)
	{
		List<Category> categories = categoryService.selectAll(page==null?0:page,limit==null?10:limit);
		if(categories.size()!=0)
			return new ResultVO(ResultCode.SUCCESS,categories);
		else
			return new ResultVO(ResultCode.RECORD_NOT_FOUND,null);
	}	
}
