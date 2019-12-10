package com.hc.para.page_base;

/**
 * 分页 用于接收参数
 * 
 * @author DDM
 *
 */
public class PageParam {
	private Integer page; // 查询页
	private Integer size; // 每页条数
	private Integer start; // 开始条数

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		if (null != page && page > 0) {
			this.page = page;
		} else {
			this.page = 1;
		}
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		if (null != size && size > 0) {
			this.size = size;
		} else {
			size = null;
		}

	}

	/**
	 * 获取启始条数
	 * 
	 * @return
	 */
	public Integer getStart() {
		start = null;
		if (null != page && null != size) {
			start = (page - 1) * size;
		}
		return start;
	}
}
