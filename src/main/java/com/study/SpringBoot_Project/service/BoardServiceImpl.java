package com.study.SpringBoot_Project.service;

import com.study.SpringBoot_Project.mapper.BoardMapper;
import com.study.SpringBoot_Project.mapper.FileMapper;
import com.study.SpringBoot_Project.vo.Board;
import com.study.SpringBoot_Project.vo.BoardFile;
import com.study.SpringBoot_Project.vo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BoardServiceImpl implements BoardService {

    private BoardMapper mapper;
    private FileMapper fileMapper;

    @Autowired
    public BoardServiceImpl(BoardMapper mapper, FileMapper fileMapper){
        this.mapper = mapper;
        this.fileMapper = fileMapper;
    }
    /**
     * 게시글 조회
     * @return 게시글
     */
    @Override
    public List<Board> searchAll(){
        return mapper.searchAll();
    }

    /**
     * 카테고리 전체 조회
     * @return 카테고리 리스트
     */
    @Override
    public List<Category> categoryAll(){
        return mapper.categoryAll();
    }

    /**
     * 첨부파일이 있는 게시글 번호
     * @return 게시글 번호리스트 반환
     */
    @Override
    public List attachmentlist(){
        return mapper.attachmentlist();
    }

    /**
     * 전체 게시글 갯수
     * @return 게시글 수
     */
    @Override
    public int selectBoardCount(){
        return mapper.selectBoardCount();
    }

    /**
     * 게시글 상세보기 + 조회수 증가
     * @param boardNo 게시글 번호
     * @return 게시글 정보
     */
    @Override
    @Transactional
    public Board viewDetail(int boardNo){
        Board result = mapper.viewDetail(boardNo);
        if(result != null) {
            //게시글 조회에 성공시 조회수 증가
            int count = mapper.updateReadCount(boardNo);
            if(count > 0){
                result.setBoardCount(result.getBoardCount()+1);
            }
        }
        return result;
    }

    /**
     * 게시글 입력
     * @param board 게시글 정보
     * @return 성공시 1 반환
     */
    @Override
    public int insertBoard(Board board){
        int result = mapper.insertBoard(board);
        if(result > 0){
            //게시글 등록 성공시 파일도 등록
            if(!board.getFile().isEmpty()){
                for(BoardFile file : board.getFile()){
                    file.setBoard(board);
                    result += fileMapper.insertFile(file);
                }
            }
        }
        return result;
    }

    /**
     * 게시판 비밀번호 확인
     * @param map boardNo 게시글 번호, check 비밀번호
     * @return 게시글 정보를 반환
     */
    @Override
    public Board passwordCheck(Map map){
        return mapper.passwordCheck(map);
    }

    /**
     * 게시글 삭제
     * @param boardNo 게시글 번호
     * @return 성공시 1반환
     */
    @Override
    public int deleteBoard(int boardNo){
        return mapper.deleteBoard(boardNo);
    }

    /**
     * 게시글 수정하기
     * @param board 게시글 정보들
     * @return 성공시 1반환
     */
    @Override
    public int updateBoard(Board board){
        return mapper.updateBoard(board);
    }

}
