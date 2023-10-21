package com.study.SpringBoot_Project.controller;

import com.study.SpringBoot_Project.service.CommentService;
import com.study.SpringBoot_Project.vo.BoardComment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private CommentService service;
    public CommentController(CommentService service){
        this.service = service;
    }


    @PostMapping("/CommentWrite")
    public ModelAndView write(ModelAndView mv, int boardNo, String comment){
        BoardComment b=BoardComment.builder()
                .boardNo(boardNo)
                .content(comment)
                .build();

        int result=service.inertBoardComment(b);

        if (result > 0) {
            mv.addObject("msg","댓글이 등록되었습니다");
        }else{
            mv.addObject("msg","댓글 등록을 실패했습니다.");
        }
        mv.addObject("loc","/board/boardDetail.do?boardNo="+boardNo);
        mv.setViewName("fragments/msg");
        return mv;
    }
}
