package com.study.SpringBoot_Project.controller;


import com.study.SpringBoot_Project.service.CommentService;
import com.study.SpringBoot_Project.service.FileService;
import com.study.SpringBoot_Project.service.BoardService;
import com.study.SpringBoot_Project.vo.Board;
import com.study.SpringBoot_Project.vo.BoardComment;
import com.study.SpringBoot_Project.vo.BoardFile;
import com.study.SpringBoot_Project.vo.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private BoardService service;
    private FileService fileservice;
    private CommentService commentservice;

    @Autowired
    public BoardController (BoardService service, FileService fileservice, CommentService commentservice){
        this.service = service;
        this.fileservice = fileservice;
        this.commentservice = commentservice;
    }

    /**
     * 게시물 목록 출력
     * @param model
     * @return
     */
    @GetMapping("/list.do")
    public String list (Model model) {
        //전체 카테고리 호출
        List<Category> categoryList = service.categoryAll();
        //전체 게시글
        List<Board> list = service.searchAll();
        //게시글수
        int boardCount = service.selectBoardCount();
        //첨부파일이 있는 게시글 번호만 출력
        List fileList = service.attachmentlist();

        model.addAttribute("boardList",list);
        model.addAttribute("categoryList",categoryList);
        model.addAttribute("totalData",boardCount);
        model.addAttribute("fileList",fileList);
        return "board/list";
    }

    /**
     * 게시글 상세페이지 + 조회수 증가
     * @param model
     * @param boardNo
     * @return
     */
    @GetMapping("/boardDetail.do")
    public String findById(Model model, int boardNo){
        //게시글 상세보기
        Board board = service.viewDetail(boardNo);
        //첨부파일 정보 가져오기
        List<BoardFile> fileList = fileservice.boardFile(boardNo);
        //댓글
        List<BoardComment> boardComment = commentservice.listComment(boardNo);

        model.addAttribute("board",board);
        model.addAttribute("boardNo",boardNo);
        model.addAttribute("fileList",fileList);
        model.addAttribute("boardComment",boardComment);
        return "board/view";
    }

    /**
     * 게시글 등록 페이지로 이동
     * @param model
     * @return
     */
    @GetMapping("/write.do")
    public String write(Model model){
        List<Category> categoryList = service.categoryAll();
        model.addAttribute("categoryList",categoryList);
        return "board/write";
    }

    /**
     * 게시글 등록
     * @param boardFile 첨부파일
     * @param category 카테고리
     * @param writer 작성자 
     * @param boardPw 게시글비밀번호
     * @param title 제목
     * @param content 내용
     * @param session
     * @return 게시글 등록
     */
    @PostMapping("/boardWrite.do")
    public ModelAndView save(ModelAndView mv, MultipartFile[] boardFile, int category,
                             String writer, String boardPw, String title, String content , HttpSession session){
        //사진 저장할 경로 
        String path = session.getServletContext().getRealPath("/upload/");

        //게시글 정보 빌더에 담기
        Board board = Board.builder()
                .cateNo(category)
                .writer(writer)
                .boardPw(boardPw)
                .title(title)
                .content(content)
                .build();

        //경로에 폴더가 있는지 확인 후 없으면 추가해줌
        File dir=new File(path);
        if(!dir.exists()) {
            dir.mkdir();
            if (!dir.mkdirs()) {
                // 디렉토리 생성에 실패했을 때의 처리
                log.error("Failed to create directory: " + path);
            }
        }

        //파일 정보를 담을 리스트
        List<BoardFile> files = new ArrayList();

        for(MultipartFile f : boardFile) {
            if (!f.isEmpty()) {
                String fileName = f.getOriginalFilename();
                String ex = fileName.substring(fileName.lastIndexOf("."));
                //리네임 규칙 생성하기
                SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
                int rnd = (int)(Math.random()*10000) + 1;
                String renameFile = sim.format(System.currentTimeMillis()) + "_" + rnd + ex;

                //파일 넣기
                try{
                    f.transferTo(new File(path + renameFile));
                    files.add(BoardFile.builder().oriName(fileName).reName(renameFile).build());
                }catch(IOException e) {
                    log.info(fileName ,e);
                    log.info(renameFile);
                    log.info("Board Information: " + board.toString());
                    log.info("Files: " + files.toString());
                }
            }
        }
        board.setFile(files);

        int result = service.insertBoard(board);

        if ( result > 0) {
            mv.addObject("msg", "게시글 등록 완료");
            mv.addObject("loc", "/board/boardDetail.do?boardNo=" + board.getBoardNo());
        }else {
            mv.addObject("msg", "게시글 등록 실패");
            mv.addObject("loc", "/board/boardWrite.do");
        }
        mv.setViewName("fragments/msg");
        return mv;
    }


    /**
     * 파일 다운로드
     * @param reName 파일 이름
     */
    @GetMapping("/boardFileDown.do")
    public void fileDown(HttpServletRequest req, HttpServletResponse res, String reName)
                                                            throws IOException {
        //InputStream을 이용해서 지정된 저장경로에서 보낸 파일명과 일치하는 파일을 가져옴
        String path = req.getServletContext().getRealPath("/upload/");
        File file = new File(path, reName);

        // 속도를 빨리하기 위해 보조스트림 이용
        BufferedInputStream bif = new BufferedInputStream(new FileInputStream(file));

        // 클라이언트에게 보낼 파일명을 인코딩 처리하기 -> 파일명이 한글일때 깨지는 현상을 방지
        String fileReName;
        String header = req.getHeader("user-agent");
        boolean isMSIE = (header != null) && (header.contains("MSIE") || header.contains("Trident"));
        if (isMSIE) {    //익스플로러
            fileReName = URLEncoder.encode(reName, "UTF-8").replaceAll("\\+", "%20");
        } else {         //기타 웹 브러우저
            fileReName = new String(reName.getBytes("UTF-8"), "ISO-8859-1");
        }

        // 응답헤더 설정하기
        res.setContentType("application/octet-stream");
        res.setHeader("Content-disposition", "attachment;filename=" + fileReName);

        // 클라이언트 연결 스트림 열기
        ServletOutputStream sos = res.getOutputStream();
        // 웹브라우저에 연결할 출력 스트림 생성
        BufferedOutputStream bos = new BufferedOutputStream(sos);

        // 연결된 스트림으로 파일 전송하기
        int read;
        while ((read = bif.read()) != -1) {
            bos.write(read);
        }
        //스트림 닫기
        bos.close();
        bif.close();
    }

    /**
     * 게시글 비밀번호 확인용 팝업창
     * @param model
     * @param boardNo 게시글 번호
     * @param type 수정 , 삭제 구분용(update 또는 delete값을 받음)
     * @return
     */
    @GetMapping("/passwordCheckPage.do")
    public String checkPage(Model model, int boardNo, String type){

        model.addAttribute("boardNo",boardNo);
        model.addAttribute("type",type);

        return "fragments/checkPassword";
    }

    /**
     * 게시판 비밀번호 확인하기
     * @param mv
     * @param boardNo
     * @param type
     * @param check
     * @return 결과값에 따른 해당 페이지로 이동
     */
    @GetMapping("/CheckPassword.do")
    public ModelAndView passwordCheck(ModelAndView mv, int boardNo, String type,String check){
        // 비밀번호 조건에 맞는 정규표현식
        String passwordCheck = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{4,16}$";
        //패턴 컴파일 하기
        Pattern pattern = Pattern.compile(passwordCheck);
        //패턴 매칭하기
        Matcher matcher = pattern.matcher(check);

        if (matcher.matches()) {
            //비밀번호가 정규표현식에 맞는 경우
            Map map = new HashMap();
            map.put("boardNo", boardNo);
            map.put("check", check);
            Board board = service.passwordCheck(map);

            if (board != null) {
                if (type.equals("update")) {
                    //수정하기 페이지로 이동
                    mv.addObject("loc", "/board/BoardUpdatePage.do?boardNo=" + boardNo);
                } else {
                    //삭제하기
                    mv.addObject("loc", "/board/DeleteBoard.do?boardNo=" + boardNo);
                }
            } else {
                log.info(map.toString());
                //비밀번호 불일치
                mv.addObject("msg", "비밀번호가 일치하지 않습니다. 다시 입력해주세요");
                mv.addObject("loc", "/board/passwordCheckPage.do?boardNo=" + boardNo + "&type=" + type);
            }
        } else {
            log.info(check);
            log.info(String.valueOf(matcher));
            log.info(String.valueOf(matcher.matches()));
            //비밀번호가 정규표현식과 매칭되지 않는 경우
            mv.addObject("msg", "비밀번호가 조건에 맞지 않습니다.");
            mv.addObject("loc", "/board/passwordCheckPage.do?boardNo=" + boardNo + "&type=" + type);
        }
        mv.setViewName("fragments/msg");
        return mv;
    }

    /**
     * 게시글 삭제하기
     * @param boardNo 게시글 번호
     * @param mv
     * @return 게시글 삭제
     */
    @GetMapping("/DeleteBoard.do")
    public ModelAndView deleteBoard(int boardNo , ModelAndView mv){

        int result = service.deleteBoard(boardNo);

        if(result > 0) {
            mv.addObject("msg","게시글이 삭제되었습니다.");
            mv.addObject("loc","/board/list.do");
        }else {
            //삭제 실패시 게시글 상세페이지로 이동
            mv.addObject("msg","게시글 삭제를 실패했습니다.");
            mv.addObject("loc","/board/boardDetail.do?boardNo=" + boardNo);
        }
        mv.setViewName("fragments/msg");
        return mv;
    }

    /**
     * 수정하기 페이지로 이동
     * @param model
     * @param boardNo
     * @return
     */
    @GetMapping("/BoardUpdatePage.do")
    public String updatePage(Model model, int boardNo){

        //게시글 상세보기
        Board board = service.viewDetail(boardNo);
        //첨부파일 정보 가져오기
        List<BoardFile> fileList = fileservice.boardFile(boardNo);

        model.addAttribute("board",board);
        model.addAttribute("fileList",fileList);
        return "board/modify";
    }

    /**
     * 게시글 수정하기
     * @param mv
     * @param boardNo 게시글 번호
     * @param writer 작성자
     * @param boardPw 비밀번호
     * @param title 제목
     * @param content 내용
     * @param fileIndex 파일번호
     * @param boardFile 파일이름
     * @param session
     * @return
     */
    @PostMapping("/boardUpdate.do")
    public ModelAndView updateForm(ModelAndView mv, int boardNo, String writer, String boardPw,
                                   String title, String content, List fileIndex,MultipartFile[] boardFile,
                                   HttpSession session){
        //파일 경로
        String path = session.getServletContext().getRealPath("/upload/");

        Board board = Board.builder()
                .boardNo(boardNo)
                .writer(writer)
                .boardPw(boardPw)
                .title(title)
                .content(content)
                .build();


//        //파일 정보를 담기
//        List<BoardFile> files = new ArrayList();
//
//        for(MultipartFile f : boardFile) {
//            if (!f.isEmpty()) {
//                String fileName = f.getOriginalFilename();
//                String ex = fileName.substring(fileName.lastIndexOf("."));
//                //리네임 규칙 생성하기
//                SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
//                int rnd = (int)(Math.random()*10000) + 1;
//                String renameFile = sim.format(System.currentTimeMillis()) + "_" + rnd + ex;
//
//                //파일 넣기
//                try{
//                    f.transferTo(new File(path + renameFile));
//                    files.add(BoardFile.builder().oriName(fileName).reName(renameFile).build());
//                }catch(IOException e) {
//                    log.info(fileName ,e);
//                    log.info("Board Information: " + board.toString());
//                    log.info("Files: " + files.toString());
//                }
//            }
//        }
//        board.setFile(files);

        int result = service.updateBoard(board);

        if (result > 0) {
            mv.addObject("msg","게시글이 수정되었습니다.");
            mv.addObject("loc","/board/boardDetail.do?boardNo=" + boardNo);
        } else {
            mv.addObject("msg","게시글이 수정되었습니다.");
            mv.addObject("loc","/board/boardDetail.do?boardNo=" + boardNo);
        }
        mv.setViewName("fragments/msg");
        return mv;
    }
}
