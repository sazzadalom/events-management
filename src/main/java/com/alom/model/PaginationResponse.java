package com.alom.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long totalRecord;
    private List<T> data;
    private int totalPage;

}
