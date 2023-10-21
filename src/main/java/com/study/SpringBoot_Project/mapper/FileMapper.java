package com.study.SpringBoot_Project.mapper;

import com.study.SpringBoot_Project.vo.Board;
import com.study.SpringBoot_Project.vo.BoardFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FileMapper {

    /**
     * 파일 업로드
     * @param file 파일 정보
     * @return 성공시 1반환
     */
    int insertFile(BoardFile file);

    /**
     * 파일 정보 가져오기
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
