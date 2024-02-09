package gdsc.skhu.drugescape.service;

import gdsc.skhu.drugescape.domain.dto.BoardDTO;
import gdsc.skhu.drugescape.domain.model.Board;
import gdsc.skhu.drugescape.domain.model.Image;
import gdsc.skhu.drugescape.domain.model.Member;
import gdsc.skhu.drugescape.domain.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final HeartRepository heartRepository;
    private final CommentRepository commentRepository;

    public BoardService(BoardRepository boardRepository,
                        MemberRepository memberRepository,
                        ImageService imageService,
                        HeartRepository heartRepository,
                        CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
        this.imageService = imageService;
        this.heartRepository = heartRepository;
        this.commentRepository = commentRepository;
    }

    public Page<Board> getBoardList(PageRequest pageRequest) {
        return boardRepository.findAll(pageRequest);
    }

    public BoardDTO getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardDTO::of)
                .orElse(null);
    }

    @Transactional
    public Long createBoard(BoardDTO boardDTO, String loginId) {
        try {
            Member member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member loginId: " + loginId));
            Board board = Board.builder()
                    .title(boardDTO.getTitle())
                    .content(boardDTO.getContent())
                    .member(member)
                    .build();

            Board savedBoard = boardRepository.save(board);

            if (boardDTO.getImageURL() != null) {
                Image image = imageService.saveImage(boardDTO.getImageURL(), savedBoard);
                savedBoard.uploadImage(image);
                boardRepository.save(savedBoard); // 이미지 정보를 포함해 다시 저장
            }

            return savedBoard.getId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Transactional
    public Long updateBoard(Long boardId, BoardDTO boardDTO) {
        try {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

            if (board.getImage() != null) {
                imageService.deleteImage(board.getImage());
            }

            Image image = null;
            if (boardDTO.getImageURL() != null) {
                image = imageService.saveImage(boardDTO.getImageURL(), board);
            }

            board.updateDetails(boardDTO.getTitle(), boardDTO.getContent(), image);
            return boardRepository.save(board).getId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image", e);
        }
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

        if (board.getImage() != null) {
            try {
                imageService.deleteImage(board.getImage());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image", e);
            }
        }

        boardRepository.deleteById(boardId);
    }
}