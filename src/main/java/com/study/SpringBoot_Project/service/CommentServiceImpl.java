package com.study.SpringBoot_Project.service;

import com.study.SpringBoot_Project.mapper.CommentMapper;
import com.study.SpringBoot_Project.vo.BoardComment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private CommentMapper mapper;

    @Autowired
    public CommentServiceImpl(CommentMapper mapper){
    this.mapper = mapper;
    }
    /**
     * 댓글 전체 조회
     * @return 댓글 목록
     */
    @Override
    public List<BoardComment> listComment(int boardNo){
        return mapper.listComment(boardNo);
    }

    /**
     * 댓글 등록
     * @param b 댓글 정보
     * @return 성공시 1 반환
     */
    public int inertBoardComment(BoardComment b){
        return mapper.inertBoardComment(b);
    }
}
