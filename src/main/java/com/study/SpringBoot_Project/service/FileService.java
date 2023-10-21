package com.study.SpringBoot_Project.service;

import com.study.SpringBoot_Project.vo.Board;
import com.study.SpringBoot_Project.vo.BoardFile;

import java.util.List;

public interface FileService {
    /**
     * 파일 전체 정보 가져오기
     * @return 파일 정보
     */
    List<BoardFile> allFile();

    /**
     * 게시글 첨부파일 정보 가져오기
     * @param boardNo 게시글 번호
     * @return
     */
    List<BoardFile> boardFile(int boardNo);


}
