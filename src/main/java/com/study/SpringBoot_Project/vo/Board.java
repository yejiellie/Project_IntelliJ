package com.study.SpringBoot_Project.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {
    /**
     * 게시글 번호, 카테고리 번호, 작성자명, 게시글 비밀번호
     * 제목, 내용, 작성일, 최종수정일 , 조회수
     */
    private int boardNo;        //게시글 번호
    private int cateNo;         //카테고리 번호
    private String writer;      //작성자명
    private String boardPw;     //게시글비밀번호
    private String title;       //제목
    private String content;     //내용
    private Timestamp createDay;//작성일
    private Timestamp updateDay;//최종 수정일
    private int boardCount;     //조회수

    private Category category;
    private List<BoardFile> file;
}
