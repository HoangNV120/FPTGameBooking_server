package com.server.controller;

import com.server.dto.request.message.WebsocketMessage;
import com.server.dto.request.room.CreateRoomRequest;
import com.server.dto.request.userteam.CreateUserJoinTeamRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.message.MessageResponse;
import com.server.dto.response.room.RoomResponse;
import com.server.dto.response.userteam.UserTeamResponse;
import com.server.service.MessageService;
import com.server.service.RoomService;
import com.server.service.UserTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/websocket")
@RequiredArgsConstructor
public class WebsocketController {

    private final UserTeamService userTeamService;
    private final MessageService messageService;
    private final RoomService roomService;

    /**
     * Xử lý yêu cầu tham gia phòng dựa trên mã phòng (codeRoom).
     * - Nhận `CreateUserJoinTeamRequest` từ client thông qua WebSocket.
     * - Trả về thông tin người dùng đã tham gia nhóm thông qua chủ đề "/subscribe/join-room/{codeRoom}".
     *
     * @param request  Thông tin yêu cầu tham gia nhóm.
     * @param codeRoom Mã phòng để xác định nhóm.
     * @return Đối tượng ResponseGlobal chứa thông tin người dùng trong nhóm.
     */
    @MessageMapping("/join-room/{codeRoom}")
    @SendTo("/subscribe/join-room/{codeRoom}")
    public ResponseGlobal<UserTeamResponse> processJoinTeamByRoom(@Payload CreateUserJoinTeamRequest request,
                                                                  @DestinationVariable String codeRoom) {
        log.info("ProcessJoinTeamByRoom====> request = {}", request);
        return new ResponseGlobal<>(userTeamService.processJoinTeamByRoom(request));
    }


    /**
     * Xử lý việc loại bỏ thành viên khỏi nhóm.
     * - Nhận mã phòng (codeRoom) và payload chứa teamId, userId từ client.
     * - Trả về thông báo qua chủ đề "/subscribe/kick-member/{codeRoom}".
     *
     * @param codeRoom Mã phòng để xác định nhóm.
     * @param payload  Dữ liệu chứa thông tin teamId và userId.
     * @return Đối tượng ResponseGlobal chứa thông tin về nhóm sau khi loại bỏ thành viên.
     */
    @MessageMapping("/kick-member/{codeRoom}")
    @SendTo("/subscribe/kick-member/{codeRoom}")
    public ResponseGlobal<UserTeamResponse> kickMemberFromTeam(@DestinationVariable("codeRoom") String codeRoom,
                                                               @Payload Map<String, String> payload) {
        String teamId = payload.get("teamId");
        String userId = payload.get("userId");

        log.info("kickMemberFromTeam==> teamId = {}, userId = {}", teamId, userId);
        return new ResponseGlobal<>(userTeamService.kickMemberFromTeam(teamId, userId));
    }


    /**
     * Xử lý việc thành viên rời khỏi phòng.
     * - Nhận mã phòng (codeRoom) và userId từ client thông qua WebSocket.
     * - Trả về thông báo qua chủ đề "/subscribe/member-exit-room/{codeRoom}".
     *
     * @param codeRoom Mã phòng để xác định nhóm.
     * @param userId   ID của người dùng rời khỏi nhóm.
     * @return Đối tượng ResponseGlobal chứa thông tin về nhóm sau khi người dùng rời đi.
     */
    @MessageMapping("/member-exit-room/{codeRoom}/{userId}")
    @SendTo("/subscribe/member-exit-room/{codeRoom}")
    public ResponseGlobal<UserTeamResponse> exitTeamRoom(@DestinationVariable("codeRoom") String codeRoom,
                                                         @DestinationVariable("userId") String userId) {
        log.info("ExitTeamRoom==> codeRoom = {}, userId = {}", codeRoom, userId);
        return new ResponseGlobal<>(userTeamService.exitTeamRoom(codeRoom, userId));
    }


