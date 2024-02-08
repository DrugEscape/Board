package gdsc.skhu.drugescape.service;

import gdsc.skhu.drugescape.domain.dto.BoardDTO;
import gdsc.skhu.drugescape.domain.dto.MemberDTO;
import gdsc.skhu.drugescape.domain.model.Board;
import gdsc.skhu.drugescape.domain.model.Image;
import gdsc.skhu.drugescape.domain.model.Member;
import gdsc.skhu.drugescape.domain.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ImageService imageService;

    public BoardService(BoardRepository boardRepository,
                        CommentRepository commentRepository,
                        HeartRepository heartRepository,
                        MemberRepository memberRepository,
                        MemberService memberService,
                        ImageService imageService
                        ) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
        this.imageService = imageService;
        this.heartRepository = heartRepository;
        this.commentRepository = commentRepository;
    }

    public ResponseEntity<?> getBoardList(PageRequest pageRequest, Principal principal) {
        MemberDTO memberInfo = memberService.getAuthenticatedMemberInfo(principal);
        Page<Board> boards = boardRepository.findAll(pageRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("memberInfo", memberInfo);
        response.put("boards", boards);
        return ResponseEntity.ok(response);
    }

    public BoardDTO getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardDTO::of)
                .orElse(null);
    }

    @Transactional
    public Long createBoard(BoardDTO boardDTO, String email) throws IOException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member email: " + email));

        Board board = Board.builder()
                .title(boardDTO.getTitle())
                .body(boardDTO.getBody())
                .member(member)
                .heartCnt(boardDTO.getHeartCnt() != null ? boardDTO.getHeartCnt() : 0)
                .commentCnt(0)
                .build();
        Board savedBoard = boardRepository.save(board);
        if (boardDTO.getImage() != null) {
            Image image = imageService.saveImage(boardDTO.getImage(), savedBoard);
            // 이미지 설정을 위해 불변 객체 패턴을 적용
            savedBoard = savedBoard.toBuilder().image(image).build();
            boardRepository.save(savedBoard); // Update board with image
        }
        return savedBoard.getId();
    }

    @Transactional
    public Long updateBoard(Long boardId, BoardDTO boardDTO) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        Image image = null;
        if (boardDTO.getNewImage() != null) {
            if (board.getImage() != null) {
                imageService.deleteImage(board.getImage());
            }
            image = imageService.saveImage(boardDTO.getNewImage(), board);
        }
        Board updatedBoard = board.toBuilder()
                .title(boardDTO.getTitle())
                .body(boardDTO.getBody())
                .image(image)
                .build();
        return boardRepository.save(updatedBoard).getId();
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        if (board.getImage() != null) {
            imageService.deleteImage(board.getImage());
        }
        boardRepository.deleteById(boardId);
    }
}