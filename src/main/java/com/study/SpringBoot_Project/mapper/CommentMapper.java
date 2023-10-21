package com.study.SpringBoot_Project.mapper;

import com.study.SpringBoot_Project.vo.BoardComment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface CommentMapper {

    /**
     * 댓글 전체 조회
     * @return 댓글 목록
     */
    List<BoardComment> listComment(int boardNo);

    /**
     * 댓글 등록
     * @param b 댓글 정보
     * @return 성공시 1 반환
     */
    int inertBoardComment(BoardComment b);
}