    /**
     * Cập nhật trạng thái thành viên trong nhóm.
     * - Nhận userTeamId và codeRoom từ client thông qua WebSocket.
     * - Trả về thông báo qua chủ đề "/subscribe/update-status-member-join-room/{codeRoom}".
     *
     * @param userTeamId ID của thành viên trong nhóm.
     * @param codeRoom   Mã phòng để xác định nhóm.
     * @return Đối tượng ResponseGlobal chứa trạng thái cập nhật của thành viên.
     */
    @MessageMapping("/update-status-member-join-room/{codeRoom}/{userTeamId}")
    @SendTo("/subscribe/update-status-member-join-room/{codeRoom}")
    public ResponseGlobal<UserTeamResponse> updateStatus(@DestinationVariable("userTeamId") String userTeamId,
                                                         @DestinationVariable("codeRoom") String codeRoom) {
        log.info("UpdateStatus==> userTeamId = {}, codeRoom = {}", userTeamId, codeRoom);
        return new ResponseGlobal<>(userTeamService.updateStatus(userTeamId));
    }


    /**
     * Xử lý việc gửi tin nhắn tới phòng.
     * - Nhận codeGame, codeRoom, và nội dung tin nhắn từ client.
     * - Trả về tin nhắn qua chủ đề "/subscribe/post-message-to-room/{codeGame}/{codeRoom}".
     *
     * @param codeGame       Mã trò chơi.
     * @param codeRoom       Mã phòng để xác định nhóm.
     * @param requestMessage Nội dung tin nhắn từ người dùng.
     * @return Đối tượng ResponseGlobal chứa thông tin tin nhắn đã gửi.
     */
    @MessageMapping("/post-message-to-room/{codeGame}/{codeRoom}")
    @SendTo("/subscribe/post-message-to-room/{codeGame}/{codeRoom}")
    public ResponseGlobal<MessageResponse> postMessageToRoom(@DestinationVariable("codeGame") String codeGame,
                                                             @DestinationVariable("codeRoom") String codeRoom,
                                                             @Payload WebsocketMessage requestMessage) {
        log.info("PostMessageToRoom==> codeGame = {}, codeRoom = {}", codeGame, codeRoom);
        return new ResponseGlobal<>(messageService.addWebsocketMessage(requestMessage));
    }

    /**
     * API thêm phòng mới thông qua giao tiếp WebSocket.
     *
     * @param request Dữ liệu payload chứa thông tin chi tiết của phòng, được kiểm tra với @Valid.
     * @return Một ResponseGlobal chứa thông tin chi tiết của phòng vừa được thêm.
     */
    @MessageMapping("/add-room/{codeGame}/{userId}")
    @SendTo("/subscribe/add-room/{codeGame}")
    public ResponseGlobal<RoomResponse> add(@RequestBody CreateRoomRequest request,
                                            @DestinationVariable("codeGame") String codeGame,
                                            @DestinationVariable("userId") String userId) {
        request.setInfoUser(userId);

        return new ResponseGlobal<>(roomService.add(request));
    }

    @MessageMapping("/pin-message-to-room/{codeGame}/{codeRoom}")
    @SendTo("/subscribe/pin-message-to-room/{codeGame}/{codeRoom}")
    public ResponseGlobal<MessageResponse> pinMessageToRoom(@DestinationVariable("codeGame") String codeGame,
                                                            @DestinationVariable("codeRoom") String codeRoom,
                                                            @Payload WebsocketMessage requestMessage) {
        log.info("PinMessageToRoom==> codeGame = {}, codeRoom = {}", codeGame, codeRoom);
        return new ResponseGlobal<>(messageService.pinMessageToRoom(requestMessage));
    }

    @MessageMapping("/post-message-to-server")
    @SendTo("/subscribe/post-message-to-server")
    public ResponseGlobal<MessageResponse> postMessageServer(@Payload WebsocketMessage requestMessage) {
        log.info("PostMessageToRoom==> requestMessage = {}", requestMessage);
        return new ResponseGlobal<>(messageService.addWebsocketMessage(requestMessage));
    }

}
