package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationService {

    public void checkAndNotifyLowBalance(User user) {
        if (user.getFcmToken() != null && user.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            sendLowBalancePush(user.getFcmToken(), user.getBalance());
        }
    }

    private void sendLowBalancePush(String token, BigDecimal currentBalance) {
        System.out.println("Sending Push to " + token + ": Your balance is under 0, limit is -2000: $" + currentBalance);

        /* Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle("Saldo Insuficiente")
                .setBody("Tu saldo actual es de $" + currentBalance + ". Recargá para evitar multas.")
                .build())
            .build();
        FirebaseMessaging.getInstance().send(message);
        */
    }
}