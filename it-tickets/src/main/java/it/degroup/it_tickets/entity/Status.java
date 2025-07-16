package it.degroup.it_tickets.entity;

import jakarta.persistence.*;


public enum Status {
    TO_DO,
    IN_PROGRESS,
    COMPLETED,
    REJECTED
}
