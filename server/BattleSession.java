package com.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.app.core.battle.Battle;
import com.app.core.battle.Board;
import com.app.core.entities.BattleEntity;
import com.app.core.entities.Creature;
import com.app.core.entities.Hero;
import com.app.core.entities.HeroClass;
import com.app.core.entities.Player;
import com.app.core.entities.Weapon;
import com.app.core.entities.WeaponType;
import com.app.core.entities.behaviour.Attacking;
import com.app.core.entities.behaviour.Movable;
import com.app.util.TargetList;

public class BattleSession {
    private static int PORT = 12345;
    private ServerSocket serverSocket;
    private HashMap<Integer, PlayerHandler> playerHandlers = new HashMap<>();
    private Battle battle;

    public BattleSession() throws IOException{
        this.serverSocket = new ServerSocket(PORT);
        startListeningForPlayers();
    }

    public void startListeningForPlayers() throws IOException{
        int id = 0;
        try {
            while(this.playerHandlers.size() != 2) {
                Socket socket = serverSocket.accept();
                PlayerHandler playerHandler = new PlayerHandler(socket, this);
                this.playerHandlers.put(id, playerHandler);
                new Thread(playerHandler).start();
                id++;
            }    
        } catch (Exception e) {
            System.out.println("Error in accepting connection");
        }

        initGame();
    }

    public void initGame() { // temporary
        Weapon weapon = new Weapon("woodensword", "Wooden Sword", WeaponType.SWORD, new int[]{5,6}, 3, 1);
        Weapon axe = new Weapon("woodenaxe", "Wooden Axe", WeaponType.AXE, new int[]{3,8}, 10, 1);
        Hero hero1 = new Hero("dwarf", "Dwarfman", HeroClass.FIGHTER, 1, weapon, axe);

        Weapon bow = new Weapon("woodenbow", "Wooden Bow", WeaponType.BOW, new int[]{4,9}, 10, 0);
        Weapon dagger = new Weapon("woodendagger", "Wooden Dagger", WeaponType.DAGGER, new int[]{3,4}, 15, 0);
        Hero hero2 = new Hero("wolfman", "Archerman", HeroClass.ARCHER, 1, bow, dagger);
        
        Player player1 = new Player("ogi", false);
        player1.getParty().add(new Creature("greywolf", "Grey Wolf", 14, 5, new int[]{3, 5},0, 1));
        player1.getParty().add(hero1);
        player1.getParty().add(hero2);

        Player player2 = new Player("ymir", false);
        player2.getParty().add(new Creature("greywolf", "Grey Wolf", 14, 5, new int[]{3, 5},0, 1));
        player2.getParty().add(new Creature("greywolf","Grey Wolf", 14, 5, new int[]{3, 5},0, 1));
        player2.getParty().add(new Creature("greywolf","Grey Wolf", 14, 5, new int[]{3, 5},0, 1));
        
        this.battle = new Battle(new Board(6, 9));

        this.battle.addPlayer(player1);
        this.battle.addPlayer(player2);

        battle.start();
        
        this.battle.getPlayers().forEach((key, value) -> {
            this.playerHandlers.get(key).sendMessage("init " + String.valueOf(key) + ";" + this.battle.getBoard().toStringHeader());
        });
        
        broadcastMessage("start" + this.battle.getBoard().toStringFields());

        changeTurn();
    }

    public void processMessage(String message) {
        String[] messageComponents = message.split(" ");
        String action = messageComponents[0];
        
        switch (action) {
            case "skip":
                handleSkip(Integer.parseInt(messageComponents[2]));
                break;
            case "moving":
                handleMoving(Integer.parseInt(messageComponents[1]), Integer.parseInt(messageComponents[2]));
                break;
            case "move":
                handleMove(Integer.parseInt(messageComponents[1]), Integer.parseInt(messageComponents[2]), Integer.parseInt(messageComponents[3]));
                break;
            case "attacking":
                handleAttacking(Integer.parseInt(messageComponents[1]), Integer.parseInt(messageComponents[2]));
                break;
            case "attack":
                handleAttack(Integer.parseInt(messageComponents[1]), Integer.parseInt(messageComponents[2]), Integer.parseInt(messageComponents[3]));
                break;
            case "x":
                System.out.println("nothing");
                break;
            default:
                break;
        }
    }

