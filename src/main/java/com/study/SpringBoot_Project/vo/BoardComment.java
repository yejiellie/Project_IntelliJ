package com.study.SpringBoot_Project.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardComment {
    /**
     * 댓글 번호 , 게시글번호
     * 댓글 작성일, 댓글 내용
     */
    private int commentNo;          //댓글 번호
    private int boardNo;            //게시글 번호 (Board테이블을 참조받음)
    private Timestamp commentDate;  //댓글 작성일
    private String content;         //댓글 내용
}
