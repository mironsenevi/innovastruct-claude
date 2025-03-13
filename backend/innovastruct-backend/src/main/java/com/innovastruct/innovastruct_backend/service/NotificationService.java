package com.innovastruct.innovastruct_backend.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.innovastruct.innovastruct_backend.exceptions.ResourceNotFoundException;
import com.innovastruct.innovastruct_backend.model.Notification;
import com.innovastruct.innovastruct_backend.repository.NotificationRepository;
import com.innovastruct.innovastruct_backend.security.services.UserDetailsImpl;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    public List<Notification> getNotificationsForCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepository.findByUserId(userDetails.getId(), Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Notification> getUnreadNotificationsForCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepository.findByUserIdAndRead(
                userDetails.getId(), false, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public long getUnreadNotificationCount() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepository.countByUserIdAndRead(userDetails.getId(), false);
    }

    public Notification markNotificationAsRead(String notificationId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        // Verify that the notification belongs to the current user
        if (!notification.getUserId().equals(userDetails.getId())) {
            throw new IllegalStateException("You are not authorized to modify this notification");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllNotificationsAsRead() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndRead(
                userDetails.getId(), false, Sort.by(Sort.Direction.DESC, "createdAt"));

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public void deleteNotification(String notificationId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        // Verify that the notification belongs to the current user
        if (!notification.getUserId().equals(userDetails.getId())) {
            throw new IllegalStateException("You are not authorized to delete this notification");
        }

        notificationRepository.delete(notification);
    }
}