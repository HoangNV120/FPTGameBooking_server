package com.server.service.impl;

import com.server.dto.request.transaction.TransactionRequest;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.transaction.TransactionResponse;
import com.server.dto.response.userteam.UserTeamResponse;
import com.server.entity.Transaction;
import com.server.entity.User;
import com.server.enums.RoleEnum;
import com.server.enums.StatusEnum;
import com.server.enums.TransactionEnum;
import com.server.exceptions.NotFoundExceptionHandler;
import com.server.exceptions.RestApiException;
import com.server.repository.TransactionRepository;
import com.server.repository.UserRepository;
import com.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        TransactionResponse response = convertTransaction(transactionRepository.save(transaction));
        simpMessagingTemplate.convertAndSend(
                "/subscribe/payment-confirmation-admin/" + optionalAdmin.get().getId(),
                new ResponseGlobal<>(response)
        );

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

        if (TransactionEnum.SUCCESS.equals(transaction.getTransactionStatus())) {
            User user = userRepository.findById(transaction.getUserApprove().getId())
                    .orElseThrow(() -> new NotFoundExceptionHandler("Người dùng không tồn tại."));

            int pointsToAdd = transaction.getAmount().intValue() / 1000;
            int currentPoints = user.getPoint();
            log.info("User {} current points = {}", user.getId(), currentPoints);

            user.setPoint(currentPoints + pointsToAdd);
            userRepository.save(user);

            // Gửi thông báo qua WebSocket khi giao dịch thành công
            simpMessagingTemplate.convertAndSend(
                    "/subscribe/payment-confirmation-success/" + transaction.getUserApprove().getId(),
                    new ResponseGlobal<>(response)
            );
        } else if (TransactionEnum.CANCELLED.equals(transaction.getTransactionStatus())) {
            // Gửi thông báo qua WebSocket khi giao dịch bị hủy
            simpMessagingTemplate.convertAndSend(
                    "/subscribe/payment-confirmation-cancelled/" + transaction.getUserApprove().getId(),
                    new ResponseGlobal<>(response)
            );
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


    private TransactionResponse convertTransaction(Transaction transaction){
        return modelMapper.map(transaction, TransactionResponse.class);
    }
}
