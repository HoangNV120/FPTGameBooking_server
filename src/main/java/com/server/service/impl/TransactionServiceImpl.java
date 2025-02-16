package com.server.service.impl;

import com.server.dto.request.transaction.FindTransactionRequest;
import com.server.dto.request.transaction.TransactionRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.transaction.TransactionMinimalResponse;
import com.server.dto.response.transaction.TransactionResponse;
import com.server.dto.response.userteam.UserTeamResponse;
import com.server.entity.Notification;
import com.server.entity.Transaction;
import com.server.entity.User;
import com.server.enums.*;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.exceptions.RestApiException;
import com.server.repository.TransactionRepository;
import com.server.repository.UserRepository;
import com.server.repository.specifications.TransactionSpecification;
import com.server.service.EmailService;
import com.server.service.NotificationService;
import com.server.service.TransactionService;
import com.server.service.UserService;
import com.server.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public TransactionResponse addTransaction(TransactionRequest request) {
        log.info("Transaction = {}", request.toString());
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            throw new RestApiException("Người dùng không tồn tại.");
        }

        Optional<User> optionalAdmin = userRepository.findByRole(RoleEnum.ROLE_ADMIN);
        if (optionalAdmin.isEmpty()) {
            throw new RestApiException("Admin không tồn tại.");
        }
        int pointsToAdd = new BigDecimal(request.getAmount()).intValue() / 1000;

        Transaction transaction = new Transaction();
        transaction.setBank(request.getBank());
        transaction.setConsumerId(request.getConsumerId());
        transaction.setAmount(new BigDecimal(request.getAmount()));
        transaction.setUserApprove(optionalUser.get());
        transaction.setTransactionStatus(TransactionEnum.PENDING);
        transaction.setPoint(pointsToAdd);

        // Tạo thông báo cho admin
        Notification notification = Notification.builder()
                .content(optionalUser.get().getName() + " đã yêu cầu mua " + pointsToAdd + " điểm với số tiền "
                        + new DecimalFormat("#,### VNĐ").format(new BigDecimal(request.getAmount())))
                .statusNotification(NotificationEnum.UNREAD)
                .type("TRANSACTION")
                .user(optionalAdmin.get())
                .senderId(null)
                .build();
        notificationService.add(notification);

        // Lưu giao dịch
        Transaction savedTransaction = transactionRepository.save(transaction);
        TransactionResponse response = convertTransaction(savedTransaction);

        // Gửi thông báo qua WebSocket
        simpMessagingTemplate.convertAndSend(
                "/subscribe/payment-confirmation-admin/" + optionalAdmin.get().getId(),
                new ResponseGlobal<>(response)
        );


        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z");
        String formattedDate = now.format(formatter);
        String userEmail = optionalUser.get().getEmail();
        String adminEmail = optionalAdmin.get().getEmail();
        String name = userService.findByEmail(userEmail).getName();
        String amount = new DecimalFormat("#,### VNĐ").format(new BigDecimal(request.getAmount()));
        // Gửi email thông báo cho admin
        emailService.sendEmailPurchasePointRequest(formattedDate, adminEmail, savedTransaction.getId(),request.getUserId(),
                userEmail, name, pointsToAdd, amount);

        return response;
    }

    @Override
    public TransactionResponse paymentConfirmation(String id, String status) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new RestApiException("Giao dịch không tồn tại.");
        }

        Transaction transaction = optionalTransaction.get();
        transaction.setTransactionStatus(TransactionEnum.fromString(status));

        TransactionResponse response = convertTransaction(transactionRepository.save(transaction));

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z");
        String formattedDate = now.format(formatter);

        if (TransactionEnum.SUCCESS.equals(transaction.getTransactionStatus())) {
            User user = userRepository.findById(transaction.getUserApprove().getId())
                    .orElseThrow(() -> new NotFoundExceptionHandler("Người dùng không tồn tại."));

            int pointsToAdd = transaction.getAmount().intValue() / 1000;
            int currentPoints = user.getPoint();
            log.info("User {} current points = {}", user.getId(), currentPoints);

            user.setPoint(currentPoints + pointsToAdd);
            user.setLevel(LevelEnum.PREMIER);
            userRepository.save(user);

            Notification notification = Notification.builder()
                    .content("Giao dịch mua " + pointsToAdd + " điểm với số tiền "
                            + new DecimalFormat("#,### VNĐ").format(transaction.getAmount()) + " đã thành công.")
                    .statusNotification(NotificationEnum.UNREAD)
                    .type("TRANSACTION")
                    .user(user)
                    .senderId(null)
                    .build();
            notificationService.add(notification);

            // Gửi thông báo qua WebSocket khi giao dịch thành công
            simpMessagingTemplate.convertAndSend(
                    "/subscribe/payment-confirmation-success/" + transaction.getUserApprove().getId(),
                    new ResponseGlobal<>(response)
            );


            // Gửi email thông báo cho người dùng
            emailService.sendEmailPurchasePointResult(formattedDate, transaction.getId(),
                    user.getEmail(), user.getName(), pointsToAdd,
                    new DecimalFormat("#,### VNĐ").format(transaction.getAmount()),
                    "Giao dịch thành công", "PurchasePointSuccess");

        } else if (TransactionEnum.CANCELLED.equals(transaction.getTransactionStatus())) {
            // Gửi thông báo qua WebSocket khi giao dịch bị hủy
            simpMessagingTemplate.convertAndSend(
                    "/subscribe/payment-confirmation-cancelled/" + transaction.getUserApprove().getId(),
                    new ResponseGlobal<>(response)
            );

            Notification notification = Notification.builder()
                    .content("Giao dịch mua " + transaction.getPoint() + " điểm với số tiền "
                            + new DecimalFormat("#,### VNĐ").format(transaction.getAmount()) + " bị hủy.")
                    .statusNotification(NotificationEnum.UNREAD)
                    .type("TRANSACTION")
                    .user(transaction.getUserApprove())
                    .senderId(null)
                    .build();
            notificationService.add(notification);

            // Gửi email thông báo cho người dùng
            emailService.sendEmailPurchasePointResult(formattedDate, transaction.getId(),
                    transaction.getUserApprove().getEmail(), transaction.getUserApprove().getName(), transaction.getPoint(),
                    new DecimalFormat("#,### VNĐ").format(transaction.getAmount()),
                    "Giao dịch thất bại", "PurchasePointFailed");
        }

        return response;
    }


    @Override
    public List<TransactionResponse> findByTransactionByStatus(String status) {
        return transactionRepository.findByTransactionStatus(TransactionEnum.PENDING)
                .stream()
                .map(this::convertTransaction)
                .toList();
    }

    @Override
    public PageableObject<TransactionMinimalResponse> searchTransactionById(FindTransactionRequest request) {

        Page<Transaction> transactionPage = findTransaction(request);

        // Convert Transaction entities to TransactionResponse DTOs
        List<TransactionMinimalResponse> transactionResponses = transactionPage.getContent().stream()
                .map(this::convertTransactionMinimal)
                .toList();

        // Create and return the PageableObject
        return new PageableObject<>(new PageImpl<>(transactionResponses,
                PageRequest.of(request.getPageNo(), request.getPageSize()), transactionPage.getTotalElements()));
    }

    @Override
    public PageableObject<TransactionResponse> searchTransaction(FindTransactionRequest request) {

        request.setUserId(null);
        Page<Transaction> transactionPage = findTransaction(request);

        // Convert Transaction entities to TransactionResponse DTOs
        List<TransactionResponse> transactionResponses = transactionPage.getContent().stream()
                .map(this::convertTransaction)
                .toList();

        // Create and return the PageableObject
        return new PageableObject<>(new PageImpl<>(transactionResponses,
                PageRequest.of(request.getPageNo(), request.getPageSize()), transactionPage.getTotalElements()));
    }

    private Page<Transaction> findTransaction(FindTransactionRequest request) {
        log.info("FindTransactionRequest = {}", request.toString());
        Specification<Transaction> specification = TransactionSpecification.buildSpecification(request);
        return transactionRepository.findAll(specification, PageRequest.of(request.getPageNo(), request.getPageSize()));
    }

    private TransactionResponse convertTransaction(Transaction transaction){
        return modelMapper.map(transaction, TransactionResponse.class);
    }

    private TransactionMinimalResponse convertTransactionMinimal(Transaction transaction){
        return modelMapper.map(transaction, TransactionMinimalResponse.class);
    }
}
