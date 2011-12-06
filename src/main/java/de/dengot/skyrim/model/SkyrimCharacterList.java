package de.dengot.skyrim.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("CharacterList")
public class SkyrimCharacterList implements Iterable<SkyrimCharacter> {

    @XStreamImplicit
    private List<SkyrimCharacter> characters;

    public SkyrimCharacterList() {
        this.characters = new ArrayList<SkyrimCharacter>();
    }
    
    public List<SkyrimCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<SkyrimCharacter> characters) {
        this.characters = characters;
    }

    public Iterator<SkyrimCharacter> iterator() {
        return this.characters.iterator();
    }
    
    public int getMaxValue(String statName){
        int maxValue = 0;
        for (SkyrimCharacter ch : this) {
            int val = ch.getMaxValue(statName);
            if (val > maxValue){
                maxValue = val;
            }
        }
        return maxValue;
    }
    
    public int getMinValue(String statName){
        int minValue = Integer.MAX_VALUE;
        for (SkyrimCharacter ch : this) {
            int val = ch.getMinValue(statName);
            if (minValue < val){
                minValue = val;
            }
        }
        return minValue;
    }
}
