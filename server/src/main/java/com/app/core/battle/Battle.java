package com.app.core.battle;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.app.core.entities.BattleEntity;
import com.app.core.entities.User;
import com.app.core.entities.Unit;
import com.app.core.entities.behaviour.Movable;

public class Battle {
    private final int LOBBY_CAPACITY = 2;
    private HashMap<Integer, User> users;
    private Board board;
    private int round;
    private Queue<BattleEntity> battleMembers;
    
    public Battle(Board board) {
        this.board = board;
        this.users = new HashMap<>();
        for(int i=0; i<LOBBY_CAPACITY; i++) {
            this.users.put(i, null);
        }
        this.round = 1;
    }

    public void start() {
        for(int i=0; i<this.users.size(); i++) {
            if(this.users.get(i) == null) {
                System.out.println(" Cant start match, lobby is not full");
                return;
            }
        }
        // TRENUTNO START PODRZAVA SAMO 2 IGRACA, POPRAVI OVO
        System.out.println("Game Started");
        this.battleMembers = new LinkedList<>();
        int i=0;
        int j=0;
        while(i<this.users.get(0).getParty().size() && j<this.users.get(1).getParty().size()) {
            int fieldId = i*this.getBoard().getColumns();
            BattleEntity battleEntity1 = this.users.get(0).getParty().get(i).copy();
            battleEntity1.setAllBattleProperties(0, i+j, fieldId);
            if(battleEntity1 instanceof Movable){
                ((Movable)battleEntity1).setDirection(0);
            }
            this.battleMembers.add(battleEntity1);
            this.board.getFields().get(fieldId).setEntity(battleEntity1);

            
            fieldId = j*this.getBoard().getColumns() + (this.getBoard().getColumns()-1);
            BattleEntity battleEntity2 = this.users.get(1).getParty().get(j).copy();
            battleEntity2.setAllBattleProperties( 1, i+j+1, fieldId);
            if(battleEntity2 instanceof Movable) {
                ((Movable)battleEntity2).setDirection(1);
            }
            this.battleMembers.add(battleEntity2);
            this.board.getFields().get(fieldId).setEntity(battleEntity2);
            
            i++;
            j++;
        }

        while(i<this.users.get(0).getParty().size()) {
            int fieldId = i*this.getBoard().getColumns();
            BattleEntity battleEntity = this.users.get(0).getParty().get(i).copy();
            battleEntity.setAllBattleProperties(0, i+j, fieldId);
            if(battleEntity instanceof Movable){
                ((Movable)battleEntity).setDirection(0);
            }
           
            this.battleMembers.add(battleEntity);
            this.board.getFields().get(fieldId).setEntity(battleEntity);
            i++;
        }
        while(j<this.users.get(1).getParty().size()) {
            int fieldId = j*this.getBoard().getColumns() + (this.getBoard().getColumns()-1);
            BattleEntity battleEntity = this.users.get(1).getParty().get(j).copy();
            battleEntity.setAllBattleProperties( 1, i+j+1, fieldId);
            if(battleEntity instanceof Movable) {
                ((Movable)battleEntity).setDirection(1);
            }
            this.battleMembers.add(battleEntity);
            this.board.getFields().get(fieldId).setEntity(battleEntity);
            j++;
        }
    }

    public BattleEntity getNextBattleMember() {
        BattleEntity member = getBattleMembers().poll();
        this.battleMembers.add(member);
        isRoundOver();
        if(!member.isAlive()) {
            member.setHasCompletedTurn(false);
            return getNextBattleMember(); 
        }
        return member;
    }

    public boolean isRoundOver() {
        for(BattleEntity battleMember : this.battleMembers) {
            if(battleMember.isAlive()) {
                if(!battleMember.getHasCompletedTurn()) {
                    return false;
                }
            }
        }
        for(BattleEntity battleMember : this.battleMembers) {
            if(battleMember instanceof Movable){
                Movable movableBattleMember = (Movable) battleMember;
                movableBattleMember.resetMovementPoints();
            }
            battleMember.setHasCompletedTurn(false);
        }
        this.round++;
        return true;
    }

    public int isOver() { // TEMPORARY FIX LATER
        ArrayList<Integer> stillAlive = new ArrayList<>();
        this.battleMembers.forEach(m -> {
            if(m.isAlive()) {
                if(!stillAlive.contains(m.getControllerId())) {
                    stillAlive.add(m.getControllerId());
                }
            }
        });

        if(stillAlive.isEmpty()) return -2;
        if(stillAlive.size() == 1) return stillAlive.get(0);
        return (-1);
    }

    // Lobby functions
    public void addUser(User user) { 
        for(int i=0; i<this.users.size(); i++) {
            if(this.users.get(i) != null) {
                if(this.users.get(i).getUsername().equals(user.getUsername())) {
                    System.out.println("Can't add same user twice ");
                    return;
                }
            }
        }

        for(int i=0; i<this.users.size(); i++) {
            if(this.users.get(i) == null) {
                this.users.replace(i, user);
                return;
            }
        }
        System.out.println(" Lobby is full");
    }
    public void removeUser(String username) {
        for(int i=0; i<this.users.size(); i++) {
            if(this.users.get(i)!=null) {
                if(this.users.get(i).getUsername().equals(username)) {
                    this.users.replace(i, null);
                }
            }
        }
        System.out.println(" That User was not in the lobby");
    }

    // Setters and Getters
    public void setBoard(Board board) {
        this.board = board;
    }
    public void setRound(int round) {
        this.round = round;
    }
    
    public HashMap<Integer, User> getUsers() { return this.users; }
    public Board getBoard() { return this.board; }
    public int getRound() { return this.round; }
    public Queue<BattleEntity> getBattleMembers() { return this.battleMembers; }
}
