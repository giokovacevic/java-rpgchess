package com.app.core.entities;

public class Hero extends Unit{
    private HeroClass class_;
    private int level;

    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;
    //private Armor headArmor;
    //private Armor bodyArmor;
    //private Armor handArmor;
    //private Necklace necklace; ? Make Necklace, Ring and Cape the same?
    //private Ring ring;
    //private Cape cape;

    public Hero(String id, String name, HeroClass class_, int level, Weapon primaryWeapon, Weapon secondaryWeapon) {
        super(id, name);
        this.class_ = class_;
        this.level = level;
        this.speed = class_.getSpeed();
        this.primaryWeapon = primaryWeapon;
        this.secondaryWeapon = secondaryWeapon;

        this.currentHealth = getMaxHealth();
        this.movementPoints = this.speed;
    }

    @Override
    public int[] getAttackDamage(int distance) { // NOT IMPLEMENTED
        int[] attackDamage = new int[2];
        attackDamage[0] = 0;
        attackDamage[1] = 1;
        if((this.primaryWeapon == null) && (this.secondaryWeapon == null)) return attackDamage;
        boolean isCrit = isCriticalHit();
        
        switch (this.class_) {
            case FIGHTER:
                if(this.primaryWeapon != null) {
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(this.secondaryWeapon!=null) {
                        attackDamage[0] = attackDamage[0] + (this.secondaryWeapon.getDamage()/4);
                    }
                    if(isCrit) attackDamage[1] = 2;
                }
                // add dexterity
                break;
            case WARRIOR:
                if(this.primaryWeapon != null) {
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(isCrit) attackDamage[1] = 2;
                }
                break;
            case CHAMPION: // TEMPORARY 
                if(this.primaryWeapon != null) {
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(distance == 2) {
                        attackDamage[0] = attackDamage[0] + (attackDamage[0]/3);
                    }
                    if(isCrit) attackDamage[1] = 2;
                }
                break;
            case PALADIN:
                if(this.primaryWeapon != null) {
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(isCrit) attackDamage[1] = 2;
                }
                
                break;
            case ASSASSIN:
                if((distance > 1) && (this.secondaryWeapon!=null)) {
                    attackDamage[0] = this.secondaryWeapon.getDamage();
                }else if(this.primaryWeapon != null){
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(isCrit) attackDamage[1] = 2;
                }
                
                break;
            case ARCHER:
                if((distance == 1) && (this.secondaryWeapon!=null)) {
                    attackDamage[0] = this.secondaryWeapon.getDamage();
                }else if(this.primaryWeapon != null){
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(isCrit) attackDamage[1] = 2;
                }
                break;
            case MAGE:
                if(this.primaryWeapon != null) {
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(isCrit) attackDamage[1] = 2;
                }
                break;
            case CONJURER:
                if(this.primaryWeapon != null) {
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(isCrit) attackDamage[1] = 2;
                }
                break;
            case CLERIC:
                if(this.primaryWeapon != null) {
                    attackDamage[0] = this.primaryWeapon.getDamage();
                    if(isCrit) attackDamage[1] = 2;
                }
                break;
        
            default:
                System.out.println("Error");
                break;
        }
        
        return attackDamage;
    }
    @Override
    public int getRange() { return (this.primaryWeapon == null ? 0 : this.primaryWeapon.getWeaponType().getMaxRange()); }
    @Override
    public int getCritChance() { // TO BE EDITED
        int totalCritChance = 0;
        if(this.primaryWeapon != null) {
            totalCritChance = totalCritChance + primaryWeapon.getCritChance();
        }
        return totalCritChance;
    }
    @Override
    public int getMaxHealth() { return (this.class_.getBaseHealth() + (this.class_.getHealthPerLevel() * this.level)); }
    @Override
    public int getCurrentHealth() { return this.currentHealth; }
    @Override
    public int[] takeDamage(int[] damage) {
        int damageTaken = (damage[0]*damage[1]);
        if(damageTaken <= 0) return new int[]{0, damage[1]};
        
        int totalBlockValue = 0;
        if(this.secondaryWeapon != null) {
            totalBlockValue = totalBlockValue + this.secondaryWeapon.getBlockValue();
        }

        damageTaken = Math.max((damageTaken - totalBlockValue), 0);

        this.currentHealth = this.currentHealth - damageTaken;
        if(this.currentHealth < 0) this.currentHealth = 0;
        
        return new int[]{damageTaken, damage[1]};
    }
    @Override
    public int getMovementPoints() {
        return this.movementPoints;
    }
    @Override
    public void resetMovementPoints() {
        this.movementPoints = speed;
    }
    @Override
    public int consumeMovementPoints(int distanceCovered) {
        if(distanceCovered > this.movementPoints) return (-1);
        this.movementPoints-=distanceCovered;
        return distanceCovered;
    }
    @Override
    public void setDirection(int direction) {
        this.direction = direction;
    }
    @Override
    public int getDirection() { return this.direction; }

    // Setters and Getters
    public void setLevel(int level) {
        this.level = level;
    }

    public HeroClass getClass_() {return this.class_;}
    public int getLevel() { return this.level; }
    public Weapon getPrimaryWeapon() { return this.primaryWeapon; }
    public Weapon getSecondaryWeapon() { return this.secondaryWeapon; }
}
