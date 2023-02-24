package com.stav.completenotes.boards;

import com.google.firebase.auth.FirebaseUser;

public class Board {
    private String name;
    private FirebaseUser owner;
    private FirebaseUser[] participants;
    private Item[] items;

    public Board(String name, FirebaseUser owner, FirebaseUser[] participants, Item[] items) {
        this.name = name;
        this.owner = owner;
        this.participants = participants;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FirebaseUser getOwner() {
        return owner;
    }

    public void setOwner(FirebaseUser owner) {
        this.owner = owner;
    }

    public FirebaseUser[] getParticipants() {
        return participants;
    }

    public void setParticipants(FirebaseUser[] participants) {
        this.participants = participants;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }
}
