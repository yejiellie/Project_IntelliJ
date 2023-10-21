package com.study.SpringBoot_Project.mapper;

import com.study.SpringBoot_Project.vo.Board;
import com.study.SpringBoot_Project.vo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BoardMapper {
    /**
     * 게시글 조회
     * @return 게시글
     */
    List<Board> searchAll();

    /**
     * 카테고리 전체 조회
     * @return 카테고리 리스트
     */
    List<Category> categoryAll();

    /**
     * 첨부파일이 있는 게시글 번호
     * @return 게시글 번호리스트 반환
     */
    List attachmentlist();

    /**
     * 전체 게시글 갯수
     * @return 게시글 수
     */
    int selectBoardCount();

    /**
     * 게시글 상세보기
     * @param boardNo 게시글 번호
     * @return 게시글 정보
     */
    Board viewDetail(int boardNo);

    /**
     * 조회수 증가
     * @param boardNo 게시글 번호
     * @return 조회수 증가
     */
    int updateReadCount(int boardNo);

    /**
     * 게시글 입력
     * @param b 게시글 정보
     * @return 성공시 1 반환
     */
    int insertBoard(Board b);

    /**
     * 게시판 비밀번호 확인
     * @param map boardNo 게시글 번호, check 비밀번호
     * @return 게시글 정보를 반환
     */
    Board passwordCheck(Map map);

    /**
     * 게시글 삭제
     * @param boardNo 게시글 번호
     * @return 성공시 1반환
     */
    int deleteBoard(int boardNo);

    /**
     * 게시글 수정하기
     * @param board 게시글 정보들
     * @return 성공시 1반환
     */
    int updateBoard(Board board);

}
