package sample;

import static java.lang.Math.random;

public class Drop {

    // peut-etre pas necessaire comme classe mais comme enum...

    /*
    todo: gestion des bonus/malus:
     - proprietes (position, effets, taille...)
     - generation et affichage
     - physique (vers le bas, collisions)
     - ...
     */

    private boolean isBonus;
    private final double BONUS_CHANCE = 0.5;
    private String effect;
    private String[] bonusEffectArray = {"increaseBarSize", "decreaseBallSpeed", "increaseBarSpeed"};
    private String[] malusEffectArray = {"decreaseBarSize", "increaseBallSpeed", "decreaseBarSpeed"};

    public Drop() {
        setRandomEffect(BONUS_CHANCE);
    }

    private void setRandomEffect(double bonusChance) {
        if (random() >= bonusChance) {
            //drop is a bonus
            int randomBonus = (int) (random() * bonusEffectArray.length);
            effect = bonusEffectArray[randomBonus];
        } else {
            //drop is a malus
            int randomBonus = (int) (random() * malusEffectArray.length);
            effect = malusEffectArray[randomBonus];
        }
    }

    public boolean isBonus() {
        return isBonus;
    }

    public void setBonus(boolean bonus) {
        isBonus = bonus;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
