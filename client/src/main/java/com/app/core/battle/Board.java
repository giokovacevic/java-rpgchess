package com.app.core.battle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.app.core.entities.BattleEntity;
import com.app.core.entities.Unit;
import com.app.core.entities.behaviour.*;

public class Board {
    private int rows, columns;
    private HashMap<Integer, Field> fields;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.fields = new HashMap<>();
        for(int i=0; i<(rows*columns); i++) {
            this.fields.put(i, new Field(i));
        }
    }

    private void bfsReset() {
        this.fields.forEach((key, tile) -> {
            tile.setDistance(-1);
            tile.setVisited(false);
        });
    }
    private void bfsForMovement(Field start) {
        bfsReset();
        Queue<Field> queue = new LinkedList<>();
        start.setVisited(true);
        start.setDistance(0);
        queue.add(start);

        while(!queue.isEmpty()) {
            Field current = queue.poll();
            for(Field neighbor : getNeighbors(current)) {
                if(!neighbor.getVisited() && !neighbor.isOccupied() && !neighbor.getBlocked()) {
                    neighbor.setVisited(true);
                    neighbor.setDistance(current.getDistance() + 1);
                    queue.add(neighbor);
                }
            }
        }
    }
    private void bfsForAttacking(Field start) {
        bfsReset();
        Queue<Field> queue = new LinkedList<>();
        start.setVisited(true);
        start.setDistance(0);
        queue.add(start);

        while(!queue.isEmpty()) {
            Field current = queue.poll();
            for(Field neighbor : getNeighbors(current)) {
                if(!neighbor.getVisited()) {
                    neighbor.setVisited(true);
                    neighbor.setDistance(current.getDistance() + 1);
                    queue.add(neighbor);
                }
            }
        }
    }
    private LinkedList<Field> getNeighbors(Field tile) {
        LinkedList<Field> neighborTiles = new LinkedList<>(); 
        int tileId = tile.getId();
        int left = -1;
        int topLeft = (-this.columns) - 1;
        int topRight = -this.columns;
        int right = 1;
        int botRight = this.columns;
        int botLeft = this.columns - 1;

        if(((tileId / this.columns)%2) == 0) {
            if(!isFirstColumn(tileId)) neighborTiles.add(this.getFields().get(tileId + left));
            if(!(isFirstColumn(tileId) || isFirstRow(tileId))) neighborTiles.add(this.getFields().get(tileId + topLeft));
            if(!isFirstRow(tileId)) neighborTiles.add(this.getFields().get(tileId + topRight));
            if(!isLastColumn(tileId)) neighborTiles.add(this.getFields().get(tileId + right));
            if(!isLastRow(tileId)) neighborTiles.add(this.getFields().get(tileId + botRight));
            if(!(isFirstColumn(tileId) || isLastRow(tileId))) neighborTiles.add(this.getFields().get(tileId + botLeft));
        }else{
            if(!isFirstColumn(tileId)) neighborTiles.add(this.getFields().get(tileId + left));
            neighborTiles.add(this.getFields().get(tileId + topLeft + 1)); // top left uvek ima??
            if(!isLastColumn(tileId)) neighborTiles.add(this.getFields().get(tileId + topRight + 1));
            if(!isLastColumn(tileId)) neighborTiles.add(this.getFields().get(tileId + right));
            if(!(isLastColumn(tileId) || isLastRow(tileId))) neighborTiles.add(this.getFields().get(tileId + botRight + 1));
            if(!isLastRow(tileId)) neighborTiles.add(this.getFields().get(tileId + botLeft + 1));
        }

        return neighborTiles;
    }

    private boolean isFirstColumn(int tileId) {
        return ((tileId%this.columns) == 0);
    }
    private boolean isLastColumn(int tileId) {
        return ((tileId%this.columns) == (this.columns - 1));
    }
    private boolean isFirstRow(int tileId) {
        return ((tileId/this.columns) == 0);
    }
    private boolean isLastRow(int tileId) {
        return ((tileId/this.columns) == (this.rows-1));
    }

    public LinkedList<Integer> getValidMovementTargets(int start, int range) {
        bfsForMovement(getFields().get(start));
        LinkedList<Integer> targets = new LinkedList<>();
        this.fields.forEach((key, value) -> {
            if(start != key) {
                if(value.getDistance() <= range && value.getDistance() > 0) targets.add(key);
            }
        });
        return targets;
    }
    public LinkedList<Integer> getValidAttackTargets(int start, int range) {
        bfsForAttacking(getFields().get(start));
        LinkedList<Integer> targets = new LinkedList<>();
        BattleEntity battleMember = this.fields.get(start).getEntity();
        this.fields.forEach((key, value) -> {
            if(value.isOccupied()) {
                if((value.getEntity()) instanceof Attackable) {
                    if((value.getDistance() <= range) && (battleMember.getControllerId() != value.getEntity().getControllerId())) {
                        targets.add(value.getId());
                    }
                }
            }
        });
        return targets;
    }

    public int move(BattleEntity battleMember, int destinationTileId) {
        int sourceTileId = battleMember.getFieldId();
        BattleEntity tempBattleMember = this.fields.get(sourceTileId).getEntity();
        this.fields.get(destinationTileId).setEntity(tempBattleMember);
        ((Movable)tempBattleMember).consumeMovementPoints(this.fields.get(destinationTileId).getDistance());
        this.fields.get(sourceTileId).setEntity(null);
        ((Movable)tempBattleMember).setDirection(getDirection(sourceTileId, destinationTileId));
        
        return sourceTileId;
    }
    public int[] attack(BattleEntity battleMember, int destinationTileId) {
        int sourceTileId = battleMember.getFieldId();
        BattleEntity tempBattleMember = this.fields.get(sourceTileId).getEntity();
        int[] damage = ((Attacking)tempBattleMember).getAttackDamage(this.getFields().get(destinationTileId).getDistance());
        BattleEntity targetedBattleMember = this.fields.get(destinationTileId).getEntity();
        int[] damageTaken = ((Attackable)targetedBattleMember).takeDamage(damage);
        if(!targetedBattleMember.isAlive()){
            this.fields.get(targetedBattleMember.getFieldId()).setEntity(null);
            targetedBattleMember.setFieldId(-1);
            damageTaken[1] = (-1) * damageTaken[1];
        } 
        ((Movable)tempBattleMember).setDirection(getDirection(sourceTileId, destinationTileId));
        System.out.println(String.valueOf(getDirection(sourceTileId, destinationTileId)) + " for: " + String.valueOf(sourceTileId) + "  " + String.valueOf(destinationTileId));
        tempBattleMember.setHasCompletedTurn(true);
        
        return damageTaken;
    }
    public void skip(BattleEntity battleMember) {
        battleMember.setHasCompletedTurn(true);
    }

    public int simulateMove(BattleEntity battleMember) {
        if(!(battleMember instanceof Movable)) return (-1);
        LinkedList<Integer> targets = getValidAttackTargets(battleMember.getFieldId(), ((Attacking)battleMember).getRange());
        if(targets.size() != 0) return (-1);

        LinkedList<Integer> validMovementTargets = getValidMovementTargets(battleMember.getFieldId(), ((Movable)battleMember).getMovementPoints());
        
        if(validMovementTargets.isEmpty()) return (-1);

        LinkedList<Integer> targetsInRange = getValidAttackTargets(battleMember.getFieldId(), ((Movable)battleMember).getMovementPoints()+1);

        if(!targetsInRange.isEmpty()) { // priblizi se dovoljno blizu najslabijeg da mozes da pucas
            int targetTileId = findWeakestEntityFieldId(targetsInRange);
            validMovementTargets = findTargetInRangeFieldIds(battleMember.getFieldId(), targetTileId);
            return (validMovementTargets.isEmpty() ? (-1) : (validMovementTargets.get(new Random().nextInt(0, validMovementTargets.size()))));
        }else{// priblizi se dovoljno blizu najslabijem na mapi
            int targetFieldId = findWeakestEntityFieldId(getValidAttackTargets(battleMember.getFieldId(), 99999));
            validMovementTargets = findTargetOutOfRangeFieldIds(battleMember.getFieldId(), targetFieldId);
            return findShortestDistanceToFieldId(targetFieldId, validMovementTargets);
        }
        
    }
    public int simulateAttack(BattleEntity battleMember) {
        if(!(battleMember instanceof Attacking)) return (-1);
        LinkedList<Integer> targets = getValidAttackTargets(battleMember.getFieldId(), ((Attacking)(battleMember)).getRange());
        if(!targets.isEmpty()) {
            return findWeakestEntityFieldId(targets);
        }
        return (-1);
    }
    private int findWeakestEntityFieldId(LinkedList<Integer> targets) {
        int fieldId = targets.get(0);
        int min = ((Attackable)(this.fields.get(fieldId).getEntity())).getCurrentHealth();
        for(int i=1; i<targets.size(); i++) {
            if(((Attackable)(this.fields.get(targets.get(i)).getEntity())).getCurrentHealth() < min) {
                min = ((Attackable)(this.fields.get(targets.get(i)).getEntity())).getCurrentHealth();
                fieldId = targets.get(i);
            }
        }
        return fieldId;  
    }
    private LinkedList<Integer> findTargetInRangeFieldIds(int sourceFieldId, int targetFieldId) {  
        LinkedList<Integer> inRangeTargets = getValidMovementTargets(targetFieldId, ((Attacking)(this.fields.get(sourceFieldId).getEntity())).getRange());
        LinkedList<Integer> possibleMovementTargets = getValidMovementTargets(sourceFieldId, ((Movable)(this.fields.get(sourceFieldId).getEntity())).getMovementPoints());
        
        LinkedList<Integer> validMovementTargets = new LinkedList<>();
        possibleMovementTargets.forEach(possibleTarget -> {
            if(inRangeTargets.contains(possibleTarget)) validMovementTargets.add(possibleTarget);
        });
        
        return validMovementTargets;
    }
    private LinkedList<Integer> findTargetOutOfRangeFieldIds(int sourceFieldId, int targetFieldId) {
        LinkedList<Integer> possibleMovementTargets = getValidMovementTargets(sourceFieldId, ((Movable)(this.fields.get(sourceFieldId).getEntity())).getMovementPoints());
        LinkedList<Integer> possibleInRangeTargets = null;
        LinkedList<Integer> inRangeTargets = new LinkedList<>();
        int i=0;
        while(inRangeTargets.isEmpty() && i<100) {
            possibleInRangeTargets = getValidMovementTargets(targetFieldId, ((Attacking)this.fields.get(sourceFieldId).getEntity()).getRange() + i);
            for(int possibleMovementTarget : possibleMovementTargets) {
                if(possibleInRangeTargets.contains(possibleMovementTarget)) inRangeTargets.add(possibleMovementTarget); 
            }
            i++;
        }
        
        return inRangeTargets;
    }

    private int findShortestDistanceToFieldId(int sourceFieldId, LinkedList<Integer> targets) {
        if(targets.isEmpty()) return (-1);
        bfsForMovement(this.fields.get(sourceFieldId));
        LinkedList<Integer> distances = new LinkedList<>();
        for(int target : targets) {
            distances.add(this.fields.get(target).getDistance());
        }

        int min = distances.get(0);
        int minIndex = 0;
        for(int j=1; j<distances.size(); j++) {
            if(distances.get(j) < min) {
                min = distances.get(j);
                minIndex = j;
            } 
        }
        
        return targets.get(minIndex);
    }

    private int getDirection(int source, int destination) {
        int sourceRow = source / this.columns;
        int sourceColumn = source % this.columns;
        int destinationRow = destination / this.columns;
        int destinationColumn =  destination % this.columns;

        
        if(((sourceRow%2) == 0) && ((destinationRow%2) == 0)) {
            if(destinationColumn < sourceColumn) {
                return 1;
            }
            return 0;
        }else if(((sourceRow%2) == 0) && ((destinationRow%2) == 1)) {
            if(destinationColumn < sourceColumn) {
                return 1;
            }
            return 0;
        }else if(((sourceRow%2) == 1) && ((destinationRow%2) == 0)) {
            if(destinationColumn <= sourceColumn) {
                return 1;
            }
            return 0;
        }else{
            if(destinationColumn < sourceColumn) {
                return 1;
            }
            return 0;
        }
    }

    // Setters and Getters

    public int getRows() {return this.rows; }
    public int getColumns() {return this.columns; }
    public HashMap<Integer, Field> getFields() {return this.fields; }
}
