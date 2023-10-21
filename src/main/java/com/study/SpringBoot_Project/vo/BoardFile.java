package com.study.SpringBoot_Project.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardFile {
    /**
     * 파일 번호, 게시글 번호, 파일 이름, 리네임
     */
    private int fileNo;         //파일 번호
    private int boardNo;        //게시글 번호 (Board테이블을 참조받음)
    private String oriName;     //파일 이름
    private String reName;      //파일 리네임

    private Board board;

}
