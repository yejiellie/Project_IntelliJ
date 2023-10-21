package com.study.SpringBoot_Project.service;

import com.study.SpringBoot_Project.mapper.FileMapper;
import com.study.SpringBoot_Project.vo.Board;
import com.study.SpringBoot_Project.vo.BoardFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private FileMapper mapper;
    @Autowired
    public FileServiceImpl(FileMapper mapper){
        this.mapper = mapper ;
    }

    /**
     * 파일 전체 정보 가져오기
     * @return 파일 정보
     */
    @Override
    public List<BoardFile> allFile(){
        return mapper.allFile();
    }

    /**
     * 게시글 첨부파일 정보 가져오기
     * @param boardNo 게시글 번호
     * @return
     */
    @Override
    public List<BoardFile> boardFile(int boardNo){
        return mapper.boardFile(boardNo);
    };



}
