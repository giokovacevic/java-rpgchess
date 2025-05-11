package com.app.core.battle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.app.core.entities.BattleEntity;
import com.app.core.entities.Player;
import com.app.core.entities.Unit;
import com.app.core.entities.behaviour.Movable;

public class Battle {
    private final int LOBBY_CAPACITY = 2;
    private HashMap<Integer, Player> players;
    private Board board;
    private int round;
    private Queue<BattleEntity> battleMembers;
    
    public Battle(Board board) {
        this.board = board;
        this.players = new HashMap<>();
        for(int i=0; i<LOBBY_CAPACITY; i++) {
            this.players.put(i, null);
        }
        this.round = 1;
    }

    public void start() {
        for(int i=0; i<this.players.size(); i++) {
            if(this.players.get(i) == null) {
                System.out.println(" Cant start match, lobby is not full");
                return;
            }
        }
        // TRENUTNO START PODRZAVA SAMO 2 IGRACA, POPRAVI OVO
        System.out.println("Game Started");
        this.battleMembers = new LinkedList<>();
        int i=0;
        int j=0;
        while(i<this.players.get(0).getParty().size() && j<this.players.get(1).getParty().size()) {
            int fieldId = i*this.getBoard().getColumns();
            this.players.get(0).getParty().get(i).setAllBattleProperties(0, i+j, fieldId);
            if(this.players.get(0).getParty().get(i) instanceof Movable){
                this.players.get(0).getParty().get(i).setDirection(0);
            }
            this.battleMembers.add(this.players.get(0).getParty().get(i));
            this.board.getFields().get(fieldId).setEntity(this.players.get(0).getParty().get(i));

            
            fieldId = j*this.getBoard().getColumns() + (this.getBoard().getColumns()-1);
            this.players.get(1).getParty().get(j).setAllBattleProperties( 1, i+j+1, fieldId);
            if(this.players.get(1).getParty().get(j) instanceof Movable) {
                this.players.get(1).getParty().get(j).setDirection(1);
            }
            this.battleMembers.add(this.players.get(1).getParty().get(j));
            this.board.getFields().get(fieldId).setEntity(this.players.get(1).getParty().get(j));
            
            i++;
            j++;
        }

        while(i<this.players.get(0).getParty().size()) {
            int fieldId = i*this.getBoard().getColumns();
            this.players.get(0).getParty().get(i).setAllBattleProperties(0, i+j, fieldId);
            if(this.players.get(0).getParty().get(i) instanceof Movable){
                this.players.get(0).getParty().get(i).setDirection(0);
            }
           
            this.battleMembers.add(this.players.get(0).getParty().get(i));
            this.board.getFields().get(fieldId).setEntity(this.players.get(0).getParty().get(i));
            i++;
        }
        while(j<this.players.get(1).getParty().size()) {
            int fieldId = j*this.getBoard().getColumns() + (this.getBoard().getColumns()-1);
            this.players.get(1).getParty().get(j).setAllBattleProperties( 1, i+j+1, fieldId);
            if(this.players.get(1).getParty().get(j) instanceof Movable) {
                this.players.get(1).getParty().get(j).setDirection(1);
            }
            this.battleMembers.add(this.players.get(1).getParty().get(j));
            this.board.getFields().get(fieldId).setEntity(this.players.get(1).getParty().get(j));
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
        boolean looser0 = true;
        boolean looser1 = true;
        for(Unit unit : this.getPlayers().get(0).getParty()) {
            if(unit.isAlive()) looser0 = false;
        } 
        for(Unit unit : this.getPlayers().get(1).getParty()) {
            if(unit.isAlive()) looser1 = false;
        } 
        
        if(looser0!=looser1) {
            if(looser0 == true) return 1;
            return 0;
        }
        if((looser0 == true) && (looser1 == true)) return 2;

        return (-1);
    }

    // Lobby functions
    public void addPlayer(Player player) { 
        for(int i=0; i<this.players.size(); i++) {
            if(this.players.get(i) != null) {
                if(this.players.get(i).getUsername().equals(player.getUsername())) {
                    System.out.println("Can't add same player twice ");
                    return;
                }
            }
        }

        for(int i=0; i<this.players.size(); i++) {
            if(this.players.get(i) == null) {
                this.players.replace(i, player);
                return;
            }
        }
        System.out.println(" Lobby is full");
    }
    public void removePlayer(String username) {
        for(int i=0; i<this.players.size(); i++) {
            if(this.players.get(i)!=null) {
                if(this.players.get(i).getUsername().equals(username)) {
                    this.players.replace(i, null);
                }
            }
        }
        System.out.println(" That player was not in the lobby");
    }

    // Setters and Getters
    public void setBoard(Board board) {
        this.board = board;
    }
    public void setRound(int round) {
        this.round = round;
    }
    
    public HashMap<Integer, Player> getPlayers() { return this.players; }
    public Board getBoard() { return this.board; }
    public int getRound() { return this.round; }
    public Queue<BattleEntity> getBattleMembers() { return this.battleMembers; }
}
