package com.example.meettify.service.chat;

import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.document.chat.ChatMessage;
import com.example.meettify.dto.chat.*;
import com.example.meettify.entity.chat_room.ChatRoomEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.chat.ChatException;
import com.example.meettify.exception.chat.ChatRoomException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.mongo.chat.ChatMessageRepository;
import com.example.meettify.repository.jpa.chat.ChatRoomRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    // 메시지 보낼 때 몽고디비에 저장
    @Override
    public ChatMessageDTO sendMessage(ChatMessageDTO message, Long roomId) {
        try {
            if(message.getType() == MessageType.PLACE) {
                log.debug("주소 공유 메시지입니다.");
            } else if (message.getType() == MessageType.TALK) {
                log.debug("일반 메시지입니다.");
            }

            ChatMessage chatMessage = ChatMessage.create(message);

            if(chatMessage.getRoomId() == null) {
                chatMessage.setRoomId(roomId);
            }

            log.debug("채팅 내용 확인 {}", chatMessage);
            // 몽고 디비에 저장
            chatMessageRepository.save(chatMessage);
            return ChatMessageDTO.change(chatMessage);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new ChatException("메시지를 작성하는데 실패했습니다.");
        }
    }

    // 채팅방의 채팅 내역 조회
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessagesByRoomId(Long roomId) {
        List<ChatMessage> findChatByRoomId = chatMessageRepository.findByRoomId(roomId);
        log.debug("message 체크 {}", findChatByRoomId);

        if (findChatByRoomId == null || findChatByRoomId.isEmpty()) {
            // 빈 배열 반환 (에러 아님)
            return new ArrayList<>();
        }

        return findChatByRoomId
                .stream().map(ChatMessageDTO::change)
                .toList();
    }

    // 단체 채팅하는 채팅방 생성
    @Override
    public ChatRoomDTO createRoom(String roomName, String email, Long meetId) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 모임글로 채팅방 조회
            ChatRoomEntity findChatRoom = chatRoomRepository.findByMeetId(meetId);

            // 채팅 중복 생성 방지
            if (findChatRoom != null) {
                throw new ChatRoomException("이미 채팅방이 생성되었습니다.");
            }

            // 채팅방 엔티티 생성
            ChatRoomEntity chatRoomEntity = ChatRoomEntity.create(roomName, findMember.getNickName(), RoomStatus.OPEN, meetId);
            // 모임장이 채팅방 유저에 등록
            chatRoomEntity.getInviteMemberIds().add(findMember.getMemberId());
            // 채팅방 디비에 저장
            ChatRoomEntity saveChatRoom = chatRoomRepository.save(chatRoomEntity);

            return ChatRoomDTO.change(saveChatRoom);
        } catch (Exception e) {
            throw new ChatRoomException("채팅방을 생성하는데 실패했습니다.");
        }
    }

    // 본인에게 해당된 채팅방 리스트 조회
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public List<ChatRoomDTO> getRooms(String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 본인의 채팅방 모두 조회
            List<ChatRoomEntity> findAllByMember = chatRoomRepository.findChatRoomsByCreatedOrInvited(findMember.getNickName(), findMember.getMemberId());
            return findAllByMember.stream()
                    .map(ChatRoomDTO::change)
                    .toList();
        } catch (Exception e) {
            throw new ChatRoomException("채팅방을 조회하는데 실패했습니다.");
        }
    }

    // 채팅방 입장시 체크
    @Override
    public boolean joinRoom(String email, Long roomId) {
        // 회원 조회
        MemberEntity findMember = memberRepository.findByMemberEmail(email);
        // 방번호와 초대받은 회원번호가 일치한지 조회
        ChatRoomEntity findChatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException("채팅방이 존재하지 않습니다."));

        if (findChatRoom == null) {
            log.warn("채팅방이 존재하지 않습니다.");
            return false;
        }
        if(findChatRoom.getInviteMemberIds().contains(findMember.getMemberId())) {
            return true;
        }

        // 회원을 채팅방에 초대
        findChatRoom.getInviteMemberIds().add(findMember.getMemberId());
        chatRoomRepository.save(findChatRoom);
        return true;
    }

    // 채팅방에 들어간 유저 리스트
    @Override
    @Transactional(readOnly = true)
    public List<ChatMemberDTO> getRoomMembers(Long roomId) {
        try {
            // 채팅방 조회
            ChatRoomEntity findChatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new ChatRoomException("채팅방이 존재하지 않습니다."));

            List<MemberEntity> memberList = findChatRoom.getInviteMemberIds()
                    .stream()
                    .map(id -> memberRepository.findById(id)
                            .orElseThrow(() -> new MemberException("유저가 존재하지 않습니다.")))
                    .toList();

            return memberList
                    .stream().map(ChatMemberDTO::getMember)
                    .toList();
        } catch (ChatRoomException e) {
            throw new ChatRoomException(e.getMessage());
        } catch (MemberException e) {
            throw new MemberException(e.getMessage());
        }
    }

    // 채팅방 나가기
    @Override
    public String leaveRoom(String email, Long roomId) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 채팅방 조회
            ChatRoomEntity findChatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new ChatRoomException("채팅방이 존재하지 않습니다."));

            // 채팅방의 생성자일 경우
            if(findMember.getNickName().equals(findChatRoom.getCreatedNickName())) {
                // 채팅방 삭제
                chatRoomRepository.delete(findChatRoom);
                return "채팅방이 삭제되었습니다.";
            }
            // 채팅방에서 나갔으므로 채팅방 유저에서 제외
            findChatRoom.getInviteMemberIds().remove(findMember.getMemberId());
            return "채팅방에서 나갔습니다.";
        } catch (ChatRoomException e) {
            throw new ChatRoomException("채팅방에서 나가는데/삭제하는데 실패했습니다.");
        }
    }

    // 모임글에 채팅방이 존재하는지 조회
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public boolean checkCreateChatRoom(Long meetId) {
        try {
            // 모임글로 채팅방 조회
            ChatRoomEntity findChatRoom = chatRoomRepository.findByMeetId(meetId);
            // 채팅방이 존재하면 true
            if (findChatRoom != null) {
                return true;
            } else {
                // 채팅방이 존재하지 않으면 false
                return false;
            }
        } catch (Exception e) {
            throw new ChatRoomException("조회하는데 실패했습니다.");
        }
    }
}
