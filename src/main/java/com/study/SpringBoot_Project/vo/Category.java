package com.study.SpringBoot_Project.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    /**
     * 카테고리 번호, 카테고리 이름
     */
    private int cateNo;         //카테고리 번호
    private String cateName;    //카테고리 이름
}
