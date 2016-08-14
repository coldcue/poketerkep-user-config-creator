package com.botcrator.support;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class UserNameGenerator {
    private static final List<String> firstNames = Arrays.asList("Runny", "Buttercup", "Dinky", "Stinky", "Crusty", "Greasy", "Gidget", "Cheesypoof", "Lumpy", "Wacky", "Tiny", "Flunky", "Fluffy", "Zippy", "Doofus", "Gobsmacked", "Slimy", "Grimy", "Salamander", "Oily", "Burrito", "Bumpy", "Loopy", "Snotty", "Irving", "Egbert", "Snicker", "Buffalo", "Gross", "Bubble", "Sheep", "Corset", "Toilet", "Lizard", "Waffle", "Kumquat", "Burger", "Chimp", "Liver", "Gorilla", "Rhino", "Emu", "Pizza", "Toad", "Gerbil", "Pickle", "Tofu", "Chicken", "Potato", "Hamster", "Lemur", "Vermin");
    private static final List<String> lastNames = Arrays.asList("face", "dip", "nose", "brain", "head", "breath", "pants", "shorts", "lips", "mouth", "muffin", "butt", "bottom", "elbow", "honker", "toes", "buns", "spew", "kisser", "fanny", "squirt", "chunks", "brains", "wit", "juice", "shower");

    public String generateUserName() {
        Random random = new Random();
        return (firstNames.get(random.nextInt(firstNames.size() - 1)) + lastNames.get(random.nextInt(lastNames.size() - 1)) + random.nextInt(1000)).toLowerCase();
    }
}