    public void changeTurn() {
        int winner = battle.isOver();
        if(winner != (-1)) {
            broadcastMessage("end " + String.valueOf(winner));
        }else{
            BattleEntity nextEntity = this.battle.getNextBattleMember();
            broadcastMessage("turn " + nextEntity.getFieldId() + " " + nextEntity.getControllerId());
        }
    }

    public void handleMoving(int controllerId, int fieldId) {
        System.out.println("request moving from " + fieldId);
        BattleEntity battleEntity = this.battle.getBoard().getFields().get(fieldId).getEntity();
        int range = 0;
        if(battleEntity instanceof Movable) {
            range =  ((Movable)battleEntity).getMovementPoints();  
        }
        TargetList validMovementTargets = this.battle.getBoard().getValidMovementTargets(fieldId, range);
        this.playerHandlers.get(controllerId).sendMessage("moving;" + validMovementTargets.toString());
    }

    public void handleMove(int controllerId, int sourceFieldId, int targetFieldId) {
        this.battle.getBoard().move(sourceFieldId, targetFieldId);
        BattleEntity battleEntity = this.battle.getBoard().getFields().get(targetFieldId).getEntity();
        int range = 0;
        if(battleEntity instanceof Movable) {
            range =  ((Movable)battleEntity).getMovementPoints();  
        }
        TargetList validMovementTargets = this.battle.getBoard().getValidMovementTargets(targetFieldId, range);
        System.out.println("move " + String.valueOf(controllerId) + " " + String.valueOf(sourceFieldId) + " " + String.valueOf(targetFieldId) + ";" + validMovementTargets.toString() + this.battle.getBoard().toStringFields());
        broadcastMessage("move " + String.valueOf(controllerId) + " " + String.valueOf(sourceFieldId) + " " + String.valueOf(targetFieldId) + ";" + validMovementTargets.toString() + this.battle.getBoard().toStringFields());
    }

    public void handleAttacking(int controllerId, int fieldId) {
        System.out.println("request attacking from " + fieldId);
        BattleEntity battleEntity = this.battle.getBoard().getFields().get(fieldId).getEntity();
        int range = 0;
        if(battleEntity instanceof Attacking) {
            range =  ((Attacking)battleEntity).getRange();  
        }
        TargetList validAttackTargets = this.battle.getBoard().getValidAttackTargets(fieldId, range);
        this.playerHandlers.get(controllerId).sendMessage("attacking;" + validAttackTargets.toString());
    }

    public void handleAttack(int controllerId, int sourceFieldId, int targetFieldId) {
        int[] damageTaken = this.battle.getBoard().attack(sourceFieldId, targetFieldId);
        broadcastMessage("attack " + String.valueOf(controllerId) + " " + String.valueOf(sourceFieldId) + " " + String.valueOf(targetFieldId) + ";" + String.valueOf(damageTaken[0]) + " " + String.valueOf(damageTaken[1]) + this.battle.getBoard().toStringFields());
        changeTurn();
    }

    public void handleSkip(int fieldId) {
        this.battle.getBoard().skip(fieldId);
        changeTurn();
    }

    public void broadcastMessage(String message) {
        this.playerHandlers.forEach((key, value) -> {
            value.sendMessage(message);
        });
    }

    public void stop() throws IOException{
        this.playerHandlers.forEach((key, value) -> {
            try {
                value.close();
            } catch (Exception e) {
                System.out.println("Error in closing playerHandler");
            }
        });
        this.serverSocket.close();
    }

    public HashMap<Integer, PlayerHandler> getPlayerHandlers() { return this.playerHandlers; }
}
