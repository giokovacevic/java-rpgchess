package com.app.network;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.app.core.battle.Battle;
import com.app.core.battle.Board;
import com.app.core.entities.BattleEntity;
import com.app.core.entities.User;
import com.app.core.entities.behaviour.Attacking;
import com.app.core.entities.behaviour.Movable;
import com.app.util.TargetList;

public class BattleServer implements IServer{
    private Server mainServer;
    private final static int MAX_USERS = 2;
    private ConcurrentHashMap<Integer, UserHandler> userHandlers = new ConcurrentHashMap<>();
    
    private Battle battle;
    //private int id;

    public BattleServer(Server mainServer) {
        this.mainServer = mainServer;
    }

    public void start() {
        this.battle = new Battle(new Board(6, 9));

        this.userHandlers.forEach((key, value) -> {
            this.battle.addUser(value.getUser());
        });

        this.battle.start();
        
        broadcastMessage("ready");

        this.battle.getUsers().forEach((key, value) -> {
            this.userHandlers.get(key).sendMessage("init " + String.valueOf(key) + ";" + this.battle.getBoard().toStringHeader());
        });
        
        broadcastMessage("start" + this.battle.getBoard().toStringFields());

        changeTurn();
        // init
        // start
        // changeTurn()
    }



    @Override
    public void processRequest(UserHandler userHandler, String request) {
        String[] requestComponents = request.split(" ");
        String action = requestComponents[0];
        
        switch (action) {
            case "skip":
                handleSkip(Integer.parseInt(requestComponents[2]));
                break;
            case "moving":
                handleMoving(Integer.parseInt(requestComponents[1]), Integer.parseInt(requestComponents[2]));
                break;
            case "move":
                handleMove(Integer.parseInt(requestComponents[1]), Integer.parseInt(requestComponents[2]), Integer.parseInt(requestComponents[3]));
                break;
            case "attacking":
                handleAttacking(Integer.parseInt(requestComponents[1]), Integer.parseInt(requestComponents[2]));
                break;
            case "attack":
                handleAttack(Integer.parseInt(requestComponents[1]), Integer.parseInt(requestComponents[2]), Integer.parseInt(requestComponents[3]));
                break;
            case "conclude":
                handleConclude(userHandler);
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
        this.userHandlers.get(controllerId).sendMessage("moving;" + validMovementTargets.toString());
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
        this.userHandlers.get(controllerId).sendMessage("attacking;" + validAttackTargets.toString());
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

    private void handleConclude(UserHandler userHandler) {
        this.userHandlers.remove(userHandler.getBattleId());
        userHandler.setServer(mainServer);
        userHandler.setBattleId(-1);
        userHandler.getUser().setUserState(UserState.ONLINE);

        if(this.userHandlers.size() == 0) {
            mainServer.closeBattleServer();
        }
    }

    public void broadcastMessage(String message) {
        this.userHandlers.forEach((key, value) -> {
            value.sendMessage(message);
        });
    }

    @Override
    public void disconnectUser(UserHandler userHandler) {
        if(this.userHandlers.containsKey(userHandler.getBattleId())) {
            this.userHandlers.remove(userHandler.getBattleId());
        }else{
            System.out.println(userHandler.getUser().getUsername() + " is already out of the Battle Lobby.");
        }
        
        mainServer.disconnectUser(userHandler);
        if(userHandlers.size() > 0) {
            this.userHandlers.forEach((key, value) -> {
                value.sendMessage("end " + key);
            });
        }else{
            this.battle = null;
        }
    }
    
    public boolean join(UserHandler userHandler) {
        if(isFull()) {
            return false;
        }
        if(this.battle != null) return false;

        userHandler.setServer(this);
        for(int i=0; i<MAX_USERS; i++) {
            if(!userHandlers.containsKey(i)) {
                userHandler.setBattleId(i);
                this.userHandlers.put(i, userHandler);
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        return this.userHandlers.size() == MAX_USERS;
    }
}
